import java.util.*;
import java.util.Queue;
import java.util.stream.Collectors;

// trieda, ktorá uchováva údaje o jednotlivých uzloch
class Node{
    private final char variable;
    private Node parent;
    private Node yesBranch;
    private Node noBranch;
    private String remaining;
    private final int level;

    public Node(char variable, int level){
        this.variable = variable;
        this.remaining = "";
        this.yesBranch = null;
        this.noBranch = null;
        this.level = level;
    }

    // gettery a settery pre jednotlivé atribúty
    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public char getVariable() {
        return variable;
    }

    public void setYesBranch(Node yesBranch) {
        this.yesBranch = yesBranch;
    }

    public Node getYesBranch() {
        return yesBranch;
    }

    public void setNoBranch(Node noBranch) {
        this.noBranch = noBranch;
    }

    public Node getNoBranch() {
        return noBranch;
    }

    public int getLevel() {
        return level;
    }

    public String getRemaining() {
        return remaining;
    }

    public void setRemaining(String remaining) {
        this.remaining = remaining;
    }
}

// trieda, ktorá reprezentuje samotný binárny vyhľadávací diagram
public class BDD {
    private final int variables_count;
    private int nodes_count;
    private int removed_nodes;
    private Node root;
    private final HashChain[] hashChains;

    public BDD(int variables_count) {
        this.root = null;
        this.variables_count = variables_count;
        this.nodes_count = 0;
        this.removed_nodes = 0;
        this.hashChains = new HashChain[variables_count+1];
        for(int i=0; i<variables_count+1; i++){
            this.hashChains[i] = new HashChain(1000);
        }
    }

    // gettery a settery triedy BDD
    public int getNodes_count() {
        return nodes_count;
    }

    public int getVariables_count() {
        return variables_count;
    }

    public HashChain getHashChains(int level) {
        return hashChains[level];
    }

    public Node getRoot() {
        return root;
    }

    public void setNodes_count() {
        this.nodes_count++;
    }

    public void removeNodes_count(){
        this.nodes_count--;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public int getRemoved_nodes(){
        return this.removed_nodes;
    }

    public void setRemoved_nodes(int removed_nodes) {
        this.removed_nodes = removed_nodes;
    }

    public int addToHash(Node node, int level){
        return this.hashChains[level].insert(node);
    }
}

// trieda, v ktorej sú implementované jednotlivé funkcie na prácu s BDD
class BDDHandle{

    private static final String[][] permutations;

    // vytvorenie maximálne 1000 permutácii pre všetky možné počty premenných
    static {
        permutations = new String[26][];
        for(int i=0; i<26; i++){
            permutations[i] = getPermutations(i+1);
        }
    }

    public static void initialize(){
    }

    // metóda, ktorá spočíta počet výskytov znaku c v reťazci
    private static int count_characters(String s, char c){
        int count = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    // metóda, ktorá vráti pole reťazcov, každý iba raz
    private static String[] onlyUnique(String[] arr){
        Set<String> uniqueStrings = new HashSet<>(Arrays.asList(arr));

        return uniqueStrings.toArray(new String[0]);
    }

    // metóda, ktorá spočíta koľko uzlov by mal úplný BDD pre daný počet premenných
    public static int fullBDDNodesCount(int variables_count){
        return (int)Math.pow(2, variables_count+1)-1;
    }

    // spočítanie jedinečných znakov v reťazci na zistenie počtu premenných
    public static int countUniqueChars(String str) {
        HashSet<Character> set = new HashSet<>();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                set.add(c);
            }
        }
        return set.size();
    }

    // vytvorenie všetkých permutácii pre y počet premenných
    public static String[] getPermutations(int y) {
        List<Integer> numList = new ArrayList<>();

        for (int i = 0; i < y; i++) {
            numList.add(i);
        }
        Set<String> permutationSet = new HashSet<>();
        Collections.shuffle(numList);
        permute(numList, 0, numList.size(), permutationSet);
        return permutationSet.toArray(new String[0]);
    }

