package com.example.vlakmiposlovensku.handlers;

import com.example.vlakmiposlovensku.exceptions.InvalidCardException;
import com.example.vlakmiposlovensku.gui.BuyTicketPane;

import java.io.*;
import java.util.Objects;

/**
 * Trieda <code>CardHandler</code> je trieda, ktorá pracuje a spracováva údaje z karty.
 * Trieda je odvodená od triedy {@link Serializable}, čím umožňuje ukladanie poslednej karty do súboru,
 * z ktorého sa dá v budúcnosti načítať. Trieda tiež kontroluje správnosť zadaných údajov.
 */
public class CardHandler implements Serializable {
    public static final long serialVersionUID = 0;

    private String number;
    private String expiry;
    private String verification;

    public CardHandler(){}

    public CardHandler(String number, String expiry, String verification){
        this.number = number;
        this.expiry = expiry;
        this.verification = verification;
    }

    public String getExpiry() {
        return expiry;
    }

    public String getNumber() {
        return number;
    }

    public String getVerification() {
        return verification;
    }

    /**
     * Metóda kontroluje správnosť zadaných údajov. V prípade, že je niektorý zo zadaných údajov zlý,
     * metóda vyhodí vlastnú výnimku {@link InvalidCardException}
     * @throws InvalidCardException         Ak je niektoré číslo z karty zadané zle
     */
    public void checkCard() throws InvalidCardException {
        if(!number.matches("\\d{4} \\d{4} \\d{4} \\d{4}")){
            throw new InvalidCardException();
        }else if(!expiry.matches("\\d{2}/\\d{2}")){
            throw new InvalidCardException();
        }else if(!verification.matches("\\d{3}")){
            throw new InvalidCardException();
        }
    }

    /**
     * Metóda, ktorá umožňuje uložiť práve zadanú kartu do súboru.
     * @throws ClassNotFoundException       v prípade, že nenájde súbor
     * @throws IOException                  v prípade, že sa nepodarí načítať súbor
     */
    public void save() throws ClassNotFoundException, IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("card.out"));
        out.writeObject(this);
        out.close();
    }

    /**
     * Metóda, ktorá umožňuje načítať posledne zadanú kartu zo súboru.
     * Metóda načíta túto kartu len v tom prípade, že zadaný overovací kód je totožný s kódom na karte,
     * ktorú chceme načítať.
     * @param pane              okno, s ktorým v metóde pracujeme
     * @param verifyNumber      overovací kód zadaný používateľom na overenie karty
     * @return                  či sa podarilo načítať poslednú zadanú kartu
     * @throws ClassNotFoundException       v prípade, že nenájde súbor
     * @throws IOException                  v prípade, že sa nepodarí načítať súbor
     */
    public boolean load(BuyTicketPane pane, String verifyNumber) throws ClassNotFoundException, IOException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("card.out"));
        CardHandler loadedCard = (CardHandler) in.readObject();
        in.close();

        number = loadedCard.getNumber();
        expiry = loadedCard.getExpiry();
        verification = loadedCard.getVerification();

        if(!Objects.equals(verification, verifyNumber)){
            pane.getWarning().setVisible(true);
            return false;
        }

        pane.getTfCardNumber().setText(number);
        pane.getTfExpiringDate().setText(expiry);
        pane.getTfVerifyNumber().setText(verification);

        return true;
    }
}
