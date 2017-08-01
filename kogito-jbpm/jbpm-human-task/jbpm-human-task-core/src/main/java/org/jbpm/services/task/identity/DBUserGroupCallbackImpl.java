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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.kie.internal.task.api.UserGroupCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data base server user group callback implementation that utilizes SQL queries 
 * to get information about user, groups and relationship of these two.
 * <br/>
 * There are four configuration parameters required by this callback:
 * <ul>
 * 	<li>db.ds.jndi.name - JNDI name of the data source to be used for connections</li>
 * 	<li>db.user.query - query used to verify existence of the user (case sensitive, expects a single parameter on position 1)</li>
 * 	<li>db.roles.query - query user to check group existence (case sensitive, expects single parameter on position 1)</li>
 * 	<li>db.user.roles.query - query used to collect group for given user (case sensitive, expects single parameter on position 1, 
 * 	retrieves group name from position 1 of returned result set)</li>
 * </ul>
 *
 */
public class DBUserGroupCallbackImpl extends AbstractUserGroupInfo implements UserGroupCallback {

	private static final Logger logger = LoggerFactory.getLogger(DBUserGroupCallbackImpl.class);
    
    protected static final String DEFAULT_PROPERTIES_NAME = "classpath:/jbpm.usergroup.callback.properties";
    
    public static final String DS_JNDI_NAME = "db.ds.jndi.name";
    public static final String PRINCIPAL_QUERY = "db.user.query";
    public static final String USER_ROLES_QUERY = "db.user.roles.query";
    public static final String ROLES_QUERY = "db.roles.query";
	
    private Properties config;
    private DataSource ds; 
    
    //no no-arg constructor to prevent cdi from auto deploy
    public DBUserGroupCallbackImpl(boolean activate) {
        String propertiesLocation = System.getProperty("jbpm.usergroup.callback.properties");        
        config = readProperties(propertiesLocation, DEFAULT_PROPERTIES_NAME);
        init();

    }
    
    public DBUserGroupCallbackImpl(Properties config) {
        this.config = config;        
        init();

    }

	public boolean existsUser(String userId) {
		if (userId == null) {
			throw new IllegalArgumentException("UserId cannot be null");
		}
		return checkExistence(this.config.getProperty(PRINCIPAL_QUERY), userId);
	}

	public boolean existsGroup(String groupId) {
		if (groupId == null) {
			throw new IllegalArgumentException("GroupId cannot be null");
		}
		return checkExistence(this.config.getProperty(ROLES_QUERY), groupId);
	}

	public List<String> getGroupsForUser(String userId) {
		
		if (userId == null) {
			throw new IllegalArgumentException("UserId cannot be null");
		}
		
		List<String> roles = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = ds.getConnection();

			ps = conn.prepareStatement(this.config.getProperty(USER_ROLES_QUERY));
			try {
				ps.setString(1, userId);
			} catch (ArrayIndexOutOfBoundsException ignore) {

			}
			rs = ps.executeQuery();
			while (rs.next()) {
				roles.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error("Error when checking roles in db, parameter: " + userId, e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
				}
			}
		}
		
		return roles;
	}
	
	protected Connection getConnection() throws SQLException {

		return ds.getConnection();
	}
	
	private void init() {
		if (this.config == null || !this.config.containsKey(DS_JNDI_NAME) || 
				!this.config.containsKey(PRINCIPAL_QUERY) || !this.config.containsKey(ROLES_QUERY) 
				|| !this.config.containsKey(USER_ROLES_QUERY)) {
			throw new IllegalArgumentException("All properties must be given ("+ DS_JNDI_NAME + ","
					+ USER_ROLES_QUERY +"," + ROLES_QUERY +"," +USER_ROLES_QUERY +")");
		}
		String jndiName = this.config.getProperty(DS_JNDI_NAME, "java:/DefaultDS");
		try {
			InitialContext ctx = new InitialContext();
			
			ds = (DataSource) ctx.lookup(jndiName);

		} catch (Exception e) {
			throw new IllegalStateException("Can get data source for DB usergroup callback, JNDI name: " + jndiName, e);
		}
	}
	
	protected boolean checkExistence(String querySql, String parameter) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			conn = ds.getConnection();

			ps = conn.prepareStatement(querySql);
			
			ps.setString(1, parameter);

			rs = ps.executeQuery();
			if (rs.next()) {
				result = true;
			}
		} catch (Exception e) {
			logger.error("Error when checking user/group in db, parameter: " + parameter, e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
				}
			}
		}
		
		
		return result;
	}

}
