import java.util.*;

// Meno študenta: Tomáš Brček
public class HandleTxs {

    // vhniezdená trieda reprezentujúca jeden uzol v grafe
    static class Node {
        private Transaction tx;
        private ArrayList<Node> children;   // nasledovnici
        private ArrayList<Node> parents;    // predchodcovia

        public Node(Transaction tx){
            this.tx = tx;
            this.children = new ArrayList<>();
            this.parents = new ArrayList<>();
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

    public UTXOPool utxo;

    /**
     * Vytvorí verejný ledger (účtovnú knihu), ktorého aktuálny UTXOPool (zbierka nevyčerpaných
     * transakčných výstupov) je {@code utxoPool}. Malo by to vytvoriť bezpečnú kópiu
     * utxoPool pomocou konštruktora UTXOPool (UTXOPool uPool).
     */
    public HandleTxs(UTXOPool utxoPool) {
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
        ArrayList<Node> withoutParents = new ArrayList<>();     //pole Nodov bez rodičov
        // prechádzanie všetkých transakcií
        for(Transaction tx : possibleTxs){
            if(allNodes.containsKey(tx))
                continue;

            Node n = new Node(tx);      //vytvorenie Nodu
            allNodes.put(tx, n);        //vloženie do HashMap
            withoutParents.add(n);      //pridanie uzla medzi uzly bez rodičov
            int index = 0;
            // vloženie každého UTXO do mapy spolu s Transakciou
            for(Transaction.Output op : tx.getOutputs()){
                UTXO u = new UTXO(tx.getHash(), index);
                txHashMap.put(u, tx);
                index++;
            }
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

        // finalne transakcie
        ArrayList<Transaction> transactions = new ArrayList<>();
        ArrayList<Node> queue = new ArrayList<>(withoutParents);    // vytvorenie radu
        while(!queue.isEmpty()){
            Node curNode = queue.remove(0);     // vyýber prvku z radu

            // kontrola transakcie v aktuálnom uzle
            Transaction tx = curNode.tx;
            if (txIsValid(curNode.tx)) {
                transactions.add(tx);
                updateUTXO(tx);
                queue.addAll(curNode.children);     // vloženie všetkých detí do radu
            }
        }

        return transactions.toArray(new Transaction[0]);
    }
}
