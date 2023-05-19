package indices;

import java.util.*;

public class TTree<K extends Comparable<K>, V> {
    private final Node<K, V> root;
    private Boolean fixed;
    private final Stack<Node<K, V>> newNodes;

    public TTree() {
        root = new Node<>(null);
        Node<K, V> t = new Node<>(root);
        root.setLeft(t);

        fixed = false;
        newNodes = new Stack<>();
    }

    public V search(K key) {
        return search(root.getLeft(), key);
    }

    private V search(Node<K, V> curr, K key) {
        if (curr == null){
            return null;
        }
        int t = curr.isBoundingNode(key);
        if (t == 0) {
            return curr.get(key);
        }
        if (t < 0) {
            return search(curr.getLeft(), key);
        }
        return search(curr.getRight(), key);
    }

    public void insert(K key, V value) {
        insert(root.getLeft(), key, value);
    }

    private void preFix(Node<K, V> child) {
        newNodes.add(child);
        if (!fixed) {
            fixed = true;
            fixTree();
        }
    }

    private void insert(Node<K, V> curr, K key, V value) {
        int t = curr.isBoundingNode(key);
        if (t == 0) {
            if (curr.get(key) != null) {
                return;
            }
            if (curr.getLength() != Node.MAX_ELEMENTS) {
                curr.insert(key, value);
                return;
            }
            K minKey = curr.getMinKey();
            V minVal = curr.getMinValue();
            curr.delete(minKey);
            curr.insert(key, value);
            if (curr.getLeft() != null) {
                insert(curr.getLeft(), minKey, minVal);
                return;
            }
            Node<K, V> child = new Node<>(curr);
            child.insert(minKey, minVal);
            curr.setLeft(child);
            preFix(child);
            return;
        }
        if (t > 0) {
            insertRight(curr, key, value);
            return;
        }
        insertLeft(curr, key, value);
    }

    private void insertRight(Node<K, V> curr, K key, V value) {
        if (curr.getRight() != null) {
            insert(curr.getRight(), key, value);
            return;
        }
        if (curr.getLength() != Node.MAX_ELEMENTS) {
            curr.insert(key, value);
            return;
        }
        Node<K, V> child = new Node<>(curr);
        child.insert(key, value);
        curr.setRight(child);
        preFix(child);
    }

    private void insertLeft(Node<K, V> curr, K key, V value) {
        if (curr.getLeft() != null) {
            insert(curr.getLeft(), key, value);
            return;
        }
        if (curr.getLength() != Node.MAX_ELEMENTS) {
            curr.insert(key, value);
            return;
        }
        Node<K, V> child = new Node<>(curr);
        child.insert(key, value);
        curr.setLeft(child);
        preFix(child);
    }

    private void fixTree() {
        while (!newNodes.isEmpty()) {
            Node<K, V> curr = newNodes.pop();
            while (curr != root) {
                curr.rebalance();
                curr = curr.getParent();
            }
        }
        fixed = false;
    }

    @Override
    public String toString() {
        return getAll(root.getLeft());
    }

    private String getAll(Node<K, V> curr) {
        StringBuilder res = new StringBuilder();
        if (curr.getLeft() != null) {
            res.append(getAll(curr.getLeft()));
        }
        res.append(curr);
        if (curr.getRight() != null)
            res.append(getAll(curr.getRight()));
        return res.toString();
    }

    public boolean isBalanced() {
        return isBalanced(root.getLeft());
    }

    private boolean isBalanced(Node<K, V> curr) {
        if (curr == null) {
            return true;
        }
        int b = curr.getBalance();
        return (b < 2) && (b > -2) && isBalanced(curr.getLeft()) && isBalanced(curr.getRight());
    }


    static class Node<K extends Comparable<K>, V> {
        static final int MIN_ELEMENTS = 30, MAX_ELEMENTS = 2 * MIN_ELEMENTS;
        private final List<K> keys;
        private final List<V> values;
        private Node<K, V> left, right, parent;
        private int h, balance;

