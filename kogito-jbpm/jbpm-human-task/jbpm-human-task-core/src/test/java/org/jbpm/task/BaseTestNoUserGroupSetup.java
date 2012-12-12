/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.task;

import javax.persistence.EntityManagerFactory;

import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.identity.UserGroupCallbackOneImpl;

public abstract class BaseTestNoUserGroupSetup extends BaseTest {
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();          
        if(!UserGroupCallbackManager.getInstance().existsCallback()) {
        	UserGroupCallbackManager.getInstance().setCallback(new UserGroupCallbackOneImpl());
        }
        taskSession.addUser(new User("Administrator"));
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        UserGroupCallbackManager.getInstance().setCallback(null);
    }
    
    @Override
    protected EntityManagerFactory createEntityManagerFactory() {
        EntityManagerFactory realEmf = super.createEntityManagerFactory();
        return new EntityManagerFactoryAndTracker(realEmf);
    }
    
    @Override
    public void disableUserGroupCallback() {
        // do not disable
    }
    
}
