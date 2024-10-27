import java.util.*;

// Meno študenta: Tomáš Brček
public class MaxFeeHandleTxs {

    // vhniezdená trieda reprezentujúca jeden uzol v grafe
    static class Node {
        private Transaction tx;
        private ArrayList<Node> children;   // nasledovnici
        private ArrayList<Node> parents;    // predchodcovia
        private double fee;                 // fee na danom uzle
        private boolean parent;             // pomocný boolean

        public Node(Transaction tx, double fee){
            this.tx = tx;
            this.children = new ArrayList<>();
            this.parents = new ArrayList<>();
            this.fee = fee;
            this.parent = false;
        }

        // pridanie uzla medzi rodičov
        public void addParent(Node parent){
            this.parents.add(parent);
        }

        // pridanie uzla medzi potomkov
        public void addChild(Node child){
            this.children.add(child);
        }
    }

    // trieda reprezentujúca podmnožinu transakcií
    static class Subset {
        private ArrayList<Transaction> transactions;    //transakcie
        private double fee;         // súčet všetkých fee
        private UTXOPool pool;      // UTXOPool v danej podmnožine

        public Subset(){
            this.transactions = new ArrayList<>();
            this.fee = 0;
        }
    }

    public UTXOPool utxo;

    /**
     * Vytvorí verejný ledger (účtovnú knihu), ktorého aktuálny UTXOPool (zbierka nevyčerpaných
     * transakčných výstupov) je {@code utxoPool}. Malo by to vytvoriť bezpečnú kópiu
     * utxoPool pomocou konštruktora UTXOPool (UTXOPool uPool).
     */
    public MaxFeeHandleTxs(UTXOPool utxoPool) {
        // IMPLEMENTOVAŤ
        utxo = new UTXOPool(utxoPool);
    }

    /**
     * @return aktuálny UTXO pool.
     * Ak nenájde žiadny aktuálny UTXO pool, tak vráti prázdny (nie nulový) objekt {@code UTXOPool}.
     */
    public UTXOPool UTXOPoolGet() {
        // IMPLEMENTOVAŤ
        if(utxo == null){
            return new UTXOPool();
        }
        return utxo;
    }

    /**
     * @return true, ak
     * (1) sú všetky nárokované výstupy {@code tx} v aktuálnom UTXO pool,           *
     * (2) podpisy na každom vstupe {@code tx} sú platné,                           *
     * (3) žiadne UTXO nie je nárokované viackrát,                                  *
     * (4) všetky výstupné hodnoty {@code tx}s sú nezáporné a                       *
     * (5) súčet vstupných hodnôt {@code tx}s je väčší alebo rovný súčtu jej        *
     *     výstupných hodnôt; a false inak.
     */
    public boolean txIsValid(Transaction tx) {
        // IMPLEMENTOVAŤ
        UTXOPool curUTXO = UTXOPoolGet();
        HashSet<UTXO> uniqueUTXO = new HashSet<>();
        double inputSum = 0;
        double outputSum = 0;
        int i = 0;
        for(Transaction.Input ip : tx.getInputs()){
            UTXO ut = new UTXO(ip.prevTxHash, ip.outputIndex);
            // kontrola bodu (1)
            if(!curUTXO.contains(ut)){
                return false;
            }else{
                // kontrola bodu (3)
                if(uniqueUTXO.contains(ut)){
                    return false;
                }else{
                    uniqueUTXO.add(ut);
                }
            }
            // kontrola bodu (2)
            RSAKey publicKey = curUTXO.getTxOutput(ut).address;
            byte[] dataToSign = tx.getDataToSign(i);
            boolean isSignatureValid = publicKey.verifySignature(dataToSign, ip.signature);

            if(!isSignatureValid){
                return false;
            }

            inputSum += curUTXO.getTxOutput(ut).value;
            i++;
        }
        for(Transaction.Output op : tx.getOutputs()){
            // kontrola bodu (4)
            if(op.value < 0){
                return false;
            }
            outputSum += op.value;
        }
        // kontrola bodu (5)
        if(inputSum < outputSum){
            return false;
        }
        return true;
    }