    private static void permute(List<Integer> list, int start, int end, Set<String> permutationSet) {
        if (start == end - 1) {
            StringBuilder sb = new StringBuilder();
            for (int num : list) {
                sb.append(num);
                sb.append("-");
            }
            sb.deleteCharAt(sb.length() - 1);
            permutationSet.add(sb.toString());
        } else {
            if(permutationSet.size() == 1000)
                return;

            for (int i = start; i < end; i++) {
                Collections.swap(list, start, i);
                permute(list, start + 1, end, permutationSet);
                Collections.swap(list, start, i);
            }
        }
    }

    // metóda, ktorá z daného výrazu určí výsledný výraz pre vetvu s vstupnou hodnotou 1
    public static String yesExpression(String[] expression, char c){
        String[] result = new String[expression.length];
        int i = 0;
        for (String exp : expression) {
            int j = exp.indexOf(c);     //pozrie sa, či sa zadaný znak nachádza vo výraze
            // ak nie, pridá sa tam celý výraz
            if(j == -1){
                result[i] = exp;
                i++;
            }else{
                // ak sa pred ním nachádza '!', výraz sa preskočí
                if(j!=0 && exp.charAt(j-1) == '!'){
                    continue;
                // inak sa zapíše výraz bez danej premennej
                }else{
                    // v prípade, ak je to len jeden znak, celá funkcia vráti 1
                    if(Objects.equals(exp, Character.toString(c))){
                        return "1";
                    }
                    result[i] = exp.replace(Character.toString(c), "");
                    i++;
                }
            }
        }
        // odstránenie rovnakých výrazov
        result = onlyUnique(result);
        int count = 0;
        int index = -1;
        for(int k=0; k<result.length; k++){
            if(result[k] != null && !result[k].isEmpty()){
                count++;
                index = k;
            }
        }
        // v prípade, že nie sú žiadne výrazy
        if(count == 0){
            if(i == 0){
                return "0";
            }else{
                return "1";
            }
        // ak je iba jeden, metóda vráti iba tento výraz
        }else if(count == 1){
            return result[index];
        // inak všetky výrazy spojí do jedného veľkého výrazu pomocou '+' a vráti, ako String
        }else{
            return Arrays.stream(result).filter(Objects::nonNull).collect(Collectors.joining("+"));
        }
    }

    // metóda, ktorá z daného výrazu určí výsledný výraz pre vetvu s vstupnou hodnotou 1
    public static String noExpression(String[] expression, char c){
        String[] result = new String[expression.length];
        int i = 0;
        for (String exp : expression) {
            int j = exp.indexOf(c);     //pozrie sa, či sa zadaný znak nachádza vo výraze
            // ak nie, pridá sa tam celý výraz
            if(j == -1){
                result[i] = exp;
                i++;
            }else{
                // ak sa pred ním nachádza '!', výraz sa zapíše bez tejto premennej
                if(j!=0 && exp.charAt(j-1) == '!'){
                    if(Objects.equals(exp, "!"+c)){
                        return "1";
                    }
                    result[i] = exp.replaceAll("!"+c, "");
                    i++;
                }
            }
        }
        // odstránenie rovnakých výrazov
        result = onlyUnique(result);
        int count = 0;
        int index = -1;
        for(int k=0; k<result.length; k++){
            if(result[k] != null && !result[k].isEmpty()){
                count++;
                index = k;
            }
        }
        // v prípade, že nie sú žiadne výrazy
        if(count == 0){
            if(i == 0){
                return "0";
            }else{
                return "1";
            }
        // ak je iba jeden, metóda vráti iba tento výraz
        }else if(count == 1){
            return result[index];
        // inak všetky výrazy spojí do jedného veľkého výrazu pomocou '+' a vráti, ako String
        }else{
            return Arrays.stream(result).filter(Objects::nonNull).collect(Collectors.joining("+"));
        }
    }

