package com.example.vlakmiposlovensku.trains;

/**
 * Trieda <code>StationsList</code> reprezentuje spájaný zoznam staníc.
 * Trieda sa využíva na reprezentáciu trasy, ako poradia jednotlivých staníc,
 * kedy každá stanica ukazuje na svojho nasledovníka.
 */
public class StationsList {
    private final Station station;
    private StationsList next;

    public StationsList(Station station){
        this.station = station;
        this.next = null;
    }

    public StationsList(String station){
        this.station = new Station(station);
        this.next = null;
    }

    /**
     * Vnorená trieda <code>Station</code> reprezentujúca jednu stanicu.
     * Jediným atribútom tejto triedy je názov stanice.
     */
    public static class Station {
        String name;

        public Station(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Metóda vracajúca stanicu.
     * @return      stanica
     */
    public Station getStation() {
        return station;
    }

    /**
     * Metóda vracajúca nasledovníka aktuálnej stanice.
     * @return      nasledovník aktuálnej stanice
     */
    public StationsList getNext() {
        return next;
    }

    /**
     * Nastavenie nasledovníka aktuálnej stanice.
     * @param next      nasledovník
     */
    public void setNext(StationsList next) {
        this.next = next;
    }
}
