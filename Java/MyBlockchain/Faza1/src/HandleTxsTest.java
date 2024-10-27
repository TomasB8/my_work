import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.SignatureException;

public class HandleTxsTest {

    RSAKeyPair pk_bob;
    RSAKeyPair pk_alice;
    RSAKeyPair pk_cyril;

    public void initializeTransactions() {
        byte[] key_bob = new byte[32];
        byte[] key_alice = new byte[32];
        byte[] key_cyril = new byte[32];

        for (int i = 0; i < 32; i++) {
            key_bob[i] = (byte) 1;
            key_alice [i] = (byte) 0;
            key_cyril [i] = (byte) 0;
        }

        PRGen prGen_bob = new PRGen(key_bob);
        PRGen prGen_alice = new PRGen(key_alice);
        PRGen prGen_cyril = new PRGen(key_cyril);

        this.pk_bob = new RSAKeyPair(prGen_bob, 265);
        this.pk_alice = new RSAKeyPair(prGen_alice, 265);
        this.pk_cyril = new RSAKeyPair(prGen_cyril, 265);
    }

    @Test
    public void testTxIsValidWithValidTransactions() throws SignatureException {
        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        HandleTxs handleTxs = new HandleTxs(utxoPool);

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublicKey());
        tx2.addOutput(3, pk_bob.getPublicKey());
        tx2.addOutput(2, pk_cyril.getPublicKey());
        tx2.signTx(pk_bob.getPrivateKey(), 0);

        assertTrue(handleTxs.txIsValid(tx2));
        handleTxs.updateUTXO(tx2);

        Tx tx3 = new Tx();
        tx3.addInput(tx2.getHash(), 0);
        tx3.addInput(tx2.getHash(), 2);
        tx3.addOutput(1, pk_bob.getPublicKey());
        tx3.addOutput(5, pk_alice.getPublicKey());
        tx3.signTx(pk_alice.getPrivateKey(), 0);
        tx3.signTx(pk_cyril.getPrivateKey(), 1);

        assertTrue(handleTxs.txIsValid(tx3));
        handleTxs.updateUTXO(tx3);

        Tx tx4 = new Tx();
        tx4.addInput(tx3.getHash(), 1);
        tx4.addOutput(3, pk_bob.getPublicKey());
        tx4.addOutput(2, pk_alice.getPublicKey());
        tx4.signTx(pk_alice.getPrivateKey(), 0);

