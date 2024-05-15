import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class HashMap<K, V> implements Iterable<K> {

    private int positionOfBucket = 0;
    private Bucket.Node currentNode = null;


    private Iterator<K> iterator = new Iterator<K>() {
        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public K next() {
            Bucket.Node result = currentNode;
            if (currentNode != null && currentNode.next != null) {
                currentNode = currentNode.next;
            } else {
                while (positionOfBucket < buckets.length - 1 && buckets[positionOfBucket + 1] == null) {
                    positionOfBucket++;
                }
                positionOfBucket++;
                if (positionOfBucket == buckets.length) {
                    positionOfBucket--;
                }
                if (buckets[positionOfBucket] != null) {
                    currentNode = buckets[positionOfBucket].head;
                } else {
                    currentNode = null;
                }
            }
            return result.entityValue.key;
        }
    };

    /**
     * Количество корзинок
     */
    private static final int FIXED_SIZE = 10;

    /**
     * Коэффициент увеличения корзинок
     */
    private static final double LOAD_FACTOR = 0.5;
    private Bucket[] buckets;

    /**
     * Количество элементов внутри каждой корзинки
     */
    private int size;

    @Override
    public Iterator<K> iterator() {
        return iterator;
    }

    @Override
    public void forEach(Consumer<? super K> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<K> spliterator() {
        return Iterable.super.spliterator();
    }

    class Bucket {

        /**
         * Одна сущность в корзинке для хранения ключа и значения - кубик
         */
        class Entity {
            K key;
            V value;
        }

        /**
         * Это класс для одного элемента и связки со следующим
         */
        class Node {
            Node next;
            Entity entityValue; //объект кубика
        }

        Node head;

        /**
         * Метод для добавления нового элемента в корзину
         */
        public V add(K key, V value) {
            Entity entity = new Entity();
            entity.key = key;
            entity.value = value;

            Node node = new Node();
            node.entityValue = entity; //Кладем в узел - node, текущий кубик - entity
            if (head == null) {
                head = node;
                return null; // return null because nothing has changed
            }
            Node currentNode = head;

            V returnValue = null;
            boolean isAdded = false;

            while (!isAdded) {
                if (currentNode.entityValue.key.equals(entity.key)) {
                    V buf = currentNode.entityValue.value; //Запоминаем текущее значение кубика в ноде
                    currentNode.entityValue.value = entity.value;
                    returnValue = buf; // return buf because we have changed an element with this key
                    isAdded = true;
                }
                if (currentNode.next != null) {
                    currentNode = currentNode.next; //следующая нода становится текущей
                } else {
                    currentNode.next = node;
                    isAdded = true;
                }
            }
            return returnValue;
        }

        public V get(K key) {
            Node node = head;
            while (node != null) {
                if (node.entityValue.key.equals(key))
                    return node.entityValue.value;
                node = node.next;
            }
            return null;
        }

        public V remove(K key) {
            if (head == null)
                return null;
            if (head.entityValue.key.equals(key)) {
                V buf = head.entityValue.value;
                head = head.next;
                return buf;
            } else { //if our key is not in the head we go to the next
                Node node = head; //we create a temp variable, so we can use it and change it if needed
                while (node.next != null) {
                    if (node.next.entityValue.key.equals(key)) {
                        V buf = node.next.entityValue.value;
                        node.next = node.next.next;
                        return buf;
                    }
                    node = node.next;
                }
            }
            return null;
        }


    }

    public HashMap() {
        buckets = new HashMap.Bucket[FIXED_SIZE];
    }

    public HashMap(int size) {
        buckets = new HashMap.Bucket[size];
    }


    private int calculateBucketIndex(K key) {
        return Math.abs(key.hashCode() % buckets.length); //Count hashCode of our key and divide it to length of our buckets and count result
    }

    private void recalculate() { //we prolong our bucket twice
        size = 0;
        Bucket[] old = buckets;
        buckets = new HashMap.Bucket[old.length * 2];
        for (int i = 0; i < old.length; i++) {
            Bucket bucket = old[i];
            if (bucket != null) {
                Bucket.Node node = bucket.head; //
                while (node != null) {
                    put(node.entityValue.key, node.entityValue.value);
                    node = node.next;
                }
            }
        }
    }

    public V put(K key, V value) {
        if (size >= buckets.length * LOAD_FACTOR) {
            recalculate();
        }
        int index = calculateBucketIndex(key);
        Bucket bucket = buckets[index];
        if (bucket == null) {
            bucket = new Bucket();
            buckets[index] = bucket;
        }
        V buf = bucket.add(key, value);
        if (size == 0) {
            currentNode = bucket.head;
            positionOfBucket = index;
        }
        if (index < positionOfBucket) {
            positionOfBucket = index;
        }

        if (buf == null) {
            size++;
        }
        return buf;
    }

    public V get(K key) {
        int index = calculateBucketIndex(key);
        Bucket bucket = buckets[index];
        if (bucket == null) {
            return null;
        }
        return bucket.get(key);
    }

    public V remove(K key) {
        int index = calculateBucketIndex(key);
        Bucket bucket = buckets[index];
        if (bucket == null)
            return null;
        V buf = bucket.remove(key);
        if (buf != null) {
            size--;
        }
        return buf;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("HashMap:\n");
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i] != null) {
                Bucket.Node tempNode = buckets[i].head;
                while (tempNode != null) {
                    result.append(tempNode.entityValue.key).append(":").append(tempNode.entityValue.value).append("\n");
                    tempNode = tempNode.next;
                }
            }
        }
        return result.toString();
    }


}
