import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Imagination {
    public static void main(String[] args) throws IOException {
        Finder finder = new Finder();
        NeuroNet neuronet = new NeuroNet();
        BufferedImage image = null;

        try {
            image = ImageIO.read(Imagination.class.getResource("пр1.png")); // загрузка из файла изображения (src\ .png)
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        //ImageIO.write(image, "PNG", new File("C:/Users/user/Desktop/Diplom-master/Diplom-master/src/Save/Безымянный.png")); // сохранение файла

        int width = image.getWidth(); // ширина изображения
        int height = image.getHeight(); // высота изображения
        double[][] pixels = new double[height][width];
        for (int row = 0; row < height; row++) {
            //System.out.println();
            for (int col = 0; col < width; col++) {
                Color mycolor = new Color(image.getRGB(col, row)); // перевод в RGB-значение
                double r = 1 - (double) mycolor.getRed()/255;
                double g = 1 - (double) mycolor.getGreen()/255;
                double b = 1 - (double) mycolor.getBlue()/255;
                // вывод матрицы-значений изображения/255 (все оттенки цвета имеют одинаковое значение); диапазон (0,1);
                // использование вычитания нужно, чтобы задать белый цвет как '0', а черный цвет - '1' (в RGB - значения противоположны)

                // настройка яркости пикселей (пока не нужно)
                /*if(((r >= 0.05 & r <= 0.8) & (g >= 0.05 & g <= 0.8) & (b >= 0.05 & b <= 0.8))){
                    if ((r >= g - 0.3 & r <= g + 0.3) | (r >= b - 0.3 & r <= b + 0.3) | (g >= b - 0.3 & g <= b + 0.3) |
                            (g >= r - 0.3 & g <= r + 0.3) | (b >= g - 0.3 & b <= g + 0.3) | (b >= r - 0.3 & b <= r + 0.3)) {
                        //System.out.println("R=" + r + " G=" + g + " B=" + b);
                        r=0.8;
                        g=0.8;
                        b=0.8;
                    }
                }*/

                //pixels[row][col] = r; // использование одного цвета
                pixels[row][col] = (r + g + b) / 3; // использование трех цветов
                pixels[row][col] = Math.round(pixels[row][col]*100)/100.0; // преобразование числа до сотых после запятой
                //System.out.print(pixels[row][col] + " ");
            }
        }

        // быстрая генерация весов в БД_весов
        /*int ne = 1;
        for(int x = 0; x < 20; x++) {
            FileWriter fv = new FileWriter("C:/Users/user/Desktop/Diplom-master/Diplom-master/src/Data/w1."+ne+" 1024.txt"); // запись текста в файл
            for (int z = 0; z < 1024; z++) { // запись данных в файлы_весов
                double rand;
                int r = (int) (Math.random()*(10+1)) - 5;
                rand = (double) r/10;
                //System.out.println("r="+r+" rand="+rand);
                String lineSeparator = System.getProperty("line.separator");
                fv.write(rand + lineSeparator);
            }
            fv.close();
            ne++;
        }*/

        finder.stringFind(pixels,height,width); // основной метод запуска нейросети
        //neuronet.preporation(image); // метод проверки нейросети одним символом
        //neuronet.trainer(img); // основной метод обучение нейросети
    }
}


