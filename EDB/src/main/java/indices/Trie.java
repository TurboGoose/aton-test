package indices;

import java.util.HashMap;
import java.util.Map;

public class Trie<V> {
    private final Node<V> root = new Node<>();

    public void put(String key, V value) {
        Node<V> current = root;
        for (char letter : key.toCharArray()) {
            if (current.children.containsKey(letter)) {
                current = current.children.get(letter);
            } else {
                Node<V> newNode = new Node<>(letter, current);
                current.children.put(letter, newNode);
                current = newNode;
            }
        }
        current.data = value;
    }

    public void remove(String key) {
        Node<V> current = search(key);
        if (current == null) {
            return;
        }
        current.data = null;
        while (current.parent != null && current.data == null && current.children.isEmpty()) {
            Node<V> parent = current.parent;
            parent.children.remove(current.character);
            current.parent = null;
            current = parent;
        }
    }

    private Node<V> search(String key) {
        Node<V> current = root;
        for (char letter : key.toCharArray()) {
            if (!current.children.containsKey(letter)) {
                return null;
            }
            current = current.children.get(letter);
        }
        return current;
    }

    public V get(String key) {
        Node<V> lastPresent = search(key);
        return lastPresent != null && lastPresent.data != null
                ? lastPresent.data
                : null;
    }

    public Leaf getLeaf(String key) {
        return new Leaf(search(key));
    }

    private String recover(Node<V> node) {
        StringBuilder result = new StringBuilder();
        while (node.parent != null) {
            result.insert(0, node.character);
            node = node.parent;
        }
        return result.toString();
    }


    public class Leaf {
        private final Node<V> leaf;

        private Leaf(Node<V> leaf) {
            this.leaf = leaf;
        }

        public V getValue() {
            if (leaf == null) {
                return null;
            }
            return leaf.data;
        }

        public String recoverString() {
            if (leaf == null) {
                return null;
            }
            return recover(leaf);
        }
    }

    private static class Node<V> {
        Character character;
        Map<Character, Node<V>> children = new HashMap<>();
        Node<V> parent;
        V data;

        public Node() {
        }

        public Node(Character character, Node<V> parent) {
            this.character = character;
            this.parent = parent;
        }
    }
}
