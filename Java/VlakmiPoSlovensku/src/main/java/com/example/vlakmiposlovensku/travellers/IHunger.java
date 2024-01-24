package com.example.vlakmiposlovensku.travellers;

/**
 * Rozhranie <code>IHunger</code> reprezentuje prácu s hladom cestujúceho {@link Traveller}.
 *
 * @see Traveller
 */
public interface IHunger {
    void increaseHunger();
    void increaseHunger(int km);
    void eat();
    double getHunger();
}
