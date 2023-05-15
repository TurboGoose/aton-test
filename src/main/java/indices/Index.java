package indices;

import java.util.Collection;

public interface Index<K, V> {
    void add(K key, V value);
    void delete(K key, V value);
    Collection<V> get(K key);
    void update(K oldKey, K newKey);
}
