/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Properties;

import org.assertj.core.api.Assertions;
import org.jbpm.persistence.util.PersistenceUtil;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;

public class DBUserInfoImplTest {

    private static final User JOHN = TaskModelProvider.getFactory().newUser("john");
    private static final Group PM = TaskModelProvider.getFactory().newGroup("PM");
    
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
        props.setProperty(DBUserInfoImpl.DS_JNDI_NAME, "jdbc/jbpm-ds");
        props.setProperty(DBUserInfoImpl.NAME_QUERY, "select name from Users where userId = ?");
        props.setProperty(DBUserInfoImpl.EMAIL_QUERY, "select email from Users where userId = ?");
        props.setProperty(DBUserInfoImpl.LANG_QUERY, "select lang from Users where userId = ?");
        props.setProperty(DBUserInfoImpl.HAS_EMAIL_QUERY, "select email from Groups where groupId = ?");
        props.setProperty(DBUserInfoImpl.MEMBERS_QUERY, "select userId from Groups where groupId = ?");
        props.setProperty(DBUserInfoImpl.ID_QUERY, "select userId from Users where email = ?");
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
            String createUserTableSql = "create table Users (userId varchar(255), email varchar(255), lang varchar(255), name varchar(255))";
            PreparedStatement st = conn.prepareStatement(createUserTableSql);
            st.execute();

            String createGroupTableSql = "create table Groups (groupId varchar(255), userId varchar(255), email varchar(255))";
            st = conn.prepareStatement(createGroupTableSql);
            st.execute();

            // insert user rows
            String insertUser = "insert into Users (userId, email, lang, name) values (?, ?, ?, ?)";
            st = conn.prepareStatement(insertUser);
            st.setString(1, "john");
            st.setString(2, "john@jbpm.org");
            st.setString(3, "en-UK");
            st.setString(4, "John Doe");
            st.execute();

            // insert group rows
            String insertGroup = "insert into Groups (groupId, userId, email) values (?, ?, ?)";
            st = conn.prepareStatement(insertGroup);
            st.setString(1, "PM");
            st.setString(2, "john");
            st.setString(3, "pm@jbpm.org");
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
    public void testGetDisplayName() {
        DBUserInfoImpl userInfo = new DBUserInfoImpl(props);
        
        String displayName = userInfo.getDisplayName(JOHN);
        Assertions.assertThat(displayName).isEqualTo("John Doe");
    }
    
    @Test
    public void testGetMembersForGroup() {
        DBUserInfoImpl userInfo = new DBUserInfoImpl(props);
        
        Iterator<OrganizationalEntity> members = userInfo.getMembersForGroup(PM);
        Assertions.assertThat(members.hasNext()).isTrue();
        User user = (User) members.next();
        Assertions.assertThat(user.getId()).isEqualTo(JOHN.getId());
    }

    @Test
    public void testHasEmail() {
        DBUserInfoImpl userInfo = new DBUserInfoImpl(props);
        
        boolean hasEmail = userInfo.hasEmail(PM);
        Assertions.assertThat(hasEmail).isTrue();
    }
    
    @Test
    public void testGetEmailForEntity() {
        DBUserInfoImpl userInfo = new DBUserInfoImpl(props);
        
        String email = userInfo.getEmailForEntity(JOHN);
        Assertions.assertThat(email).isEqualTo("john@jbpm.org");
    }
    
    @Test
    public void testGetLanguageForEntity() {
        DBUserInfoImpl userInfo = new DBUserInfoImpl(props);
        
        String lang = userInfo.getLanguageForEntity(JOHN);
        Assertions.assertThat(lang).isEqualTo("en-UK");
    }
    
    @Test
    public void testGetEntityForEmail() {
        DBUserInfoImpl userInfo = new DBUserInfoImpl(props);
        
        String id = userInfo.getEntityForEmail("john@jbpm.org");
        Assertions.assertThat(id).isEqualTo(JOHN.getId());
    }

    private void setDatabaseSpecificDataSourceProperties(PoolingDataSource pds, Properties dsProps) {
        PersistenceUtil.setDatabaseSpecificDataSourceProperties(pds, dsProps);
    }
}
