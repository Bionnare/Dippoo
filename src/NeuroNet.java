// НЕЙРОСЕТЬ: ПОДАЁТСЯ МАССИВ-МАТРИЦА ЗНАЧЕНИЙ ПИКСЕЛЕЙ ИЗОБРАЖЕНИЯ ИЗ КЛАССА FINDER!

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NeuroNet {
    List<Neuron> neurons = new ArrayList<>(); // список нейронов текущего слоя

    int x = 1; // счетчик файлов
    int a = 1, b = 1, c = 1024; // значения для счетчика БД_весов
    int sl = 20; // размер следующего слоя
    double[] znac = new double[c]; // значения нейронов для текущего слоя
    double[] newZnac = new double[sl]; // выходные значения нейронов для текущего слоя
    double out; // переменная для выходного значения одного нейрона текущего слоя
    double w1 = 0, w2 = 0; // веса смещения

     // ЗАПИСЬ ЗНАЧЕНИЙ НЕЙРОНОВ
    public void preporation(BufferedImage image) throws IOException { // прописать исключение 'если изображение не существует' !!!
        String string = "C:/Users/user/Desktop/Diplom-master/Diplom-master/src/Save/neuron" + x + ".png"; // используем сохраненные в папке 'Save' изображения символов
        File f = new File(string);
        //System.out.println("cycle="+x);
        image = resize(image, 32, 32); // изменяем размер под стандарт
        ImageIO.write(image, "PNG", new File("C:/Users/user/Desktop/Diplom-master/Diplom-master/src/Save/NEURON.png")); // сохранение изображения (для проверки)

        // записываем пиксели в массив значений нейронов
        int width = image.getWidth(); // ширина изображения
        int height = image.getHeight(); // высота изображения
        neurons = new ArrayList<>(); // обнуляем список
        int q = 0;
        for (int row = 0; row < height; row++) {
            //System.out.println();
            for (int col = 0; col < width; col++) {
                Color mycolor = new Color(image.getRGB(col, row)); // перевод в RGB-значение
                double r = 1 - (double) mycolor.getRed()/255;
                double g = 1 - (double) mycolor.getGreen()/255;
                double b = 1 - (double) mycolor.getBlue()/255;

                //double p = r; // использование одного цвета
                double p = (r + g + b) / 3; // использование трех цветов
                p = Math.round(p*100)/100.0; // преобразование числа до сотых после запятой
                znac[q] = p;
                q++;
                //System.out.print(p + " ");
            }
        }
        neuroBody(); // ввод в нейросеть
        // устанавливаем параметры по умолчанию
        a++;
        b = 1;
        c = 1024;
        x++;
    }

    // КООРДИНАТОР НЕЙРОСЕТИ
    public int neuroBody () throws IOException { // создание списка нейронов входного слоя
        int k = 0; // нужно для return / после удалить!

        double[] vesa = new double[c]; // массив весов текущего слоя
        newZnac = new double[sl]; // выходные значения текущего слоя
        int v = 0;
        for(int i = 0; i < sl; i++) { // чтение весов из файлов БД_весов
            FileReader fr = new FileReader("C:/Users/user/Desktop/Diplom-master/Diplom-master/src/Data/w"+a+"."+b+" "+c+".txt");
            Scanner scanner = new Scanner(fr);
            while (scanner.hasNextLine()) { // загрузка весов из файла
                String str = scanner.nextLine();
                vesa[v] = Double.parseDouble(str);
                neurons.add(new Neuron(znac[v],vesa[v])); // запись / создание нейрона
                //System.out.println("v= "+v);
                v++;
            }
            vesa = new double[c];
            v = 0;

            /*for(Neuron n : neurons){ // вывод списка нейронов
                System.out.println("№файла="+b+" znachenie="+n.getX() + " ves="+n.getW());
            }
            */

            out = summator(neurons); // нахождение выходного значения одного нейрона для текущего слоя
            //System.out.println("out"+b+"= "+out);
            newZnac[i] = out - w1; // запись нового значения
            neurons = new ArrayList<>();
            fr.close(); // закрытие потока чтения файла_весов
            b++;
        }
        for(int j = 0; j < sl; j++){ // вывод массива выходных значений текущего слоя
            System.out.println(newZnac[j]);
        }
        return k;
    }

    // СУММАТОР ПРОИЗВЕДЕНИЙ ВСЕХ ЗНАЧЕНИЯ*ВЕС НЕЙРОНОВ
    public double summator (List<Neuron> neuro){
        double u = 0;

        for (Neuron n : neuro){
            u += n.getX() * n.getW();
        }
        u = activating(u); // функция активации

        return u;
    }

    // ФУНКЦИЯ АКТИВАЦИЯ
    public double activating (double u){
        double e = Math.E; // нахождение экспоненты
        e = Math.round(e*100)/100.0; // преобразование числа до сотых после запятой
        //System.out.println(u);

        // из-за проблем с переполнением double и записи в BigDecimal
        // используется данный 'идиотский' костыль, просчитывающий заданную функцию активации

        // использование функции активации "Гиперболический тангенс" [ (Math.pow(e, 2*u) - 1) / (Math.pow(e, 2*u) + 1) ]
        u = Math.pow(e, 2*u);
        u = Math.round(u*10000)/10000.0;
        BigDecimal uu = BigDecimal.valueOf(u);
        //System.out.println("1этап="+uu);
        BigDecimal u1 = uu.add(BigDecimal.valueOf(1.0));
        BigDecimal u2 = uu.subtract(BigDecimal.valueOf(1.0));
        //System.out.println("2.1этап="+u1);
        //System.out.println("2.2этап="+u2);
        double uu1 = u2.doubleValue();
        double uu2 = u1.doubleValue();
        u = uu1 / uu2;
        //System.out.println("3этап="+u);

        //u = (double) Math.round(u*100)/100.0;
        //u = 1 / (1 + Math.pow(e, -u));
        // функция активации

        return u;
    }

    private static BufferedImage resize (BufferedImage img, int height, int width) { // стандартизация изображения под один размер
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
}

// КЛАСС НЕЙРОНА
class Neuron {
    double xnac;
    double wes;

    Neuron(double x, double w){
        xnac = x;
        wes = w;
    }

    double getX() {
        return xnac;
    }

    void setX(double x) {
        xnac = x;
    }

    double getW() {
        return wes;
    }

    void setW(double w) {
        wes = w;
    }
}
