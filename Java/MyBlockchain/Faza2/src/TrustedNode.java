// Meno študenta: Tomáš Brček
import java.util.*;

/* TrustedNode označuje uzol, ktorý dodržuje pravidlá (nie je byzantský) */
public class TrustedNode implements Node {

    private double p_graph;
    private double p_malicious;
    private double p_tXDistribution;
    private int numRounds;

    private boolean[] curFollowees;

    private Set<Transaction> pendingTxs, consensusTxs;
    private HashMap<Integer, HashSet<Transaction>> previousTxs;
    private HashMap<Integer, Integer> counter;

    private HashSet<Integer> susFollowees;
    private int curRound;

    // konštruktor triedy TrustedNode
    public TrustedNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.p_graph = p_graph;
        this.p_malicious = p_malicious;
        this.p_tXDistribution = p_txDistribution;
        this.numRounds = numRounds;
        this.curRound = 0;
        this.counter = new HashMap<>();
        this.previousTxs = new HashMap<>();
    }

    // metóda, ktorá nastaví uzlu jeho nasledovníkov
    public void followeesSet(boolean[] followees) {
        this.curFollowees = followees;
        this.susFollowees = new HashSet<>();     // množina podozrivých uzlov
        for(int i = 0; i < followees.length; i++){
            previousTxs.put(i, new HashSet<>());    // inicializácia množiny predchádzajúcich transakcií pre každý uzol
            counter.put(i, 0);          // pocitanie koľkokrát daný Node odoslal tie isté transakcie
        }
    }

    // metóda, ktorá nastaví transakcie
    public void pendingTransactionSet(Set<Transaction> pendingTransactions) {
        this.pendingTxs = pendingTransactions;
        this.consensusTxs = new HashSet<Transaction>();
    }

    // vyčistenie množiny posledných odoslaných transakcii pre každý uzol
    private void clearPreviousTxs(){
        for(int i = 0; i < curFollowees.length; i++){
            previousTxs.get(i).clear();
        }
    }

    // metóda, ktorá kontroluje, či sa dve HashMap rovnajú a prípadne dopĺňa množinu byzantských uzlov
    private HashSet<Integer> hashMapEquals(HashSet<Integer> suspicious, HashMap<Integer, HashSet<Transaction>> hm1, HashMap<Integer, HashSet<Transaction>> hm2){
        for(int i = 0; i < hm1.size(); i++){
            if(hm1.get(i).equals(hm2.get(i))){
                counter.put(i, counter.getOrDefault(i, 0) + 1);
                if(counter.get(i) >= 3 && curRound <= 3)    // v prípade, že uzol poslal v prvých troch kolách rovnaké transakcie je považovaný za podozrivý
                    suspicious.add(i);
            }
        }
        return suspicious;
    }

    // metóda, ktorá deteguje byzantské uzly
    private HashSet<Integer> checkByzantineNodes(ArrayList<Integer[]> candidates){
        HashSet<Integer> suspicious = new HashSet<>(this.susFollowees);     // podozrivé uzly
        Set<Integer> sendingNodes = new HashSet<>();                        // odosielatelia
        HashMap<Integer, HashSet<Transaction>> sentTxs = new HashMap<>();   //prijaté transakcie

        for(int i = 0; i < curFollowees.length; i++){
            sentTxs.put(i, new HashSet<>());
        }

        for (Integer[] candidate : candidates) {
            sendingNodes.add(candidate[1]);
            sentTxs.get(candidate[1]).add(new Transaction(candidate[0]));
        }

        for (int i = 0; i < curFollowees.length; i++) {
            if (curFollowees[i]){
                if(!sendingNodes.contains(i)){
                    suspicious.add(i);
                }
            }
        }
        return hashMapEquals(suspicious, previousTxs, sentTxs);
    }

    // metóda, ktorá odosiela transakcie susedným uzlom
    public Set<Transaction> followersSend() {
        this.curRound++;
        Set<Transaction> transactions;
        // ak už prebehol definovaný počet kôl, vráti transakcie, na ktorých bol dosiahnutý konsenzus
        if(curRound < numRounds){
            transactions = new HashSet<>(pendingTxs);
        }else{
            return new HashSet<>(consensusTxs);
        }
        // vyprázdnenie množín
        pendingTxs.clear();
        consensusTxs.clear();
        return transactions;
    }

    // metóda, ktorá prijíma transakcie od sledujúcich uzlov
    public void followeesReceive(ArrayList<Integer[]> candidates) {

        this.susFollowees = checkByzantineNodes(candidates);

        clearPreviousTxs();

        for (Integer[] candidate : candidates) {
            int senderIndex = candidate[1];
            this.previousTxs.get(candidate[1]).add(new Transaction(candidate[0]));

            Transaction tx = new Transaction(candidate[0]);
            if (!susFollowees.contains(senderIndex)) {
                pendingTxs.add(tx);
                consensusTxs.add(tx);
            }
        }
    }
}
