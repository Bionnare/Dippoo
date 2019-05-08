// НЕЙРОСЕТЬ: ПОДАЁТСЯ МАССИВ-МАТРИЦА ЗНАЧЕНИЙ ПИКСЕЛЕЙ ИЗОБРАЖЕНИЯ ИЗ КЛАССА FINDER!

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
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
        // ввод в нейросеть
        for (a = 1; a < 4; a++) {
            if (a == 1){ // для входного слоя
                b = 1;
                c = 1024;
                sl = 20;
            }
            if (a == 2){ // для первого скрытого слоя
                b = 1;
                c = 20;
                sl = 20;
                rewrite();
            }
            if (a == 3){ // для второго скрытого слоя
                b = 1;
                c = 20;
                sl = 16;
                rewrite();
            }
            neuroBody();
        }
        System.out.println("РЕЗУЛЬТАТ!!!");
        for(int j = 0; j < sl; j++){ // вывод массива выходных значений текущего слоя
            System.out.println(newZnac[j]);
        }
        System.out.println();
        // устанавливаем параметры по умолчанию
        a = 1;
        b = 1;
        c = 1024;
        x++;
    }

    // КООРДИНАТОР НЕЙРОСЕТИ
    public int neuroBody () throws IOException { // создание списка нейронов входного слоя
        int k = 0; // нужно для return / после удалить!
        double w0 = 0;

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
                //System.out.println("v= "+vesa[v]);
                v++;
            }
            vesa = new double[c];
            v = 0;

            /*for(Neuron n : neurons){ // вывод списка нейронов
                System.out.println("№файла="+a+"."+b+" znachenie="+n.getX() + " ves="+n.getW());
            }*/

            out = summator(neurons); // нахождение выходного значения одного нейрона для текущего слоя
            System.out.println("out"+a+"."+b+"= "+out);

            // использование определенного веса смещения
            if (a == 1){ // для первого скрытого слоя
                w0 = w1;
            }
            if (a == 2){ // для второго скрытого слоя
                w0 = w2;
            }
            if (a == 3){ // для выходных - вес смещения не нужен
                w0 = 0;
            }
            newZnac[i] = out - w0; // запись нового значения
            neurons = new ArrayList<>();
            fr.close(); // закрытие потока чтения файла_весов
            b++;
        }
        for(int j = 0; j < sl; j++){ // вывод массива выходных значений текущего слоя
            System.out.println(newZnac[j]);
        }
        System.out.println();
        return k;
    }

    // СУММАТОР ПРОИЗВЕДЕНИЙ ВСЕХ ЗНАЧЕНИЯ*ВЕС НЕЙРОНОВ
    public double summator (List<Neuron> neuro){
        double u = 0;
        double res = 0;

        for (Neuron n : neuro){
            //System.out.println("№файла="+a+"."+b+" znachenie="+n.getX() + " ves="+n.getW());
            u += n.getX() * n.getW();
            //System.out.println("ПРОВЕРКА ЦИКЛА СУММИРОВАНИЯ: u"+a+"."+b+"= "+u);
        }
        //System.out.println("u"+a+"."+b+"= "+uu);
        res = activating(u); // функция активации

        return res;
    }

    // ФУНКЦИЯ АКТИВАЦИЯ
    public double activating (double u){

        // модифицированная функция активации 'Relu' [ max(0, x) ] при отрицательных значениях используется 'Линейная функция' [ 0.1*x ]
        double uu;
        if (u < 0){
            uu = 0.1 * u;
        }
        else {
            uu = u;
        }

/////////////////////////ЧЕРНОВИК/////////////////////////

        //double e = Math.E; // нахождение экспоненты
        //e = Math.round(e*100)/100.0; // преобразование числа до сотых после запятой
        /*int ea = (int) e;
        int eb = 100;
        BigInteger eaa = BigInteger.valueOf(ea);
        BigInteger ebb = BigInteger.valueOf(eb);*/

        //System.out.println(u);

        // из-за проблем с переполнением double и записи в BigDecimal
        // используется данный 'идиотский' костыль, просчитывающий заданную функцию активации

        // использование функции активации "Гиперболический тангенс" [ (Math.pow(e, 2*u) - 1) / (Math.pow(e, 2*u) + 1) ]
        //double uu = pow(e, 2*u);
        /*BigInteger uu = eaa.pow(2*u);
        System.out.println("uu="+uu);
        BigInteger uuu = ebb.pow(2*u);
        System.out.println("uuu="+uuu);
        BigInteger uuuu = uu.divide(uuu);
        System.out.println("uuuu="+uuuu);

        long xx1 = uu.longValue();
        long xx2 = uuu.longValue();

        System.out.println("xx1="+xx1);
        System.out.println("xx2="+xx2);*/

        //u = 1 / (1 + Math.pow(e, -u));

        //double uu = Math.pow(e,-u);
        //uu = Math.round(uu*10000)/10000.0; //
        /*double uu = pow(e,u);
        System.out.println("uu="+uu + " u=" + u);
        double u1 = uu - 1;
        System.out.println("u1="+u1);
        double u2 = uu + 1;
        System.out.println("u2="+u2);
        //double uuu = u1 / u2;
        double uuu = 1/ u2;
        //uuu = uuu - 0.5;
        System.out.println("uuu="+uuu);*/

        /*double temp = Math.round(u*10000);
        int ua = (int) temp;
        int ub = 10000;*/
        //u = Math.pow(e,ua);
        //u = Math.exp(u*Math.log10(e));
        //pomosh(u, ub);
        //System.out.println("-1="+e1);
        //System.out.println("-2="+uu);

        /*double uu = Math.pow(e,2*u);
        System.out.println("uu="+uu);
        double u1 = uu - 1;
        double u2 = uu + 1;
        System.out.println("u1="+u1);
        System.out.println("u2="+u2);
        double uuu = u1 / u2;
        System.out.println("uuu="+uuu);*/

        /*double ue = Math.pow(e,2*u);

        //BigDecimal ee = BigDecimal.valueOf(e);
        BigDecimal uu = BigDecimal.valueOf(ue);

        System.out.println("1этап="+uu);
        BigDecimal u1 = uu.add(BigDecimal.valueOf(1.0));
        BigDecimal u2 = uu.subtract(BigDecimal.valueOf(1.0));
        System.out.println("2.1этап="+u1);
        System.out.println("2.2этап="+u2);
        BigDecimal u3 = u1.divide(u2, 25, ROUND_HALF_UP);
        double uu1 = u2.doubleValue();
        double uu2 = u1.doubleValue();
        double uuu = uu1 / uu2;
        System.out.println("3этап="+u3);*/

        //u = (double) Math.round(u*100)/100.0;

        // функция активации

/////////////////////////ЧЕРНОВИК/////////////////////////

        return uu;
    }

    // ПЕРЕЗАПИСЬ МАССИВА ЗНАЧЕНИЙ ТЕКУЩЕГО СЛОЯ
    public void rewrite (){
        znac = new double[c]; // выходные значения текущего слоя

        for (int i = 0; i < znac.length; i++) {
            znac[i] = newZnac[i];
        }
    }

    /*
    // МЕТОД ВОЗВЕДЕНИЯ В СТЕПЕНЬ
    public double pow (double u, int ub){
        u = 1/u;
        while(ub != 1 & ub != 0.5){
            System.out.println("pow0="+u);
            u = Math.round(u*10000)/10000.0; //
            System.out.println("pow1="+u+" ub=" + ub);
            u = Math.pow(u,2);
            System.out.println("pow222="+u);
            ub = ub/2;
        }
        return u;
    }*/

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
