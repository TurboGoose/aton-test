package indices;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class GeneralIndex<K extends Comparable<K>, V> implements Index<K, V> {
    private final Map<K, Collection<V>> map = new TreeMap<>();

    @Override
    public void add(K key, V value) {
        Collection<V> accounts = map.computeIfAbsent(key, k -> new HashSet<>());
        accounts.add(value);
    }

    @Override
    public void delete(K key, V value) {
        Collection<V> present = map.get(key);
        if (present == null) {
            return;
        }
        present.remove(value);
        if (present.isEmpty()) {
            map.remove(key);
        }
    }

    @Override
    public Collection<V> get(K key) {
        return map.get(key);
    }

    @Override
    public void update(K oldKey, K newKey) {
        Collection<V> present = map.get(oldKey);
        map.remove(oldKey);
        map.put(newKey, present);
    }
}