    public void updateUTXO(Transaction tx){
        ArrayList<Transaction.Input> inputs = tx.getInputs();       // získanie inputov z transakcie
        ArrayList<Transaction.Output> outputs = tx.getOutputs();    // získanie outputov z transakcie

        int index = 0;
        // odstránenie inputov z UTXOPool
        for(Transaction.Input in : inputs){
            UTXO u = new UTXO(in.prevTxHash, in.outputIndex);
            utxo.removeUTXO(u);
        }
        // pridanie outputov do UTXOPool
        for(Transaction.Output op : outputs){
            UTXO u = new UTXO(tx.getHash(), index);
            utxo.addUTXO(u, op);
            index++;
        }
    }

    // metóda, ktorá vracia súčet všetkých inputov v transakcii
    public double getInputsSum(ArrayList<Transaction.Input> inputs, UTXOPool up){
        double sum = 0;
        for(Transaction.Input in: inputs){
            UTXO ut = new UTXO(in.prevTxHash, in.outputIndex);
            sum += up.getTxOutput(ut).value;
        }
        return sum;
    }

    // metóda, ktorá vracia súčet všetkých outputov v transakcii
    public double getOutputsSum(ArrayList<Transaction.Output> outputs){
        double sum = 0;
        for(Transaction.Output out: outputs){
            sum += out.value;
        }
        return sum;
    }

    // metóda, ktorá vracia fee, teda rozdiel súčtu inputov a súčtu outputov
    public double getFee(Transaction tx, UTXOPool up){
        ArrayList<Transaction.Input> inputs = tx.getInputs();
        ArrayList<Transaction.Output> outputs = tx.getOutputs();

        return getInputsSum(inputs, up) - getOutputsSum(outputs);
    }

    // metóda, ktorá pre daný uzol kontroluje, či medzi jeho deťmi dochádza k double-spendingu
    public ArrayList<Node> checkDoubleSpending(Node n){
        Node[] children = n.children.toArray(new Node[0]);  // všetky deti daného uzla
        HashMap<UTXO, Node> hm = new HashMap<>();   // HashMap na kontrolu double-spendingu
        ArrayList<Node> doubleSpends = new ArrayList<>();   //uzly, ktoré double-spendujú
        for(Node ch : children){
            Transaction tx = ch.tx;
            for(Transaction.Input in: tx.getInputs()){
                UTXO u = new UTXO(in.prevTxHash, in.outputIndex);
                // ak už sa toto UTXO nachádza v mape, ide o double-spending
                if(hm.containsKey(u)){
                    doubleSpends.add(ch);
                    doubleSpends.add(hm.get(u));
                }else{
                    hm.put(u, ch);
                }
            }
        }
        return doubleSpends;
    }

    // rekurzívna metóda, ktorá vracia množinu s maximálnym fee v podstrome
    public Subset handleTxs(ArrayList<Node> roots, UTXOPool uPool, HashSet<Node> processedTransactions){
        ArrayList<Node> queue = new ArrayList<>(roots);
        Subset sub = new Subset();
        utxo = new UTXOPool(uPool);

        // triviálny prípad
        if(roots.isEmpty())
            return sub;

        while(!queue.isEmpty()){
            Node curNode = queue.remove(0);     // vyýber prvku z radu
            curNode.parent = false;     // resetovanie pomocnej premennej

            boolean notProcessed = false;
            // kontrola, či už boli spracovaní všetci rodičia daného uzla
            for(Node x : curNode.parents){
                // ak nie, vložia sa do radu
                if(!processedTransactions.contains(x)){
                    x.parent = true;
                    queue.add(x);
                    notProcessed = true;
                }else{
                    x.parent = false;
                }
            }
            // preskočenie tohto uzla
            if(notProcessed)
                continue;

            processedTransactions.add(curNode);     // pridanie medzi spracované

            // kontrola transakcie v aktuálnom uzle
            Transaction tx = curNode.tx;
            if (txIsValid(curNode.tx)) {
                if(!curNode.parent){
                    sub.transactions.add(tx);   // vloženie transakcie do podmnožiny
                    sub.fee += curNode.fee;     // pripočítanie fee
                }

                updateUTXO(tx);     // aktualizovanie UTXOPoolu
                // kontrola, či niektoré z detí ne-double-spendujú
                ArrayList<Node> d = checkDoubleSpending(curNode);
                // toto sa vykoná v prípade, že double-spendujú
                if(!d.isEmpty()){
                    ArrayList<Subset> doubles = new ArrayList<>();
                    // vytvorí sa kópia UTXOPoolu
                    UTXOPool sendUtxo = new UTXOPool(utxo);
                    for(Node del : d){
                        ArrayList<Node> dob = new ArrayList<>();
                        dob.add(del);
                        // vloženie vrátenej podmnožiny z rekurzívneho volania do poľa doubles
                        doubles.add(handleTxs(dob, sendUtxo, processedTransactions));
                        // odstránenie z potomkov a radu
                        curNode.children.remove(del);
                        queue.remove(del);
                    }
                    // získanie podmnožiny s maximálnym fee
                    ArrayList<Transaction> maxFeeTxs = doubles.get(0).transactions;
                    double maxFee = doubles.get(0).fee;
                    Subset maxSubset = doubles.get(0);
                    for(Subset s : doubles){
                        if(maxFee < s.fee){
                            maxFeeTxs = s.transactions;
                            maxFee = s.fee;
                            maxSubset = s;
                        }
                    }
                    sub.fee += maxFee;  // pripočítanie fee
                    utxo = new UTXOPool(maxSubset.pool);    // aktualizácia poolu
                    // odstránenie všetkých spracovaných uzlov z UTXOPoolu
                    for(Transaction t : maxFeeTxs){
                        queue.remove(t);
                    }
                    sub.transactions.addAll(maxFeeTxs); //pridanie transakcií do aktuálnej množiny
                }
                // vloženie všetkých detí do radu
                queue.addAll(curNode.children);
            }
        }

        sub.pool = new UTXOPool(utxo);
        return sub;
    }

