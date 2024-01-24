package com.example.vlakmiposlovensku.travellers;

/**
 * Rozhranie <code>IToilet</code> reprezentuje prácu s potrebou WC cestujúceho {@link Traveller}.
 *
 * @see Traveller
 */
public interface IToilet {
    double getToilet();
    void increaseToilet();
    void increaseToilet(int km);
    void increaseToilet(double x);
    void useToilet();
}
