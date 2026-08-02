package hashmap;

import java.util.*;


/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */

    public static final int initialSize = 16;

    public static final double loadFactor = 0.75;

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    protected class Node {
        final K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!

    private int size = 0;

    private double maxLoad = loadFactor;

    /** Constructors */
    public MyHashMap() {
//        size = 0;
//        this.maxLoad = loadFactor;
//        this.buckets = createTable(initialSize);
//        for(int i = 0; i<buckets.length; i++) {
//            buckets[i] = createBucket();
//        }
        this(initialSize, loadFactor);
    }

    public MyHashMap(int initialSize) {
//        size = 0;
//        this.maxLoad = loadFactor;
//        this.buckets = createTable(initialSize);
//        for(int i = 0; i<buckets.length; i++) {
//            buckets[i] = createBucket();
//        }
        this(initialSize, loadFactor);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        size = 0;
        this.maxLoad = maxLoad;
        this.buckets = createTable(initialSize);
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i] != null) {
                buckets[i].clear();
            }
        }
        this.size = 0;
    }

    private Node getNode(K key) {
        if (key == null) {
            return null;
        }
        int index = Math.floorMod(key.hashCode(), buckets.length);
        if (buckets[index] != null) {
            for (Node current : buckets[index]) {
                if (current.key.equals(key)) {
                    return current;
                }
            }
        }
        return null;
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     */
    @Override
    public boolean containsKey(K key) {
        Node cur = getNode(key);
        if (cur == null) {
            return false;
        }
        return true;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        Node cur = getNode(key);
        if (cur == null) {
            return null;
        }
        ;
        return cur.value;
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    @Override
    public int size() {
        return this.size;
    }

    private void resize() {
        Collection<Node>[] newbuckets = createTable(buckets.length * 2);
        for (int i = 0; i < newbuckets.length; i++) {
            newbuckets[i] = createBucket();
        }
        for (int j = 0; j < buckets.length; j++) {
            if (buckets[j] != null) {
                for (Node cur : buckets[j]) {
                    int index = Math.floorMod(cur.key.hashCode(), newbuckets.length);
                    newbuckets[index].add(cur);
                }
            }
        }
        buckets = newbuckets;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key can not be null!");
        }
        Node cur = getNode(key);
        if (cur != null) {
            cur.value = value;
            return;
        }
        int index = Math.floorMod(key.hashCode(), buckets.length);
        cur = createNode(key, value);
        if (buckets[index] == null) {
            buckets[index] = createBucket();
        }
        buckets[index].add(cur);
        this.size++;
        if ((double) size / buckets.length > maxLoad) {
            resize();
        }
    }

    /**
     * Returns a Set view of the keys contained in this map.
     */
    @Override
    public Set<K> keySet() {
        Set<K> mapset = new HashSet<>();
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i] != null) {
                for (Node cur : buckets[i]) {
                    mapset.add(cur.key);
                }
            }
        }
        return mapset;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    @Override
    public V remove(K key) {
        if (key == null) {
            return null;
        }
        int index = Math.floorMod(key.hashCode(), buckets.length);
        Node current = null;
        if (buckets[index] != null) {
            for (Node cur : buckets[index]) {
                if (cur.key.equals(key)) {
                    current = cur;
                    break;
                }
            }
        }
        if (current != null) {
            buckets[index].remove(current);
            size--;
            return current.value;
        }
        return null;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    @Override
    public V remove(K key, V value) {
        if (key == null) {
            return null;
        }
        int index = Math.floorMod(key.hashCode(), buckets.length);
        Node current = null;
        if (buckets[index] != null) {
            for (Node cur : buckets[index]) {
                if (cur.key.equals(key)) {
                    current = cur;
                    break;
                }
            }
        }
        if (current != null) {
            if (current.value == value) {
                buckets[index].remove(current);
                size--;
                return current.value;
            }
        }
        return null;
    }
}
