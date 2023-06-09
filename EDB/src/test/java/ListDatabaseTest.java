import models.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DatabaseTest {
    Database db;

    @BeforeEach
    public void setUp() {
        db = new DatabaseImpl();
    }

    @Test
    public void whenAddAccountWithNonExistentIdThenAdd() {
        Account account = new Account(1, "Alex", 50.1);
        db.add(account);
        assertThat(db.getById(1).get(), is(account));
    }

    @Test
    public void whenAddAccountWithExistentIdThenThrow() {
        Account account1 = new Account(1, "Alex", 50.1);
        Account account2 = new Account(1, "Anton", 60.1);
        db.add(account1);
        assertThrows(IllegalArgumentException.class, () -> db.add(account2));
    }

    @Test
    public void whenUpdateByExistingIdThenUpdate() {
        Account account = new Account(1, "Alex", 50.1);
        db.add(account);
        Account updated = new Account(1, "Alexander", 100);
        db.update(updated);
        assertThat(db.getById(1).get(), is(updated));
    }

    @Test
    public void whenUpdateByNonExistentIdThenThrow() {
        Account updated = new Account(1, "Alex", 50.1);
        assertThrows(IllegalArgumentException.class, () -> db.update(updated));
    }

    @Test
    public void whenDeleteByExistentIdThenDelete() {
        Account account = new Account(1, "Alex", 50.1);
        db.add(account);
        db.delete(1);
        assertThat(db.getById(1).isEmpty(), is(true));
    }

    @Test
    public void whenDeleteByNonExistentIdThenNothing() {
        Account account = new Account(1, "Alex", 50.1);
        db.add(account);
        db.delete(2);
        assertThat(db.getById(1).get(), is(account));
        assertThat(db.getById(2).isEmpty(), is(true));
    }

    @Test
    public void whenGetByExistentIdThenReturnOptionalWithAccount() {
        Account account = new Account(1, "Alex", 50.1);
        db.add(account);
        assertThat(db.getById(1).get(), is(account));
    }

    @Test
    public void whenGetByNonExistentIdThenReturnEmptyOptional() {
        assertThat(db.getById(1).isEmpty(), is(true));
    }

    @Test
    public void whenGetByExistingNameThenReturnListOfAccounts() {
        Account account1 = new Account(1, "Alex", 50.1);
        Account account2 = new Account(2, "Alex", 60.1);
        db.add(account1);
        db.add(account2);
        assertThat(db.getByName("Alex"), containsInAnyOrder(account1, account2));
    }

    @Test
    public void whenGetByNonExistentNameThenEmptyList() {
        assertThat(db.getByName("Alex").isEmpty(), is(true));
    }

    @Test
    public void whenGetByExistingValueThenReturnListOfAccounts() {
        Account account1 = new Account(1, "Alex", 50.1);
        Account account2 = new Account(2, "Anton", 50.1);
        db.add(account1);
        db.add(account2);
        assertThat(db.getByValue(50.1), containsInAnyOrder(account1, account2));
    }

    @Test
    public void whenGetByNonExistentValueThenEmptyList() {
        assertThat(db.getByValue(50.1).isEmpty(), is(true));
    }
}