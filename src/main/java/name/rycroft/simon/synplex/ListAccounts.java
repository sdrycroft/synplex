package name.rycroft.simon.synplex;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ListAccounts {

    private static final String LIST_ACCOUNTS_SQL = """
            SELECT id, name FROM accounts WHERE id > ?
            """;

    private final Database database;

    @Inject
    public ListAccounts(Database database) {
        this.database = database;
    }

    public Map<String, Long> listAccounts() throws SQLException {
        Map<String, Long> map;
        ResultSet resultSet = database.select(LIST_ACCOUNTS_SQL, 0);
        map = new HashMap<>();
        while (resultSet.next()) {
            map.put(resultSet.getString("name"), resultSet.getLong("id"));
        }
        return map;
    }
}
