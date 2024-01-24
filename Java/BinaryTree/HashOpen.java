import java.util.Objects;
import java.util.Random;

public class HashOpen{
    int capacity;
    int size;
    HashData[] slots;

    private static class HashData{
        String key;
        int value;

        public HashData(String key, int value){
            this.key = key;
            this.value = value;
        }
    }

    public HashOpen(int bucketNum){
        this.capacity = bucketNum;
        this.slots = new HashData[this.capacity];
        this.size = 0;
        for(int i=0; i<this.capacity; i++){
            slots[i] = null;
        }
    }

    public int getSize(){
        return this.size;
    }

    private int hashFunction(String key){
        return Math.abs(key.hashCode() % this.capacity);
    }

    private int findPosition(HashData[] slots, String key, int value){
        int pos;
        int x = hashFunction(key);
        for(int i=0; i<this.capacity; i++){
            pos = (x+i) % this.capacity;
            if(slots[pos]!=null && (slots[pos].value == value)){
                break;
            }

            if(slots[pos] == null || (slots[pos].key.equals("deleted") && slots[pos].value == 0)){
                return pos;
            }
        }
        return -1;
    }

    private void resize(int newSize){
        HashData[] newSlots = new HashData[newSize];
        int oldSize = this.capacity;
        for(int i=0; i<newSize; i++){
            newSlots[i] = null;
        }
        this.capacity = newSize;

        for(int i=0; i<oldSize; i++){
            HashData data = this.slots[i];
            if(data != null && data.value != 0 && !Objects.equals(data.key, "deleted")){
                int newKey = findPosition(newSlots, data.key, data.value);
                if(newKey != -1){
                    newSlots[newKey] = data;
                }
            }
        }

        this.slots = newSlots;
    }

    public void insert(String key, int value){
        int pos = findPosition(this.slots, key, value);
        if(pos == -1)
            return;

        this.slots[pos] = new HashData(key, value);
        this.size++;

        if((double)this.size / this.capacity >= 0.75){
            resize(2 * this.capacity);
        }
    }

    public boolean search(String key, int value){
        int hash = hashFunction(key);
        if(this.slots[hash] == null){
            return false;
        }else{
            for(int i=0; i<this.capacity; i++){
                int pos = (hash+i)%this.capacity;
                if(this.slots[pos] != null && (this.slots[pos].value == value)){
                    return true;
                }
            }
        }
        return false;
    }

    public void delete(String key, int value){
        int hash = hashFunction(key);
        for(int i=0; i<this.capacity; i++){
            int pos = (hash+i)%this.capacity;
            if(this.slots[pos] != null && Objects.equals(this.slots[pos].value, value)){
                this.slots[pos] = new HashData("deleted", 0);
                this.size--;
                break;
            }
        }

        if((double)this.size / this.capacity <= 0.25){
            resize(this.capacity / 2);
        }
    }

    public void printTable(){
        for(int i=0; i<this.capacity; i++){
            HashData data = this.slots[i];
            System.out.printf("%d -> ", i);
            if(data != null && data.value != 0 && !Objects.equals(data.key, "deleted")){
                System.out.printf("%s -> ", data.key);
                System.out.printf("%d", data.value);
            }
            System.out.println();
        }
    }

    public static String randomString(int length){
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";

        String alphabet = upper + lower + numbers;

        StringBuilder sb = new StringBuilder();

        Random random = new Random();

        for(int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());

            char randChar = alphabet.charAt(index);

            sb.append(randChar);
        }

        return sb.toString();
    }

    public static String[] randomStrings(){
        String[] pole = new String[1000000];

        for(int i=0; i<1000000; i++){
            pole[i] = randomString(10);
        }

        return pole;
    }

    public static void main(String[] args) {
        HashOpen table = new HashOpen(10);
        int n = 1000000;

        int[] pole = new int[n];
        for(int i=0; i<n; i++){
            int random = (int)(Math.random()*10000000);
            pole[i] = random;
        }

        String[] randString = randomStrings();
        for(int i=0; i<10; i++){
            table.insert(randString[i], pole[i]);
        }

        table.printTable();
    }
}