package org.jbpm.test.timer.quartz;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.quartz.utils.ConnectionProvider;

public class NonTransactionalConnectionProvider implements ConnectionProvider {

    private String driverClassName;
    private String url;
    private String user;
    private String password;
    
    @Override
    public Connection getConnection() throws SQLException {
        Class driverClazz;
        try {
            driverClazz = Class.forName(driverClassName);

            Driver driver = (Driver) driverClazz.newInstance();
            Properties props = new Properties();
            if (user != null) {
                props.setProperty("user", user);
            }
            if (password != null) {
                props.setProperty("password", password);
            }
            Connection connection = driver.connect(url, props);
            
            return connection;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void shutdown() throws SQLException {
 
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
