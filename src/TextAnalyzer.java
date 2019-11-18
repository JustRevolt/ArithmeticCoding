import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Stream;

class TextAnalyzer {
    private char[] text;
    private int textLength;
    private Map<Character, Integer> numberOfSymbols = new LinkedHashMap<Character, Integer>();
    private Map<Character, Double> probabilityOfSymbols = new LinkedHashMap<Character, Double>();
    private Map<Character, Double> entropyOfSymbols = new LinkedHashMap<Character, Double>();
    private double textEntropy = 0;

    private Map<String, Double> numberOfPairs = new LinkedHashMap<String, Double>();
    private Map<String, Double> probabilityOfPairs = new LinkedHashMap<String, Double>();
    private double textEntropyWithPairs = 0;

    TextAnalyzer(char[] text){
        this.text = text;
        textLength = text.length;
    }

    int getTextLength() {
        return textLength;
    }

    Map<Character, Integer> getNumberOfSymbols() {
        numberOfSymbols = sortByValue(numberOfSymbols);
        return numberOfSymbols;
    }

    Map<Character, Double> getProbabilityOfSymbols() {
        probabilityOfSymbols = sortByValue(probabilityOfSymbols);
        return probabilityOfSymbols;
    }

    Map<Character, Double> getEntropyOfSymbols() {
        Set<Character> value = entropyOfSymbols.keySet();
        for (Character i : value) {
            entropyOfSymbols.replace(i, new BigDecimal(entropyOfSymbols.get(i)).setScale(4, RoundingMode.UP).doubleValue());
        }
        entropyOfSymbols = sortByValue(entropyOfSymbols);
        return entropyOfSymbols;
    }

    double getTextEntropy() {
        textEntropy = new BigDecimal(textEntropy).setScale(4, RoundingMode.UP).doubleValue();
        return textEntropy;
    }

    double getTextEntropyWithPairs() {
        textEntropyWithPairs = new BigDecimal(textEntropyWithPairs).setScale(4, RoundingMode.UP).doubleValue();
        return textEntropyWithPairs;
    }

    void analyse(){
        symbolAnalyse();
        symbolsEntropyCalculation();
        textEntropyCalculation();
        pairsAnalyse();
        entropyWithPairCalculation();
    }

    private void symbolAnalyse(){
        for (char symbol:text) {
            if (numberOfSymbols.containsKey(symbol)) {
                numberOfSymbols.replace(symbol, numberOfSymbols.get(symbol) + 1);
            } else {
                numberOfSymbols.put(symbol, 1);
            }
        }
        Set<Character> keys = numberOfSymbols.keySet();
        for (Character symbol: keys){
            probabilityOfSymbols.put(symbol, (double) numberOfSymbols.get(symbol) / (double) textLength);
        }
    }

    private void symbolsEntropyCalculation(){
        Set<Character> value = probabilityOfSymbols.keySet();
        for (Character i : value) {
            entropyOfSymbols.put(i, Math.log((double) 1 / probabilityOfSymbols.get(i)));
        }
    }

    private void textEntropyCalculation(){
        Set<Character> value = probabilityOfSymbols.keySet();
        for (Character i : value) {
            if (entropyOfSymbols.get(i) != Double.POSITIVE_INFINITY)
                textEntropy += probabilityOfSymbols.get(i) * entropyOfSymbols.get(i);
        }
    }

    private void pairsAnalyse(){

        for (int n = 1; n<textLength; n++) {
            String pair = String.valueOf(text[n-1])+String.valueOf(text[n]);
            if (numberOfPairs.containsKey(pair)) {
                numberOfPairs.replace(pair, numberOfPairs.get(pair) + 1);
                probabilityOfPairs.replace(pair, probabilityOfPairs.get(pair) + (double) 1/textLength);
            } else {
                numberOfPairs.put(pair, 1.0);
                probabilityOfPairs.put(pair, (double) 1/textLength);
            }
        }
    }

    private void entropyWithPairCalculation() {
        Set<String> value = probabilityOfPairs.keySet();
        for (String i : value) {
            textEntropyWithPairs -= probabilityOfPairs.get(i) * probabilityOfSymbols.get(i.charAt(0)) * Math.log(probabilityOfPairs.get(i));
        }

    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K, V>> st = map.entrySet().stream();

        st.sorted(Comparator.comparing(e -> e.getValue()))
                .forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }
}
