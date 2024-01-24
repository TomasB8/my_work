package com.example.vlakmiposlovensku.travellers;

/**
 * Trieda <code>Kids</code> reprezentujúca deti.
 * Trieda je odvodená od triedy {@link Children}, ktorú rozširuje o atribút discount.
 *
 * @see Children
 */
public class Kids extends Children{
    private final double discount;

    public Kids(String firstName, String lastName) {
        super(firstName, lastName);
        this.discount = 0.1;
    }

    /**
     * Metóda znižujúca hodnotu energie.
     */
    public void decreaseEnergy() {
        super.decreaseEnergy(0.4);
    }

    /**
     * Metóda vracajúca zľavu.
     * @return      zľava
     */
    public double getDiscount(){
        return this.discount;
    }
}
