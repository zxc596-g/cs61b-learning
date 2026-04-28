package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private class Node {
        private K key;
        private V val;
        private Node left, right;

        public Node(K k, V v) {
            key = k;
            val = v;
        }
    }
    private Node root;
    private int size = 0;

    public BSTMap() { }

    @Override
    public void clear() {

    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to containsKey() is null");
        }
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        return get(root, key);
    }

    private V get(Node cur, K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls get() with a null key");
        }
        if (cur == null) {
            return null;
        }
        int cmp = cur.key.compareTo(key);
        if (cmp == 0) {
            return cur.val;
        } else if (cmp > 0) {
            return get(cur.left, key);
        } else {
            return get(cur.right, key);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("calls put() with a null key or null value");
        }
        /*if (containsKey(key)) {
            throw new IllegalArgumentException("key is already exist");
            put(root,key,value);
        }*/
        root = put(root, key, value);
    }

    private Node put(Node root, K key, V value) {
        if (root == null) {
            return new Node(key, value);
        }
        int cmp = root.key.compareTo(key);
        if (cmp > 0) {
            root.left = put(root.left, key, value);
        } else if (cmp < 0) {
            root.right = put(root.right, key, value);
        } else {
            root.val = value;
        }
        return root;
    }

    @Override
    public Set<K> keySet() {
    }

    @Override
    public V remove(K key) {
    }

    @Override
    public V remove(K key, V value) {

    }

    @Override
    public Iterator<K> iterator() {
        return null;
    }
}