    // metóda BDD_create, ktorá vytvorí binárny vyhľadávací diagram a vráti ukazovateľ naň
    public static BDD BDD_create(String bfunkcia, String poradie){
        int p = count_characters(poradie, '-')+1;       // spočítanie, koľko premenných sa vo výraze vyskytuje
        BDD newBDD = new BDD(p);        // vytvorenie BDD s daným počtom premenných
        String[] o;
        char[] order = new char[p];
        boolean noUsed, yesUsed;

        noUsed = false;
        yesUsed = false;

        // záverečné uzly, ktoré sa vložia na úplný koniec BDD, obsahujú len hodnotu 0 alebo 1
        Node yes = new Node('1', p);
        Node no = new Node('0', p);

        // načítanie poradia do poľa znakov order, aby sa vedelo v akom poradí sa majú vkladať
        o = poradie.split("-");
        for(int i=0; i<o.length; i++){
            order[i] = (char)('A' + Integer.parseInt(String.valueOf(o[i])));
        }

        // vytvorenie prvého uzla, ktorý bude zároveň koreňom BDD
        newBDD.setRoot(new Node(order[0], 0));
        newBDD.setNodes_count();        // zvýšenie počtu uzlov
        newBDD.getRoot().setRemaining(bfunkcia);

        if(o.length == 1){
            String[] z = onlyUnique(bfunkcia.split("\\+"));
            if(z.length == 1){
                if(Objects.equals(z[0], "A")){
                    newBDD.getRoot().setNoBranch(no);
                    newBDD.getRoot().setYesBranch(yes);
                    noUsed = true;
                    yesUsed = true;
                }else if(Objects.equals(z[0], "!A")){
                    newBDD.getRoot().setNoBranch(yes);
                    newBDD.getRoot().setYesBranch(no);
                    noUsed = true;
                    yesUsed = true;
                }
            }else{
                newBDD.getRoot().setNoBranch(yes);
                newBDD.getRoot().setYesBranch(yes);
                yesUsed = true;
            }
            if(yesUsed)
                newBDD.setNodes_count();
            if(noUsed)
                newBDD.setNodes_count();

            newBDD.setRemoved_nodes(fullBDDNodesCount(newBDD.getVariables_count()) - newBDD.getNodes_count());
            return newBDD;
        }

        Queue<Node> nodes = new LinkedList<>();     // spájaný zoznam, do ktorého sa budú vkladať ešte nespracované uzly
        nodes.add(newBDD.getRoot());
        Node current;

        // while cyklus, kým neprejdeme všetky nespracované uzly
        while(!nodes.isEmpty()){
            current=nodes.remove();     // vybratie prvého vloženého prvku
            int level = current.getLevel()+1;
            String[] expressions;

            expressions = current.getRemaining().split("\\+");      // rozdelenie výrazu rodiča na menšie podvýrazy

            // vytvorenie dvoch nových uzlov pre true a false
            Node noNode = new Node(order[level], level);
            Node yesNode = new Node(order[level], level);

            // zistenie zostávajúcich výrazov pre tieto uzly
            String noExp = noExpression(expressions, order[level-1]);
            String yesExp = yesExpression(expressions, order[level-1]);

            noNode.setRemaining(noExp);
            noNode.setParent(current);
            yesNode.setRemaining(yesExp);
            yesNode.setParent(current);

            int n=0, y=0;       // pomocné premenné, ktoré zisťujú, či sa daný uzol už v tejto úrovni nenachádza

            if(!Objects.equals(noExp, "1") && !Objects.equals(noExp, "0")){
                n = newBDD.addToHash(noNode,level+1);
            }
            if(!Objects.equals(yesExp, "1") && !Objects.equals(yesExp, "0")){
                y = newBDD.addToHash(yesNode,level+1);
            }

            // ak ešte nie je taký uzol v tejto úrovni, vykoná sa nasledujúca časť kódu
            if(n != -1){
                // pripojí sa na predchádzajúci uzol
                current.setNoBranch(noNode);
                newBDD.setNodes_count();

                // ak je zvyšok už len daná premenná
                if(Objects.equals(noExp, String.valueOf(order[level]))){
                    noNode.setNoBranch(no);
                    noNode.setYesBranch(yes);
                    noUsed = true;
                    yesUsed = true;
                // ak je zvyšok už len daná premenná, ale negovaná
                }else if(Objects.equals(noExp, "!"+order[level])){
                    noNode.setNoBranch(yes);
                    noNode.setYesBranch(no);
                    noUsed = true;
                    yesUsed = true;
                // ak je zvyšok už len 1
                }else if(Objects.equals(noExp, "1")){
                    if(noNode.getParent().getYesBranch() == noNode){
                        noNode.getParent().setYesBranch(yes);
                        yesUsed = true;
                    }else if(current.getNoBranch() == noNode){
                        current.setNoBranch(yes);
                        yesUsed = true;
                    }
                    newBDD.removeNodes_count();     //odčítanie počtu uzlov
                // ak je zvyšok už len 0
                }else if(Objects.equals(noExp, "0")){
                    if(noNode.getParent().getYesBranch() == noNode){
                        current.setYesBranch(no);
                        noUsed = true;
                    }else if(noNode.getParent().getNoBranch() == noNode){
                        current.setNoBranch(no);
                        noUsed = true;
                    }
                    newBDD.removeNodes_count();     // odčítanie počtu uzlov
                }else{
                    String[] z = noExp.split("\\+");        // rozdelenie výrazu na základe '+'
                    // ak je podvýrazov viac, ako 1
                    if(z.length > 1){
                        // kontrola počtu znakov
                        if((z[0].length() == 2 && z[1].length() == 1) || (z[0].length() == 1 && z[1].length() == 2)){
                            // ak je to, to isté len jeden z dvoch výrazov je negovaný
                            if(Objects.equals("!"+z[0], z[1]) || Objects.equals(z[0], "!"+z[1])){
                                noNode.setYesBranch(yes);
                                noNode.setNoBranch(yes);
                                yesUsed = true;
                                reduction_S(noNode, newBDD);
                            }else{
                                nodes.add(noNode);      // pridanie uzla do radu
                            }
                        }else{
                            nodes.add(noNode);      // pridanie uzla do radu
                        }
                    // ak je počet podvýrazov len 1, uzol sa pridá do radu
                    }else if(z.length == 1){
                        nodes.add(noNode);
                    }
                }
            // v prípade, že sa taký uzol už v tejto úrovni nachádza, prepojí sa aktuálny uzol už s vytvoreným uzlom
            }else{
                current.setNoBranch(newBDD.getHashChains(level+1).search(noExp));
            }

            // ak ešte nie je taký uzol v tejto úrovni, vykoná sa nasledujúca časť kódu
            if(y != -1){
                // pripojí sa na predchádzajúci uzol
                current.setYesBranch(yesNode);
                newBDD.setNodes_count();

                // ak je zvyšok už len daná premenná
                if(Objects.equals(yesExp, String.valueOf(order[level]))){
                    yesNode.setNoBranch(no);
                    yesNode.setYesBranch(yes);
                    noUsed = true;
                    yesUsed = true;
                // ak je zvyšok už len daná premenná, ale negovaná
                }else if(Objects.equals(yesExp, "!"+order[level])){
                    yesNode.setNoBranch(yes);
                    yesNode.setYesBranch(no);
                    noUsed = true;
                    yesUsed = true;
                // ak je zvyšok už len 1
                }else if(Objects.equals(yesExp, "1")){
                    if(yesNode.getParent().getYesBranch() == yesNode){
                        yesNode.getParent().setYesBranch(yes);
                        yesUsed = true;
                    }
                    else if(yesNode.getParent().getNoBranch() == yesNode){
                        yesNode.getParent().setNoBranch(yes);
                        yesUsed = true;
                    }

                    newBDD.removeNodes_count();     //odčítanie počtu uzlov
                // ak je zvyšok už len 0
                }else if(Objects.equals(yesExp, "0")){
                    if(yesNode.getParent().getYesBranch() == yesNode){
                        yesNode.getParent().setYesBranch(no);
                        noUsed = true;
                    }
                    else if(yesNode.getParent().getNoBranch() == yesNode){
                        yesNode.getParent().setNoBranch(no);
                        noUsed = true;
                    }

                    newBDD.removeNodes_count();     //odčítanie počtu uzlov
                }else{
                    String[] z = yesExp.split("\\+");       // rozdelenie výrazu na základe '+'
                    // ak je podvýrazov viac, ako 1
                    if(z.length > 1){
                        // kontrola počtu znakov
                        if((z[0].length() == 2 && z[1].length() == 1) || (z[0].length() == 1 && z[1].length() == 2)){
                            // ak je to, to isté len jeden z dvoch výrazov je negovaný
                            if(Objects.equals("!"+z[0], z[1]) || Objects.equals(z[0], "!"+z[1])){
                                yesNode.setYesBranch(yes);
                                yesNode.setNoBranch(yes);
                                yesUsed = true;
                                reduction_S(yesNode, newBDD);
                            }else {
                                nodes.add(yesNode);     // pridanie uzla do radu
                            }
                        }else{
                            nodes.add(yesNode);     // pridanie uzla do radu
                        }

                    }else{
                        nodes.add(yesNode);     // pridanie uzla do radu
                    }
                }
            // v prípade, že sa taký uzol už v tejto úrovni nachádza, prepojí sa aktuálny uzol už s vytvoreným uzlom
            }else{
                Node nod = newBDD.getHashChains(level+1).search(yesExp);
                current.setYesBranch(nod);
            }

            reduction_S(current, newBDD);
        }

        if(yesUsed)
            newBDD.setNodes_count();
        if(noUsed)
            newBDD.setNodes_count();

        // nastavenie počtu odstránených uzlov odčítaním uzlov v úplnom BDD a vytvorených uzlov
        newBDD.setRemoved_nodes(fullBDDNodesCount(newBDD.getVariables_count()) - newBDD.getNodes_count());
        return newBDD;
    }

