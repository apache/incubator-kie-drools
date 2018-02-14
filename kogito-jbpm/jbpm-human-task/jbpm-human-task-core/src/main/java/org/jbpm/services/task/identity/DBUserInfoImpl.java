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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUserInfoImpl extends AbstractUserGroupInfo implements UserInfo {

    private static final Logger logger = LoggerFactory.getLogger(DBUserInfoImpl.class);    
    protected static final String DEFAULT_PROPERTIES_NAME = "classpath:/jbpm.user.info.properties";
    
    public static final String DS_JNDI_NAME = "db.ds.jndi.name";
    public static final String NAME_QUERY = "db.name.query";
    public static final String EMAIL_QUERY = "db.email.query";
    public static final String LANG_QUERY = "db.lang.query";
    public static final String HAS_EMAIL_QUERY = "db.has.email.query";
    public static final String MEMBERS_QUERY = "db.group.mem.query";
    public static final String ID_QUERY = "db.id.query";
    
    private Properties config;
    private DataSource ds; 
    
    //no no-arg constructor to prevent cdi from auto deploy
    public DBUserInfoImpl(boolean activate) {
        String propertiesLocation = System.getProperty("jbpm.user.info.properties");        
        config = readProperties(propertiesLocation, DEFAULT_PROPERTIES_NAME);
        init();

    }
    
    public DBUserInfoImpl(Properties config) {
        this.config = config;        
        init();

    }
    
	protected Connection getConnection() throws SQLException {

		return ds.getConnection();
	}
	
	private void init() {
		if (this.config == null || !this.config.containsKey(DS_JNDI_NAME) || 
				!this.config.containsKey(NAME_QUERY) || !this.config.containsKey(EMAIL_QUERY) 
				|| !this.config.containsKey(MEMBERS_QUERY) || !this.config.containsKey(LANG_QUERY)) {
			throw new IllegalArgumentException("All properties must be given ("+ DS_JNDI_NAME + ","
					+ NAME_QUERY +"," + EMAIL_QUERY +"," + LANG_QUERY + "," + MEMBERS_QUERY +")");
		}
		String jndiName = this.config.getProperty(DS_JNDI_NAME, "java:/DefaultDS");
		try {
			InitialContext ctx = new InitialContext();
			
			ds = (DataSource) ctx.lookup(jndiName);

		} catch (Exception e) {
			throw new IllegalStateException("Can get data source for DB usergroup callback, JNDI name: " + jndiName, e);
		}
	}
    
	@Override
	public String getDisplayName(OrganizationalEntity entity) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String displayName = null;
		try {
			conn = ds.getConnection();

			ps = conn.prepareStatement(this.config.getProperty(NAME_QUERY));

			ps.setString(1, entity.getId());
			rs = ps.executeQuery();
			if (rs.next()) {
				displayName = rs.getString(1);
			}
		} catch (Exception e) {
			logger.error("Error when checking roles in db, parameter: " + entity.getId(), e);
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
		return displayName;
	}
	
    @Override
    public String getEntityForEmail(String email) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String displayName = null;
        try {
            conn = ds.getConnection();

            ps = conn.prepareStatement(this.config.getProperty(ID_QUERY));

            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                displayName = rs.getString(1);
            }
        } catch (Exception e) {
            logger.error("Error when looking up id for email in db, parameter: " + email, e);
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
        return displayName;
    }

	@Override
	public Iterator<OrganizationalEntity> getMembersForGroup(Group group) {
		List<OrganizationalEntity> roles = new ArrayList<OrganizationalEntity>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = ds.getConnection();

			ps = conn.prepareStatement(this.config.getProperty(MEMBERS_QUERY));
			try {
				ps.setString(1, group.getId());
			} catch (ArrayIndexOutOfBoundsException ignore) {

			}
			rs = ps.executeQuery();
			while (rs.next()) {
				roles.add(TaskModelProvider.getFactory().newUser(rs.getString(1)));
			}
		} catch (Exception e) {
			logger.error("Error when fetching members of a group from db, groups id: ", group.getId(), e);
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
		return roles.iterator();
	}

	@Override
	public boolean hasEmail(Group group) {
		boolean result = false;
		if (config.containsKey(HAS_EMAIL_QUERY)) {
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			
			try {
				conn = ds.getConnection();

				ps = conn.prepareStatement(this.config.getProperty(HAS_EMAIL_QUERY));

				ps.setString(1, group.getId());
				rs = ps.executeQuery();
				if (rs.next()) {
					result = true;
				}
			} catch (Exception e) {
				logger.error("Error when checking roles in db, parameter: " + group.getId(), e);
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
		} else {
			String email = getEmailForEntity(group);
			if (email != null) {
				return true;
			}
		}
		return result;
	}

	@Override
	public String getEmailForEntity(OrganizationalEntity entity) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String emailAddress = null;
		try {
			conn = ds.getConnection();

			ps = conn.prepareStatement(this.config.getProperty(EMAIL_QUERY));

			ps.setString(1, entity.getId());
			rs = ps.executeQuery();
			if (rs.next()) {
				emailAddress = rs.getString(1);
			}
		} catch (Exception e) {
			logger.error("Error when fetching email address from db for entity {}", entity.getId(), e);
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
		return emailAddress;
	}

	@Override
	public String getLanguageForEntity(OrganizationalEntity entity) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String language = null;
		try {
			conn = ds.getConnection();

			ps = conn.prepareStatement(this.config.getProperty(LANG_QUERY));

			ps.setString(1, entity.getId());
			rs = ps.executeQuery();
			if (rs.next()) {
				language = rs.getString(1);
			}
		} catch (Exception e) {
			logger.error("Error when fetching language for entity {} ", entity.getId(), e);
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
		return language;
	}

}
