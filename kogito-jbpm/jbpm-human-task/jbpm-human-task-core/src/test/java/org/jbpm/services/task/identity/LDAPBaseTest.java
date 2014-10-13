/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.identity;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPException;
import org.junit.After;
import org.junit.Before;

public abstract class LDAPBaseTest {

    private InMemoryDirectoryServer server;

    @Before
    public void startDirectoryServer() throws LDAPException {
        InMemoryListenerConfig listenerConfig = InMemoryListenerConfig.createLDAPConfig("default", 10389);

        InMemoryDirectoryServerConfig serverConfig = new InMemoryDirectoryServerConfig(new DN("dc=jbpm,dc=org"));
        serverConfig.setListenerConfigs(listenerConfig);
        serverConfig.addAdditionalBindCredentials("uid=admin,ou=system", "secret");
        serverConfig.setSchema(null);

        server = new InMemoryDirectoryServer(serverConfig);
        server.importFromLDIF(false, "src/test/resources/ldap-config.ldif");
        server.startListening();
    }

    @After
    public void stopDirectoryServer() {
        if (server != null) {
            server.shutDown(true);
        }
    }

}
