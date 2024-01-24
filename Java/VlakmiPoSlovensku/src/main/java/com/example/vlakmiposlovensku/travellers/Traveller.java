package com.example.vlakmiposlovensku.travellers;

/**
 * Abstraktná trieda <code>Traveller</code> reprezentuje všetkých cestujúcich.
 * Trieda implementuje rozhrania: {@link ITraveller}, {@link IEnergy}, {@link IHunger} a {@link IToilet}.
 * Od tejto triedy sú odvodené všetky Ďalšie typy cestujúcich.
 *
 * @see ITraveller
 * @see IEnergy
 * @see IHunger
 * @see IToilet
 */
public abstract class Traveller implements ITraveller, IHunger, IEnergy, IToilet {
    private final String firstName, lastName;
    private double energy, hunger, toilet;

    public Traveller(String firstName, String lastname){
        this.firstName = firstName;
        this.lastName = lastname;
        this.energy = 100;
        this.hunger = 0;
        this.toilet = 0;
    }

    /**
     * Metóda nastavujúca potrebu WC na zadanú hodnotu.
     * @param toilet        zadaná hodnota WC
     */
    public void setToilet(double toilet) {
        this.toilet = toilet;
    }

    /**
     * Metóda nastavujúca hodnotu energie na zadanú hodnotu.
     * @param energy        zadaná hodnota energie
     */
    public void setEnergy(double energy) {
        this.energy = energy;
    }

    /**
     * Metóda nastavujúca hodnotu hladu na zadanú hodnotu.
     * @param hunger        zadaná hodnota hladu
     */
    public void setHunger(double hunger) {
        this.hunger = hunger;
    }

    /**
     * Metóda vracajúca meno cestujúceho.
     * @return      meno cestujúceho
     */
    @Override
    public String inform() {
        return getFirstName() + " " + getLastName() + "\n";
    }

    /**
     * Metóda vracajúca zľavu.
     * @return      zľava
     */
    @Override
    public double getDiscount() {
        return 0;
    }

    /**
     * Metóda vracajúca meno cestujúceho.
     * @return      meno cestujúceho
     */
    @Override
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Metóda vracajúca priezvisko cestujúceho.
     * @return      priezvisko cestujúceho
     */
    @Override
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Metóda vracajúca hodnotu energie.
     * @return      hodnota energie
     */
    @Override
    public double getEnergy() {
        return this.energy;
    }

    /**
     * Metóda znižujúca hodnotu energie.
     */
    @Override
    public void decreaseEnergy() {}

    /**
     * Metóda zvyšujúca hodnotu hladu.
     */
    @Override
    public void increaseHunger() {}

    /**
     * Metóda resetujúca hodnotu energie.
     */
    @Override
    public void resetEnergy() {
        this.energy = 100;
    }

    /**
     * Metóda resetujúca hodnotu hladu.
     */
    public void resetHunger(){
        this.hunger = 0;
    }

    /**
     * Metóda zvyšujúca hodnotu hladu na základe prejdeného počtu kilometrov počas spánku.
     * @param km        počet kilometrov počas spánku
     */
    @Override
    public void increaseHunger(int km) {}

    /**
     * Metóda reprezentujúca jedenie.
     */
    @Override
    public void eat() {}

    /**
     * Metóda vracajúca hodnotu hladu cestujúceho.
     * @return      hlad cestujúceho
     */
    @Override
    public double getHunger() {
        return this.hunger;
    }

    /**
     * Metóda vracajúca potrebu WC cestujúceho.
     * @return      potreba WC cestujúceho
     */
    @Override
    public double getToilet() {
        return this.toilet;
    }

    /**
     * Metóda zvyšujúca hodnotu potreby WC.
     */
    @Override
    public void increaseToilet() {}

    /**
     * Metóda zvyšujúca hodnotu potreby WC na základe prejdeného počtu kilometrov počas spánku.
     * @param km        počet kilometrov počas spánku
     */
    @Override
    public void increaseToilet(int km) {}

    /**
     * Metóda zvyšujúca hodnotu potreby WC o zadanú hodnotu.
     * @param x        zadaná hodnota
     */
    @Override
    public void increaseToilet(double x) {}

    /**
     * Metóda, ktorá resetuje potrebu WC.
     */
    @Override
    public void useToilet() {
        this.toilet = 0;
    }
}
