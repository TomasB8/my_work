package com.example.vlakmiposlovensku.travellers;

/**
 * Trieda <code>Infants</code> reprezentujúca batoľatá.
 * Trieda je odvodená od triedy {@link Children}, ktorú rozširuje o atribút discount.
 *
 * @see Children
 */
public class Infants extends Children{
    private final double discount;

    public Infants(String firstName, String lastName){
        super(firstName, lastName);
        this.discount = 0;
    }

    /**
     * Metóda znižujúca hodnotu energie.
     */
    public void decreaseEnergy() {
        super.decreaseEnergy(1);
    }

    /**
     * Metóda zvyšujúca hodnotu potreby WC.
     */
    @Override
    public void increaseToilet() {
        super.setToilet(1);
    }

    /**
     * Metóda vracajúca zľavu.
     * @return      zľava
     */
    public double getDiscount(){
        return this.discount;
    }
}
