package cz.fi.muni.pv217.employee.management.extensions;

import io.quarkus.test.junit.callback.QuarkusTestAfterEachCallback;
import io.quarkus.test.junit.callback.QuarkusTestMethodContext;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;


/**
 * Quarkus sucks at cleaning up DB when doing integration tests
 * https://stackoverflow.com/questions/8523423/reset-embedded-h2-database-periodically
 */
public class H2DatabaseCleaner implements QuarkusTestAfterEachCallback {
    DataSource dataSource;

    public H2DatabaseCleaner() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:tcp://localhost/mem:test");
        ds.setPassword("test");
        ds.setUser("test");
        dataSource = ds;
    }

    @Override
    public void afterEach(QuarkusTestMethodContext context) {
        try {
            Connection c = dataSource.getConnection();
            Statement s = c.createStatement();

            // Disable FK
            s.execute("SET REFERENTIAL_INTEGRITY FALSE");

            // Find all tables and truncate them
            Set<String> tables = new HashSet<>();
            ResultSet rs = s.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'");
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
            rs.close();
            for (String table : tables) {
                s.executeUpdate("TRUNCATE TABLE " + table);
            }

            // Idem for sequences
            Set<String> sequences = new HashSet<String>();
            rs = s.executeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'");
            while (rs.next()) {
                sequences.add(rs.getString(1));
            }
            rs.close();
            for (String seq : sequences) {
                s.executeUpdate("ALTER SEQUENCE " + seq + " RESTART WITH 1");
            }

            // Enable FK
            s.execute("SET REFERENTIAL_INTEGRITY TRUE");
            s.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