    public static void reduction_S(Node current, BDD bdd){
        if(current.getNoBranch() == current.getYesBranch()){
            // ak je tento uzol potomkom rodiča pre false vetvu
            if(current.getParent()!=null && current == current.getParent().getNoBranch()){
                current.getParent().setNoBranch(current.getNoBranch());
                bdd.removeNodes_count();
                // ak je tento uzol potomkom rodiča pre false vetvu
            }else if(current.getParent()!=null){
                current.getParent().setYesBranch(current.getYesBranch());
                bdd.removeNodes_count();
            }
        }
    }

    // metóda, ktorá vyskúša rôzne permutácie, aby našla najlepšie poradie, pre ktoré bude počet vytvorených uzlov čo najmenší
    public static BDD BDD_create_with_best_order(String bfunkcia){
        int variables_count = countUniqueChars(bfunkcia);       // spočítanie počtu premenných
        int best = Integer.MAX_VALUE;
        BDD best_BDD = null;

        // pole maximálne 1000 náhodných permutácii poradí prvkov
        String[] perms = permutations[variables_count-1];

        for(String p : perms){
            BDD newBDD = BDD_create(bfunkcia, p);
            if(newBDD.getNodes_count() < best){
                best = newBDD.getNodes_count();
                best_BDD = newBDD;
            }
        }

        return best_BDD;
    }

