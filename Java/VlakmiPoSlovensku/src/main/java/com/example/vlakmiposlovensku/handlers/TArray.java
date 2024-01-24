package com.example.vlakmiposlovensku.handlers;

/**
 * Vlastná generická trieda, ktorá slúži na ukladanie údajov typu T.
 * @param <T>
 */
public class TArray<T> {
    private final T[] array;
    private int i;

    public TArray(int size){
        array = (T[]) new Object[size];
        this.i = 0;
    }

    /**
     * Metóda vracajúca prvok poľa triedy.
     * @param index     pozícia, z ktorej má byť vrátený prvok
     * @return          prvok poľa
     */
    public T get(int index){
        return array[index];
    }

    /**
     * Metóda, ktorá vloží na určité miesto nejaký prvok.
     * @param index     pozícia, na ktorú má byť vložený prvok
     * @param value     hodnota typu T
     */
    public void set(int index, T value){
        array[index] = value;
        i++;
    }

    /**
     * Metóda vracajúca veľkosť poľa.
     * @return      počet prvkov
     */
    public int size(){
        return this.i;
    }

    /**
     * Metóda pridávajúca prvok na koniec poľa.
     * @param value     hodnota, ktorá má byť pridaná
     */
    public void add(T value){
        array[i] = value;
        i++;
    }
}
