import models.Account;

import java.util.Collection;
import java.util.Optional;

public interface Database {
    void add(Account account);

    void delete(long id);

    void update(Account account);

    Optional<Account> getById(long id);

    Collection<Account> getByName(String name);

    Collection<Account> getByValue(double value);
}