        public Node(Node<K, V> parent) {
            keys = new ArrayList<>(MAX_ELEMENTS);
            values = new ArrayList<>(MAX_ELEMENTS);
            this.parent = parent;
            h = 1;
            balance = 0;
        }

        public int isBoundingNode(K key) {
            if (keys.isEmpty()) {
                return 0;
            }
            if (key.compareTo(keys.get(0)) < 0) {
                return -1;
            }
            if (key.compareTo(keys.get(keys.size() - 1)) > 0) {
                return 1;
            }
            return 0;
        }

        public void insert(K key, V value) {
            if (keys.size() == MAX_ELEMENTS) {
                return;
            }
            int insertIndex = -Collections.binarySearch(keys, key) - 1;
            keys.add(insertIndex, key);
            values.add(insertIndex, value);
        }

        public void delete(K key) {
            int index = Collections.binarySearch(keys, key);
            if (index < 0) {
                return;
            }
            keys.remove(index);
            values.remove(index);
        }

        public V get(K key) {
            int index = Collections.binarySearch(keys, key);
            return index >= 0 ? values.get(index) : null;
        }

        public int getLength() {
            return keys.size();
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < keys.size(); i++) {
                result.append(keys.get(i)).append(" : ").append(values.get(i)).append("\n");
            }
            return result.toString();
        }

        public boolean isLeaf() {
            return left == null || right == null;
        }

        public Node<K, V> getLeft() {
            return left;
        }

        public Node<K, V> getRight() {
            return right;
        }

        public Node<K, V> getParent() {
            return parent;
        }

        public void setLeft(Node<K, V> x) {
            left = x;
            updateBalance();
        }

        public void setRight(Node<K, V> x) {
            right = x;
            updateBalance();
        }

        public K getMinKey() {
            return keys.get(0);
        }

        public K getMaxKey() {
            return keys.get(keys.size() - 1);
        }

        public V getMinValue() {
            return values.get(0);
        }

        public V getMaxValue() {
            return values.get(keys.size() - 1);
        }

        public int getBalance() {
            return balance;
        }

        private void updateBalance() {
            int l = 0, r = 0;
            if (left != null) {
                l = left.h;
            }
            if (right != null) {
                r = right.h;
            }
            h = 1 + Math.max(l, r);
            balance = l - r;
        }

        public void rebalance() {
            updateBalance();
            if (balance == -2) {
                if (right.balance == -1) {
                    leftRotation();
                }
                else {
                    if (right.left.isLeaf() && right.right == null && left == null && right.left.keys.size() == 1) {
                        replaceMin(right, right.left);
                    }
                    right.rightRotation();
                    leftRotation();
                }
            } else if (balance == 2) {
                if (left.balance == 1) {
                    rightRotation();
                }
                else {
                    if (left.right.isLeaf() && left.left == null && right == null && left.right.keys.size() == 1) {
                        replaceMax(left, left.right);
                    }
                    left.leftRotation();
                    rightRotation();
                }
            }
        }

        private void replaceMin(Node<K, V> from, Node<K, V> to) {
            while (from.keys.size() != 1) {
                to.insert(from.getMinKey(), from.getMinValue());
                from.delete(from.getMinKey());
            }
        }

        private void replaceMax(Node<K, V> from, Node<K, V> to) {
            while (from.keys.size() != 1) {
                to.insert(from.getMaxKey(), from.getMaxValue());
                from.delete(from.getMaxKey());
            }
        }

        private void leftRotation() {
            Node<K, V> p = parent;
            Node<K, V> r = right;

            right = r.left;
            if (r.left != null) {
                r.left.parent = this;
            }
            r.left = this;
            parent = r;
            if (p.left == this) {
                p.left = r;
            }
            else {
                p.right = r;
            }
            r.parent = p;
            updateBalance();
            r.updateBalance();
        }

        private void rightRotation() {
            Node<K, V> p = parent;
            Node<K, V> l = left;

            left = l.right;
            if (l.right != null) {
                l.right.parent = this;
            }
            l.right = this;
            parent = l;
            if (p.left == this) {
                p.left = l;
            }
            else {
                p.right = l;
            }
            l.parent = p;
            updateBalance();
            l.updateBalance();
        }
    }
}