    // metóda, ktorá na základe vstupu zistí výslednú hodnotu výrazu
    public static char BDD_use(BDD bdd, String vstupy){
        // uloženie všetkých premenných aj s ich hodnotou, buď 1-true alebo 0-false
        Map<Character, Integer> map = new HashMap<>();

        if(vstupy.length() > bdd.getVariables_count()){
            return (char) -1;
        }

        for(int i=0; i<vstupy.length(); i++){
            map.put((char) ('A'+i), Integer.parseInt(String.valueOf(vstupy.charAt(i))));
        }

        // postupné prejdenie všetkých uzlov od koreňa
        Node current = bdd.getRoot();
        int j = 0;
        while(j!=vstupy.length() && current.getVariable() != '0' && current.getVariable() != '1'){
            // na základe hodnoty premennej sa určí nasledujúca vetva
            if(map.get(current.getVariable()) == 0){
                current = current.getNoBranch();
            }else if(map.get(current.getVariable()) == 1){
                current = current.getYesBranch();
            }
            j++;
        }
        char var = current.getVariable();       // zistenie výslednej hodnoty
        if(var == '0'){
            return var;
        }else if(var == '1'){
            return var;
        }else{
            return (char)-1;        // v prípade iného výsledku, ako 0 alebo 1, metóda vráti -1
        }
    }

