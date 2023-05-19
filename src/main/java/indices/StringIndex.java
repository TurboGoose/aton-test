package indices;

import java.util.Collection;
import java.util.HashSet;

public class StringIndex<T> implements Index<String, T> {
    private final Trie<Collection<T>> trie = new Trie<>();

    @Override
    public void add(String key, T value) {
        Collection<T> present = trie.get(key);
        if (present == null) {
            present = new HashSet<>();
            trie.put(key, present);
        }
        present.add(value);
    }

    @Override
    public void delete(String key, T value) {
        Collection<T> present = trie.get(key);
        if (present == null) {
            return;
        }
        present.remove(value);
        if (present.isEmpty()) {
            trie.remove(key);
        }
    }

    @Override
    public Collection<T> get(String key) {
        return trie.get(key);
    }

    @Override
    public void update(String oldKey, String newKey) {
        Collection<T> present = trie.get(oldKey);
        trie.remove(oldKey);
        trie.put(newKey, present);
    }
}
