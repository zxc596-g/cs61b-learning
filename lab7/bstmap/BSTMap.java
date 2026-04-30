package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private class Node {
        private K key;
        private V val;
        private Node left, right;
        private int size = 0;

        public Node(K k, V v) {
            key = k;
            val = v;
            this.size = 1;
        }
    }
    private Node root;


    public BSTMap() { }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to containsKey() is null");
        }
        return getNodebyKey(root, key) != null;
    }
    private Node getNodebyKey(Node n,K key) {
        if (n == null) {
            return null;
        }
        int cmp = n.key.compareTo(key);
        if (cmp == 0) {
            return n;
        } else if (cmp < 0) {
            return getNodebyKey(n.right,key);
        } else {
            return getNodebyKey(n.left,key);
        }
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
        return size(root);
    }

    public int size(Node n){
        if (n == null){
            return 0;
        } else {
            return n.size;
        }
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with a null key");
        }
        /*if (containsKey(key))
            throw new IllegalArgumentException("key is already exist");
            put(root,key,value);
        }*/
        root = put(root, key, value);
    }

    private Node put(Node n, K key, V value) {
        if (n == null) {
            return new Node(key, value);
        }
        int cmp = n.key.compareTo(key);
        if (cmp > 0) {
            n.left = put(n.left, key, value);
        } else if (cmp < 0) {
            n.right = put(n.right, key, value);
        } else {
            n.val = value;
        }
        n.size = 1 + size(n.left) + size(n.right);
        return n;
    }

    @Override
    public Set<K> keySet() {
        throw new IllegalArgumentException("Not required for lab7");
    }

    @Override
    public V remove(K key) {
        throw new IllegalArgumentException("Not required for lab7");
    }

    @Override
    public V remove(K key, V value) {
        throw new IllegalArgumentException("Not required for lab7");

    }

    @Override
    public Iterator<K> iterator() {
        return null;
    }
}
