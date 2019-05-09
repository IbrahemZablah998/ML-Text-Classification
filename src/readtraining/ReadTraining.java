package readtraining;

import java.io.*;
import java.util.*;

public class ReadTraining {
    
    public static int numberOfClasses = 1;
    public static int numberOfDocuments = 1;
    public static int n = 0;
    public static String currentDirectory;
    public static Stemmer stemmer = new Stemmer();
    public static HashMap<String, Integer> wordGivenFreq = new HashMap<>();
    public static ArrayList<String> wordStemmer = new ArrayList<String>();
    public static ArrayList<String> stopWord = new ArrayList<String>();
    public static HashMap<String, Integer> biGramGivenFreq = new HashMap<>();
    public static HashMap<String, Double> biGramGivenProbilities = new HashMap<>();
    public static ArrayList<String> wordStemmerTest = new ArrayList<String>();
    public static ArrayList<String> biGramTest = new ArrayList<String>();
    public static HashMap<String, Double> wordGivenClass = new HashMap<>();
    public static HashMap<String, Double> classGivenWord = new HashMap<>();
    public static ArrayList<String> wordStemmerTestClass = new ArrayList<>();
    public static HashMap<String, Double> resultWordGivenClass = new HashMap<>();
    public static HashMap<String, Double> Prior = new HashMap<>();
    public static HashMap<String, Integer> classGivenOfDocument = new HashMap<>();
    public static String[][] matrix;
    public static HashMap<String, Integer> IndexArray = new HashMap<>();
    
