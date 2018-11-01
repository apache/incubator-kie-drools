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

package org.jbpm.services.task.identity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Properties;

import org.jbpm.persistence.util.PersistenceUtil;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DBUserGroupCallbackImplTest {

    protected static final String DATASOURCE_PROPERTIES = "/datasource.properties";
    private PoolingDataSource pds;
    private Properties props;

    @Before
    public void setup() {

        Properties dsProps = loadDataSourceProperties();

        pds = new PoolingDataSource();
        pds.setUniqueName("jdbc/jbpm-ds");
        pds.setClassName(dsProps.getProperty("className"));
        for (String propertyName : new String[]{"user", "password"}) {
            pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
        }
        setDatabaseSpecificDataSourceProperties(pds, dsProps);

        pds.init();

        prepareDb();

        props = new Properties();
        props.setProperty(DBUserGroupCallbackImpl.DS_JNDI_NAME, "jdbc/jbpm-ds");
        props.setProperty(DBUserGroupCallbackImpl.PRINCIPAL_QUERY, "select userId from Users where userId = ?");
        props.setProperty(DBUserGroupCallbackImpl.ROLES_QUERY, "select groupId from Groups where groupId = ?");
        props.setProperty(DBUserGroupCallbackImpl.USER_ROLES_QUERY, "select groupId from Groups where userId = ?");
    }

    protected Properties loadDataSourceProperties() {

        InputStream propsInputStream = getClass().getResourceAsStream(DATASOURCE_PROPERTIES);

        Properties dsProps = new Properties();
        if (propsInputStream != null) {
            try {
                dsProps.load(propsInputStream);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return dsProps;
    }

    protected void prepareDb() {
        try {
            Connection conn = pds.getConnection();
            String createUserTableSql = "create table Users (userId varchar(255))";
            PreparedStatement st = conn.prepareStatement(createUserTableSql);
            st.execute();

            String createGroupTableSql = "create table Groups (groupId varchar(255), userId varchar(255))";
            st = conn.prepareStatement(createGroupTableSql);
            st.execute();

            // insert user rows
            String insertUser = "insert into Users (userId) values (?)";
            st = conn.prepareStatement(insertUser);
            st.setString(1, "john");
            st.execute();

            // insert group rows
            String insertGroup = "insert into Groups (groupId, userId) values (?, ?)";
            st = conn.prepareStatement(insertGroup);
            st.setString(1, "PM");
            st.setString(2, "john");
            st.execute();


            st.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    protected void cleanDb() {
        try {
            Connection conn = pds.getConnection();
            String dropUserTableSql = "drop table Users";
            PreparedStatement st = conn.prepareStatement(dropUserTableSql);
            st.execute();

            String dropGroupTableSql = "drop table Groups";
            st = conn.prepareStatement(dropGroupTableSql);

            st.execute();

            st.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @After
    public void cleanup() {
        cleanDb();
        pds.close();
    }

    @Test
    public void testUserExists() {



        DBUserGroupCallbackImpl callback = new DBUserGroupCallbackImpl(props);
        boolean exists = callback.existsUser("john");
        assertTrue(exists);
    }

    @Test
    public void testGroupExists() {

        DBUserGroupCallbackImpl callback = new DBUserGroupCallbackImpl(props);
        boolean exists = callback.existsGroup("PM");
        assertTrue(exists);
    }

    @Test
    public void testUserGroups() {

        DBUserGroupCallbackImpl callback = new DBUserGroupCallbackImpl(props);
        List<String> groups = callback.getGroupsForUser("john");
        assertNotNull(groups);
        assertEquals(1, groups.size());
        assertEquals("PM", groups.get(0));
    }

    @Test
    public void testUserNotExists() {

        DBUserGroupCallbackImpl callback = new DBUserGroupCallbackImpl(props);
        boolean exists = callback.existsUser("mike");
        assertFalse(exists);
    }

    @Test
    public void testGroupNotExists() {

        DBUserGroupCallbackImpl callback = new DBUserGroupCallbackImpl(props);
        boolean exists = callback.existsGroup("HR");
        assertFalse(exists);
    }

    @Test
    public void testNoUserGroups() {

        DBUserGroupCallbackImpl callback = new DBUserGroupCallbackImpl(props);
        List<String> groups = callback.getGroupsForUser("mike");
        assertNotNull(groups);
        assertEquals(0, groups.size());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConfiguration() {

        Properties invalidProps = new Properties();
        DBUserGroupCallbackImpl callback = new DBUserGroupCallbackImpl(invalidProps);
        callback.getGroupsForUser("mike");
        fail("Should fail as it does not have valid configuration");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidArgument() {

        DBUserGroupCallbackImpl callback = new DBUserGroupCallbackImpl(props);
        callback.getGroupsForUser(null);
        fail("Should fail as it does not have valid configuration");

    }

    private void setDatabaseSpecificDataSourceProperties(PoolingDataSource pds, Properties dsProps) {
        PersistenceUtil.setDatabaseSpecificDataSourceProperties(pds, dsProps);
    }
}
