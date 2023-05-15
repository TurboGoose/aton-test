package trees;

import java.util.HashMap;


public class Tree<T> {
    private static final int NO_MISMATCH = -1;
    private Node root;

    public Tree() {
        root = new Node(false);
    }

    private int getFirstMismatchLetter(String word, String edgeWord) {
        int LENGTH = Math.min(word.length(), edgeWord.length());
        for (int i = 1; i < LENGTH; i++) {
            if (word.charAt(i) != edgeWord.charAt(i)) {
                return i;
            }
        }
        return NO_MISMATCH;
    }

    //Helpful method to debug and to see all the words
    public void printAllWords() {
        printAllWords(root, "");
    }

    private void printAllWords(Node current, String result) {
        if (current.isLeaf) {
            System.out.println(result);
        }

        for (Edge edge : current.edges.values()) {
            printAllWords(edge.next, result + edge.label);
        }
    }

    public void insert(String word) {
        Node current = root;
        int currIndex = 0;

        //Iterative approach
        while (currIndex < word.length()) {
            char transitionChar = word.charAt(currIndex);
            Edge currentEdge = current.getTransition(transitionChar);
            //Updated version of the input word
            String currStr = word.substring(currIndex);

            //There is no associated edge with the first character of the current string
            //so simply add the rest of the string and finish
            if (currentEdge == null) {
                current.edges.put(transitionChar, new Edge(currStr));
                break;
            }

            int splitIndex = getFirstMismatchLetter(currStr, currentEdge.label);
            if (splitIndex == NO_MISMATCH) {
                //The edge and leftover string are the same length
                //so finish and update the next node as a word node
                if (currStr.length() == currentEdge.label.length()) {
                    currentEdge.next.isLeaf = true;
                    break;
                } else if (currStr.length() < currentEdge.label.length()) {
                    //The leftover word is a prefix to the edge string, so split
                    String suffix = currentEdge.label.substring(currStr.length());
                    currentEdge.label = currStr;
                    Node newNext = new Node(true);
                    Node afterNewNext = currentEdge.next;
                    currentEdge.next = newNext;
                    newNext.addEdge(suffix, afterNewNext);
                    break;
                } else { //currStr.length() > currentEdge.label.length()
                    //There is leftover string after a perfect match
                    splitIndex = currentEdge.label.length();
                }
            } else {
                //The leftover string and edge string differed, so split at point
                String suffix = currentEdge.label.substring(splitIndex);
                currentEdge.label = currentEdge.label.substring(0, splitIndex);
                Node prevNext = currentEdge.next;
                currentEdge.next = new Node(false);
                currentEdge.next.addEdge(suffix, prevNext);
            }

            //Traverse the tree
            current = currentEdge.next;
            currIndex += splitIndex;
        }
    }

    public void delete(String word) {
        root = delete(root, word);
    }

    private Node delete(Node current, String word) {
        //base case, all the characters have been matched from previous checks
        if (word.isEmpty()) {
            //Has no other edges,
            if (current.edges.isEmpty() && current != root) {
                return null;
            }
            current.isLeaf = false;
            return current;
        }

        char transitionChar = word.charAt(0);
        Edge edge = current.getTransition(transitionChar);
        //Has no edge for the current word or the word doesn't exist
        if (edge == null || !word.startsWith(edge.label)) {
            return current;
        }

        Node deleted = delete(edge.next, word.substring(edge.label.length()));
        if (deleted == null) {
            current.edges.remove(transitionChar);
            if (current.totalEdges() == 0 && !current.isLeaf && current != root) {
                return null;
            }
        } else if (deleted.totalEdges() == 1 && !deleted.isLeaf) {
            current.edges.remove(transitionChar);
            for (Edge afterDeleted : deleted.edges.values()) {
                current.addEdge(edge.label + afterDeleted.label, afterDeleted.next);
            }
        }
        return current;
    }

    public boolean search(String word) {
        Node current = root;
        int currIndex = 0;
        while (currIndex < word.length()) {
            char transitionChar = word.charAt(currIndex);
            Edge edge = current.getTransition(transitionChar);
            if (edge == null) {
                return false;
            }

            String currSubstring = word.substring(currIndex);
            if (!currSubstring.startsWith(edge.label)) {
                return false;
            }
            currIndex += edge.label.length();
            current = edge.next;
        }

        return current.isLeaf;
    }


    private class Node { //This code is nested inside the RadixTree class
        private boolean isLeaf;
        private HashMap<Character, Edge> edges;
        // TODO: add parent
        // TODO: add payload

        public Node(boolean isLeaf) {
            this.isLeaf = isLeaf;
            edges = new HashMap<>();
        }

        public Edge getTransition(char transitionChar) {
            return edges.get(transitionChar);
        }

        public void addEdge(String label, Node next) {
            edges.put(label.charAt(0), new Edge(label, next));
        }

        public int totalEdges() {
            return edges.size();
        }
    }

    private class Edge { //This code is nested inside the RadixTree class
        private String label;
        private Node next;

        public Edge(String label) {
            this(label, new Node(true));
        }

        public Edge(String label, Node next) {
            this.label = label;
            this.next = next;
        }
    }
}