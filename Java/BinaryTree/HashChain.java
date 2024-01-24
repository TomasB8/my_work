import java.util.Random;

public class HashChain{
    int bucketNum;
    int size;
    HashData[] buckets;

    private static class HashData{
        String key;
        int value;
        HashData next;

        public HashData(String key, int value){
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    public HashChain(int bucketNum){
        this.bucketNum = bucketNum;
        this.buckets = new HashData[this.bucketNum];
        this.size = 0;
        for(int i=0; i<this.bucketNum; i++){
            buckets[i] = null;
        }
    }

    public int getSize(){
        return this.size;
    }

    private int hashFunction(String key){
        return Math.abs(key.hashCode() % this.bucketNum);
    }

    private void resize(int newSize){
        HashData[] newBuckets = new HashData[newSize];
        int oldSize = this.bucketNum;
        for(int i=0; i<newSize; i++){
            newBuckets[i] = null;
        }
        this.bucketNum = newSize;

        for(int i=0; i<oldSize; i++){
            HashData data = buckets[i];
            if(data != null){
                while(data != null){

                    int newKey = hashFunction(data.key);
                    if(newBuckets[newKey] == null){
                        newBuckets[newKey] = new HashData(data.key, data.value);
                    }else{
                        HashData data2 = newBuckets[newKey];
                        HashData current = data2;
                        while(current != null){
                            if(current.value == data.value){
                                return;
                            }
                            current = current.next;
                        }
                        HashData newData = new HashData(data.key, data.value);
                        newData.next = data2;
                        newBuckets[newKey] = newData;
                    }
                    data = data.next;
                }

            }
        }

        this.buckets = newBuckets;
    }

    public void insert(String key, int value){
        int pos = hashFunction(key);
        if(this.buckets[pos] == null){
            this.buckets[pos] = new HashData(key, value);
            this.size += 1;
        }else{
            HashData data = this.buckets[pos];
            HashData current = data;
            while(current != null){
                if(current.value == value){
                    return;
                }
                current = current.next;
            }
            HashData newData = new HashData(key, value);
            newData.next = data;
            this.buckets[pos] = newData;
            this.size += 1;
        }

        if((double)this.size / this.bucketNum >= 0.75){
            resize(2 * this.bucketNum);
        }
    }

    public boolean search(String key, int value){
        int pos = hashFunction(key);
        if(this.buckets[pos] == null){
            return false;
        }else{
            HashData data = this.buckets[pos];
            while(data != null){
                if(data.value == value){
                    return true;
                }
                data = data.next;
            }
        }
        return false;
    }

    public void delete(String key, int value){
        int pos = hashFunction(key);
        if(this.buckets[pos] != null){
            HashData prevData = null;
            HashData data = this.buckets[pos];
            while(data.next != null && data.value != value){
                prevData = data;
                data = data.next;
            }
            if(data.value == value){
                if(prevData == null){
                    this.buckets[pos] = data.next;
                    this.size -= 1;
                }else{
                    prevData.next = data.next;
                    this.size -= 1;
                }
            }
        }

        if((double)this.size / this.bucketNum <= 0.25){
            resize(bucketNum / 2);
        }
    }

    public void printTable(){
        for(int i=0; i<this.bucketNum; i++){
            HashData data = this.buckets[i];
            System.out.printf("%d: ", i);
            if(data != null){
                while(data != null){
                    System.out.printf("%s (%d) -> ", data.key, data.value);
                    data = data.next;
                }
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
        HashChain table = new HashChain(10);
        int n = 10;

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