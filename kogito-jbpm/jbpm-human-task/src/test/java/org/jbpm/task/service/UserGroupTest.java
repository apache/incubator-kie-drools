/*
 * Copyright 2012 JBoss Inc
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

import org.jbpm.task.BaseTestNoUserGroupSetup;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.junit.Test;


public class UserGroupTest extends BaseTestNoUserGroupSetup {

    @Test
    public void testAddUser() {
        User user = new User("mike");
        assertFalse(taskSession.getTaskPersistenceManager().userExists(user.getId()));
        taskSession.addUser(user);
        
        assertTrue(taskSession.getTaskPersistenceManager().userExists(user.getId()));
    }
    
    @Test
    public void testAddDuplicatedUser() {
        User user = new User("mike");
        assertFalse(taskSession.getTaskPersistenceManager().userExists(user.getId()));
        taskSession.addUser(user);
        
        assertTrue(taskSession.getTaskPersistenceManager().userExists(user.getId()));
        User user2 = new User("mike");
        taskSession.addUser(user2);
        
    }
    
    @Test
    public void testAddGroup() {
        Group group = new Group("mike");
        assertFalse(taskSession.getTaskPersistenceManager().groupExists(group.getId()));
        taskSession.addGroup(group);
        
        assertTrue(taskSession.getTaskPersistenceManager().groupExists(group.getId()));
    }
    
    @Test
    public void testAddDuplicatedGroup() {
        Group group = new Group("mike");
        assertFalse(taskSession.getTaskPersistenceManager().groupExists(group.getId()));
        taskSession.addGroup(group);
        
        assertTrue(taskSession.getTaskPersistenceManager().groupExists(group.getId()));
        
        Group group2 = new Group("mike");
        
        taskSession.addGroup(group2);
    }
}
