import java.util.List;
import java.util.Optional;

public interface Database {
    void add(Account account);

    void delete(long id);

    void update(Account account);

    Optional<Account> getById(long id);

    List<Account> getByName(String name);

    List<Account> getByValue(double value);
}
