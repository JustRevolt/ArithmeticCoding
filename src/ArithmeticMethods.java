import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ArithmeticMethods {
    private char[] text;
    private int textLength;
    private double[][] compressionRatio;

    public double[][] getCompressionRatio() {
        return compressionRatio;
    }

    public int getTextLength() {
        return textLength;
    }

    //---------------------------------------------------------------------------------------
// Работа с дробными числами(double)
    private Map<Character, Double> probabilityOfSymbols;
    private Map<Character, Double> cumulativeProbabilities =new LinkedHashMap<Character, Double>();
    private double[] lowBorder;
    private double[] heightBorder;
    private double[] range;

    ArithmeticMethods(Map<Character, Double> probabilityOfSymbols, char[] text, int textLength, boolean n) {
        this.probabilityOfSymbols = probabilityOfSymbols;

        this.text = text;
        this.textLength = textLength;
        this.compressionRatio = new double[textLength][2];

        lowBorder = new double[textLength+1];
        heightBorder = new double[textLength+1];
        range = new double[textLength+1];

        lowBorder[0] = 0;
        heightBorder[0] = 1;
        range[0] = 1;
    }

    String codingByDouble(){
        CalculateCumulativeProbabilities();

        double qLow;
        double qHeight;

        int i = 1;

        for (char symbol:text) {
            qLow = cumulativeProbabilities.get(symbol)-probabilityOfSymbols.get(symbol);
            qHeight = cumulativeProbabilities.get(symbol);
            lowBorder[i] = lowBorder[i-1] + range[i-1] * qLow;
            heightBorder[i] = lowBorder[i-1] + range[i-1] * qHeight;
            range[i] = heightBorder[i] - lowBorder[i];

            int codeLength = (int) Math.ceil( Math.abs( Math.log(range[i]) / Math.log(2) ) );

            compressionRatio[i-1][0] = (double)codeLength;
            compressionRatio[i-1][1] = (double)codeLength/(double)textLength;

            i+=1;
        }

        double decimalCode =  heightBorder[heightBorder.length-1];
        int codeLength = (int) Math.ceil( Math.abs( Math.log(range[range.length-1]) / Math.log(2) ) );
        return DecimalToBinary(decimalCode, codeLength);
    }

    private void CalculateCumulativeProbabilities(){
        double n = 0;
        Set<Character> keys = probabilityOfSymbols.keySet();
        for (Character symbol : keys) {
            n = n + probabilityOfSymbols.get(symbol);
            cumulativeProbabilities.put(symbol, n);
        }
    }

    private String DecimalToBinary(Object number, int binaryLength){
        StringBuilder binary = new StringBuilder();
        if (number.getClass() == Double.class){
            double decimal = (double) number;
            for (int i =0; i<binaryLength; i++){
                int symbol = (int) (decimal=decimal*2);
                binary.append(symbol);
                decimal = decimal%1;
            }

        }
        return String.valueOf(binary);
    }

    Object BinaryToDecimal(String number){
        Double decimal = 0.0;
        char[] binary = number.toCharArray();
        for (int i = 0; i<binary.length; i++){
            int symbol = Integer.parseInt(String.valueOf(binary[i]));
            decimal += symbol * Math.pow(2,-(i+1));
        }
        return decimal;
    }

//---------------------------------------------------------------------------------------
// Работа с дробями(x/y)
    private Map<Character, Integer> numberOfSymbols;
    private Map<Character, Integer> cumulativeNumbers =new LinkedHashMap<Character, Integer>();
//ToDo Может ли массив иметь длинну textLength
    private int[] lowBorderNumerator;
    private int[] heightBorderNumerator;
    private int[] rangeNumerator;
    private int[] denominator;

    public ArithmeticMethods(Map<Character, Integer> numberOfSymbols, char[] text, int textLength){
        this.textLength = textLength;
        this.text = text;
        this.numberOfSymbols = numberOfSymbols;
        this.compressionRatio = new double[textLength][2];

        lowBorderNumerator = new int[textLength+1];
        heightBorderNumerator = new int[textLength+1];
        rangeNumerator = new int[textLength+1];
        denominator = new int[textLength+1];

        lowBorderNumerator[0] = 0;
        heightBorderNumerator[0] = 1;
        rangeNumerator[0] = 1;
        denominator[0] = 1;
    }

    String codingByFraction(){
        CalculateCumulativeNumbers();
        int degree = 0;
        int i = 1;
        int qLow;
        int qHeight;

        String[] codes = new String[textLength+1];

        for (char symbol:text) {
        // Приведение значений к необходимой точности для дальнейших вычислений
            degree = (int) Math.ceil((Math.log(textLength) - Math.log(rangeNumerator[i-1]))/Math.log(2));
            lowBorderNumerator[i] = (int) (lowBorderNumerator[i-1] * Math.pow(2,degree));
            heightBorderNumerator[i] = (int) (heightBorderNumerator[i-1] * Math.pow(2,degree));
            rangeNumerator[i] = heightBorderNumerator[i] - lowBorderNumerator[i];
            denominator[i] = (int) (denominator[i-1] * Math.pow(2,degree));
        // Расчет новых границ
            qLow = cumulativeNumbers.get(symbol) - numberOfSymbols.get(symbol);
            qHeight = cumulativeNumbers.get(symbol);

            heightBorderNumerator[i] = lowBorderNumerator[i] + rangeNumerator[i] * qHeight / textLength;
            lowBorderNumerator[i] =  lowBorderNumerator[i] + rangeNumerator[i] * qLow / textLength;
            rangeNumerator[i] = heightBorderNumerator[i] - lowBorderNumerator[i];

        // Кодирование heightBorder
            int hNumerator = heightBorderNumerator[i];
            int hDenominator = denominator[i];

            int codeLength = (int) Math.ceil( Math.abs( (Math.log(rangeNumerator[i]) - Math.log(denominator[i])) / Math.log(2) ) );
            int hDegree = (int) (Math.log(hDenominator)/Math.log(2)) - codeLength;

            hNumerator = hNumerator / (int) Math.pow(2, hDegree);
            hDenominator = hDenominator / (int) Math.pow(2, hDegree);

            StringBuilder outputBuffer = new StringBuilder();
            boolean buf = false;
            while (hDenominator != 1){
                int x = hNumerator % 2;
                hNumerator = hNumerator/2;
                hDenominator = hDenominator/2;
                if(!buf){
                    buf = x == 1;

                }else {
                    outputBuffer.insert(0, x);
                    denominator[i] /= 2;


                    if ((heightBorderNumerator[i] % 2 == 1 ||  lowBorderNumerator[i] % 2 == 1 )&& x==1){
                        heightBorderNumerator[i] *= 2;
                        lowBorderNumerator[i] *= 2;
                        denominator[i] *= 2;
                        rangeNumerator[i] = heightBorderNumerator[i] - lowBorderNumerator[i];
                    }

                    heightBorderNumerator[i] /= x==1?2:1;
                    lowBorderNumerator[i] /= x==1?2:1;
                }
            }
            rangeNumerator[i] = heightBorderNumerator[i] - lowBorderNumerator[i];
            codes[i] = String.valueOf(outputBuffer);
            i++;
        }

        return "";
    }

    private void CalculateCumulativeNumbers(){
        int n=0;
        Set<Character> keys = numberOfSymbols.keySet();
        for (Character symbol : keys) {
            n = n + numberOfSymbols.get(symbol);
            cumulativeNumbers.put(symbol, n);
        }
    }



}
