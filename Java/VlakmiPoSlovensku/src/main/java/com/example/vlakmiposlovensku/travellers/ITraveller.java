package com.example.vlakmiposlovensku.travellers;

/**
 * Rozhranie <code>ITraveller</code> reprezentuje samotného cestujúceho {@link Traveller}.
 *
 * @see Traveller
 */
public interface ITraveller {
    String inform();
    double getDiscount();
    String getFirstName();
    String getLastName();
}
