import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NeuroNet {
    List<Neuron> neurons = new ArrayList<>(); // список нейронов текущего слоя
    double[][] weightMatrix1 = new double[20][1024]; // матрица весов первого слоя
    double[][] weightMatrix2 = new double[20][20]; // матрица весов скрытого слоя
    double[][] weightMatrix3 = new double[17][20]; // матрица второго скрытого слоя

    double[] in = new double[1024]; // массив значений входного слоя
    double[] h1 = new double[20]; // массив значений первого скрытого слоя
    double[] h2 = new double[20]; // массив значений второго скрытого слоя
    double[] result = new double[17]; // массив выходных значений нейросети

    int x = 1; // счетчик файлов
    int a = 1, b = 1, c = 1024; // значения для счетчика БД_весов
    int sl = 20; // размер следующего слоя
    double[] znac = new double[c]; // значения нейронов для текущего слоя
    double[] newZnac = new double[sl]; // выходные значения нейронов для текущего слоя
    double out; // переменная для выходного значения одного нейрона текущего слоя
    double w1 = 0, w2 = 0, w3 = 0; // веса смещения

     // ЗАПИСЬ ЗНАЧЕНИЙ НЕЙРОНОВ
    public void preporation(BufferedImage image) throws IOException { // прописать исключение 'если изображение не существует' !!!
        String string = "C:/Users/user/Desktop/Diplom-master/Diplom-master/src/Save/neuron" + x + ".png"; // используем сохраненные в папке 'Save' изображения символов
        File f = new File(string);
        //System.out.println("cycle="+x);
        image = resize(image, 32, 32); // изменяем размер под стандарт
        //ImageIO.write(image, "PNG", f); // сохранение изображения

        // записываем пиксели в массив значений нейронов
        int width = image.getWidth(); // ширина изображения
        int height = image.getHeight(); // высота изображения
        neurons = new ArrayList<>(); // обнуляем список
        znac = new double[c]; // значения нейронов для текущего слоя
        int q = 0;
        FileWriter fv = new FileWriter("C:/Users/user/Desktop/Diplom-master/Diplom-master/src/Data/znac/z"+x+"."+a+" "+c+".txt"); // запись текста в файл
        String lineSeparator = System.getProperty("line.separator");
        for (int row = 0; row < height; row++) {
            //System.out.println();
            for (int col = 0; col < width; col++) {
                Color mycolor = new Color(image.getRGB(col, row)); // перевод в RGB-значение
                double red = 1 - (double) mycolor.getRed()/255;
                double green = 1 - (double) mycolor.getGreen()/255;
                double blue = 1 - (double) mycolor.getBlue()/255;

                //double p = red; // использование одного цвета
                double p = (red + green + blue) / 3; // использование трех цветов
                p = Math.round(p*100)/100.0; // преобразование числа до сотых после запятой
                znac[q] = p;
                in[q] = p;
                fv.write(znac[q] + lineSeparator);
                q++;
                //System.out.print(p + " ");
            }
        }
        fv.close();
        // ввод в нейросеть параметров
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
                for(int u = 0; u < newZnac.length; u++){ // запись в память значений слоя
                    h1[u] = newZnac[u];
                }
                rewrite(); // перезапись текущего массива входных значений слоя
            }
            if (a == 3){ // для второго скрытого слоя
                b = 1;
                c = 20;
                sl = 17;
                for(int u = 0; u < newZnac.length; u++){ // запись в память значений слоя
                    h2[u] = newZnac[u];
                }
                rewrite(); // перезапись текущего массива входных значений слоя
            }
            neuroBody(); // запуск вычислений
        }
        FileWriter fr = new FileWriter("C:/Users/user/Desktop/Diplom-master/Diplom-master/src/Data/znac/result"+x+" "+sl+".txt"); // запись текста в файл
        System.out.println("РЕЗУЛЬТАТ!!!");
        for(int j = 0; j < sl; j++){ // вывод массива выходных значений текущего слоя
            System.out.println(newZnac[j]);
            result[j] = newZnac[j];
            fr.write(newZnac[j] + lineSeparator); // запись результатов в файл
        }
        System.out.println();
        fr.close();

        // устанавливаем параметры по умолчанию
        a = 1;
        b = 1;
        c = 1024;
        x++; // ПОКА! (ИСПРАВИТЬ!) при работе обучателя, комментить этот инкремент
    }

    // КООРДИНАТОР НЕЙРОСЕТИ
    public int neuroBody () throws IOException { // создание списка нейронов входного слоя
        int k = 0; // нужно для return / после удалить!
        double w0 = 0; // вес смещения (не используется, пока не используется)

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
            //System.out.println("out"+a+"."+b+"= "+out);

            // использование определенного веса смещения
            if (a == 1){ // для первого скрытого слоя
                w0 = w1;
                int u = 0;
                for(Neuron neu : neurons){ // запись весов в память
                    weightMatrix1[i][u] = neu.getW();
                    u++;
                }
            }
            if (a == 2){ // для второго скрытого слоя
                w0 = w2;
                int u = 0;
                for(Neuron neu : neurons){
                    weightMatrix2[i][u] = neu.getW(); // запись весов в память
                    u++;
                }
            }
            if (a == 3){ // для выходных
                w0 = w3;
                int u = 0;
                for(Neuron neu : neurons){
                    weightMatrix3[i][u] = neu.getW(); // запись весов в память
                    u++;
                }
            }
            newZnac[i] = out - w0; // запись нового значения
            neurons = new ArrayList<>();
            fr.close(); // закрытие потока чтения файла_весов
            b++;
        }
        /*for(int j = 0; j < sl; j++){ // вывод массива выходных значений текущего слоя
            System.out.println(newZnac[j]);
        }*/
        //System.out.println();
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
        //u = Math.round(u*1000)/1000.0; // преобразование числа до сотых после запятой
        res = activating(u); // функция активации
        String format = new DecimalFormat("#.#####").format(res).replaceAll(",", ".");
        res = Double.parseDouble(format); // задаем кол-во знаков после запятой

        return res;
    }

    // ФУНКЦИЯ АКТИВАЦИЯ
    public double activating (double u){

        // модифицированная функция активации 'Relu' [ max(0, x) ] при отрицательных значениях используется 'Линейная функция' [ 0.1*x ]
        double uu;
        if (u < 0){
            uu = 0.1*u;
        }
        else {
            uu = 1;
        }

        //double uu = Math.tanh(u);

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
    public void rewrite () throws IOException {
        znac = new double[c]; // выходные значения текущего слоя
        FileWriter fv = new FileWriter("C:/Users/user/Desktop/Diplom-master/Diplom-master/src/Data/znac/z"+x+"."+a+" "+c+".txt"); // запись текста в файл
        String lineSeparator = System.getProperty("line.separator");

        for (int i = 0; i < znac.length; i++) {
            znac[i] = newZnac[i];
            fv.write(znac[i] + lineSeparator);
        }
        fv.close();
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

    // ИЗМЕНЕНИЕ РАЗМЕРА ИЗОБРАЖЕНИЯ
    private static BufferedImage resize (BufferedImage img, int height, int width) { // стандартизация изображения под один размер
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    int im = 0;

    // ОБУЧАТЕЛЬ
    public void trainer (BufferedImage image) throws IOException{
        // очередь правильных значений при обучении нейросети
        int[] queue = {10,11,12,13,16,0,3,4,5,6,9,8,2,7,1,14,15,
                        10,12,13,11,0,1,3,2,5,4,7,8,9,14,16,15,6,
                        12,13,16,1,16,1,0,14,15,
                        10,11,16,2,9,8,16,3,5,6,15,7,4,14,15,
                        0,6,5,4,10,14,2,9,8,12,1,11,16,3,7,15,13,
                        5,9,7,9,0,0,11,10,5,4,9,8,1,2,14,15,15,12,13,10,2,3,6,16,
                        10,11,16,9,8,16,1,5,0,0,0,14,15,
                        10,11,16,1,0,0,16,9,8,15,5,0,14,15,
                        12,13,16,1,16,5,6,15,7,7,14,15,
                        12,13,16,2,3,0,15,4,5,14,15,
                        1,0,0,2,3,0,3,4,0,4,5,0,5,0,9,9,1,1,0,9,0,2,0,0,5,0,0,1,0,0,0,
        15,0,0,15,9,9,1,0,0,15,15,5,0,1,5,0,15,1,3,0,15,14,15,
                        8,9,10,1,5,13,12,15,7,4,0,14,16,11,2,6,3,
                        10,11,12,13,10,11,16,12,13,16,10,11,16,9,8,10,11,16,1,0,0,
        10,11,16,9,5,12,13,16,1,12,13,16,2,12,13,16,
                        13,5,7,1,11,14,8,0,9,2,12,10,16,4,15,3,6,
                        15,7,6,1,14,0,2,10,12,13,5,8,4,11,16,3,
                        9,12,3,15,5,1,14,16,10,2,11,8,13,4,0,6,7,
                        0,1,7,14,3,9,
                        9,4,12,2,6,
                        10,16,15,5,11,13,
                        9,8,10,
                        7,14,15,16,
                        10,11,12,13,14,15,0,15,0,0,15,5,0,0,15,9,9,10,11,16,12,13,16,16,
        1,0,1,0,0,9,8,9,5,9,9,1,2,0,10,11,16,9,8,10,11,16,1,0,0,10,11,16,9,5,12,13,16,1,
        12,13,16,2,
                        6,14,13,1,0,2,10,12,15,4,7,3,11,16,8,9,5,
                        11,9,1,6,4,13,14,16,2,0,3,5,10,12,15,7,8,
                        5,13,1,14,15,10,9,4,2,8,6,11,7,0,12,16,
                        10,7,0,1,11,16,3,9,8,12,14,15,4,6,5,2,13,7,11,1,0,9,5,12,14,15,16,
        3,6,15,2,4,8,7,10,13,4,13,9,1,14,5,0,16,1,15,2,3,12,7,8,10,11}; // очередь обучения
        int epo = 1; // счетчик итераций
        double[] err = new double [17]; // вычисление ошибки
        //BufferedImage image;
        String lineSeparator = System.getProperty("line.separator");

        //image = ImageIO.read(Imagination.class.getResource("Base/"+im+".png"));

        do {
            // символы:       0    1    2    3    4    5    6    7    8    9    А    И    Д    Т    р    .    -
            // индекс:        0    1    2    3    4    5    6    7    8    9    10   11   12   13   14   15   16
            double[] goal = {0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1}; // целевой вектор

            preporation(image); // вычисление предварительных результатов нейросети

            // анализ выходного слоя обучателем
            int g = queue[im]; // задаем приоритет
            double te = 0;
            for(int i = 0; i < goal.length; i++){
                if(i == g){
                    goal[i] = 1; // задаем приоритет
                }
                err[i] = (goal[i] - result[i]) * 1; // вычисление ошибки

                for(int j = 0; j < 20; j++){
                    te += weightMatrix3[i][j]*h2[j]; // сумма весов на входные значения для вычисления производной
                }
                if(te < 0){ // производная для Relu
                    err[i] = err[i] * 0.1;
                    //err[i] = 1 / (Math.cosh(te)*Math.cosh(te));
                    err[i] = Math.abs(err[i]); // модуль
                }
                te = 0;
                //System.out.println(err[i] + " - " + newZnac[i] + " - " + goal[i]);
            }

            int ne = 1;
            for(int x = 0; x < 17; x++) { // перезапись файлов весов выходного слоя
                FileWriter fv = new FileWriter("C:/Users/user/Desktop/Diplom-master/Diplom-master/src/Data/w3."+ne+" 20.txt"); // запись текста в файл
                for (int j = 0; j < 20; j++) { // запись данных в файлы_весов
                    double r = 0.01 * err[x] * h2[j];
                    r = weightMatrix3[x][j] + r;
                    String format = new DecimalFormat("#.#####").format(r).replaceAll(",", ".");
                    r = Double.parseDouble(format); // задаем кол-во знаков после запятой
                    //System.out.println("r="+r+" rand="+rand);
                    fv.write(r + lineSeparator);
                }
                fv.close();
                ne++;
            }

            // анализ второг скрытого слоя
            double[] err2 = new double[20];
            for(int x = 0; x < 20; x++) {
                for (int j = 0; j < 17; j++) {
                    err2[x] += err[j] * weightMatrix3[j][x];
                }
                te = 0;
                for(int i = 0; i < 20; i++){
                    te += weightMatrix2[x][i]*h1[i];
                }
                if(te < 0){
                    err2[x] = err2[x] * 0.1;
                    //err2[x] = 1 / (Math.cosh(te)*Math.cosh(te));
                    err2[x] = Math.abs(err2[x]);
                }
            }
            ne = 1;
            for(int x = 0; x < 20; x++) { // перезапись файлов весов второго скрытого слоя
                FileWriter fv = new FileWriter("C:/Users/user/Desktop/Diplom-master/Diplom-master/src/Data/w2."+ne+" 20.txt"); // запись текста в файл
                for (int j = 0; j < 20; j++) { // запись данных в файлы_весов
                    double r = 0.1 * err2[x] * h1[j];
                    r = weightMatrix2[x][j] + r;
                    String format = new DecimalFormat("#.#####").format(r).replaceAll(",", ".");
                    r = Double.parseDouble(format);
                    //System.out.println("r="+r+" rand="+rand);
                    fv.write(r + lineSeparator);
                }
                fv.close();
                ne++;
            }

            // анализ первого скрытого слоя
            double[] err3 = new double[20];
            for(int x = 0; x < 20; x++) {
                for (int j = 0; j < 20; j++) {
                    err3[x] += err2[x] * weightMatrix2[x][j];
                }
                te = 0;
                for(int i = 0; i < 1024; i++){
                    te += weightMatrix1[x][i]*in[i];
                }
                if(te < 0){
                    err3[x] = err3[x] * 0.1;
                    //err3[x] = 1 / (Math.cosh(te)*Math.cosh(te));
                    err3[x] = Math.abs(err3[x]);
                }
            }
            ne = 1;
            for(int x = 0; x < 20; x++) { // перезапись файлов весов первого скрытого слоя
                FileWriter fv = new FileWriter("C:/Users/user/Desktop/Diplom-master/Diplom-master/src/Data/w1."+ne+" 1024.txt"); // запись текста в файл
                for (int j = 0; j < 1024; j++) { // запись данных в файлы_весов
                    double r = 0.1 * err3[x] * in[j];
                    r = weightMatrix1[x][j] + r;
                    String format = new DecimalFormat("#.#####").format(r).replaceAll(",", ".");
                    r = Double.parseDouble(format);
                    //System.out.println("r="+r+" rand="+rand);
                    fv.write(r + lineSeparator);
                }
                fv.close();
                ne++;
            }
            epo++;
        } while (epo < 1000); // сделать 1000 итераций
        x++;
        im++;
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
