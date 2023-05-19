import indices.GeneralIndex;
import indices.StringIndex;
import models.Account;

import java.util.*;

public class DatabaseImpl implements Database {
    private final List<Account> accounts = new ArrayList<>(); // sorted by id
    private final StringIndex<Account> nameIndex = new StringIndex<>();
    private final GeneralIndex<Double, Account> valueIndex = new GeneralIndex<>();

    @Override
    public void add(Account account) { // O(N)
        accounts.add(findIndexToInsert(account.getId()), account);
        nameIndex.add(account.getName(), account);
        valueIndex.add(account.getValue(), account);
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
            Account deleted = accounts.remove(findIndexOfExistingAccount(id));
            nameIndex.delete(deleted.getName(), deleted);
            valueIndex.delete(deleted.getValue(), deleted);
        } catch (IllegalArgumentException ignore) {}
    }

    @Override
    public void update(Account account) { // O(logN)
        Account accountToUpdate = accounts.get(findIndexOfExistingAccount(account.getId()));
        if (!accountToUpdate.getName().equals(account.getName())) {
            nameIndex.update(accountToUpdate.getName(), account.getName());
            valueIndex.update(accountToUpdate.getValue(), account.getValue());
            accountToUpdate.setName(account.getName());
        }
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
    public Collection<Account> getByName(String name) { // O(K) where K = name.size()
        Collection<Account> accounts = nameIndex.get(name);
        return accounts == null ? new HashSet<>() : accounts;
    }

    @Override
    public Collection<Account> getByValue(double value) { // O(N)
        Collection<Account> accounts = valueIndex.get(value);
        return accounts == null ? new HashSet<>() : accounts;
    }
}
