import java.util.*;

public class DatabaseImpl implements Database {
    private final List<Account> accounts = new ArrayList<>(); // sorted by id

    @Override
    public void add(Account account) { // O(N)
        accounts.add(findIndexToInsert(account.getId()), account);
    }

    private int findIndexToInsert(long id) {
        int index = Collections.binarySearch(accounts, id, Comparator.comparingLong(
                acc -> acc instanceof Account ? ((Account) acc).getId() : (long) acc
        ));
        if (index >= 0) {
            throw new IllegalArgumentException();
        }
        return -index - 1;
    }

    @Override
    public void delete(long id) { // O(N)
        try {
            accounts.remove(findIndexOfExistingAccount(id));
        } catch (IllegalArgumentException ignore) {}
    }

    @Override
    public void update(Account account) { // O(logN)
        Account accountToUpdate = accounts.get(findIndexOfExistingAccount(account.getId()));
        accountToUpdate.setName(account.getName());
        accountToUpdate.setValue(account.getValue());
    }

    private int findIndexOfExistingAccount(long id) {
        int index = Collections.binarySearch(accounts, id, Comparator.comparingLong(
                acc -> acc instanceof Account ? ((Account) acc).getId() : (long) acc
        ));
        if (index < 0) {
            throw new IllegalArgumentException();
        }
        return index;
    }

    @Override
    public Optional<Account> getById(long id) { // O(logN)
        try {
            return Optional.of(accounts.get(findIndexOfExistingAccount(id)));
        } catch (IllegalArgumentException exc) {
            return Optional.empty();
        }
    }

    @Override
    public List<Account> getByName(String name) { // O(N)
        return accounts.stream()
                .filter(acc -> acc.getName().equals(name))
                .toList();
    }

    @Override
    public List<Account> getByValue(double value) { // O(N)
        return accounts.stream()
                .filter(acc -> acc.getValue() == value)
                .toList();
    }
}