    public static void stopWord() throws Exception {

        File stopWords = new File(currentDirectory + "/StopWord.txt");
        BufferedReader inFile = new BufferedReader(new FileReader(stopWords));

        Scanner scanner = new Scanner(inFile);
        while (scanner.hasNextLine()) {
            stopWord.add(scanner.nextLine().toUpperCase());
        }
        scanner.close();
    }
    public static void readFile(File file) throws Exception {
        File root = file;
        File[] list = root.listFiles();
        if (list == null) {
            return;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                n++;
                readFile(f.getAbsoluteFile());
                
            } else {
                Analysis(f, root.getName());
            }
           
        }
         numberOfClasses++;
    }
    public static void Analysis(File f, String root) throws Exception {
        numberOfDocuments++;
        File file = f.getAbsoluteFile();
        StringBuilder r = readFiles(file);
        StringTokenizer st = new StringTokenizer(r.toString(), " 0123456789\t\t\t\r\f,\"/.:)(;?!+*@#$%^&=[]'><-)(_");
       
        if (classGivenOfDocument.containsKey(root)) {
            int c = classGivenOfDocument.get(root);
            c += 1;
            classGivenOfDocument.replace(root, c);
                if (classGivenWord.containsKey(root)) {
                    Double backNumberClass = classGivenWord.get(root);
                    backNumberClass += st.countTokens();
                    classGivenWord.replace(root, backNumberClass);
                }
        } else {
            classGivenOfDocument.put(root, 1);
            classGivenWord.put(root, (double) st.countTokens());
        }
        while (st.hasMoreTokens()) {
            String tokn = st.nextToken().trim().toUpperCase();
            boolean isWhitespace = tokn.matches("^\\s*$");
            if (tokn.length() < 3 || stopWord.contains(tokn) || tokn.isEmpty()) {
                continue;
            } else if (isWhitespace == false) {
                String stem = stemmer.stemWord(tokn);
                wordStemmer.add(stem);
                
                
                if (wordGivenFreq.containsKey(stem)) {
                    Integer backNumber = wordGivenFreq.get(stem);
                    backNumber += 1;
                    wordGivenFreq.replace(stem, backNumber);
                } else {
                    wordGivenFreq.put(stem, 1);
                }
            }
        }
    }
    public static void wordGivenAllClass () {
        for (String st : classGivenWord.keySet()) {
            for (String str : wordStemmer) {
                String ConvertToTwoWord = str + " " + st;
                if (wordGivenClass.containsKey(ConvertToTwoWord)) {
                    Double backNumber = wordGivenClass.get(ConvertToTwoWord);
                    backNumber += 1;
                    wordGivenClass.replace(ConvertToTwoWord, backNumber);
                } else {
                    wordGivenClass.put(ConvertToTwoWord, 1.0);
                }
            }
        }
    }
    public static StringBuilder readFiles(File file) throws Exception {
        BufferedReader inFile;
        StringBuilder stringBuilder = new StringBuilder();

        inFile = new BufferedReader(new FileReader(file));
        Scanner scanner = new Scanner(inFile);

        while (scanner.hasNext()) {
            String line = scanner.next().toUpperCase();
            stringBuilder.append(line);
            stringBuilder.append(" ");
        }
        scanner.close();
        return stringBuilder;
    }
   
    public static void mergeBiGram() {
        for (int i = 0; i < wordStemmer.size() - 1; i++) {
            String text = wordStemmer.get(i) + " " + wordStemmer.get(i + 1);
            if (biGramGivenFreq.containsKey(text)) {
                Integer numBack = biGramGivenFreq.get(text);
                numBack += 1;
                biGramGivenFreq.put(text, numBack);
            } else {
                biGramGivenFreq.put(text, 1);
            }
        }
    }
    public static void resultBiGramGivenProbilities() throws Exception {
        File file = new File(System.getProperty("user.dir") + "/result.txt");
        PrintWriter printWriter = new PrintWriter(file);
        for (String name : biGramGivenFreq.keySet()) {
            String names[] = name.split(" ");
            String value = biGramGivenFreq.get(name).toString();
            if (wordGivenFreq.containsKey(names[0])) {
                biGramGivenProbilities.put(name, (Double.parseDouble(value) + 1) / (wordGivenFreq.get(names[0]) + wordGivenFreq.size()));
                printWriter.println(name + " " + (Double.parseDouble(value) + 1) / (wordGivenFreq.get(names[0]) + wordGivenFreq.size()));
            }
        }
        printWriter.close();
    }
    // test Data 
    public static void AnalysisTest(File f) throws Exception {
        File file = f.getAbsoluteFile();
        StringBuilder r = readFilesTest(file);
        StringTokenizer st = new StringTokenizer(r.toString(), " 0123456789\t\t\t\r\f,\"/.:)(;?!+*@#$%^&=[]'><-)(_");
        while (st.hasMoreTokens()) {
            String tokn = st.nextToken().trim().toUpperCase();
            boolean isWhitespace = tokn.matches("^\\s*$");
            if (isWhitespace || tokn.length() < 3 || stopWord.contains(tokn) || tokn.isEmpty()) {
                continue;
            } else {
                String stem = stemmer.stemWord(tokn);
                wordStemmerTest.add(stem);
            }
        }
    }
    public static StringBuilder readFilesTest(File file) throws Exception {
        BufferedReader inFile;
        StringBuilder stringBuilder = new StringBuilder();
        inFile = new BufferedReader(new FileReader(file));
        Scanner scanner = new Scanner(inFile);
        while (scanner.hasNext()) {
            String line = scanner.next().toUpperCase();
            stringBuilder.append(line);
            stringBuilder.append(" ");
        }
        scanner.close();
        return stringBuilder;
    }
    public static void mergeBiGramTest() {
        for (int i = 0; i < wordStemmerTest.size() - 1; i++) {
            String text = wordStemmerTest.get(i) + " " + wordStemmerTest.get(i + 1);
            biGramTest.add(text);
        }
    }
    public static void finalProbilitiesBiGram() {
        double p = 1;
        for (String name : biGramTest) {
            if (biGramGivenProbilities.containsKey(name)) {
                p *= biGramGivenProbilities.get(name);
            } else {
                System.out.println("error detection => " + name);
                String r[] = name.split(" ");
                if (wordGivenFreq.containsKey(r[0])) {
                    p *= ((double) (1.0) / ((wordGivenFreq.get(r[0])) + wordGivenFreq.size()));
                } else {
                    p *= ((double) (1.0) / (1.0 + wordGivenFreq.size()));
                }
            }
        }
        String allDocuments = "";
        for (String name : wordStemmerTest) {
            allDocuments += name;
            allDocuments += " ";
        }
        System.out.println(allDocuments + "probilites is = " + p);
    }
    // Training Data Classes 
    public static void priors() {
        for (String name : classGivenOfDocument.keySet()) {
            String value = classGivenOfDocument.get(name).toString();
            double result = (Double.parseDouble(value) / numberOfClasses);
            Prior.put(name, result);
        }
    }
    public static void resultWordGivenClasses() {
        for (String str : wordGivenClass.keySet()) {
            String value = wordGivenClass.get(str).toString();
            String resultname[] = str.split(" ");
            if (classGivenWord.containsKey(resultname[1])) {
                double result
                        = ((Double.parseDouble(value) + 1)
                        / ((classGivenWord.get(resultname[1])) + wordGivenFreq.size()));
                resultWordGivenClass.put(str, result);
            }
        }
    }
    
    // Test Dataset Classes
    public static int countsToIndes = 1;
    public static void readFileTestClass(File f) throws Exception {
        File root = f;
        File[] list = root.listFiles();
        if (list == null) {
            return;
        }
        for (File ff : list) {
            if (ff.isDirectory()) {
                readFileTestClass(ff.getAbsoluteFile());
            } else {
                analysisTestClass(ff);
                String s  = resultsTestClass();
//                System.out.println(s);
                int index = IndexArray.get(s);
                //System.out.println(s + " " + index);
                String resultIndexFromMatrix = matrix [index][index];
                double r = Double.parseDouble(resultIndexFromMatrix);
                matrix [index][index] = String.valueOf(r+1);
                wordStemmerTestClass.clear();
            }
        }
        countsToIndes++;
    }
    public static void analysisTestClass(File f) throws Exception {
//        System.out.println(f.getName());
        File file = f.getAbsoluteFile();
        StringBuilder r = readFilesTestClass(f);
        StringTokenizer st = new StringTokenizer(r.toString(), " 0123456789\t\t\t\r\f,\"/.:)(;?!+*@#$%^&=[]'><-)(_");
        while (st.hasMoreTokens()) {
            String tokn = st.nextToken().trim().toUpperCase();
            boolean isWhitespace = tokn.matches("^\\s*$");
            if (isWhitespace || tokn.length() < 3 || stopWord.contains(tokn) || tokn.isEmpty()) {
                continue;
            } else {
                String stem = stemmer.stemWord(tokn);
                wordStemmerTestClass.add(stem);
                
            }
        }
    }
    public static StringBuilder readFilesTestClass(File file) throws Exception {
        BufferedReader inFile;
        StringBuilder stringBuilder = new StringBuilder();
        inFile = new BufferedReader(new FileReader(file));
        Scanner scanner = new Scanner(inFile);
        while (scanner.hasNext()) {
            String line = scanner.next().toUpperCase();
            stringBuilder.append(line);
            stringBuilder.append(" ");
        }
        scanner.close();
        return stringBuilder;
    }
    
    public static String resultsTestClass() {
        Double resultProbilites = 1.0;
        Double max = 0.0, g = 0.0;
        String path = "";
        for (String Class : classGivenOfDocument.keySet()) { // name of the class
//            System.out.println("Class is " + Class);
            Double GetPrior = Prior.get(Class);
            resultProbilites *= GetPrior;
            for (String SubClass : wordStemmerTestClass) { // Class|Document in Test
                String s = SubClass + " " + Class;
//                System.out.print(s+ " ");
                if (resultWordGivenClass.containsKey(s)) {
                    resultProbilites *= (resultWordGivenClass.get(s));
//                    System.out.println(resultWordGivenClass.get(s));
                }
            }
            
            if (g == 0) {
                max = resultProbilites;
                path = Class;
                g = 9.0;
            } else {
                
                if (resultProbilites >= max) {
                    max = resultProbilites;
                    path = Class;
                }
            }
            
        }
//        System.out.println(max + " " + path);
            resultProbilites = 1.0;
        return path;
    }
    /*
    readFileTestClass
    resultsTestClass
    main
    
    */
    public static void initialValuesMatrix () {
        matrix= new String [classGivenOfDocument.size()+1][classGivenOfDocument.size()+1];
        matrix [0][0] = "0";
        int j1 = 1;
        for (String Class : classGivenOfDocument.keySet()) {
                IndexArray.put(Class, j1);
                matrix[0][j1] = Class;
                matrix[j1][0] = Class;
                j1++;
        }
        for (int i = 1 ; i < classGivenOfDocument.size()+1; i++) {
            for (int j = 1 ; j < classGivenOfDocument.size()+1; j++) {
                matrix[i][j] = "0";
            }
        }
    }
    public static void main(String[] args) throws Exception {
        currentDirectory = System.getProperty("user.dir");
        stopWord();
        readFile(new File(currentDirectory + "/training"));
      mergeBiGram();
      resultBiGramGivenProbilities();
      AnalysisTest(new File (currentDirectory + "/test1.txt"));
      mergeBiGramTest();
      finalProbilitiesBiGram();
        wordGivenAllClass();
        priors();
        resultWordGivenClasses();
        initialValuesMatrix();
        readFileTestClass(new File (currentDirectory  + "/test"));
        double precision = 0;
        double recall = 0;
        double accuracy = 0;
        double f_measure = 0;
        double fAvg_measure;
        double avgAccuracy;
        double sumOfMatrix = 0;
         for (int i = 1 ; i < n+1; i++) {
            for (int j = 1 ; j < n+1; j++) {
                sumOfMatrix += Double.parseDouble(matrix[i][j]);
                precision += Double.parseDouble(matrix[i][j]);
                recall += Double.parseDouble(matrix[j][i]);
            }
            f_measure += (2*((Double.parseDouble(matrix[i][i]))/precision)*
                            ((Double.parseDouble(matrix[i][i]))/recall))/
                        (recall+precision);
            accuracy += Double.parseDouble(matrix[i][i]);
        }
        fAvg_measure = f_measure / n+1;
        avgAccuracy = accuracy / sumOfMatrix;
        System.out.println((avgAccuracy*100));
//        for (int i = 0 ; i < n+1; i++) {
//            for (int j = 0 ; j < n+1; j++) {
//                System.out.print(matrix [i][j] + " ");
//            }
//            System.out.println("");
//        }
        System.gc();
    }
}