package com.example.vlakmiposlovensku.travellers;

/**
 * Abstraktná trieda <code>Children</code> reprezentujúca deti.
 * Trieda je odvodená od triedy {@link Traveller}.
 * Od tejto triedy sú odvodené triedy: {@link Kids} a {@link Infants}
 *
 * @see Traveller
 * @see Kids
 * @see Infants
 */
public abstract class Children extends Traveller {

    public Children(String firstName, String lastName){
        super(firstName, lastName);
    }

    /**
     * Metóda resetujúca hodnotu energie.
     */
    public void resetEnergy() {
        setEnergy(100);
    }

    /**
     * Metóda znižujúca hodnotu energie.
     */
    public void decreaseEnergy(double minus){
        setEnergy(getEnergy()-minus);
    }

    /**
     * Metóda zvyšujúca hodnotu hladu.
     */
    public void increaseHunger(){
        setHunger(getHunger()+1.5);
    }

    /**
     * Metóda zvyšujúca hodnotu hladu na základe prejdeného počtu kilometrov počas spánku.
     * @param km        počet kilometrov počas spánku
     */
    @Override
    public void increaseHunger(int km) {
        setHunger(getHunger()+(0.25*km));
    }

    /**
     * Metóda resetujúca hodnotu hladu.
     */
    public void resetHunger(){
        setHunger(0);
    }

    /**
     * Metóda nastavujúca potrebu WC.
     * @param toilet    nová zadaná potreba WC
     */
    public void setToilet(double toilet) {
        super.setToilet(getToilet()+toilet);
    }

    /**
     * Metóda zvyšujúca hodnotu potreby WC.
     */
    @Override
    public void increaseToilet() {
        setToilet(getToilet()+0.5);
    }

    /**
     * Metóda zvyšujúca hodnotu potreby WC na základe prejdeného počtu kilometrov počas spánku.
     * @param km        počet kilometrov počas spánku
     */
    @Override
    public void increaseToilet(int km) {
        setToilet(getToilet()+(0.1*km));
    }

    /**
     * Metóda zvyšujúca hodnotu potreby WC o zadanú hodnotu.
     * @param x        zadaná hodnota
     */
    @Override
    public void increaseToilet(double x) {
        setToilet(getToilet()+x);
    }

    /**
     * Metóda, ktorá resetuje potrebu WC.
     */
    @Override
    public void useToilet() {
        setToilet(0);
    }

    /**
     * Metóda reprezentujúca jedenie.
     */
    @Override
    public void eat() {
        resetHunger();
        increaseToilet(20.0);
    }
}
