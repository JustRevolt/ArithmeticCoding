
import dnl.utils.text.table.TextTable;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;


//E:\University\3course\AppliedMath\lab2\program\src\War_and_Peace
public class Main {

    public static void main(String[] args) throws IOException {
        char[] text = connectToFile();

        TextAnalyzer textAnalyzer = new TextAnalyzer(text);
        textAnalyzer.analyse();

        ArithmeticMethods arithmetic = new ArithmeticMethods(textAnalyzer.getProbabilityOfSymbols(), text, text.length, true);
        String textCode = arithmetic.codingByDouble();

//        ArithmeticMethods arithmetic = new ArithmeticMethods(textAnalyzer.getNumberOfSymbols(), text, text.length);
//        String textCode = arithmetic.codingByFraction();

//ToDo реализовать декодирование
        printResults(arithmetic, textCode);
    }

    private static char[] connectToFile() {
        String fileName = "error";
        byte[] buffer = new byte[0];
        char[] text;
        boolean connection = false;
        Scanner in = new Scanner(System.in);
        //String fileName = "error";

        while (!connection) {
            System.out.println("Enter file name for analysis:");
            try {
                fileName = in.nextLine();
                FileInputStream fis = new FileInputStream(fileName);
                buffer = new byte[fis.available()];
                fis.read(buffer, 0, fis.available());
                connection = true;
                fis.close();
            } catch (FileNotFoundException e) {
                System.out.println("!!!File with name \"" + fileName + "\" not found!!!");
                System.out.println("!!!Try again!!!");
                connection = false;
            } catch (IOException e) {
                System.out.println("!!!Error connecting to file with name \"" + fileName + "\"!!!");
                System.out.println("!!!Try again!!!");
                connection = false;
            } catch (NoSuchElementException e) {
                System.out.println("-----Closing program-----");
                System.exit(0);
            }
        }

        text = new char[buffer.length];

        for (int i = 0; i < buffer.length; i++) {
            text[i] = (char) buffer[i];
        }
        System.out.println(fileName);
        return text;
    }

    private static void printResults(ArithmeticMethods arithmetic, String textCode) {
        System.out.println("\n---Зависимость коэффициента сжатия от длины блока---");

        String[] header1 = {"Block length", "Compression Ratio"};
        String[][] data1 = new String[arithmetic.getTextLength()][2];

        for (int i = 0; i < arithmetic.getTextLength(); i++) {
            data1[i][0] = String.valueOf(arithmetic.getCompressionRatio()[i][0]);
            data1[i][1] = String.valueOf(arithmetic.getCompressionRatio()[i][1]);
        }
        TextTable tt1 = new TextTable(header1, data1);
//ToDo не работает вывод таблицы
        tt1.printTable();

        System.out.println("\n---Закодированный текст---");

        String[] header2 = {"Number system", "Code"};
        String[][] data2 = new String[2][2];
        data2[0] = new String[]{"Decimal", String.valueOf(arithmetic.BinaryToDecimal(textCode))};
        data2[1] = new String[]{"Binary", textCode};

        TextTable tt2 = new TextTable(header2, data2);
        tt2.printTable();

        System.out.println("\n---Коэффициент сжатия---");
        System.out.println(arithmetic.getCompressionRatio()[arithmetic.getTextLength() - 1]);

    }

}