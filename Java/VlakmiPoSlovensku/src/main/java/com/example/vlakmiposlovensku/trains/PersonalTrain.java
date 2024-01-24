package com.example.vlakmiposlovensku.trains;

import com.example.vlakmiposlovensku.travellers.Traveller;

/**
 * Trieda <code>PersonalTrain</code> reprezentuje typ osobných vlakov.
 * Trieda je odvodená od abstraktnej triedy {@link Train}, ktorú rozširuje o atribúty pricePerKm a freeSeats.
 */
public class PersonalTrain extends Train {
    private final double pricePerKm;
    private final int freeSeats;

    public PersonalTrain(String name, int velocity, int capacity, String[] timetable, int freeTickets, double pricePerKm, String[] stations){
        super(name, velocity, capacity, timetable, freeTickets, stations);

        this.pricePerKm = pricePerKm;
        this.freeSeats = countFreeSeats();
    }

    /**
     * Metóda vracajúca počet voľných miest.
     * @return          počet voľných miest
     */
    public int getFreeSeats() {
        return freeSeats;
    }

    /**
     * Metóda, ktorá určí počet voľných miest, ktoré vypočíta ako rozdiel kapacity vlaku a náhodného čísla
     * od 0 po kapacitu vlaku.
     * @return          počet voľných miest
     */
    @Override
    protected int countFreeSeats(){
        return this.getCapacity() - (int)((Math.random() * (this.getCapacity()/2)));
    }

    /**
     * Metóda, ktorá určí počet voľných miest, ktoré vypočíta ako rozdiel kapacity vlaku a náhodného čísla
     * od 0 po kapacitu vlaku vynásobenú nejakým číslom.
     * @param factor        upravenie rozsahu hodnôt
     * @return              počet voľných miest
     */
    @Override
    protected int countFreeSeats(double factor){
        return this.getCapacity() - (int)((Math.random() * (this.getCapacity()/2))*factor);
    }

    /**
     * Výpočet ceny pre jednotlivé tipy cestujúcich.
     * @param traveller     typ cestujúceho
     * @param total_km      počet precestovaných kilometrov
     * @return              cena pre daného cestujúceho
     */
    @Override
    public double count_price(Traveller traveller, int total_km) {
        return total_km * this.pricePerKm*traveller.getDiscount();
    }
}
