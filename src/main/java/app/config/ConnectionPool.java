package app.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class ConnectionPool {

    private static final String URL = "jdbc:postgresql://localhost:5432/fog_carport";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";

    private static HikariDataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();

            config.setJdbcUrl(URL);
            config.setUsername(USERNAME);
            config.setPassword(PASSWORD);
            config.setMaximumPoolSize(10);

            dataSource = new HikariDataSource(config);
        }

        return dataSource;
    }
}