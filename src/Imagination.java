import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// КООРДИНАТОР ПРОЕКТА
// 1) здесь определяется путь к файлу изображения, который нужно оцифровать
// 2) производится преобразование значений RGB изображения в относительное число-значение, определяющее яркость каждого пикселя
// 3) отправка преобразованного изображения (в виде массива значений типа double) в сегментатор_текста
// 4) после всех вычислений (то есть в конце работы программы) идет обращение к методу_получения оцифрованного текста (в виде строки)
// 5) вывод результата (строки)
// ДОПОЛНИТЕЛЬНО:
// 6) генератор, заполняющий файлы весов случайными значениями (в диапозоне (-0.5; 0.5)): используется при переобучении (для удобства)
// 7) возможность подать в нейросеть один единственный символ (для удобства)
// 8) возможность подать в обучатель_нейросети один единственный символ (для удобства) - не использовался

public class Imagination {
    public static void main(String[] args) throws IOException { // запускает работу всего проекта
        Finder finder = new Finder();
        NeuroNet neuronet = new NeuroNet();
        BufferedImage image = null; // изображение для оцифровки
        String string; // строка с результатом оцифровки
        String path = "C:\\Users\\user\\Desktop\\Diplom-master\\Diplom-master\\src\\пр1.png"; // путь к файлу, который нужно оцифровать
        File f = new File(path);

        try {
            image = ImageIO.read(f); // загрузка изображения
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        int width = image.getWidth(); // ширина изображения
        int height = image.getHeight(); // высота изображения
        double[][] pixels = new double[height][width]; // массив для передачи значений в сегментатор

        for (int row = 0; row < height; row++) {
            //System.out.println();
            for (int col = 0; col < width; col++) {
                Color rgb = new Color(image.getRGB(col, row)); // перевод в RGB-значение
                double red = 1 - (double) rgb.getRed()/255;
                double green = 1 - (double) rgb.getGreen()/255;
                double blue = 1 - (double) rgb.getBlue()/255;
                // вывод матрицы-значений изображения/255 (все оттенки цвета имеют одинаковое значение); диапазон (0,1);
                // использование вычитания нужно, чтобы задать белый цвет как '0', а черный цвет - '1' (в RGB - значения противоположны)

                pixels[row][col] = (red + green + blue) / 3; // получение среднего значения от всех оттенков цвета RGB
                pixels[row][col] = Math.round(pixels[row][col]*100)/100.0; // преобразование числа до сотых после запятой
                //System.out.print(pixels[row][col] + " "); // вывод массива для передачи значений в сегментатор
            }
        }

        // быстрая генерация весов в БД_весов
        /*int ne = 1;
        for(int x = 0; x < 40; x++) {
            FileWriter fv = new FileWriter("C:/Users/user/Desktop/Diplom-master/Diplom-master/src/Data/w1."+ne+" 784.txt"); // запись текста в файл
            //FileWriter fv = new FileWriter("C:/Users/user/Desktop/Diplom-master/Diplom-master/src/Data/ws3.txt"); // запись текста в файл
            for (int z = 0; z < 784; z++) { // запись данных в файлы_весов
                double rand;
                int r = (int) (Math.random()*(10+1)) - 5;
                rand = (double) r/10;
                //System.out.println("r="+r+" rand="+rand);
                String lineSeparator = System.getProperty("line.separator");
                fv.write(0 + lineSeparator);
            }
            fv.close();
            ne++;
        }*/

        finder.stringFind(pixels, height, width); // основной метод запуска нейросети
        //neuronet.preporation(image); // метод проверки нейросети одним символом
        //neuronet.trainer(image); // метод обучение нейросети одним символом

        string = neuronet.getString(); // получение результата оцифровывания
        System.out.println();
        System.out.print(string); // его вывод
    }
}


