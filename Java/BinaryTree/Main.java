import java.time.Duration;
import java.time.Instant;
import java.util.Random;

public class Main {

    public static void testInsert(boolean print, int[] numbers, String[] strings, int n, AVLTree avl, SplayTree splay, HashChain chain, HashOpen open){
        Instant start, finish;
        long timeElapsed;

        if(!print){
            for(int i=0; i<n; i++){
                avl.insertNode(avl.getRoot(), numbers[i], strings[i]);
                splay.insert(numbers[i], strings[i]);
                chain.insert(strings[i], numbers[i]);
                open.insert(strings[i], numbers[i]);
            }
            return;
        }

        System.out.printf("INSERT - %d dat\n", n);
        System.out.println("-----------------------------------------");

        start = Instant.now();
        for(int i=0; i<n; i++){
            avl.insertNode(avl.getRoot(), numbers[i], strings[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insertu do AVL je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insertu do AVL je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            splay.insert(numbers[i], strings[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insertu do Splay je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insertu do Splay je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            chain.insert(strings[i], numbers[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insertu do Hash Chain je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insertu do Hash Chain je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            open.insert(strings[i], numbers[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insertu do Open Chain je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insertu do Open Chain je: " + timeElapsed + " ms");
        }
    }

    public static void testSearch(int[] numbers, String[] strings, int n, AVLTree avl, SplayTree splay, HashChain chain, HashOpen open){
        Instant start, finish;
        long timeElapsed;
        Node found;

        if(avl.getTotal()==0 || splay.getTotal()==0 || chain.getSize()==0 || open.getSize()==0){
            testInsert(false, numbers, strings, n, avl, splay, chain, open);
        }

        System.out.printf("SEARCH - %d dat\n", n);
        System.out.println("-----------------------------------------");

        start = Instant.now();
        for(int i=0; i<n; i++){
            found = avl.search(avl.getRoot(), numbers[i], strings[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas searchu v AVL je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas searchu v AVL je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=n-1; i>=0; i--){
            splay.search(numbers[i], strings[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas searchu v Splay je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas searchu v Splay je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            chain.search(strings[i], numbers[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas searchu v Hash Chain je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas searchu v Hash Chain je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            open.search(strings[i], numbers[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas searchu v Hash Open je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas searchu v Hash Open je: " + timeElapsed + " ms");
        }
    }

    public static void testDelete(int[] numbers, String[] strings, int n, AVLTree avl, SplayTree splay, HashChain chain, HashOpen open){
        Instant start, finish;
        long timeElapsed;

        if(avl.getTotal()==0 || splay.getTotal()==0 || chain.getSize()==0 || open.getSize()==0){
            testInsert(false, numbers, strings, n, avl, splay, chain, open);
        }

        System.out.printf("DELETE - %d dat\n", n);
        System.out.println("-----------------------------------------");

        start = Instant.now();
        for(int i=0; i<n; i++){
            avl.delete(avl.getRoot(), numbers[i], strings[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas deletu AVL je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas deletu AVL je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            splay.delete(splay.getRoot(), numbers[i], strings[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas deletu Splay je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas deletu Splay je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            chain.delete(strings[i], numbers[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas deletu Hash Chain je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas deletu Hash Chain je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            open.delete(strings[i], numbers[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas deletu Hash Open je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas deletu Hash Open je: " + timeElapsed + " ms");
        }
    }

    public static void testInsertDelete(int[] numbers, String[] strings, int n, int size){
        AVLTree avl = new AVLTree();
        SplayTree splay = new SplayTree();
        HashChain chain = new HashChain(size);
        HashOpen open = new HashOpen(size);

        Instant start, finish;
        long timeElapsed;

        System.out.printf("INSERT -> DELETE - %d dat\n", n);
        System.out.println("-----------------------------------------");

        start = Instant.now();
        for(int i=0; i<n; i++){
            avl.insertNode(avl.getRoot(), numbers[i], strings[i]);
        }
        for(int i=0; i<n; i++){
            avl.delete(avl.getRoot(), numbers[i], strings[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insert->delete v AVL je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insert->delete v AVL je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            splay.insert(numbers[i], strings[i]);
        }
        for(int i=n-1; i>=0; i--){
            splay.delete(splay.getRoot(), numbers[i], strings[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insert->delete v Splay je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insert->delete v Splay je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            chain.insert(strings[i], numbers[i]);
        }
        for(int i=0; i<n; i++){
            chain.delete(strings[i], numbers[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insert->delete v Hash Chain je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insert->delete v Hash Chain je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            open.insert(strings[i], numbers[i]);
        }
        for(int i=0; i<n; i++){
            open.delete(strings[i], numbers[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insert->delete v Hash Open je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insert->delete v Hash Open je: " + timeElapsed + " ms");
        }
    }

    public static void testInsertSearch(int[] numbers, String[] strings, int n, int size){
        AVLTree avl = new AVLTree();
        SplayTree splay = new SplayTree();
        HashChain chain = new HashChain(size);
        HashOpen open = new HashOpen(size);

        Node found;

        Instant start, finish;
        long timeElapsed;

        System.out.printf("INSERT -> SEARCH - %d dat\n", n);
        System.out.println("-----------------------------------------");

        start = Instant.now();
        for(int i=0; i<n; i++){
            avl.insertNode(avl.getRoot(), numbers[i], strings[i]);
        }
        for(int i=0; i<n; i++){
            found = avl.search(avl.getRoot(), numbers[i], strings[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insert->search v AVL je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insert->search v AVL je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            splay.insert(numbers[i], strings[i]);
        }
        for(int i=n-1; i>=0; i--){
            splay.search(numbers[i], strings[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insert->search v Splay je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insert->search v Splay je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            chain.insert(strings[i], numbers[i]);
        }
        for(int i=0; i<n; i++){
            chain.search(strings[i], numbers[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insert->search v Hash Chain je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insert->search v Hash Chain je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            open.insert(strings[i], numbers[i]);
        }
        for(int i=0; i<n; i++){
            open.search(strings[i], numbers[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insert->search v Hash Open je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insert->search v Hash Open je: " + timeElapsed + " ms");
        }
    }

    public static void testInsertDeleteSearch(int[] numbers, String[] strings, int n, int size){
        AVLTree avl = new AVLTree();
        SplayTree splay = new SplayTree();
        HashChain chain = new HashChain(size);
        HashOpen open = new HashOpen(size);

        Node found;

        Instant start, finish;
        long timeElapsed;

        System.out.printf("INSERT -> DELETE -> SEARCH - %d dat\n", n);
        System.out.println("-----------------------------------------");

        start = Instant.now();
        for(int i=0; i<n; i++){
            avl.insertNode(avl.getRoot(), numbers[i], strings[i]);
        }
        for(int i=0; i<n; i++){
            avl.delete(avl.getRoot(), numbers[i], strings[i]);
        }
        for(int i=0; i<n; i++){
            found = avl.search(avl.getRoot(), numbers[i], strings[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insert->delete->search v AVL je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insert->delete->search v AVL je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            splay.insert(numbers[i], strings[i]);
        }
        for(int i=n-1; i>=0; i--){
            splay.delete(splay.getRoot(), numbers[i], strings[i]);
        }
        for(int i=0; i<n; i++){
            splay.search(numbers[i], strings[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insert->delete->search v Splay je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insert->delete->search v Splay je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            chain.insert(strings[i], numbers[i]);
        }
        for(int i=0; i<n; i++){
            chain.delete(strings[i], numbers[i]);
        }
        for(int i=0; i<n; i++){
            chain.search(strings[i], numbers[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insert->delete->search v Hash Chain je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insert->delete->search v Hash Chain je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            open.insert(strings[i], numbers[i]);
        }
        for(int i=0; i<n; i++){
            open.delete(strings[i], numbers[i]);
        }
        for(int i=0; i<n; i++){
            open.search(strings[i], numbers[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insert->delete->search v Hash Open je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insert->delete->search v Hash Open je: " + timeElapsed + " ms");
        }
    }

    public static void testInsertSearchDelete(int[] numbers, String[] strings, int n, int size){
        AVLTree avl = new AVLTree();
        SplayTree splay = new SplayTree();
        HashChain chain = new HashChain(size);
        HashOpen open = new HashOpen(size);

        Node found;

        Instant start, finish;
        long timeElapsed;

        System.out.printf("INSERT -> SEARCH -> DELETE - %d dat\n", n);
        System.out.println("-----------------------------------------");

        start = Instant.now();
        for(int i=0; i<n; i++){
            avl.insertNode(avl.getRoot(), numbers[i], strings[i]);
        }
        for(int i=0; i<n; i++){
            found = avl.search(avl.getRoot(), numbers[i], strings[i]);
        }
        for(int i=0; i<n; i++){
            avl.delete(avl.getRoot(), numbers[i], strings[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insert->search->delete v AVL je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insert->search->delete v AVL je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            splay.insert(numbers[i], strings[i]);
        }
        for(int i=n-1; i>=0; i--){
            splay.search(numbers[i], strings[i]);
        }
        for(int i=0; i<n; i++){
            splay.delete(splay.getRoot(), numbers[i], strings[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insert->search->delete v Splay je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insert->search->delete v Splay je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            chain.insert(strings[i], numbers[i]);
        }
        for(int i=0; i<n; i++){
            chain.search(strings[i], numbers[i]);
        }
        for(int i=0; i<n; i++){
            chain.delete(strings[i], numbers[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insert->search->delete v Hash Chain je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insert->search->delete v Hash Chain je: " + timeElapsed + " ms");
        }

        start = Instant.now();
        for(int i=0; i<n; i++){
            open.insert(strings[i], numbers[i]);
        }
        for(int i=0; i<n; i++){
            open.search(strings[i], numbers[i]);
        }
        for(int i=0; i<n; i++){
            open.delete(strings[i], numbers[i]);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        if(timeElapsed == 0){
            timeElapsed = Duration.between(start, finish).toNanos();
            System.out.println("Cas insert->search->delete v Hash Open je: " + timeElapsed + " ns");
        }else{
            System.out.println("Cas insert->search->delete v Hash Open je: " + timeElapsed + " ms");
        }
    }

    public static String randomString(int length){
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";

        String alphabet = upper + lower + numbers;

        StringBuilder sb = new StringBuilder();

        Random random = new Random();

        for(int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());

            char randChar = alphabet.charAt(index);

            sb.append(randChar);
        }

        return sb.toString();
    }

    public static String[] randomStrings(int number){
        String[] pole = new String[number];

        for(int i=0; i<number; i++){
            pole[i] = randomString(15);
        }

        return pole;
    }

    public static int[] numbersRandom(int number){
        int[] pole = new int[number];

        for(int i=0; i<number; i++){
            pole[i] = (int)(Math.random()*1000000000);
        }

        return pole;
    }

    public static void main(String[] args) {
        AVLTree avl = new AVLTree();
        SplayTree splay = new SplayTree();
        HashChain chain = new HashChain(10);
        HashOpen open = new HashOpen(10);

        int n;
        int[] randNum;
        String[] randString;

        // 100 prvkov
        n = 100;
        randNum = numbersRandom(n);
        randString = randomStrings(n);

        testInsert(true, randNum, randString, n, avl, splay, chain, open);
        System.out.println();
        testSearch(randNum, randString, n, avl, splay, chain, open);
        System.out.println();
        testDelete(randNum, randString, n, avl, splay, chain, open);
        System.out.println();
        testInsertDelete(randNum, randString, n, 10);
        System.out.println();
        testInsertSearch(randNum, randString, n, 10);
        System.out.println();
        testInsertDeleteSearch(randNum, randString, n, 10);
        System.out.println();
        testInsertSearchDelete(randNum, randString, n, 10);
        System.out.println();

        System.out.println("####################################################");

        // 1 000 prvkov
        n = 1000;
        randNum = numbersRandom(n);
        randString = randomStrings(n);

        testInsert(true, randNum, randString, n, avl, splay, chain, open);
        System.out.println();
        testSearch(randNum, randString, n, avl, splay, chain, open);
        System.out.println();
        testDelete(randNum, randString, n, avl, splay, chain, open);
        System.out.println();
        testInsertDelete(randNum, randString, n, 10);
        System.out.println();
        testInsertSearch(randNum, randString, n, 10);
        System.out.println();
        testInsertDeleteSearch(randNum, randString, n, 10);
        System.out.println();
        testInsertSearchDelete(randNum, randString, n, 10);
        System.out.println();

        System.out.println("####################################################");

        for(int i=100000; i<=1000000; i+=100000){
            randNum = numbersRandom(i);
            randString = randomStrings(i);

            testInsert(true, randNum, randString, i, avl, splay, chain, open);
            System.out.println();
            testSearch(randNum, randString, i, avl, splay, chain, open);
            System.out.println();
            testDelete(randNum, randString, i, avl, splay, chain, open);
            System.out.println();
            testInsertDelete(randNum, randString, i, 10);
            System.out.println();
            testInsertSearch(randNum, randString, i, 10);
            System.out.println();
            testInsertDeleteSearch(randNum, randString, i, 10);
            System.out.println();
            testInsertSearchDelete(randNum, randString, i, 10);
            System.out.println();

            System.out.println("####################################################");
        }
    }
}