package name.rycroft.simon.synplex;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class Database {

    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private final Connection connection;

    @Inject
    public Database(Arguments arguments) throws SQLException, ArgumentParserException {
        connection = DriverManager.getConnection("jdbc:sqlite:/%s".formatted(arguments.databaseFilePath()));
    }

    public void update(String sql, Object... values) throws SQLException {
        PreparedStatement ps = prepareStatement(sql, values);
        int rows = ps.executeUpdate();
        logger.info("Updated {} rows", rows);
    }

    public ResultSet select(String sql, Object... values) throws SQLException {
        PreparedStatement ps = prepareStatement(sql, values);
        return ps.executeQuery();
    }

    private PreparedStatement prepareStatement(String sql, Object... values) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        for (int i = 0; i < values.length; i++) {
            switch (values[i]) {
                case String string -> ps.setString(i + 1, string);
                case Integer integer -> ps.setInt(i + 1, integer);
                case Long lng -> ps.setLong(i + 1, lng);
                default ->
                        throw new IllegalStateException("Unexpected value type: %s".formatted(values[i]));
            }
        }
        return ps;
    }
}
