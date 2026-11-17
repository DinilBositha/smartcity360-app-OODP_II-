package com.dinilbositha.smartcity360.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class DBConnection {

    private static volatile DBConnection instance;
    private final DataSource dataSource;

    private DBConnection() {
        this.dataSource = initDataSource();
    }

    private static DataSource initDataSource() {
        // 1. Check for JNDI data sources (e.g. if deployed in WildFly / Tomcat)
        String[] jndiNames = {
                "java:comp/env/jdbc/SmartCity360DS",
                "jdbc/SmartCity360DS",
                "java:jboss/datasources/SmartCity360DS"
        };

        for (String jndiName : jndiNames) {
            try {
                Context initCtx = new InitialContext();
                DataSource ds = (DataSource) initCtx.lookup(jndiName);
                if (ds != null) {
                    System.out.println("[DBConnection] Connected using JNDI DataSource: " + jndiName);
                    return ds;
                }
            } catch (Exception ignored) {
                // Not found in JNDI, proceed to fallback
            }
        }

        // 2. Direct MySQL Connection Pool with HikariCP
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://localhost:3306/smartcity360?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8");
            config.setUsername("root");
            config.setPassword("Db24800123");

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(30000);
            config.setPoolName("SmartCity360HikariPool");

            HikariDataSource ds = new HikariDataSource(config);
            System.out.println("[DBConnection] Connected to MySQL database 'smartcity360' via HikariCP.");
            return ds;
        } catch (Throwable t) {
            System.err.println("[DBConnection] HikariCP setup: " + t.getMessage() + ". Using MysqlDataSource fallback.");
            try {
                com.mysql.cj.jdbc.MysqlDataSource ds = new com.mysql.cj.jdbc.MysqlDataSource();
                ds.setUrl("jdbc:mysql://localhost:3306/smartcity360?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8");
                ds.setUser("root");
                ds.setPassword("Db24800123");
                System.out.println("[DBConnection] Connected to MySQL database 'smartcity360' via MysqlDataSource.");
                return ds;
            } catch (Throwable ex) {
                throw new RuntimeException("Failed to initialize database connection to smartcity360: " + ex.getMessage(), ex);
            }
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource not initialized.");
        }
        return dataSource.getConnection();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void close(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }
}
