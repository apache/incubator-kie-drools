/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.functional.timer.addon;

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
    
    @SuppressWarnings("rawtypes")
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

    @Override
    public void initialize() throws SQLException {
        // TODO Auto-generated method stub
        
    }

}
