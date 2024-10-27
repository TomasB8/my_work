/*
* Main pre zadanie 1 na predmete DMBLOCK - faza 1
*/

import java.math.BigInteger;
import java.security.*;

public class Main_faza1 {

    public static void main(String[] args)
            throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        /*
         * Vygeneruje páry kľúčov pre Alicu a Boba
         */
        byte[] key_bob = new byte[32];
        byte[] key_alice = new byte[32];

        for (int i = 0; i < 32; i++) {
           key_bob[i] = (byte) 1;
           key_alice [i] = (byte) 0;
        }

        PRGen prGen_bob = new PRGen(key_bob);
        PRGen prGen_alice = new PRGen(key_alice);
      
        RSAKeyPair pk_bob = new RSAKeyPair(prGen_bob, 265);
        RSAKeyPair pk_alice = new RSAKeyPair(prGen_alice, 265);

        /*
         * Vytvor root transakciu:
         *
         * Generovanie root transakcie tx "z ničoho," takže Bob vlastní mincu s hodnotou 10.
         * Slovom "z ničoho" chceme vyjadriť, že táto tx nebude overená, len ju potrebujeme, 
         * aby sme mali nejakú transakciu a výstup, ktorý potom môžeme vložiť do UTXOPool,
         * ktorý bude následne posunutý HandleTxs.
         */
        Tx tx = new Tx();
        tx.addOutput(10,  pk_bob.getPublicKey());

        // Táto hodnota nemá žiadny význam, ale tx.getDataToSign(0) k nej pristúpi v 
        // prevTxHash;
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);

        tx.signTx(pk_bob.getPrivateKey(), 0);

        /*
         * Nastav UTXOPool
         */
        // Výstup root transakcie je inicializačný neminutý (unspent) výstup.
        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));
        
        /** 
         * 
         * Vytvor test transakciu
         */
        Tx tx2 = new Tx();
        
        // Transaction.Output z tx na pozícii 0 má hodnotu 10
        tx2.addInput(tx.getHash(), 0);

        // Rozdelíme coin hodnoty 10 na 3 coiny a všetky pošleme pre jednoduchosť 
        // na rovnakú adresu
        // (Alice)
        tx2.addOutput(5, pk_alice.getPublicKey());
        tx2.addOutput(3, pk_alice.getPublicKey());
        tx2.addOutput(2, pk_alice.getPublicKey());
         // Všimnite si, že v reálnom svete by sa pre hodnoty používali typy s fixed-point, 
         // nie double. Doubles predstavujú floating-point, a teda majú aj chyby 
         // zaokrúhľovania. Tento typ by mal byť napríklad BigInteger a pracovať
         // s najmenšími zlomkami mincí (satoshi v Bitcoine).

         // Existuje iba jeden (na pozícii 0) Transaction.Input v tx2
         // a obsahuje mincu od Boba, preto musím túto tx podpísať s
         // privátnym kľúčom Boba
        tx2.signTx(pk_bob.getPrivateKey(), 0);
        System.out.println("TX2 hash: " + tx2.getHash());

        utxo = new UTXO(tx2.getHash(), 0);
        utxoPool.addUTXO(utxo, tx2.getOutput(0));
        utxo = new UTXO(tx2.getHash(), 1);
        utxoPool.addUTXO(utxo, tx2.getOutput(1));
        utxo = new UTXO(tx2.getHash(), 2);
        utxoPool.addUTXO(utxo, tx2.getOutput(2));

        Tx tx3 = new Tx();
        tx3.addInput(tx2.getHash(), 0);
        tx3.addInput(tx2.getHash(), 2);
        tx3.addOutput(1, pk_bob.getPublicKey());
        tx3.addOutput(3, pk_bob.getPublicKey());
        tx3.signTx(pk_alice.getPrivateKey(), 0);
        tx3.signTx(pk_alice.getPrivateKey(), 1);
        System.out.println("TX3 hash: " + tx3.getHash());
        System.out.println("TX2 input hash: " + tx3.getInput(0).prevTxHash);
        System.out.println("TX2 input hash: " + tx3.getInput(1).prevTxHash);

        utxo = new UTXO(tx3.getHash(), 0);
        utxoPool.addUTXO(utxo, tx3.getOutput(0));
        utxo = new UTXO(tx3.getHash(), 1);
        utxoPool.addUTXO(utxo, tx3.getOutput(1));

        Tx tx4 = new Tx();
        tx4.addInput(tx3.getHash(), 1);
        tx4.addOutput(1, pk_bob.getPublicKey());
        tx4.addOutput(2, pk_alice.getPublicKey());
        tx4.signTx(pk_bob.getPrivateKey(), 0);
        System.out.println("TX4 hash: " + tx4.getHash());

        utxo = new UTXO(tx4.getHash(), 0);
        utxoPool.addUTXO(utxo, tx4.getOutput(0));
        utxo = new UTXO(tx4.getHash(), 1);
        utxoPool.addUTXO(utxo, tx4.getOutput(1));

        Tx tx5 = new Tx();
        tx5.addInput(tx3.getHash(), 1);
        tx5.addOutput(1, pk_alice.getPublicKey());
        tx5.addOutput(1, pk_alice.getPublicKey());
        tx5.signTx(pk_bob.getPrivateKey(), 0);
        System.out.println("TX5 hash: " + tx5.getHash());


        /** 
         * Spusti test
         */
        // Pamätajte, že utxoPool obsahuje jeden neminutý Transaction.Output, 
        // ktorým je minca od Boba.
        HandleTxs handleTxs = new HandleTxs(utxoPool);
        System.out.println("handleTxs.txIsValid(tx2) returns: " + handleTxs.txIsValid(tx2));
        System.out.println("handleTxs.txIsValid(tx3) returns: " + handleTxs.txIsValid(tx3));
        System.out.println("handleTxs.txIsValid(tx4) returns: " + handleTxs.txIsValid(tx4));
        System.out.println("handleTxs.txIsValid(tx5) returns: " + handleTxs.txIsValid(tx5));
        System.out.println("handleTxs.handler(new Transaction[]{tx2, tx3, tx4}) returns: "
                + handleTxs.handler(new Transaction[]{tx4, tx3, tx2, tx5}).length + " transaction(s)");
    }

    public static class Tx extends Transaction {
        public void signTx(RSAKey sk, int input) throws SignatureException {
            byte[] sig = null;
            try {
                sig = sk.sign(this.getDataToSign(input));
            } catch (NullPointerException e) {
                throw new RuntimeException(e);
            }
            this.addSignature(sig, input);
            // Poznámka: táto funkcia je nevhodne pomenovaná a v skutočnosti
            // by nemala overridovať metódu finalize Java garbage kolektoru.
            this.finalize();
        }
    }
}