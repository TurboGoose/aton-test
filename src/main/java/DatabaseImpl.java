import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseImpl implements Database {
    private final List<Account> data = new ArrayList<>();

    @Override
    public void add(Account account) { // O(1)
        data.add(account);
    }

    @Override
    public void delete(long id) { // O(N)
        data.removeIf(acc -> acc.getId() == id);
    }

    @Override
    public void update(Account account) { // O(N)
        Account accountToUpdate = null;
        for (Account acc : data) {
            if (acc.getId() == account.getId()) {
                accountToUpdate = acc;
                break;
            }
        }
        if (accountToUpdate == null) {
            return;
        }
        accountToUpdate.setName(account.getName());
        accountToUpdate.setValue(account.getValue());
    }

    @Override
    public Optional<Account> getById(long id) { // O(N)
        return data.stream()
                .filter(acc -> acc.getId() == id)
                .findFirst();
    }

    @Override
    public List<Account> getByName(String name) { // O(N)
        return data.stream()
                .filter(acc -> acc.getName().equals(name))
                .toList();
    }

    @Override
    public List<Account> getByValue(double value) { // O(N)
        return data.stream()
                .filter(acc -> acc.getValue() == value)
                .toList();
    }
}
