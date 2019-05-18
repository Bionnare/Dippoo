import javax.imageio.ImageIO;
import java.applet.Applet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Finder extends Applet {

    NeuroNet neuronet = new NeuroNet();
    boolean isEmpty1 = true, isEmpty2 = true, isEmpty3 = true; // булеаны для пустого массива, в который сохраняются строки текста
    boolean whiteString = false; // нахождение конечной границы строки текста
    boolean itsSymbol = false;
    boolean analyzerString = false; // булеан для искателя строк
    boolean analyzerWord = false; // булеан для искателя слов
    boolean analyzerLetter = false; // булеан для искателя символов
    double pict[][]; // временный массив
    int n = 0, nn = 0, nnn = 0; // временные переменные для создания новых изображений / для определения размеров полученного массива на выходе из каждого искателя

    // ИСКАТЕЛЬ СТРОК
    public double[][] stringFind (double[][] pix, int height, int width) throws IOException { // искатель строк текста
        analyzerString = true; // включение искателя строк
        analyzerLetter = false;
        analyzerWord = false;
        pict = new double[height][width]; // идентифицируем размеры временного массива

        for (int i = 0; i < height; i++) {
            //System.out.println();
            for (int j = 0; j < width; j++) {
                //System.out.print(pix[i][j] + " ");
                if (pix[i][j] >= 0.5) { // порог черного пикселя / находим стартовую границу строки текста
                    whiteString = false;
                    zoner(pix, height, width, i); // запись строки пикселей
                    break;
                }
                else {
                    whiteString = true;
                }
            }
            if ((!isEmpty1 & whiteString) | (!isEmpty1 & i == height - 1)) { // находим конечную границу строки текста
                //System.out.println("\n\nЗаход строки!!!" + n + " из " + height);
                double newPict[][] = new double[n][width];
                for (int q = 0; q < n; q++) {
                    //System.out.println();
                    for (int w = 0; w < width; w++) {
                        newPict[q][w] = pict[q][w];
                        //System.out.print(newPict[q][w] + " ");
                    }
                }

                //crimg(newPict, n, width); // ВЫГРУЗКА СТРОК КАК ИЗОБРАЖЕНИЙ
                wordFind(newPict, n, width); // ПЕРЕДАЧА МАССИВА В ИСКАТЕЛЬ СЛОВ

                // ЗДЕСЬ ДОЛЖНА БЫТЬ ПЕРЕДАЧА ИНФОРМАЦИИ О НОВОЙ СТРОКЕ В СОСТАВИТЕЛЬ_ЭЛЕКТРОННОГО_ТЕКСТА

                // устанавливаем значения по умолчанию
                isEmpty1 = true;
                whiteString = false;
                itsSymbol = false;
                analyzerString = true;
                analyzerLetter = false;
                analyzerWord = false;
                n = 0;
                pict = new double[height][width];
            }
        }
        return pix;
    }

    // ИСКАТЕЛЬ СЛОВ
    public double[][] wordFind(double[][] pix, int height, int width) throws IOException { // искатель слов
        double[][] worders = new double[height][width];

        for (int i = 0; i < height; i++) { // размытие изображения (все соседние пиксели у черного пикселя устанавливаются как '1')
            for (int j = 0; j < width; j++) {
                if (pix[i][j] > 0.5) {
                    if (i != 0 & j != 0 & i != height - 1 & j != width - 1 & i != 1 & j != 1 & i != height - 2 & j != width - 2) {
                        worders[i][j] = 1;
                        worders[i + 1][j] = 1;
                        worders[i][j + 1] = 1;
                        worders[i - 1][j] = 1;
                        worders[i][j - 1] = 1;
                        worders[i + 1][j + 1] = 1;
                        worders[i - 1][j - 1] = 1;
                        worders[i + 1][j - 1] = 1;
                        worders[i - 1][j + 1] = 1;

                        worders[i][j + 2] = 1;
                        worders[i][j - 2] = 1;
                        worders[i + 2][j] = 1;
                        worders[i + 2][j + 1] = 1;
                        worders[i + 2][j + 2] = 1;
                        worders[i + 2][j - 1] = 1;
                        worders[i + 2][j - 2] = 1;
                        worders[i + 1][j + 2] = 1;
                        worders[i + 1][j - 2] = 1;
                        worders[i - 2][j] = 1;
                        worders[i - 2][j + 1] = 1;
                        worders[i - 2][j + 2] = 1;
                        worders[i - 2][j - 1] = 1;
                        worders[i - 2][j - 2] = 1;
                        worders[i - 1][j + 2] = 1;
                        worders[i - 1][j - 2] = 1;

                    }
                    // условия приграничных пикселей
                    if (i == 0) {
                        worders[i][j] = 1;
                        worders[i + 1][j] = 1;
                        if (j != 0) {
                            worders[i][j - 1] = 1;
                            worders[i + 1][j - 1] = 1;
                        }
                        if (j != width - 1) {
                            worders[i][j + 1] = 1;
                            worders[i + 1][j + 1] = 1;
                        }
                    }

                    if (i == height - 1) {
                        worders[i][j] = 1;
                        worders[i - 1][j] = 1;
                        if (j != 0) {
                            worders[i][j - 1] = 1;
                            worders[i - 1][j - 1] = 1;
                        }
                        if (j != width - 1) {
                            worders[i][j + 1] = 1;
                            worders[i - 1][j + 1] = 1;
                        }
                    }

                    if (j == 0) {
                        worders[i][j] = 1;
                        worders[i][j + 1] = 1;
                    }
                    if (j == width - 1) {
                        worders[i][j] = 1;
                        worders[i][j - 1] = 1;
                    }
                } else { // белые - в '0'
                    worders[i][j] = 0;
                }
            }
        }

        // находим средние значения яркости пикселей для каждого столбца в строке
        int temp = 0; // сумма пикселей в одном столбце
        double avg = 0; // среднее значение пикселей в столбце
        double[] arravg = new double[width]; // массив средних значений каждого столбца одной строки
        for (int q = 0; q < width; q++) {
            for (int w = 0; w < height; w++) {
                temp += worders[w][q];
            }
            avg = (double) temp / height;
            avg = Math.round(avg * 100) / 100.0;
            arravg[q] = avg;
            //System.out.println("Средняя яркость строк" + arravg[q]);
            temp = 0;
        }

        // находим среднее значение яркости пикселей для всей строки
        double average = 0; // средний порог значений пикселей для полной одной строки
        double t = 0;
        for (int e = 0; e < arravg.length; e++) {
            t += arravg[e];
        }
        average = t / arravg.length;
        average = Math.round(average * 100) / 100.0;
        //System.out.println("Средняя яркость строк" + average);

        // сравниваем все полученные ранее значения яркости для каждого столбца со средним порогом яркости для всей строки
        int[] zhe = new int[arravg.length];
        for (int a = 0; a < arravg.length; a++) {
            if (arravg[a] >= average * 0.1) {
                zhe[a] = 1;
                //System.out.println("гран=" + zhe[a] + " size=" + a);
            } else {
                zhe[a] = 0;
                //System.out.println("гран=" + zhe[a] + " size=" + a);
            }
        }

        // запись нового изображения
        analyzerString = false; // включение искателя слов
        analyzerWord = true;
        analyzerLetter = false;
        pict = new double[height][width]; // идентифицируем размеры временного массива
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (zhe[i] == 1) { // исходя из полученных данных предыдущих вычислений записываем пиксели в новый массив
                    whiteString = false;
                    zoner(pix, height, width, i);
                    break;
                } else {
                    whiteString = true;
                }
            }
            if ((!isEmpty3 & whiteString) | (!isEmpty3 & i == width - 1)) { // находим конечную границу слова
                //System.out.println("\n\nЗаход строки!!!" + nnn + " из " + width);
                double newPict[][] = new double[height][nnn];
                for (int q = 0; q < height; q++) {
                    //System.out.println();
                    for (int w = 0; w < nnn; w++) {
                        newPict[q][w] = pict[q][w];
                        //System.out.print(newPict[q][w] + " ");
                    }
                }
                //crimg(newPict, height, nnn); // ВЫГРУЗКА СЛОВ КАК ИЗОБРАЖЕНИЙ
                letterFind(newPict, height, nnn); // ПЕРЕДАЧА МАССИВА В ИСКАТЕЛЬ СИМВОЛОВ

                // ЗДЕСЬ ДОЛЖНА БЫТЬ ПЕРЕДАЧА ИНФОРМАЦИИ О НОВОМ СЛОВЕ В СОСТАВИТЕЛЬ_ЭЛЕКТРОННОГО_ТЕКСТА

                // устанавливаем значения по умолчанию
                isEmpty3 = true;
                whiteString = false;
                itsSymbol = false;
                analyzerString = false;
                analyzerWord = true;
                analyzerLetter = false;
                nnn = 0;
                pict = new double[height][width];
            }
        }

        return pix;
    }

    // ИСКАТЕЛЬ СИМВОЛОВ
    public double[][] letterFind(double[][] pix, int height, int width) throws IOException {
        double tp = 0.3 * height; // примерная ширина символа
        int interval = (int) Math.round(tp);

        // находим средние значения яркости пикселей для каждого столбца в слове
        int temp = 0; // сумма пикселей в одном столбце
        double avg = 0; // среднее значение пикселей в столбце
        double[] arravg = new double[width]; // массив средних значений каждого столбца одного слова
        for (int q = 0; q < width; q++) {
            for (int w = 0; w < height; w++) {
                temp += pix[w][q];
            }
            avg = (double) temp / height;
            avg = Math.round(avg * 100) / 100.0;
            arravg[q] = avg;
            //System.out.println("Средняя яркость строк=" + arravg[q]);
            temp = 0;
        }

        // находим среднее значение яркости пикселей для всего слова
        double average = 0; // средний порог значений пикселей для полного одного слова
        double t = 0;
        for (int e = 0; e < arravg.length; e++) {
            t += arravg[e];
        }
        average = t / arravg.length;
        average = Math.round(average * 100) / 100.0;
        average = average * 0.1;
        //System.out.println("Средняя яркость " + average);

        //System.out.println("Интервал=" + interval);
        List<Integer> gran = new ArrayList<>(); // список межсимвольных границ
        double min = arravg[0]; // переменная минимума для нахождения межсимвольных границ
        int index = 0; // индекс найденных минимумов
        int s = 0;
        // находим минимальное среднее значение столбца на отрезке (0, interval)
        // далее находим на отрезке от (min+1, interval+1)
        // и так далее до конечной границы слова
        // таким образом получаем группу предварительных межсимвольных границ
        while (interval + s < arravg.length) {
            //System.out.println("s=" + s + " length=" + (interval+s));
            for (int q = s; q < interval + s; q++) {
                //System.out.println("arravg=" + arravg[q]);
                if (arravg[q] < min) {
                    min = arravg[q];
                    index = q;
                    //System.out.println("Минимум=" + min);

                }
            }
            gran.add(index);
            s = index + 1;
            index = s;
            min = arravg[s];
        }
        //System.out.println("Минимум=" + min);
        for (int q = s; q < arravg.length; q++) { // этот цикл нужен для прохода последнего отрезка
            //System.out.println("arravg=" + arravg[q]);
            if (arravg[q] < min) {
                min = arravg[q];
                index = q;
                //System.out.println("Минимум=" + min);

            }
        }
        gran.add(index);
        //System.out.println("List1=" + gran);

        // первый шаг удаления лишних границ
        for (int q = 2; q < gran.size() - 2; q++) {
            int mn = gran.get(q);
            if ((arravg[mn] < average) & ((arravg[mn - 2] > average) | (arravg[mn + 2] > average))) {
                Iterator<Integer> iter = gran.iterator();
                while (iter.hasNext()) {
                    Integer next = iter.next();
                    if (next.equals(q)) {
                        iter.remove();
                    }
                }
            }
        }
        //System.out.println("List2=" + gran);

        // делим изображение слова на три уровня
        double t1 = 0.3 * height; // верхний - 30% от высоты
        double t2 = 0.4 * height; // средний - 40%
        int wu = (int) Math.round(t1);
        int wm = (int) Math.round(t2);
        int wl = height - wu - wm; // нижний - 30%
        boolean bool1 = false, bool2 = false, bool3 = false; // булеаны для каждого условия удаления последних лишних границ
        // максимальные среди средних значений яркости для каждого уровня (текущий столбец, следующий, предыдущий)
        double max1 = 0, max1n = 0, max1a = 0;
        double max2 = 0, max2n = 0, max2a = 0;
        double max3 = 0, max3n = 0, max3a = 0;

        // следующий шаг удаления лишних границ
        for (int q = 1; q < width - 1; q++) {
            for (int w = 0; w < height; w++) { // находим максимальные значения для каждого столбца и соседних ему
                if (w <= wu) { // на каждом уровне
                    if (max1 < pix[w][q]) {
                        max1 = pix[w][q];

                    }
                    if (max1n < pix[w][q + 1]) {
                        max1n = pix[w][q + 1];

                    }
                    if (max1a < pix[w][q - 1]) {
                        max1a = pix[w][q - 1];

                    }
                    //System.out.println("w=" + w + " wu=" + wu + " max=" + max1 + " max1n=" + max1n + " max1a=" +max1a + " q=" + q);
                }
                if ((w > wu) & (w < wl)) {
                    if (max2 < pix[w][q]) {
                        max2 = pix[w][q];

                    }
                    if (max2n < pix[w][q + 1]) {
                        max2n = pix[w][q + 1];

                    }
                    if (max2a < pix[w][q - 1]) {
                        max2a = pix[w][q - 1];

                    }
                }
                if (w >= wu) {
                    if (max3 < pix[w][q]) {
                        max3 = pix[w][q];

                    }
                    if (max3n < pix[w][q + 1]) {
                        max3n = pix[w][q + 1];

                    }
                    if (max3a < pix[w][q - 1]) {
                        max3a = pix[w][q - 1];

                    }
                }

            }
            if ((max1 == max1n) | (max2 == max2n) | (max3 == max3n) | (max1 == max1a) | (max2 == max2a) | (max3 == max3a)) {
                bool1 = true; // если есть последовательные черные символы, то это ложная граница
            }
            // нахождения максимума для полных соседних столбцов
            if (max1n > max2n & max1n > max3n) {
                max1n = max1n;
            }
            if (max3n > max2n & max1n < max3n) {
                max1n = max3n;
            }
            if (max1n < max2n & max2n > max3n) {
                max1n = max2n;
            }
            if (max1a > max2a & max1a > max3a) {
                max1a = max1a;
            }
            if (max3a > max2a & max1a < max3a) {
                max1a = max3a;
            }
            if (max1a < max2a & max2a > max3a) {
                max1a = max2a;
            }
            // если среднее значение текущего столбца меньше максимального соседних, то это ложная граница
            if ((arravg[q] < max1n) & (arravg[q] < max1a)) {
                bool2 = true;
            }
            // нахождение максимума для текущего полного столбца
            if (max1 > max2 & max1 > max3) {
                max1 = max1;
            }
            if (max3 > max2 & max1 < max3) {
                max1 = max3;
            }
            if (max1 < max2 & max2 > max3) {
                max1 = max2;
            }
            // третье условие для ложных границ
            double m = 2 * Math.abs(max1 - max1n);
            double ma = 2 * Math.abs(max1 - max1a);
            if ((max1 > m) & (max1 > ma)) {
                bool3 = true;
            }

            // при срабатывании всех трех условий, граница удаляется
            if (bool1 & bool2 & bool3) {
                Iterator<Integer> iter = gran.iterator();
                while (iter.hasNext()) {
                    Integer next = iter.next();
                    if (next.equals(q)) {
                        iter.remove();
                    }
                }
            }
            // обнуляем переменные
            bool1 = false;
            bool2 = false;
            bool3 = false;
            max1 = 0;
            max1n = 0;
            max1a = 0;
            max2 = 0;
            max2n = 0;
            max2a = 0;
            max3 = 0;
            max3n = 0;
            max3a = 0;
        }
        //System.out.println("List3=" + gran);

        // записываем в массив финальную группу межсимвольных границ
        double porog = 0.4 * height;
        int count = 0;
        int[] zhe = new int[arravg.length];
        for (int a = 0; a < arravg.length; a++) {
            if (gran.contains(a) & (count > porog)) { // границы не должны быть слишком близко
                zhe[a] = 0;
                count = 0;
                //System.out.println("гран=" + zhe[a] + " size=" + a);
            } else {
                zhe[a] = 1;
                count++;
                //System.out.println("гран=" + zhe[a] + " size=" + a);
            }
        }

        // запись нового изображения
        analyzerString = false; // включение искателя символов
        analyzerWord = false;
        analyzerLetter = true;
        pict = new double[height][width]; // идентифицируем размеры временного массива
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (zhe[i] == 1) { // исходя из полученных данных предыдущих вычислений записываем пиксели в новый массив
                    whiteString = false;
                    zoner(pix, height, width, i);
                    break;
                } else {
                    whiteString = true;
                }
            }
            if ((!isEmpty2 & whiteString) | (!isEmpty2 & i == width - 1)) { // записываем конечную границу символа
                //System.out.println("\n\nЗаход строки!!!" + nn + " из " + width);
                double newPict[][] = new double[height][nn];
                for (int q = 0; q < height; q++) {
                    //System.out.println();
                    for (int w = 0; w < nn; w++) {
                        newPict[q][w] = pict[q][w];
                        //System.out.print(newPict[q][w] + " ");
                    }
                }
                if (nn > 3) { // используем массивы с шириной больше '3'
                    itsSymbol = true;
                    crimg(newPict, height, nn); // ВЫГРУЗКА СИМВОЛОВ КАК ИЗОБРАЖЕНИЙ
                }

                // устанавливаем значения по умолчанию
                isEmpty2 = true;
                whiteString = false;
                itsSymbol = false;
                analyzerString = false;
                analyzerWord = false;
                analyzerLetter = true;
                nn = 0;
                pict = new double[height][width];
            }
        }
        return pix;
    }


    // производит запись строк пикселей во временый массив pict[][] для каждого искателя
    public void zoner(double[][] pix, int height, int width, int i) {
        //System.out.println("\nЗаписываемые строки n="+n);
        if (analyzerString) { // для искателя строк
            for (int j = 0; j < width; j++) {
                pict[n][j] = pix[i][j];
                //System.out.print(pix[i][j] + " ");
            }
            isEmpty1 = false;
            n++;
        }
        //System.out.println("\nЗаписываемые строки n="+nnn);
        if (analyzerWord) { // для искателя слов
            for (int j = 0; j < height; j++) {
                pict[j][nnn] = pix[j][i];
                //System.out.print(pix[j][i] + " ");
            }
            nnn++;
            isEmpty3 = false;
        }
        //System.out.println("\nЗаписываемые строки n="+nn);
        if (analyzerLetter) { // для искателя символов
            for (int j = 0; j < height; j++) {
                pict[j][nn] = pix[j][i];
                //System.out.print(pix[j][i] + " ");
            }
            nn++;
            isEmpty2 = false;
        }
    }

    int sc = 1; // счетчик файлов
    int xx = 1; // переменная для принта

    // преобразование полученных массивов из каждого искателя в изображение
    public void crimg(double[][] pixels, int n, int w) throws IOException {
        //System.out.println("\n\nВысота при создании изображения = "+ n + " Ширина = " + w + " счетчик=" + xx++);
        BufferedImage img = new BufferedImage(w, n, BufferedImage.TYPE_INT_RGB);
        String string = "C:/Users/user/Desktop/Diplom-master/Diplom-master/src/Save/img" + sc + ".png";
        File f = new File(string);

        int im[] = new int[n * w]; // массив со значениями пикселей, на основе которого будет создаваться изображение

        int i = 0;

        for (int y = 0; y < n; y++) { // заполнение массива
            for (int x = 0; x < w; x++) {
                double temp = (1 - pixels[y][x]) * 100 * 255;
                int zap = (int) temp;
                int r = zap & 0xff;
                int g = zap & 0xff;
                int b = zap & 0xff;

                im[i] = (r << 16) | (g << 8) | b; // кодировка пикселя в тип RGB
                img.setRGB(x, y, im[i]);
                i++;
            }
        }
        if (analyzerLetter){ // если изображение - символ, то передается в нейросеть
            //System.out.println("cycle="+sc);
            //neuronet.preporation(img); // ПЕРЕДАЧА МАССИВА ПИКСЕЛЕЙ В НЕЙРОСЕТЬ
            neuronet.trainer(img); // ПЕРЕДАЧА МАССИВА ПИКСЕЛЕЙ В ОБУЧАТЕЛЬ

        }
        //System.out.println(sc);
        ImageIO.write(img, "PNG", f); // сохранение изображения
        sc++;
    }
}