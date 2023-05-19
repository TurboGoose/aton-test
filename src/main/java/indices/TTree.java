package indices;

import java.util.Arrays;
import java.util.Random;
import java.util.Stack;
import java.util.StringTokenizer;

public class TTree {
    private Node root;
    private Boolean fixed;
    private Stack<Node> newNodes;

    public TTree() {
        root = new Node(null);
        Node t = new Node(root);
        root.setLeft(t);

        fixed = false;
        newNodes = new Stack<>();
    }

    public boolean search(int x) {
        return search(root.getLeft(), x);
    }

    private boolean search(Node curr, int x) {
        if (curr == null){
            return false;
        }
        int t = curr.isBoundingNode(x);
        if (t == 0) {
            return curr.isContains(x);
        }
        if (t < 0) {
            return search(curr.getLeft(), x);
        }
        return search(curr.getRight(), x);
    }

    public void insert(int x) {
        insert(root.getLeft(), x);
    }

    private void preFix(Node child) {
        newNodes.add(child);
        if (!fixed) {
            fixed = true;
            fixTree();
        }
    }

    private void insert(Node curr, int x) {
        int t = curr.isBoundingNode(x);
        if (t == 0) {
            if (curr.isContains(x)) {
                return;
            }
            if (curr.getLength() != Node.MAX_ELEMENTS) {
                curr.insert(x);
                return;
            }
            int min = curr.getMinimum();
            curr.delete(min);
            curr.insert(x);
            if (curr.getLeft() != null) {
                insert(curr.getLeft(), min);
                return;
            }
            Node child = new Node(curr);
            child.insert(min);
            curr.setLeft(child);
            preFix(child);
            return;
        }
        if (t > 0) {
            insertRight(curr, x);
            return;
        }
        insertLeft(curr, x);
    }

    private void insertRight(Node curr, int x) {
        if (curr.getRight() != null) {
            insert(curr.getRight(), x);
            return;
        }
        if (curr.getLength() != Node.MAX_ELEMENTS) {
            curr.insert(x);
            return;
        }
        Node child = new Node(curr);
        child.insert(x);
        curr.setRight(child);
        preFix(child);
    }

    private void insertLeft(Node curr, int x) {
        if (curr.getLeft() != null) {
            insert(curr.getLeft(), x);
            return;
        }
        if (curr.getLength() != Node.MAX_ELEMENTS) {
            curr.insert(x);
            return;
        }
        Node child = new Node(curr);
        child.insert(x);
        curr.setLeft(child);
        preFix(child);
    }

    private void fixTree() {
        while (!newNodes.isEmpty()) {
            Node curr = newNodes.pop();
            while (curr != root) {
                curr.rebalance();
                curr = curr.getParent();
            }
        }
        fixed = false;
    }

    public String toString() {
        return getAll(root.getLeft());
    }

    private String getAll(Node curr) {
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

    private boolean isBalanced(Node curr) {
        if (curr == null) {
            return true;
        }
        int b = curr.getBalance();
        return (b < 2) && (b > -2) && isBalanced(curr.getLeft()) && isBalanced(curr.getRight());
    }
}


class Node {
    static final int MIN_ELEMENTS = 30, MAX_ELEMENTS = 2 * MIN_ELEMENTS;
    private int[] data;
    private int len;
    private Node left, right, parent;
    private int h, balance;

    public Node(Node parent) {
        len = 0;
        data = new int[MAX_ELEMENTS];
        this.parent = parent;
        h = 1;
        balance = 0;
    }

    public int isBoundingNode(int x) {
        if (len == 0) {
            return 0;
        }
        if (x < data[0]) {
            return -1;
        }
        if (x > data[len - 1]) {
            return 1;
        }
        return 0;
    }

    public void insert(int x) {
        if (len == MAX_ELEMENTS) {
            return;
        }
        int i = len;
        while (i > 0 && data[i - 1] > x) {
            data[i] = data[i - 1];
            i--;
        }
        data[i] = x;
        len++;
    }

    public void delete(int x) {
        int i = 0;
        while (data[i] != x && i < len) {
            i++;
        }
        if (i == len) {
            return;
        }
        while (i < len - 1) {
            data[i] = data[i + 1];
            i++;
        }
        len--;
    }

    public boolean isContains(int x) {
        return Arrays.binarySearch(data, 0, len, x) >= 0;
    }

    public int getLength() {
        return len;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < len; i++) {
            result.append(data[i]).append(" ");
        }
        return result.toString();
    }

    public boolean isLeaf() {
        return left == null || right == null;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public Node getParent() {
        return parent;
    }

    public void setLeft(Node x) {
        left = x;
        updateBalance();
    }

    public void setRight(Node x) {
        right = x;
        updateBalance();
    }

    public int getMinimum() {
        return data[0];
    }

    public int getMaximum() {
        return data[len - 1];
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
                if (right.left.isLeaf() && right.right == null && left == null && right.left.len == 1) {
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
                if (left.right.isLeaf() && left.left == null && right == null && left.right.len == 1) {
                    replaceMax(left, left.right);
                }
                left.leftRotation();
                rightRotation();
            }
        }
    }

    private void replaceMin(Node from, Node to) {
        while (from.len != 1) {
            to.insert(from.getMinimum());
            from.delete(from.getMinimum());
        }
    }

    private void replaceMax(Node from, Node to) {
        while (from.len != 1) {
            to.insert(from.getMaximum());
            from.delete(from.getMaximum());
        }
    }

    private void leftRotation() {
        Node p = parent;
        Node r = right;

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
        Node p = parent;
        Node l = left;

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


class Test {

    private TTree tree = new TTree();
    private boolean[] wasAdded;

    void insert() {
        Random rand = new Random();
        for (int i = 0; i < 40000; i++) {
            int t = rand.nextInt(300000);
            tree.insert(t);
            wasAdded[t] = true;
            //System.out.println(i + " Insert " + t);
        }
    }

    void search() {
        Random rand = new Random();
        for (int i = 0; i < 20000; i++){
            int t = rand.nextInt(300000);
            tree.search(t);
            //System.out.println(i + " Search " + t + " - " + tree.search(t));
        }
    }

    public void runRandomTest() {
        wasAdded = new boolean[300000];
        insert();
        search();
        boolean f = check(tree);
        if (!f) {
            System.out.println("ERROR!!!");
            System.out.println(tree);
            System.exit(1);
        }
        else {
            System.out.println("OK");
        }
    }

    private boolean check(TTree tree) {
        return tree.isBalanced() && isSortedAndNoRepeat(tree);
    }

    private boolean isSortedAndNoRepeat(TTree tree) {
        String s = tree.toString();
        StringTokenizer tokenizer = new StringTokenizer(s);
        int prev = -1;
        while (tokenizer.hasMoreTokens()) {
            int curr = Integer.parseInt(tokenizer.nextToken());
            if (curr <= prev) {
                return false;
            }
            prev = curr;
        }
        return true;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 300; i++){
            System.out.println("Random Test #" + i);
            new Test().runRandomTest();
        }
    }
}