import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class TestFile {

    // metóda, ktorá na základe funkcie a zadaného vstupu zistí očakávaný výstup
    public static char expectedOutput(String bfunkcia, String input){
        // uloženie všetkých premenných aj s ich hodnotou, buď 1-true alebo 0-false
        Map<Character, Integer> map = new HashMap<>();

        for(int i=0; i<input.length(); i++){
            map.put((char) ('A'+i), Integer.parseInt(String.valueOf(input.charAt(i))));
        }

        // postupné nahradenie všetkých premenných ich hodnotami
        for(int j=0; j<input.length(); j++){
            bfunkcia = bfunkcia.replace((char)('A'+j), input.charAt(j));
        }

        // nahradenie v prípade, že je pred znakom '!'
        for(int x=0; x<bfunkcia.length(); x++){
            if(bfunkcia.charAt(x) == '!' && (x+1)!=bfunkcia.length()){
                bfunkcia = bfunkcia.substring(0, x) + bfunkcia.substring(x + 1);
                if(bfunkcia.charAt(x) == '0'){
                    bfunkcia = bfunkcia.substring(0, x) + "1" + bfunkcia.substring(x+1);
                }else{
                    bfunkcia = bfunkcia.substring(0, x) + "0" + bfunkcia.substring(x + 1);
                }
            }
        }

        // rozdelenie výrazu na podvýrazy
        String[] vyrazy = bfunkcia.split("\\+");
        for(String vyraz : vyrazy){
            boolean only1 = true;
            // ak je aspoň jeden podvýraz tvorený samými 1, výstup je 1, inak 0
            for(int k=0; k<vyraz.length(); k++){
                if(vyraz.charAt(k) == '0'){
                    only1 = false;
                    break;
                }
            }
            if(only1){
                return '1';
            }
        }
        return '0';
    }

    // funkcia, ktorá vytvorí booleovskú funkciu, ktorá vyhovuje mojim požiadavkám
    public static String createDNF(int numVariables) {
        Random rand = new Random();
        int prechod = 0;
        StringBuilder stringBuilder = new StringBuilder();

        for(int y=0; y<numVariables; y++){
            stringBuilder.append((char)('A' + y));
        }

        String variables = stringBuilder.toString();

        StringBuilder sb = new StringBuilder();

        while(!variables.equals("")){
            prechod++;
            StringBuilder term = new StringBuilder();
            int i = 0;
            for (int j = 0; j < numVariables; j++) {
                if (rand.nextBoolean()) {
                    if(rand.nextBoolean()){
                        term.append((char) ('A' + j));
                        variables = variables.replace(String.valueOf(('A' + j)), "");
                    }else{
                        term.append('!');
                        term.append((char) ('A' + j));
                        variables = variables.replace(String.valueOf((char)('A' + j)), "");
                    }
                    i++;
                }
            }
            sb.append(term);
            if (i != 0) {
                sb.append('+');
            }
        }
        if(prechod > 10){
            sb.append(variables);
        }
        sb.replace(sb.length()-1, sb.length(), "");
        return sb.toString();
    }

    // funkcie, ktoré postupne generujú všetky možné vstupy pre daný počet premenných
    public static List<String> generateBinaryStrings(int numVariables) {
        List<String> result = new ArrayList<>();
        generateBinaryStringsHelper(numVariables, "", result);
        return result;
    }

    private static void generateBinaryStringsHelper(int numVariables, String current, List<String> result) {
        if (current.length() == numVariables) {
            result.add(current);
            return;
        }
        generateBinaryStringsHelper(numVariables, current + "0", result);
        generateBinaryStringsHelper(numVariables, current + "1", result);
    }

    // funkcia, ktorá vytvára poradie od 0-n-1
    public static String create_order(int n){
        String[] s = new String[n];
        for(int i=0; i<n; i++){
            s[i] = String.valueOf(i);
        }
        return String.join("-", s);
    }


    public static void main(String[] args){
        int max_variables = 18;
        int every_variable = 100;
        double average;
        double[] averages_normal = new double[max_variables];
        double[] averages_best = new double[max_variables];
        long[] times_normal = new long[max_variables];
        long[] times_best = new long[max_variables];
        long[] times_use_normal = new long[max_variables];
        long[] times_use_best = new long[max_variables];

        Instant start, finish;

        String[][] functions = new String[max_variables][every_variable];
        List<List<String>> inputs = new ArrayList<>();
        for(int x=0; x<max_variables; x++){
            inputs.add(new ArrayList<>());
        }
        String[] basicOrder = new String[max_variables];
        for(int i=1; i<max_variables+1; i++){
            for(int j=0; j<every_variable; j++){
                functions[i-1][j] = createDNF(i);
            }
            inputs.set(i-1, generateBinaryStrings(i));
            basicOrder[i-1] = create_order(i);
        }

        BDDHandle.initialize();

        for(int k=0; k<max_variables; k++) {
            for (int l = 0; l < every_variable; l++) {
                start = Instant.now();
                BDDHandle.BDD_create(functions[k][l], basicOrder[k]);
                finish = Instant.now();
                times_normal[k] += Duration.between(start, finish).toNanos();
            }
            times_normal[k] = times_normal[k]/every_variable;
        }

        for(int k=0; k<max_variables; k++){
            for(int l=0; l<every_variable; l++){
                System.out.println(functions[k][l]);
                start = Instant.now();
                BDD bdd = BDDHandle.BDD_create(functions[k][l], basicOrder[k]);
                finish = Instant.now();
                times_normal[k] += Duration.between(start, finish).toNanos();
                System.out.println("Počet uzlov: " + bdd.getNodes_count());
                System.out.println("Počet odstránených uzlov: " + bdd.getRemoved_nodes());
                average = (double)bdd.getRemoved_nodes()/(bdd.getRemoved_nodes()+bdd.getNodes_count());
                averages_normal[k] += average;
                System.out.println("Percentuálna miera zredukovania: " + average);

                start = Instant.now();
                for(int m=0; m<inputs.get(k).size(); m++){
                    if(BDDHandle.BDD_use(bdd, inputs.get(k).get(m)) != expectedOutput(functions[k][l], inputs.get(k).get(m))){
                        System.out.println(BDDHandle.BDD_use(bdd, inputs.get(k).get(m)) + " - " + expectedOutput(functions[k][l], inputs.get(k).get(m)) + " -> chyba");
                    }
                }
                finish = Instant.now();
                times_use_normal[k] += Duration.between(start, finish).toNanos();

                System.out.println("--------------------------------------------");
                start = Instant.now();
                bdd = BDDHandle.BDD_create_with_best_order(functions[k][l]);
                finish = Instant.now();
                times_best[k] += Duration.between(start, finish).toNanos();
                System.out.println("Počet uzlov: " + bdd.getNodes_count());
                System.out.println("Počet odstránených uzlov: " + bdd.getRemoved_nodes());
                average = (double)bdd.getRemoved_nodes()/(bdd.getRemoved_nodes()+bdd.getNodes_count());
                averages_best[k] += average;
                System.out.println("Percentuálna miera zredukovania: " + average);

                start = Instant.now();
                for(int m=0; m<inputs.get(k).size(); m++){
                    if(BDDHandle.BDD_use(bdd, inputs.get(k).get(m)) != expectedOutput(functions[k][l], inputs.get(k).get(m))){
                        System.out.println(BDDHandle.BDD_use(bdd, inputs.get(k).get(m)) + " - " + expectedOutput(functions[k][l], inputs.get(k).get(m)) + " -> chyba");
                    }
                }
                finish = Instant.now();
                times_use_best[k] += Duration.between(start, finish).toNanos();
                System.out.println("#############################################################");
            }
            averages_normal[k] = averages_normal[k]/every_variable;
            averages_best[k] = averages_best[k]/every_variable;
            times_normal[k] = times_normal[k]/every_variable;
            times_best[k] = times_best[k]/every_variable;
            times_use_normal[k] = times_use_normal[k]/every_variable;
            times_use_best[k] = times_use_best[k]/every_variable;
        }

        for(int n=0; n<max_variables; n++){
            System.out.println(n+1 + " premennych - zredukovanie: " + averages_normal[n]);
            System.out.println(n+1 + " premennych - best zredukovanie: " + averages_best[n]);
            System.out.println(n+1 + " premennych - cas: " + times_normal[n] + " ns");
            System.out.println(n+1 + " premennych - best cas: " + times_best[n] + " ns");
            System.out.println(n+1 + " premennych - use: " + times_use_normal[n] + " ns");
            System.out.println(n+1 + " premennych - best use: " + times_use_best[n] + " ns");
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }
    }
}
