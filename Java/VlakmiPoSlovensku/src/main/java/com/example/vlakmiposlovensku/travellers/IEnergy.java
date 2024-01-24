package com.example.vlakmiposlovensku.travellers;

import java.util.Random;

/**
 * Rozhranie <code>IEnergy</code> reprezentuje prácu s energiou cestujúceho {@link Traveller}.
 * Rozhranie je odvodené od ďalších rozhraní: {@link IHunger} a {@link IToilet}.
 * V rozhraní je implementovaná aj default metóda sleep.
 *
 * @see Traveller
 * @see IHunger
 * @see IToilet
 */
public interface IEnergy extends IHunger, IToilet {
    double getEnergy();
    void decreaseEnergy();
    void resetEnergy();

    /**
     * Default metóda slúžiaca na spánok cestujúceho.
     * Spánok môže byť rôzne dlhý, a preto sa trasa skráti o náhodný počet kilometrov.
     * @param km        zostávajúci počet kilometrov
     * @return          počet kilometrov strávených spánkom
     */
    default int sleep(int km){
        Random random = new Random();
        int rnd;
        if(km < 100)
            rnd = random.nextInt(km);
        else
            rnd = random.nextInt(100);

        resetEnergy();
        increaseHunger(rnd);
        increaseToilet(rnd);
        return rnd;
    }
}
