package com.example.vlakmiposlovensku.travellers;

/**
 * Trieda <code>Student</code> reprezentujúca študentov.
 * Trieda je odvodená od triedy {@link Traveller}, ktorú rozširuje o atribút discount.
 *
 * @see Traveller
 */
public class Student extends Traveller {
    private final double discount;

    public Student(String firstName, String lastName){
        super(firstName, lastName);
        this.discount = 0;
    }

    /**
     * Metóda znižujúca hodnotu energie.
     */
    public void decreaseEnergy(){
        setEnergy(getEnergy()-0.1);
    }

    /**
     * Metóda zvyšujúca hodnotu hladu.
     */
    public void increaseHunger(){
        setHunger(getHunger()+0.5);
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
     * Metóda zvyšujúca hodnotu potreby WC.
     */
    @Override
    public void increaseToilet() {
        setToilet(getToilet()+0.2);
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
     * Metóda reprezentujúca jedenie.
     */
    @Override
    public void eat() {
        resetHunger();
        increaseToilet(20.0);
    }

    /**
     * Metóda vracajúca zľavu.
     * @return          zľava
     */
    public double getDiscount(){
        return this.discount;
    }
}
