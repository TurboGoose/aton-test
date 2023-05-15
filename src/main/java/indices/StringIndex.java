package indices;

import java.util.Collection;
import java.util.HashSet;

public class StringIndex<T> implements Index<String, T> {
    private final RadixTree<Collection<T>> tree = new RadixTree<>();

    @Override
    public void add(String key, T value) {
        Collection<T> present = tree.get(key);
        if (present == null) {
            present = new HashSet<>();
            tree.put(key, present);
        }
        present.add(value);
    }

    @Override
    public void delete(String key, T value) {
        Collection<T> present = tree.get(key);
        if (present == null) {
            return;
        }
        present.remove(value);
        if (present.isEmpty()) {
            tree.remove(key);
        }
    }

    @Override
    public Collection<T> get(String key) {
        return tree.get(key);
    }

    @Override
    public void update(String oldKey, String newKey) {
        Collection<T> present = tree.get(oldKey);
        tree.remove(oldKey);
        tree.put(newKey, present);
    }
}