    /**
     * Spracováva každú epochu (iteráciu) prijímaním neusporiadaného radu navrhovaných
     * transakcií, kontroluje správnosť každej transakcie, vracia pole vzájomne
     * platných prijatých transakcií a aktualizuje aktuálny UTXO pool podľa potreby.
     */
    public Transaction[] handler(Transaction[] possibleTxs) {
        // IMPLEMENTOVAŤ
        // mapovanie transakcií na Node
        HashMap<Transaction, Node> allNodes = new HashMap<>();
        // mapovanie UTXO na Transakcie
        HashMap<UTXO, Transaction> txHashMap = new HashMap<>();
        ArrayList<Node> withoutParents = new ArrayList<>(); //pole Nodov bez rodičov
        UTXOPool allUTXO = new UTXOPool(utxo);  //UTXOPool obsahujúci všetky UTXO
        // prechádzanie všetkých transakcií
        for(Transaction tx : possibleTxs){
            ArrayList<Transaction.Output> outputs = tx.getOutputs();
            int index = 0;
            // vloženie všetkých Outputov do UTXOPoolu
            for(Transaction.Output op : outputs){
                UTXO u = new UTXO(tx.getHash(), index);
                allUTXO.addUTXO(u, op);
                index++;
            }
        }
        // prechádzanie všetkých transakcií
        for(Transaction tx : possibleTxs){
            if(allNodes.containsKey(tx))
                continue;

            int index = 0;
            for(Transaction.Output op : tx.getOutputs()){
                UTXO u = new UTXO(tx.getHash(), index);
                txHashMap.put(u, tx);
                index++;
            }

            Node n = new Node(tx, getFee(tx, allUTXO)); //vytvorenie Nodu
            allNodes.put(tx, n);    //vloženie do HashMap
            withoutParents.add(n);  //pridanie uzla medzi uzly bez rodičov
        }
        // prechádzanie všetkých transakcií
        for(Transaction tx : possibleTxs){
            ArrayList<Transaction.Input> inputs = tx.getInputs();
            // prechádzanie všetkých inputov všetkých transakcií
            for(Transaction.Input ip : inputs){
                UTXO u = new UTXO(ip.prevTxHash, ip.outputIndex);
                Node curNode = allNodes.get(tx);        // vybratie zodpovedajúcej transakcie
                Node parentNode = allNodes.get(txHashMap.get(u));   // rodicovsky uzol
                if(parentNode == null)
                    continue;
                // aktualizovanie rodičov aktuálneho uzla
                if(!curNode.parents.contains(parentNode))
                    curNode.addParent(parentNode);
                // aktualizovanie detí rodičovského uzla
                if(!parentNode.children.contains(curNode))
                    parentNode.addChild(curNode);

                withoutParents.remove(curNode);     // odstránenie uzla z množiny bez rodičov
            }
        }
        // množina spracovaných transakcií
        HashSet<Node> processedTransactions = new HashSet<>();
        // volanie rekurzívnej metódy
        Subset sub = handleTxs(withoutParents, utxo, processedTransactions);

        return sub.transactions.toArray(new Transaction[0]);
    }
}