    public static void printBDD(Node node, String prefix) {
        if (node.getVariable() == '0' || node.getVariable() == '1') {
            return;
        }

        String nodeStr = prefix + node.getVariable() + ":" + node.getRemaining();
        System.out.println(nodeStr);

        if (node.getNoBranch() != null && node.getYesBranch() != null) {
            String lowStr = prefix + "  0 -- ";
            String highStr = prefix + "  1 -- ";
            if (prefix.equals("")) {
                lowStr += node.getNoBranch().getVariable() + ":" + node.getNoBranch().getRemaining();
                highStr += node.getYesBranch().getVariable() + ":" + node.getYesBranch().getRemaining();
            } else {
                lowStr += node.getNoBranch().getVariable() + ":" + node.getNoBranch().getRemaining();
                highStr += node.getYesBranch().getVariable() + ":" + node.getYesBranch().getRemaining();
            }
            System.out.println(lowStr);
            printBDD(node.getNoBranch(), prefix + "  | ");
            System.out.println(highStr);
            printBDD(node.getYesBranch(), prefix + "   ");
        } else if (node.getNoBranch() == null && node.getYesBranch() != null) {
            String highStr = prefix + "  1 -- ";
            highStr += node.getYesBranch().getVariable() + ":" + node.getYesBranch().getRemaining();
            System.out.println(highStr);
            printBDD(node.getYesBranch(), prefix + "   ");
        } else if (node.getNoBranch() != null && node.getYesBranch() == null) {
            String lowStr = prefix + "  0 -- ";
            lowStr += node.getNoBranch().getVariable() + ":" + node.getNoBranch().getRemaining();
            System.out.println(lowStr);
            printBDD(node.getNoBranch(), prefix + "  | ");
        }

    }

    public static void main(String[] args){
        BDD b = BDD_create("!ABD+B!CD+A!C+!A!BCD", "2-1-3-0");

        printBDD(b.getRoot(), "");
        System.out.println("Počet uzlov: " + b.getNodes_count());
        System.out.println("Počet odstránených uzlov: " + b.getRemoved_nodes());
        System.out.println("Počet uzlov v úplnom BDD: " + fullBDDNodesCount(4));

        System.out.println("#################################################################");

        b = BDD_create("AB+C", "0-1-2");
        //System.out.println(BDD_use(b, "111011"));
        if(BDD_use(b,"000") != '0')
            System.out.println("error, for A=0, B=0, C=0 result should be 0.");
        if(BDD_use(b,"001") != '1')
            System.out.println("error, for A=0, B=0, C=1 result should be 1.");
        if(BDD_use(b,"010") != '0')
            System.out.println("error, for A=0, B=1, C=0 result should be 0.");
        if(BDD_use(b,"011") != '1')
            System.out.println("error, for A=0, B=1, C=1 result should be 1.");
        if(BDD_use(b,"100") != '0')
            System.out.println("error, for A=1, B=0, C=0 result should be 0.");
        if(BDD_use(b,"101") != '1')
            System.out.println("error, for A=1, B=0, C=1 result should be 1.");
        if(BDD_use(b,"110") != '1')
            System.out.println("error, for A=1, B=1, C=0 result should be 1.");
        if(BDD_use(b,"111") != '1')
            System.out.println("error, for A=1, B=1, C=1 result should be 1.");
    }
}
