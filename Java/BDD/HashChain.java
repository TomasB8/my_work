import java.util.Objects;
import java.util.Random;

public class HashChain{
    int bucketNum;
    int size;
    HashData[] buckets;

    private static class HashData{
        String key;
        int value;
        Node node;
        HashData next;

        public HashData(String key, int value, Node node){
            this.key = key;
            this.value = value;
            this.node = node;
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
                        newBuckets[newKey] = new HashData(data.key, data.value, data.node);
                    }else{
                        HashData data2 = newBuckets[newKey];
                        HashData current = data2;
                        while(current != null){
                            if(current.value == data.value){
                                return;
                            }
                            current = current.next;
                        }
                        HashData newData = new HashData(data.key, data.value, data.node);
                        newData.next = data2;
                        newBuckets[newKey] = newData;
                    }
                    data = data.next;
                }

            }
        }

        this.buckets = newBuckets;
    }

    public int insert(Node node){
        int pos = hashFunction(node.getRemaining());
        if(this.buckets[pos] == null){
            this.buckets[pos] = new HashData(node.getRemaining(), node.getVariable(), node);
            this.size += 1;
        }else{
            HashData data = this.buckets[pos];
            HashData current = data;
            while(current != null){
                if(Objects.equals(current.key, node.getRemaining())){
                    return -1;
                }
                current = current.next;
            }
            HashData newData = new HashData(node.getRemaining(), node.getVariable(), node);
            newData.next = data;
            this.buckets[pos] = newData;
            this.size += 1;
        }

        if((double)this.size / this.bucketNum >= 0.75){
            resize(2 * this.bucketNum);
        }

        return 1;
    }

    public Node search(String key){
        int pos = hashFunction(key);
        if(this.buckets[pos] == null){
            return null;
        }else{
            HashData data = this.buckets[pos];
            while(data != null){
                if(Objects.equals(data.key, key)){
                    return data.node;
                }
                data = data.next;
            }
        }
        return null;
    }
}