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

package org.jbpm.test;

import java.util.Properties;
import javax.naming.Context;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPException;
import org.jbpm.services.task.identity.LDAPUserGroupCallbackImpl;
import org.junit.After;
import org.junit.Before;

public abstract class LdapJbpmTestCase extends JbpmTestCase {

    private static InMemoryDirectoryServer server;

    private final String ldif;

    public LdapJbpmTestCase(String ldif) {
        this.ldif = ldif;
    }

    @Before
    public void startDirectoryServer() throws LDAPException {
        InMemoryListenerConfig listenerConfig = InMemoryListenerConfig.createLDAPConfig("default", 10389);

        InMemoryDirectoryServerConfig serverConfig = new InMemoryDirectoryServerConfig(new DN("dc=jboss,dc=org"));
        serverConfig.setListenerConfigs(listenerConfig);
        serverConfig.addAdditionalBindCredentials("uid=admin,ou=system", "secret");
        serverConfig.setSchema(null);

        server = new InMemoryDirectoryServer(serverConfig);
        server.importFromLDIF(false, ldif);
        server.startListening();
    }

    @After
    public void stopDirectoryServer() {
        if (server != null) {
            server.shutDown(true);
        }
    }

    protected Properties createUserGroupCallbackProperties() {
        Properties properties = new Properties();
        properties.setProperty(Context.PROVIDER_URL, "ldap://localhost:10389");
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_CTX, "ou=People,dc=jboss,dc=org");
        properties.setProperty(LDAPUserGroupCallbackImpl.ROLE_CTX, "ou=Roles,dc=jboss,dc=org");
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_FILTER, "(uid={0})");
        properties.setProperty(LDAPUserGroupCallbackImpl.ROLE_FILTER, "(cn={0})");
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_ROLES_FILTER, "(member={0})");
        return properties;
    }

}
