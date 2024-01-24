package com.example.vlakmiposlovensku.trains;

import com.example.vlakmiposlovensku.exceptions.InvalidTimeFormatException;
import com.example.vlakmiposlovensku.travel.Path;
import com.example.vlakmiposlovensku.travellers.Traveller;

/**
 * Abstraktná trieda <code>Train</code> reprezentuje množinu všetkých vlakov.
 * Od tejto triedy sú odvodené triedy {@link FastTrain} a {@link PersonalTrain}.
 * Táto trieda je abstraktná, čo znamená, že nemôže vytvárať inštancie.
 *
 * @see FastTrain
 * @see PersonalTrain
 */
public abstract class Train implements ITrain {
    private final String name;
    private final int velocity;
    private final int capacity;
    private final String[] timetable;
    private final int freeTickets;
    private StationsList route;
    private final int freeSeats;
    private final boolean free;

    public Train(String name, int velocity, int capacity, String[] timetable, int freeTickets, String[] stations){
        this.name = name;
        this.velocity = velocity;
        this.capacity = capacity;
        this.timetable = timetable;
        this.freeTickets = freeTickets;
        this.freeSeats = countFreeSeats();
        this.free = checkFree();

        StationsList previous = null;
        for(String station : stations){
            StationsList newSt = new StationsList(station);
            if(previous == null){
                this.route = newSt;
            }else{
                previous.setNext(newSt);
            }
            previous = newSt;
        }
    }

    /**
     * Metóda vracajúca trasu.
     * @return          trasa
     */
    public StationsList getRoute(){
        return this.route;
    }

    /**
     * Metóda vracajúca názov vlaku.
     * @return          názov vlaku
     */
    public String getName(){
        return this.name;
    }

    /**
     * Metóda vracajúca rýchlosť vlaku.
     * @return          rýchlosť vlaku
     */
    public int getVelocity(){
        return this.velocity;
    }

    /**
     * Metóda vracajúca kapacitu vlaku.
     * @return          kapacita vlaku
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Metóda vracajúca počet voľných miest.
     * @return          počet voľných miest
     */
    public int getFreeSeats() {
        return freeSeats;
    }

    /**
     * Metóda, ktorá vráti počet lístkov zadarmo na daný vlak
     * @return          počet lístkov zadarmo
     */
    public int getFreeTickets() {
        return freeTickets;
    }

    /**
     * Metóda, ktorá zistí, či ešte sú vo vlaku voľné lístky.
     * @return          či sú vo vlaku voľné lístky
     */
    public boolean isFree() {
        return free;
    }

    /**
     * Metóda hľadajúca najbližší odchod daného vlaku.
     * V metóde je tiež kontrolovaná správnosť zadaného času, v prípade zlého času sa vyhodí vlastná výnimka.
     * Metóda hľadá najbližší odchod vlaku od času zadaného používateľom cez GUI.
     * Metóda sa pozrie do atribútu timetable, v ktorom nájde čas, ktorý je najbližší zadanému času a ten vráti.
     * @param time      zadaný čas cez GUI
     * @param path      odkaz na trasu
     * @return          čas najbližšieho odchodu vlaku
     * @throws InvalidTimeFormatException       ak je nesprávne zadaný čas
     */
    public String findNearestDeparture(String time, Path path) throws InvalidTimeFormatException {
        try{
            if (!time.matches("\\d{2}:\\d{2}")) {
                throw new InvalidTimeFormatException();
            }
        }catch(NullPointerException e){
            throw new InvalidTimeFormatException();
        }

        String[] tmp;
        tmp = (time.split(":"));

        int hours = Integer.parseInt(tmp[0]);
        int minutes = Integer.parseInt(tmp[1]);

        for (String s : this.timetable) {
            String[] tmp1;
            tmp1 = (s.split(":"));
            int h = Integer.parseInt(tmp1[0]);
            int m = Integer.parseInt(tmp1[1]);
            if (hours > h) {
                continue;
            } else if (hours == h && minutes > m) {
                continue;
            } else {
                return s;
            }
        }
        path.setOverMidnight(true);
        return this.timetable[0];
    }

    /**
     * Metóda, ktorá na základe rýchlosti a vzdialenosti vypočíta čas trasy.
     * @param velocity      rýchlosť vlaku
     * @param distance      vzdialenosť, ktorú má vlak prejsť
     * @return              čas trasy
     */
    public int countTime(int velocity, int distance){
        return (int)(((double)distance/velocity)*60);
    }

    /**
     * Metóda, ktorá k odchodu vlaku pripočíta čas trasy a tento nový čas vráti.
     * @param path          odkaz na triedu {@link Path}
     * @param time          odchod vlaku
     * @param minutes       čas trasy v minútach
     * @return              nový čas po pridaní dĺžky trasy
     * @throws InvalidTimeFormatException       ak je nesprávne zadaný čas
     */
    public String addToTime(Path path, String time, int minutes) throws InvalidTimeFormatException {
        if (!time.matches("\\d{2}:\\d{2}")) {
            throw new InvalidTimeFormatException();
        }

        String[] tmp;
        tmp = (time.split(":"));
        int h = Integer.parseInt(tmp[0]);
        int m = Integer.parseInt(tmp[1]);
        int min = h*60 + m + minutes;
        if(min/60 > 23){
            path.setOverMidnight(true);
        }
        return String.format("%02d", (min/60)%24) + ":" + String.format("%02d", min%60);
    }

    /**
     * Metóda, ktorá určí počet voľných miest, ktoré vypočíta ako rozdiel kapacity vlaku a náhodného čísla
     * od 0 po kapacitu vlaku.
     * @return      počet voľných miest
     */
    protected int countFreeSeats(){
        return this.getCapacity();
    }

    /**
     * Metóda, ktorá určí počet voľných miest, ktoré vypočíta ako rozdiel kapacity vlaku a náhodného čísla
     * od 0 po kapacitu vlaku vynásobenú nejakým číslom.
     * @param factor        upravenie rozsahu hodnôt
     * @return              počet voľných miest
     */
    protected int countFreeSeats(double factor){
        return (int)(this.getCapacity() * factor);
    }

    /**
     * Výpočet ceny pre jednotlivé tipy cestujúcich.
     * @param traveller     typ cestujúceho
     * @param total_km      počet precestovaných kilometrov
     * @return              cena pre daného cestujúceho
     */
    public double count_price(Traveller traveller, int total_km){
        return 0;
    }

    /**
     * Metóda, ktorá zistí, či sú ešte dostupné voľné lístky pre študentov
     * @return              či sú dostupné voľné lístky
     */
    public boolean checkFree(){
        return true;
    }
}
