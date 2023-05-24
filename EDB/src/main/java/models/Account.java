package models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Account {
    private final long id; // primary key, sorted -> binary search
    private String name; // trie
    private double value; // rb tree (TreeMap)
}
