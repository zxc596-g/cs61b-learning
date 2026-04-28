package bstmap;

import java.util.Set;

/* Your implementation BSTMap should implement this interface. To do so,
 * append "implements Map61B<K,V>" to the end of your "public class..."
 * declaration, though you can use other formal type parameters if you'd like.
 * 你编写的 BSTMap 实现类需要实现该接口。具体操作为：
在你的「public class……」类声明末尾追加「implements Map61B<K,V>」，你也可以根据需求使用其他合法的类型形参。

 */
public interface Map61B<K, V> extends Iterable<K> {

    /** Removes all of the mappings from this map.
     * 移除
     * 此映射中的所有對應關係。*/
    void clear();

    /* Returns true if this map contains a mapping for the specified key.
    * 如果此映射包含指定键的映射关系，则返回 true*/
    boolean containsKey(K key);

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     返回指定键所映射的值；若此映射中不存在该键的映射关系，则返回空值。*/
    V get(K key);

    /* Returns the number of key-value mappings in this map.
    * 返回此映射中的键值对数量。*/
    int size();

    /* Associates the specified value with the specified key in this map.
    * 将指定的值与此映射中的指定键相关联。 */
    void put(K key, V value);

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException.
     * 返回此映射中包含的键的集合视图。实验 7 无需实现该方法。
若未实现此方法，则抛出不支持操作异常。*/
    Set<K> keySet();

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException.
     * 如果该映射中存在指定键，则移除其映射关系。
第七次实验无需实现该方法。若未实现此方法，需抛出
不支持操作异常。*/
    V remove(K key);

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.
     * 仅当指定键当前映射至
指定值时，才移除该键对应的条目。实验 7 不要求实现此方法。如果未实现该方法，
则抛出不支持操作异常。*/
    V remove(K key, V value);

}