        assertTrue(handleTxs.txIsValid(tx4));
    }

    @Test
    public void testTxIsValidWithWrongSignatures2() throws SignatureException {
        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublicKey());
        tx2.addOutput(3, pk_alice.getPublicKey());
        tx2.addOutput(2, pk_alice.getPublicKey());
        tx2.signTx(pk_alice.getPrivateKey(), 0);

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
        tx3.addOutput(5, pk_bob.getPublicKey());
        tx3.signTx(pk_alice.getPrivateKey(), 0);
        tx3.signTx(pk_bob.getPrivateKey(), 1);

        utxo = new UTXO(tx3.getHash(), 0);
        utxoPool.addUTXO(utxo, tx3.getOutput(0));
        utxo = new UTXO(tx3.getHash(), 1);
        utxoPool.addUTXO(utxo, tx3.getOutput(1));

        Tx tx4 = new Tx();
        tx4.addInput(tx3.getHash(), 1);
        tx4.addOutput(3, pk_bob.getPublicKey());
        tx4.addOutput(2, pk_alice.getPublicKey());
        tx4.signTx(pk_bob.getPrivateKey(), 0);

        HandleTxs handleTxs = new HandleTxs(utxoPool);
        assertFalse(handleTxs.txIsValid(tx2));
        assertFalse(handleTxs.txIsValid(tx3));
        assertTrue(handleTxs.txIsValid(tx4));
    }

    @Test
    public void testTxIsValidWithWrongSignatures() throws SignatureException {
        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublicKey());
        tx2.addOutput(3, pk_alice.getPublicKey());
        tx2.addOutput(2, pk_alice.getPublicKey());
        tx2.signTx(pk_alice.getPrivateKey(), 0);

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
        tx3.addOutput(5, pk_bob.getPublicKey());
        tx3.signTx(pk_alice.getPrivateKey(), 0);
        tx3.signTx(pk_bob.getPrivateKey(), 1);

        utxo = new UTXO(tx3.getHash(), 0);
        utxoPool.addUTXO(utxo, tx3.getOutput(0));
        utxo = new UTXO(tx3.getHash(), 1);
        utxoPool.addUTXO(utxo, tx3.getOutput(1));

        Tx tx4 = new Tx();
        tx4.addInput(tx3.getHash(), 1);
        tx4.addOutput(3, pk_bob.getPublicKey());
        tx4.addOutput(2, pk_alice.getPublicKey());
        tx4.signTx(pk_bob.getPrivateKey(), 0);

        HandleTxs handleTxs = new HandleTxs(utxoPool);
        assertFalse(handleTxs.txIsValid(tx2));
        assertFalse(handleTxs.txIsValid(tx3));
        assertTrue(handleTxs.txIsValid(tx4));
    }

    @Test
    public void testTxIsValidWithBiggerOutput() throws SignatureException {
        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublicKey());
        tx2.addOutput(3, pk_bob.getPublicKey());
        tx2.addOutput(2, pk_cyril.getPublicKey());
        tx2.signTx(pk_bob.getPrivateKey(), 0);

        utxo = new UTXO(tx2.getHash(), 0);
        utxoPool.addUTXO(utxo, tx2.getOutput(0));
        utxo = new UTXO(tx2.getHash(), 1);
        utxoPool.addUTXO(utxo, tx2.getOutput(1));
        utxo = new UTXO(tx2.getHash(), 2);
        utxoPool.addUTXO(utxo, tx2.getOutput(2));

        Tx tx3 = new Tx();
        tx3.addInput(tx2.getHash(), 0);
        tx3.addInput(tx2.getHash(), 2);
        tx3.addOutput(8, pk_bob.getPublicKey());
        tx3.addOutput(5, pk_alice.getPublicKey());
        tx3.signTx(pk_alice.getPrivateKey(), 0);
        tx3.signTx(pk_cyril.getPrivateKey(), 1);

        utxo = new UTXO(tx3.getHash(), 0);
        utxoPool.addUTXO(utxo, tx3.getOutput(0));
        utxo = new UTXO(tx3.getHash(), 1);
        utxoPool.addUTXO(utxo, tx3.getOutput(1));

        Tx tx4 = new Tx();
        tx4.addInput(tx3.getHash(), 1);
        tx4.addOutput(3, pk_bob.getPublicKey());
        tx4.addOutput(6, pk_alice.getPublicKey());
        tx4.signTx(pk_alice.getPrivateKey(), 0);

        HandleTxs handleTxs = new HandleTxs(utxoPool);
        assertTrue(handleTxs.txIsValid(tx2));
        assertFalse(handleTxs.txIsValid(tx3));
        assertFalse(handleTxs.txIsValid(tx4));
    }

    @Test
    public void testTxIsValidWithTxsNotInUTXO() throws SignatureException {
        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublicKey());
        tx2.addOutput(3, pk_bob.getPublicKey());
        tx2.addOutput(2, pk_cyril.getPublicKey());
        tx2.signTx(pk_bob.getPrivateKey(), 0);

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
        tx3.addOutput(5, pk_alice.getPublicKey());
        tx3.signTx(pk_alice.getPrivateKey(), 0);
        tx3.signTx(pk_cyril.getPrivateKey(), 1);

        utxo = new UTXO(tx3.getHash(), 0);
        utxoPool.addUTXO(utxo, tx3.getOutput(0));

        Tx tx4 = new Tx();
        tx4.addInput(tx3.getHash(), 1);
        tx4.addOutput(3, pk_bob.getPublicKey());
        tx4.addOutput(2, pk_alice.getPublicKey());
        tx4.signTx(pk_alice.getPrivateKey(), 0);

        HandleTxs handleTxs = new HandleTxs(utxoPool);
        assertTrue(handleTxs.txIsValid(tx2));
        assertTrue(handleTxs.txIsValid(tx3));
        assertFalse(handleTxs.txIsValid(tx4));
    }

    @Test
    public void testTxIsValidWithSameUTXO() throws SignatureException {
        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        HandleTxs handleTxs = new HandleTxs(utxoPool);

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublicKey());
        tx2.addOutput(3, pk_bob.getPublicKey());
        tx2.addOutput(2, pk_cyril.getPublicKey());
        tx2.signTx(pk_bob.getPrivateKey(), 0);

        assertTrue(handleTxs.txIsValid(tx2));
        handleTxs.updateUTXO(tx2);

        Tx tx3 = new Tx();
        tx3.addInput(tx2.getHash(), 0);
        tx3.addInput(tx2.getHash(), 2);
        tx3.addOutput(1, pk_bob.getPublicKey());
        tx3.addOutput(5, pk_alice.getPublicKey());
        tx3.signTx(pk_alice.getPrivateKey(), 0);
        tx3.signTx(pk_cyril.getPrivateKey(), 1);

        assertTrue(handleTxs.txIsValid(tx3));
        handleTxs.updateUTXO(tx3);

        Tx tx4 = new Tx();
        tx4.addInput(tx2.getHash(), 0);
        tx4.addOutput(3, pk_bob.getPublicKey());
        tx4.addOutput(2, pk_alice.getPublicKey());
        tx4.signTx(pk_alice.getPrivateKey(), 0);

        assertFalse(handleTxs.txIsValid(tx4));

        Tx tx5 = new Tx();
        tx5.addInput(tx3.getHash(), 1);
        tx5.addOutput(3, pk_bob.getPublicKey());
        tx5.addOutput(2, pk_alice.getPublicKey());
        tx5.signTx(pk_alice.getPrivateKey(), 0);

        assertTrue(handleTxs.txIsValid(tx5));
        handleTxs.updateUTXO(tx5);

        Tx tx6 = new Tx();
        tx6.addInput(tx3.getHash(), 1);
        tx6.addInput(tx5.getHash(), 0);
        tx6.addOutput(3, pk_bob.getPublicKey());
        tx6.addOutput(2, pk_alice.getPublicKey());
        tx6.signTx(pk_alice.getPrivateKey(), 0);
        tx6.signTx(pk_bob.getPrivateKey(), 1);

        assertFalse(handleTxs.txIsValid(tx6));
    }

    @Test
    public void testTxIsValidWithOutputsBelowZero() throws SignatureException {
        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        HandleTxs handleTxs = new HandleTxs(utxoPool);

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublicKey());
        tx2.addOutput(3, pk_bob.getPublicKey());
        tx2.addOutput(2, pk_cyril.getPublicKey());
        tx2.signTx(pk_bob.getPrivateKey(), 0);

        assertTrue(handleTxs.txIsValid(tx2));
        handleTxs.updateUTXO(tx2);

        Tx tx3 = new Tx();
        tx3.addInput(tx2.getHash(), 0);
        tx3.addInput(tx2.getHash(), 2);
        tx3.addOutput(1, pk_bob.getPublicKey());
        tx3.addOutput(5, pk_alice.getPublicKey());
        tx3.signTx(pk_alice.getPrivateKey(), 0);
        tx3.signTx(pk_cyril.getPrivateKey(), 1);

        assertTrue(handleTxs.txIsValid(tx3));
        handleTxs.updateUTXO(tx3);

        Tx tx4 = new Tx();
        tx4.addInput(tx3.getHash(), 1);
        tx4.addOutput(-3, pk_bob.getPublicKey());
        tx4.addOutput(2, pk_alice.getPublicKey());
        tx4.signTx(pk_alice.getPrivateKey(), 0);

        assertFalse(handleTxs.txIsValid(tx4));
    }

    @Test
    public void testHandleTxsWithRightTxs() throws SignatureException {

        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        HandleTxs handleTxs = new HandleTxs(utxoPool);

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublicKey());  //
        tx2.addOutput(3, pk_alice.getPublicKey());  //
        tx2.addOutput(2, pk_alice.getPublicKey());  //
        tx2.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx3 = new Tx();
        tx3.addInput(tx2.getHash(), 0);
        tx3.addInput(tx2.getHash(), 2);
        tx3.addOutput(1, pk_bob.getPublicKey());    //
        tx3.addOutput(5, pk_bob.getPublicKey());    //
        tx3.signTx(pk_alice.getPrivateKey(), 0);
        tx3.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx4 = new Tx();
        tx4.addInput(tx3.getHash(), 1);
        tx4.addOutput(3, pk_bob.getPublicKey());    //
        tx4.addOutput(2, pk_alice.getPublicKey());
        tx4.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx5 = new Tx();
        tx5.addInput(tx4.getHash(), 1);
        tx5.addInput(tx2.getHash(), 1);
        tx5.addOutput(3, pk_bob.getPublicKey());   //
        tx5.addOutput(2, pk_alice.getPublicKey());  //
        tx5.signTx(pk_alice.getPrivateKey(), 0);
        tx5.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx6 = new Tx();
        tx6.addInput(tx5.getHash(), 0);
        tx6.addOutput(1, pk_bob.getPublicKey());
        tx6.addOutput(2, pk_alice.getPublicKey());  //
        tx6.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx7 = new Tx();
        tx7.addInput(tx5.getHash(), 1);
        tx7.addOutput(1, pk_bob.getPublicKey());
        tx7.addOutput(1, pk_alice.getPublicKey());
        tx7.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx8 = new Tx();
        tx8.addInput(tx3.getHash(), 0);
        tx8.addInput(tx4.getHash(), 0);
        tx8.addInput(tx6.getHash(), 1);
        tx8.addOutput(3, pk_bob.getPublicKey());
        tx8.addOutput(2, pk_alice.getPublicKey());
        tx8.signTx(pk_bob.getPrivateKey(), 0);
        tx8.signTx(pk_bob.getPrivateKey(), 1);
        tx8.signTx(pk_alice.getPrivateKey(), 2);

        assertEquals(7, handleTxs.handler(new Transaction[]{tx2, tx3, tx4, tx5, tx6, tx7, tx8}).length);
    }

    @Test
    public void testHandleTxsWithWrongSignatures() throws SignatureException {

        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        HandleTxs handleTxs = new HandleTxs(utxoPool);

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublicKey());  //
        tx2.addOutput(3, pk_alice.getPublicKey());  //
        tx2.addOutput(2, pk_alice.getPublicKey());  //
        tx2.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx3 = new Tx();
        tx3.addInput(tx2.getHash(), 0);
        tx3.addInput(tx2.getHash(), 2);
        tx3.addOutput(1, pk_bob.getPublicKey());
        tx3.addOutput(5, pk_bob.getPublicKey());    //
        tx3.signTx(pk_alice.getPrivateKey(), 0);
        tx3.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx4 = new Tx();
        tx4.addInput(tx3.getHash(), 1);
        tx4.addOutput(3, pk_bob.getPublicKey());
        tx4.addOutput(2, pk_alice.getPublicKey());
        tx4.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx5 = new Tx();
        tx5.addInput(tx4.getHash(), 1);
        tx5.addInput(tx2.getHash(), 1);
        tx5.addOutput(3, pk_bob.getPublicKey());   //
        tx5.addOutput(2, pk_alice.getPublicKey());
        tx5.signTx(pk_alice.getPrivateKey(), 0);
        tx5.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx6 = new Tx();
        tx6.addInput(tx5.getHash(), 0);
        tx6.addOutput(1, pk_bob.getPublicKey());
        tx6.addOutput(2, pk_alice.getPublicKey());
        tx6.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx7 = new Tx();
        tx7.addInput(tx5.getHash(), 0);
        tx7.addOutput(1, pk_bob.getPublicKey());
        tx7.addOutput(2, pk_alice.getPublicKey());
        tx7.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx8 = new Tx();
        tx8.addInput(tx3.getHash(), 0);
        tx8.addInput(tx5.getHash(), 1);
        tx8.addOutput(2, pk_bob.getPublicKey());
        tx8.addOutput(2, pk_alice.getPublicKey());
        tx8.signTx(pk_cyril.getPrivateKey(), 0);
        tx8.signTx(pk_bob.getPrivateKey(), 1);

        assertEquals(5, handleTxs.handler(new Transaction[]{tx2, tx3, tx4, tx5, tx6, tx7, tx8}).length);
    }

    @Test
    public void testHandleTxsWithBiggerOutput() throws SignatureException {

        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        HandleTxs handleTxs = new HandleTxs(utxoPool);

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublicKey());  //
        tx2.addOutput(3, pk_alice.getPublicKey());  //
        tx2.addOutput(2, pk_alice.getPublicKey());  //
        tx2.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx3 = new Tx();
        tx3.addInput(tx2.getHash(), 0);
        tx3.addInput(tx2.getHash(), 2);
        tx3.addOutput(1, pk_bob.getPublicKey());    //
        tx3.addOutput(5, pk_bob.getPublicKey());    //
        tx3.signTx(pk_alice.getPrivateKey(), 0);
        tx3.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx4 = new Tx();
        tx4.addInput(tx3.getHash(), 1);
        tx4.addOutput(3, pk_bob.getPublicKey());    //
        tx4.addOutput(2, pk_alice.getPublicKey());
        tx4.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx5 = new Tx();
        tx5.addInput(tx4.getHash(), 1);
        tx5.addInput(tx2.getHash(), 1);
        tx5.addOutput(3, pk_bob.getPublicKey());   //
        tx5.addOutput(2, pk_alice.getPublicKey());  //
        tx5.signTx(pk_alice.getPrivateKey(), 0);
        tx5.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx6 = new Tx();
        tx6.addInput(tx5.getHash(), 0);
        tx6.addOutput(1, pk_bob.getPublicKey());
        tx6.addOutput(9, pk_alice.getPublicKey());  //
        tx6.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx7 = new Tx();
        tx7.addInput(tx5.getHash(), 1);
        tx7.addOutput(1, pk_bob.getPublicKey());
        tx7.addOutput(1, pk_alice.getPublicKey());
        tx7.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx8 = new Tx();
        tx8.addInput(tx3.getHash(), 0);
        tx8.addInput(tx4.getHash(), 0);
        tx8.addInput(tx6.getHash(), 1);
        tx8.addOutput(3, pk_bob.getPublicKey());
        tx8.addOutput(5, pk_alice.getPublicKey());
        tx8.signTx(pk_bob.getPrivateKey(), 0);
        tx8.signTx(pk_bob.getPrivateKey(), 1);
        tx8.signTx(pk_alice.getPrivateKey(), 2);

        assertEquals(5, handleTxs.handler(new Transaction[]{tx2, tx3, tx4, tx5, tx6, tx7, tx8}).length);
    }

    @Test
    public void testHandleTxsWithDoubleSpends() throws SignatureException {

        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());      //
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        HandleTxs handleTxs = new HandleTxs(utxoPool);

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublicKey());  //
        tx2.addOutput(2, pk_alice.getPublicKey());  //
        tx2.addOutput(2, pk_alice.getPublicKey());  //
        tx2.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx3 = new Tx();
        tx3.addInput(tx2.getHash(), 0);
        tx3.addInput(tx2.getHash(), 2);
        tx3.addOutput(1, pk_bob.getPublicKey());    //
        tx3.addOutput(4, pk_bob.getPublicKey());    //
        tx3.signTx(pk_alice.getPrivateKey(), 0);
        tx3.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx4 = new Tx();
        tx4.addInput(tx3.getHash(), 1);
        tx4.addOutput(2, pk_bob.getPublicKey());    //
        tx4.addOutput(2, pk_alice.getPublicKey());  //  //
        tx4.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx5 = new Tx();
        tx5.addInput(tx4.getHash(), 1);
        tx5.addInput(tx2.getHash(), 1);
        tx5.addOutput(1, pk_bob.getPublicKey());
        tx5.addOutput(1, pk_alice.getPublicKey());      //  //
        tx5.signTx(pk_alice.getPrivateKey(), 0);
        tx5.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx6 = new Tx();
        tx6.addInput(tx4.getHash(), 1);
        tx6.addOutput(1, pk_bob.getPublicKey());
        tx6.addOutput(1, pk_alice.getPublicKey());  //
        tx6.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx7 = new Tx();
        tx7.addInput(tx5.getHash(), 1);
        tx7.addInput(tx5.getHash(), 1);
        tx7.addOutput(1, pk_bob.getPublicKey());
        tx7.addOutput(1, pk_alice.getPublicKey());
        tx7.signTx(pk_alice.getPrivateKey(), 0);
        tx7.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx8 = new Tx();
        tx8.addInput(tx3.getHash(), 0);
        tx8.addInput(tx4.getHash(), 0);
        tx8.addInput(tx6.getHash(), 1);
        tx8.addOutput(1, pk_bob.getPublicKey());
        tx8.addOutput(2, pk_alice.getPublicKey());
        tx8.signTx(pk_bob.getPrivateKey(), 0);
        tx8.signTx(pk_bob.getPrivateKey(), 1);
        tx8.signTx(pk_alice.getPrivateKey(), 2);

        assertEquals(4, handleTxs.handler(new Transaction[]{tx2, tx3, tx4, tx5, tx6, tx7, tx8}).length);
    }

    @Test
    public void testHandleTxsWithWrongOrder() throws SignatureException {

        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        HandleTxs handleTxs = new HandleTxs(utxoPool);

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublicKey());  //
        tx2.addOutput(3, pk_alice.getPublicKey());  //
        tx2.addOutput(2, pk_alice.getPublicKey());  //
        tx2.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx3 = new Tx();
        tx3.addInput(tx2.getHash(), 0);
        tx3.addInput(tx2.getHash(), 2);
        tx3.addOutput(1, pk_bob.getPublicKey());    //
        tx3.addOutput(5, pk_bob.getPublicKey());    //
        tx3.signTx(pk_alice.getPrivateKey(), 0);
        tx3.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx4 = new Tx();
        tx4.addInput(tx3.getHash(), 1);
        tx4.addOutput(3, pk_bob.getPublicKey());    //
        tx4.addOutput(2, pk_alice.getPublicKey());
        tx4.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx5 = new Tx();
        tx5.addInput(tx4.getHash(), 1);
        tx5.addInput(tx2.getHash(), 1);
        tx5.addOutput(3, pk_bob.getPublicKey());   //
        tx5.addOutput(2, pk_alice.getPublicKey());  //
        tx5.signTx(pk_alice.getPrivateKey(), 0);
        tx5.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx6 = new Tx();
        tx6.addInput(tx5.getHash(), 0);
        tx6.addOutput(1, pk_bob.getPublicKey());
        tx6.addOutput(2, pk_alice.getPublicKey());  //
        tx6.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx7 = new Tx();
        tx7.addInput(tx5.getHash(), 1);
        tx7.addOutput(1, pk_bob.getPublicKey());
        tx7.addOutput(1, pk_alice.getPublicKey());
        tx7.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx8 = new Tx();
        tx8.addInput(tx3.getHash(), 0);
        tx8.addInput(tx4.getHash(), 0);
        tx8.addInput(tx6.getHash(), 1);
        tx8.addOutput(3, pk_bob.getPublicKey());
        tx8.addOutput(2, pk_alice.getPublicKey());
        tx8.signTx(pk_bob.getPrivateKey(), 0);
        tx8.signTx(pk_bob.getPrivateKey(), 1);
        tx8.signTx(pk_alice.getPrivateKey(), 2);

        assertEquals(7, handleTxs.handler(new Transaction[]{tx8, tx3, tx2, tx7, tx6, tx5, tx4}).length);
    }

    @Test
    public void testHandleTxsWithWrongUTXO() throws SignatureException {

        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        HandleTxs handleTxs = new HandleTxs(utxoPool);

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublicKey());  //
        tx2.addOutput(3, pk_alice.getPublicKey());  //
        tx2.addOutput(2, pk_alice.getPublicKey());  //
        tx2.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx3 = new Tx();
        tx3.addInput(tx2.getHash(), 0);
        tx3.addInput(tx2.getHash(), 2);
        tx3.addOutput(1, pk_bob.getPublicKey());    //
        tx3.addOutput(5, pk_bob.getPublicKey());    //
        tx3.signTx(pk_alice.getPrivateKey(), 0);
        tx3.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx4 = new Tx();
        tx4.addInput(tx3.getHash(), 1);
        tx4.addOutput(3, pk_bob.getPublicKey());    //
        tx4.addOutput(2, pk_alice.getPublicKey());
        tx4.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx5 = new Tx();
        tx5.addInput(tx4.getHash(), 1);
        tx5.addInput(tx2.getHash(), 1);
        tx5.addOutput(3, pk_bob.getPublicKey());   //
        tx5.addOutput(2, pk_alice.getPublicKey());  //
        tx5.signTx(pk_alice.getPrivateKey(), 0);
        tx5.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx6 = new Tx();
        tx6.addInput(tx5.getHash(), 2);
        tx6.addOutput(1, pk_bob.getPublicKey());
        tx6.addOutput(2, pk_alice.getPublicKey());  //
        tx6.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx7 = new Tx();
        tx7.addInput(tx5.getHash(), 4);
        tx7.addOutput(1, pk_bob.getPublicKey());
        tx7.addOutput(1, pk_alice.getPublicKey());
        tx7.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx8 = new Tx();
        tx8.addInput(tx3.getHash(), 0);
        tx8.addInput(tx4.getHash(), 0);
        tx8.addInput(tx6.getHash(), 1);
        tx8.addOutput(3, pk_bob.getPublicKey());
        tx8.addOutput(2, pk_alice.getPublicKey());
        tx8.signTx(pk_bob.getPrivateKey(), 0);
        tx8.signTx(pk_bob.getPrivateKey(), 1);
        tx8.signTx(pk_alice.getPrivateKey(), 2);

        assertEquals(4, handleTxs.handler(new Transaction[]{tx2, tx3, tx4, tx5, tx6, tx7, tx8}).length);
    }

    @Test
    public void testHandleTxsWithCallingSameTxs() throws SignatureException {

        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        HandleTxs handleTxs = new HandleTxs(utxoPool);

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublicKey());  //
        tx2.addOutput(3, pk_alice.getPublicKey());  //
        tx2.addOutput(2, pk_alice.getPublicKey());  //
        tx2.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx3 = new Tx();
        tx3.addInput(tx2.getHash(), 0);
        tx3.addInput(tx2.getHash(), 2);
        tx3.addOutput(1, pk_bob.getPublicKey());    //
        tx3.addOutput(5, pk_bob.getPublicKey());    //
        tx3.signTx(pk_alice.getPrivateKey(), 0);
        tx3.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx4 = new Tx();
        tx4.addInput(tx3.getHash(), 1);
        tx4.addOutput(3, pk_bob.getPublicKey());    //
        tx4.addOutput(2, pk_alice.getPublicKey());
        tx4.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx5 = new Tx();
        tx5.addInput(tx4.getHash(), 1);
        tx5.addInput(tx2.getHash(), 1);
        tx5.addOutput(3, pk_bob.getPublicKey());   //
        tx5.addOutput(2, pk_alice.getPublicKey());  //
        tx5.signTx(pk_alice.getPrivateKey(), 0);
        tx5.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx6 = new Tx();
        tx6.addInput(tx5.getHash(), 0);
        tx6.addOutput(1, pk_bob.getPublicKey());
        tx6.addOutput(2, pk_alice.getPublicKey());  //
        tx6.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx7 = new Tx();
        tx7.addInput(tx5.getHash(), 1);
        tx7.addOutput(1, pk_bob.getPublicKey());
        tx7.addOutput(1, pk_alice.getPublicKey());
        tx7.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx8 = new Tx();
        tx8.addInput(tx3.getHash(), 0);
        tx8.addInput(tx4.getHash(), 0);
        tx8.addInput(tx6.getHash(), 1);
        tx8.addOutput(3, pk_bob.getPublicKey());
        tx8.addOutput(2, pk_alice.getPublicKey());
        tx8.signTx(pk_bob.getPrivateKey(), 0);
        tx8.signTx(pk_bob.getPrivateKey(), 1);
        tx8.signTx(pk_alice.getPrivateKey(), 2);

        assertEquals(7, handleTxs.handler(new Transaction[]{tx2, tx3, tx2, tx4, tx5, tx5, tx6, tx7, tx8, tx4}).length);
    }

    @Test
    public void testMaxFeeHandleTxs1() throws SignatureException {

        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());      //
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        MaxFeeHandleTxs handleTxs = new MaxFeeHandleTxs(utxoPool);

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(2, pk_alice.getPublicKey());  //
        tx2.addOutput(5, pk_alice.getPublicKey());  //
        tx2.addOutput(3, pk_alice.getPublicKey());  //
        tx2.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx3 = new Tx();
        tx3.addInput(tx2.getHash(), 0);
        tx3.addOutput(1, pk_bob.getPublicKey());    //
        tx3.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx4 = new Tx();
        tx4.addInput(tx2.getHash(), 1);
        tx4.addOutput(2, pk_bob.getPublicKey());    //
        tx4.addOutput(2, pk_alice.getPublicKey());  //  //
        tx4.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx5 = new Tx();
        tx5.addInput(tx2.getHash(), 1);
        tx5.addOutput(3, pk_bob.getPublicKey());
        tx5.addOutput(2, pk_alice.getPublicKey());      //  //
        tx5.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx6 = new Tx();
        tx6.addInput(tx5.getHash(), 0);
        tx6.addOutput(1, pk_bob.getPublicKey());
        tx6.signTx(pk_bob.getPrivateKey(), 0);

        assertEquals(4, handleTxs.handler(new Transaction[]{tx2, tx3, tx4, tx5, tx6}).length);
    }

    @Test
    public void testMaxFeeHandleTxs2() throws SignatureException {

        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());      //
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        Tx txx = new Tx();
        txx.addOutput(100,  this.pk_bob.getPublicKey());      //
        byte[] initialHash1 = BigInteger.valueOf(0).toByteArray();
        txx.addInput(initialHash1, 0);
        txx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));
        UTXO u = new UTXO(txx.getHash(), 0);
        utxoPool.addUTXO(u, txx.getOutput(0));

        MaxFeeHandleTxs handleTxs = new MaxFeeHandleTxs(utxoPool);

        Tx tx10 = new Tx();
        tx10.addInput(txx.getHash(), 0);
        tx10.addOutput(20, pk_alice.getPublicKey());  //
        tx10.addOutput(10, pk_alice.getPublicKey());  //
        tx10.addOutput(30, pk_alice.getPublicKey());  //
        tx10.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx11 = new Tx();
        tx11.addInput(tx10.getHash(), 0);
        tx11.addOutput(18, pk_alice.getPublicKey());  //
        tx11.addOutput(2, pk_alice.getPublicKey());  //
        tx11.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(2, pk_alice.getPublicKey());  //
        tx2.addOutput(5, pk_alice.getPublicKey());  //
        tx2.addOutput(3, pk_alice.getPublicKey());  //
        tx2.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx3 = new Tx();
        tx3.addInput(tx2.getHash(), 0);
        tx3.addOutput(1, pk_bob.getPublicKey());    //
        tx3.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx4 = new Tx();
        tx4.addInput(tx2.getHash(), 1);
        tx4.addOutput(3, pk_bob.getPublicKey());    //
        tx4.addOutput(1, pk_alice.getPublicKey());  //  //
        tx4.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx5 = new Tx();
        tx5.addInput(tx2.getHash(), 1);
        tx5.addOutput(3, pk_bob.getPublicKey());
        tx5.addOutput(2, pk_alice.getPublicKey());      //  //
        tx5.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx6 = new Tx();
        tx6.addInput(tx5.getHash(), 0);
        tx6.addOutput(2, pk_alice.getPublicKey());
        tx6.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx7 = new Tx();
        tx7.addInput(tx6.getHash(), 0);
        tx7.addOutput(2, pk_bob.getPublicKey());
        tx7.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx8 = new Tx();
        tx8.addInput(tx6.getHash(), 0);
        tx8.addOutput(2, pk_bob.getPublicKey());
        tx8.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx9 = new Tx();
        tx9.addInput(tx7.getHash(), 0);
        tx9.addOutput(1, pk_bob.getPublicKey());
        tx9.signTx(pk_bob.getPrivateKey(), 0);

        assertEquals(8, handleTxs.handler(new Transaction[]{tx2, tx3, tx4, tx5, tx6, tx7, tx8, tx9, tx10, tx11}).length);
    }

    @Test
    public void testMaxFeeHandleTxsWithDoubleSpends() throws SignatureException {

        initializeTransactions();

        Tx tx = new Tx();
        tx.addOutput(10,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);
        tx.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx.getHash(), 0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        MaxFeeHandleTxs handleTxs = new MaxFeeHandleTxs(utxoPool);

        Tx tx2 = new Tx();
        tx2.addInput(tx.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublicKey());  //
        tx2.addOutput(3, pk_alice.getPublicKey());  //
        tx2.addOutput(2, pk_alice.getPublicKey());  //
        tx2.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx3 = new Tx();
        tx3.addInput(tx2.getHash(), 0);
        tx3.addInput(tx2.getHash(), 2);
        tx3.addOutput(1, pk_bob.getPublicKey());    //
        tx3.addOutput(5, pk_bob.getPublicKey());    //
        tx3.signTx(pk_alice.getPrivateKey(), 0);
        tx3.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx4 = new Tx();
        tx4.addInput(tx3.getHash(), 1);
        tx4.addOutput(3, pk_bob.getPublicKey());    //
        tx4.addOutput(2, pk_alice.getPublicKey());
        tx4.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx5 = new Tx();
        tx5.addInput(tx4.getHash(), 1);
        tx5.addInput(tx2.getHash(), 1);
        tx5.addOutput(3, pk_bob.getPublicKey());   //
        tx5.addOutput(2, pk_alice.getPublicKey());  //
        tx5.signTx(pk_alice.getPrivateKey(), 0);
        tx5.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx6 = new Tx();
        tx6.addInput(tx4.getHash(), 1);
        tx6.addOutput(1, pk_bob.getPublicKey());
        tx6.addOutput(1, pk_alice.getPublicKey());  //
        tx6.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx7 = new Tx();
        tx7.addInput(tx5.getHash(), 1);
        tx7.addInput(tx5.getHash(), 1);
        tx7.addOutput(1, pk_bob.getPublicKey());
        tx7.addOutput(1, pk_alice.getPublicKey());
        tx7.signTx(pk_alice.getPrivateKey(), 0);
        tx7.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx8 = new Tx();
        tx8.addInput(tx3.getHash(), 0);
        tx8.addInput(tx4.getHash(), 0);
        tx8.addInput(tx6.getHash(), 1);
        tx8.addOutput(3, pk_bob.getPublicKey());
        tx8.addOutput(2, pk_alice.getPublicKey());
        tx8.signTx(pk_bob.getPrivateKey(), 0);
        tx8.signTx(pk_bob.getPrivateKey(), 1);
        tx8.signTx(pk_alice.getPrivateKey(), 2);

        assertEquals(5, handleTxs.handler(new Transaction[]{tx2, tx3, tx4, tx5, tx6, tx7, tx8}).length);
    }

    @Test
    public void ultimateTest() throws SignatureException {

        initializeTransactions();

        Tx tx01 = new Tx();
        tx01.addOutput(100,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx01.addInput(initialHash, 0);
        tx01.signTx(this.pk_bob.getPrivateKey(), 0);

        Tx tx02 = new Tx();
        tx02.addOutput(100,  this.pk_alice.getPublicKey());
        byte[] initialHash2 = BigInteger.valueOf(0).toByteArray();
        tx02.addInput(initialHash2, 0);
        tx02.signTx(this.pk_alice.getPrivateKey(), 0);

        Tx tx03 = new Tx();
        tx03.addOutput(100,  this.pk_cyril.getPublicKey());
        byte[] initialHash3 = BigInteger.valueOf(0).toByteArray();
        tx03.addInput(initialHash3, 0);
        tx03.signTx(this.pk_cyril.getPrivateKey(), 0);

        Tx tx04 = new Tx();
        tx04.addOutput(100,  this.pk_alice.getPublicKey());
        byte[] initialHash4 = BigInteger.valueOf(0).toByteArray();
        tx04.addInput(initialHash4, 0);
        tx04.signTx(this.pk_alice.getPrivateKey(), 0);

        Tx tx05 = new Tx();
        tx05.addOutput(100,  this.pk_bob.getPublicKey());
        byte[] initialHash5 = BigInteger.valueOf(0).toByteArray();
        tx05.addInput(initialHash5, 0);
        tx05.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx01.getHash(), 0);
        utxoPool.addUTXO(utxo, tx01.getOutput(0));
        UTXO utxo2 = new UTXO(tx02.getHash(), 0);
        utxoPool.addUTXO(utxo2, tx02.getOutput(0));
        UTXO utxo3 = new UTXO(tx03.getHash(), 0);
        utxoPool.addUTXO(utxo3, tx03.getOutput(0));
        UTXO utxo4 = new UTXO(tx04.getHash(), 0);
        utxoPool.addUTXO(utxo4, tx04.getOutput(0));
        UTXO utxo5 = new UTXO(tx05.getHash(), 0);
        utxoPool.addUTXO(utxo5, tx05.getOutput(0));

        HandleTxs handleTxs = new HandleTxs(utxoPool);

        Tx tx1 = new Tx();      //100
        tx1.addInput(tx01.getHash(), 0);
        tx1.addOutput(50, pk_alice.getPublicKey());  //
        tx1.addOutput(30, pk_alice.getPublicKey());  //
        tx1.addOutput(20, pk_alice.getPublicKey());  //
        tx1.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx2 = new Tx();      //100
        tx2.addInput(tx02.getHash(), 0);
        tx2.addOutput(10, pk_bob.getPublicKey());    //
        tx2.addOutput(50, pk_cyril.getPublicKey());    //
        tx2.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx3 = new Tx();      //100
        tx3.addInput(tx03.getHash(), 0);
        tx3.addOutput(30, pk_bob.getPublicKey());    //
        tx3.addOutput(20, pk_alice.getPublicKey());
        tx3.signTx(pk_cyril.getPrivateKey(), 0);

        Tx tx4 = new Tx();      //100
        tx4.addInput(tx04.getHash(), 0);
        tx4.addOutput(30, pk_bob.getPublicKey());   //
        tx4.addOutput(20, pk_alice.getPublicKey());  //
        tx4.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx5 = new Tx();      //100
        tx5.addInput(tx05.getHash(), 0);
        tx5.addOutput(10, pk_bob.getPublicKey());
        tx5.addOutput(10, pk_alice.getPublicKey());  //
        tx5.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx6 = new Tx();      //100
        tx6.addInput(tx1.getHash(), 0);
        tx6.addInput(tx2.getHash(), 1);
        tx6.addOutput(60, pk_bob.getPublicKey());
        tx6.addOutput(10, pk_alice.getPublicKey());
        tx6.signTx(pk_alice.getPrivateKey(), 0);
        tx6.signTx(pk_cyril.getPrivateKey(), 1);

        Tx tx7 = new Tx();      //20+60 = 80
        tx7.addInput(tx3.getHash(), 1);
        tx7.addInput(tx6.getHash(), 0);
        tx7.addOutput(30, pk_bob.getPublicKey());
        tx7.addOutput(20, pk_alice.getPublicKey());
        tx7.signTx(pk_alice.getPrivateKey(), 0);
        tx7.signTx(pk_bob.getPrivateKey(), 1);

        Tx tx8 = new Tx();      //30
        tx8.addInput(tx3.getHash(), 0);
        tx8.addOutput(10, pk_cyril.getPublicKey());
        tx8.addOutput(10, pk_alice.getPublicKey());
        tx8.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx9 = new Tx();      //30+10 = 40
        tx9.addInput(tx7.getHash(), 0);
        tx9.addInput(tx8.getHash(), 1);
        tx9.addOutput(20, pk_cyril.getPublicKey());
        tx9.addOutput(20, pk_alice.getPublicKey());
        tx9.signTx(pk_bob.getPrivateKey(), 0);
        tx9.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx10 = new Tx();     //30
        tx10.addInput(tx4.getHash(), 0);
        tx10.addOutput(25, pk_cyril.getPublicKey());
        tx10.addOutput(5, pk_alice.getPublicKey());
        tx10.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx11 = new Tx();     //25
        tx11.addInput(tx10.getHash(), 0);
        tx11.addOutput(5, pk_cyril.getPublicKey());
        tx11.addOutput(20, pk_bob.getPublicKey());
        tx11.signTx(pk_cyril.getPrivateKey(), 0);

        Tx tx12 = new Tx();     //20
        tx12.addInput(tx11.getHash(), 1);
        tx12.addOutput(5, pk_cyril.getPublicKey());
        tx12.addOutput(10, pk_alice.getPublicKey());
        tx12.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx13 = new Tx();
        tx13.addInput(tx4.getHash(), 1);
        tx13.addInput(tx9.getHash(), 0);
        tx13.addInput(tx12.getHash(), 1);
        tx13.addOutput(2, pk_cyril.getPublicKey());
        tx13.addOutput(2, pk_alice.getPublicKey());
        tx13.signTx(pk_alice.getPrivateKey(), 0);
        tx13.signTx(pk_cyril.getPrivateKey(), 1);
        tx13.signTx(pk_alice.getPrivateKey(), 2);

        // Act & Assert
        assertEquals(13, handleTxs.handler(new Transaction[]{tx11, tx10, tx2, tx4, tx13, tx7, tx1, tx6, tx8, tx5, tx12, tx3, tx9}).length);
    }

    @Test
    public void ultimateTest2() throws SignatureException {

        initializeTransactions();

        Tx tx01 = new Tx();
        tx01.addOutput(100,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx01.addInput(initialHash, 0);
        tx01.signTx(this.pk_bob.getPrivateKey(), 0);

        Tx tx02 = new Tx();
        tx02.addOutput(100,  this.pk_alice.getPublicKey());
        byte[] initialHash2 = BigInteger.valueOf(0).toByteArray();
        tx02.addInput(initialHash2, 0);
        tx02.signTx(this.pk_alice.getPrivateKey(), 0);

        Tx tx03 = new Tx();
        tx03.addOutput(100,  this.pk_cyril.getPublicKey());
        byte[] initialHash3 = BigInteger.valueOf(0).toByteArray();
        tx03.addInput(initialHash3, 0);
        tx03.signTx(this.pk_cyril.getPrivateKey(), 0);

        Tx tx04 = new Tx();
        tx04.addOutput(100,  this.pk_alice.getPublicKey());
        byte[] initialHash4 = BigInteger.valueOf(0).toByteArray();
        tx04.addInput(initialHash4, 0);
        tx04.signTx(this.pk_alice.getPrivateKey(), 0);

        Tx tx05 = new Tx();
        tx05.addOutput(100,  this.pk_bob.getPublicKey());
        byte[] initialHash5 = BigInteger.valueOf(0).toByteArray();
        tx05.addInput(initialHash5, 0);
        tx05.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx01.getHash(), 0);
        utxoPool.addUTXO(utxo, tx01.getOutput(0));
        UTXO utxo2 = new UTXO(tx02.getHash(), 0);
        utxoPool.addUTXO(utxo2, tx02.getOutput(0));
        UTXO utxo3 = new UTXO(tx03.getHash(), 0);
        utxoPool.addUTXO(utxo3, tx03.getOutput(0));
        UTXO utxo4 = new UTXO(tx04.getHash(), 0);
        utxoPool.addUTXO(utxo4, tx04.getOutput(0));
        UTXO utxo5 = new UTXO(tx05.getHash(), 0);
        utxoPool.addUTXO(utxo5, tx05.getOutput(0));

        MaxFeeHandleTxs handleTxs = new MaxFeeHandleTxs(utxoPool);

        Tx tx1 = new Tx();      //100
        tx1.addInput(tx01.getHash(), 0);
        tx1.addOutput(50, pk_alice.getPublicKey());  //
        tx1.addOutput(30, pk_alice.getPublicKey());  //
        tx1.addOutput(20, pk_alice.getPublicKey());  //
        tx1.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx2 = new Tx();      //100
        tx2.addInput(tx02.getHash(), 0);
        tx2.addOutput(10, pk_bob.getPublicKey());    //
        tx2.addOutput(50, pk_cyril.getPublicKey());    //
        tx2.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx3 = new Tx();      //100
        tx3.addInput(tx03.getHash(), 0);
        tx3.addOutput(30, pk_bob.getPublicKey());    //
        tx3.addOutput(20, pk_alice.getPublicKey());
        tx3.signTx(pk_cyril.getPrivateKey(), 0);

        Tx tx4 = new Tx();      //100
        tx4.addInput(tx04.getHash(), 0);
        tx4.addOutput(30, pk_bob.getPublicKey());   //
        tx4.addOutput(20, pk_alice.getPublicKey());  //
        tx4.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx5 = new Tx();      //100
        tx5.addInput(tx05.getHash(), 0);
        tx5.addOutput(10, pk_bob.getPublicKey());
        tx5.addOutput(10, pk_alice.getPublicKey());  //
        tx5.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx6 = new Tx();      //100
        tx6.addInput(tx1.getHash(), 0);
        tx6.addInput(tx2.getHash(), 1);
        tx6.addOutput(60, pk_bob.getPublicKey());
        tx6.addOutput(10, pk_alice.getPublicKey());
        tx6.signTx(pk_alice.getPrivateKey(), 0);
        tx6.signTx(pk_cyril.getPrivateKey(), 1);

        Tx tx7 = new Tx();      //20+60 = 80
        tx7.addInput(tx3.getHash(), 1);
        tx7.addInput(tx6.getHash(), 0);
        tx7.addOutput(30, pk_bob.getPublicKey());
        tx7.addOutput(20, pk_alice.getPublicKey());
        tx7.signTx(pk_alice.getPrivateKey(), 0);
        tx7.signTx(pk_bob.getPrivateKey(), 1);

        Tx tx8 = new Tx();      //30
        tx8.addInput(tx3.getHash(), 0);
        tx8.addOutput(10, pk_cyril.getPublicKey());
        tx8.addOutput(10, pk_alice.getPublicKey());
        tx8.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx9 = new Tx();      //30+10 = 40
        tx9.addInput(tx7.getHash(), 0);
        tx9.addInput(tx8.getHash(), 1);
        tx9.addOutput(20, pk_cyril.getPublicKey());
        tx9.addOutput(20, pk_alice.getPublicKey());
        tx9.signTx(pk_bob.getPrivateKey(), 0);
        tx9.signTx(pk_alice.getPrivateKey(), 1);

        Tx tx10 = new Tx();     //30
        tx10.addInput(tx4.getHash(), 0);
        tx10.addOutput(25, pk_cyril.getPublicKey());
        tx10.addOutput(5, pk_alice.getPublicKey());
        tx10.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx11 = new Tx();     //25
        tx11.addInput(tx10.getHash(), 0);
        tx11.addOutput(5, pk_cyril.getPublicKey());
        tx11.addOutput(20, pk_bob.getPublicKey());
        tx11.signTx(pk_cyril.getPrivateKey(), 0);

        Tx tx12 = new Tx();     //20
        tx12.addInput(tx11.getHash(), 1);
        tx12.addOutput(5, pk_cyril.getPublicKey());
        tx12.addOutput(10, pk_alice.getPublicKey());
        tx12.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx13 = new Tx();     //20+20+10 = 50
        tx13.addInput(tx4.getHash(), 0);
        tx13.addInput(tx9.getHash(), 0);
        tx13.addInput(tx12.getHash(), 1);
        tx13.addOutput(2, pk_cyril.getPublicKey());
        tx13.addOutput(2, pk_alice.getPublicKey());
        tx13.signTx(pk_alice.getPrivateKey(), 0);
        tx13.signTx(pk_cyril.getPrivateKey(), 1);
        tx13.signTx(pk_alice.getPrivateKey(), 2);

        assertEquals(12, handleTxs.handler(new Transaction[]{tx11, tx10, tx2, tx4, tx13, tx7, tx1, tx6, tx8, tx5, tx12, tx3, tx9}).length);
    }

    @Test
    public void ultimateTest3() throws SignatureException {

        initializeTransactions();

        Tx tx01 = new Tx();
        tx01.addOutput(100,  this.pk_bob.getPublicKey());
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx01.addInput(initialHash, 0);
        tx01.signTx(this.pk_bob.getPrivateKey(), 0);

        Tx tx02 = new Tx();
        tx02.addOutput(100,  this.pk_alice.getPublicKey());
        byte[] initialHash2 = BigInteger.valueOf(0).toByteArray();
        tx02.addInput(initialHash2, 0);
        tx02.signTx(this.pk_alice.getPrivateKey(), 0);

        Tx tx03 = new Tx();
        tx03.addOutput(100,  this.pk_cyril.getPublicKey());
        byte[] initialHash3 = BigInteger.valueOf(0).toByteArray();
        tx03.addInput(initialHash3, 0);
        tx03.signTx(this.pk_cyril.getPrivateKey(), 0);

        Tx tx04 = new Tx();
        tx04.addOutput(100,  this.pk_alice.getPublicKey());
        byte[] initialHash4 = BigInteger.valueOf(0).toByteArray();
        tx04.addInput(initialHash4, 0);
        tx04.signTx(this.pk_alice.getPrivateKey(), 0);

        Tx tx05 = new Tx();
        tx05.addOutput(100,  this.pk_bob.getPublicKey());
        byte[] initialHash5 = BigInteger.valueOf(0).toByteArray();
        tx05.addInput(initialHash5, 0);
        tx05.signTx(this.pk_bob.getPrivateKey(), 0);

        UTXOPool utxoPool = new UTXOPool();
        UTXO utxo = new UTXO(tx01.getHash(), 0);
        utxoPool.addUTXO(utxo, tx01.getOutput(0));
        UTXO utxo2 = new UTXO(tx02.getHash(), 0);
        utxoPool.addUTXO(utxo2, tx02.getOutput(0));
        UTXO utxo3 = new UTXO(tx03.getHash(), 0);
        utxoPool.addUTXO(utxo3, tx03.getOutput(0));
        UTXO utxo4 = new UTXO(tx04.getHash(), 0);
        utxoPool.addUTXO(utxo4, tx04.getOutput(0));
        UTXO utxo5 = new UTXO(tx05.getHash(), 0);
        utxoPool.addUTXO(utxo5, tx05.getOutput(0));

        HandleTxs handleTxs = new HandleTxs(utxoPool);

        Tx tx1 = new Tx();      //100
        tx1.addInput(tx01.getHash(), 0);
        tx1.addOutput(50, pk_alice.getPublicKey());  //
        tx1.addOutput(30, pk_alice.getPublicKey());  //
        tx1.addOutput(20, pk_alice.getPublicKey());  //
        tx1.signTx(pk_bob.getPrivateKey(), 0);

        Tx tx2 = new Tx();      //100
        tx2.addInput(tx02.getHash(), 0);
        tx2.addOutput(10, pk_bob.getPublicKey());    //
        tx2.addOutput(50, pk_cyril.getPublicKey());    //
        tx2.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx3 = new Tx();      //100
        tx3.addInput(tx03.getHash(), 0);
        tx3.addOutput(30, pk_bob.getPublicKey());    //
        tx3.addOutput(20, pk_alice.getPublicKey());
        tx3.signTx(pk_cyril.getPrivateKey(), 0);

        Tx tx4 = new Tx();      //100
        tx4.addInput(tx04.getHash(), 0);
        tx4.addOutput(30, pk_bob.getPublicKey());   //
        tx4.addOutput(20, pk_alice.getPublicKey());  //
        tx4.signTx(pk_alice.getPrivateKey(), 0);

        Tx tx5 = new Tx();      //100
        tx5.addInput(tx05.getHash(), 0);
        tx5.addOutput(10, pk_bob.getPublicKey());
        tx5.addOutput(10, pk_alice.getPublicKey());  //
        tx5.signTx(pk_bob.getPrivateKey(), 0);

        assertEquals(5, handleTxs.handler(new Transaction[]{tx2, tx4, tx1, tx5, tx3}).length);
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
