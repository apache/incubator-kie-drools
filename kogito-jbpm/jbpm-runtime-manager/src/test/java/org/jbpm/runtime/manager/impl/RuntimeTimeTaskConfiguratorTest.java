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

package org.jbpm.runtime.manager.impl;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.runtime.manager.impl.factory.LocalTaskServiceFactory;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.identity.PropertyUserInfoImpl;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.task.UserInfo;
import org.kie.internal.task.api.InternalTaskService;


public class RuntimeTimeTaskConfiguratorTest extends AbstractBaseTest {
    private static String oldUserInfoValue;
    private static String oldUserInfoImpl;
    private PoolingDataSource pds;

    @BeforeClass
    public static void enter() {
        oldUserInfoValue = System.setProperty("org.jbpm.ht.userinfo", "custom");
        oldUserInfoImpl = System.setProperty("org.jbpm.ht.custom.userinfo", "org.jbpm.runtime.manager.impl.CustomUserInfoImpl");
    }

    @AfterClass
    public static void exit() {
        if(oldUserInfoValue != null) {
            System.setProperty("org.jbpm.ht.userinfo", oldUserInfoValue);
        } else {
            System.clearProperty("org.jbpm.ht.userinfo");
        }
        if(oldUserInfoImpl != null) {
            System.setProperty("org.jbpm.ht.custom.userinfo", oldUserInfoImpl);
        } else {
            System.clearProperty("org.jbpm.ht.custom.userinfo");
        }
    }
    @Before
    public void setup() {
        pds = TestUtil.setupPoolingDataSource();
    }

    @After
    public void teardown() {
        pds.close();
    }

    @Test
    public void testRuntimeEnvironmentUserInfo() {
        UserInfo userInfo = new PropertyUserInfoImpl(true);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                 .newDefaultBuilder()
                 .entityManagerFactory(emf)
                 .userInfo(userInfo)
                 .get();

        LocalTaskServiceFactory factory = new LocalTaskServiceFactory(environment);
        InternalTaskService service = (InternalTaskService) factory.newTaskService();

        // we should get the same instance provided in the environment
        Assert.assertTrue(service.getUserInfo().equals(userInfo));
    }

    @Test
    public void testRuntimeEnvironmentUserDataServiceProviderUserInfo() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                 .newDefaultBuilder()
                 .entityManagerFactory(emf)
                 .get();

        LocalTaskServiceFactory factory = new LocalTaskServiceFactory(environment);
        InternalTaskService service = (InternalTaskService) factory.newTaskService();
        // the instance provided should be the one setup at custom and not the default one
        Assert.assertTrue(service.getUserInfo() instanceof CustomUserInfoImpl);
    }
}
