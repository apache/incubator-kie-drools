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
package org.jbpm.task.service;

import java.util.List;

import org.jbpm.task.service.UserGroupCallbackManager;

import junit.framework.TestCase;

public class UserGroupCallbackTest extends TestCase {
    
    @Override
    protected void tearDown()  {
        UserGroupCallbackManager.resetCallback();
    }
    
    public void testUserGroupCallbackViaSystemProperty() throws Exception {
        System.setProperty(UserGroupCallbackManager.USER_GROUP_CALLBACK_KEY, "org.jbpm.task.service.UserGroupCallbackTwoImpl");
        
        assertTrue(UserGroupCallbackManager.getInstance().existsCallback());
        
        assertFalse(UserGroupCallbackManager.getInstance().getCallback().existsUser("Darth Vader"));
        assertTrue(UserGroupCallbackManager.getInstance().getCallback().existsUser("sales-rep"));
        
        assertTrue(UserGroupCallbackManager.getInstance().getCallback().existsGroup("Crusaders"));
        assertFalse(UserGroupCallbackManager.getInstance().getCallback().existsGroup("Volleyball Players"));
        
        List<String> groups = UserGroupCallbackManager.getInstance().getCallback().getGroupsForUser("sales-rep");
        assertNotNull(groups);
        assertEquals(groups.size(), 1);        
        System.clearProperty(UserGroupCallbackManager.USER_GROUP_CALLBACK_KEY);
    }
    
    public void testUserGroupCallbackViaPropertiesFile() throws Exception {
        
        assertTrue(UserGroupCallbackManager.getInstance().existsCallback());
        
        assertTrue(UserGroupCallbackManager.getInstance().getCallback().existsUser("Darth Vader"));
        assertFalse(UserGroupCallbackManager.getInstance().getCallback().existsUser("tsurdilo"));
        
        assertTrue(UserGroupCallbackManager.getInstance().getCallback().existsGroup("Crusaders"));
        assertFalse(UserGroupCallbackManager.getInstance().getCallback().existsGroup("Volleyball Players"));
        
        List<String> groups = UserGroupCallbackManager.getInstance().getCallback().getGroupsForUser("Darth Vader");
        assertNotNull(groups);
        assertEquals(groups.size(), 2);
    }
}
