/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.example.cdi.scopes;

import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

@RunWith(Arquillian.class)
public class SimpleConversationScopedRulesJUnitTest {

    public SimpleConversationScopedRulesJUnitTest() {
    }

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .addClasses(MyBean.class, MySimpleConversationScopedBean.class)
                .addPackages(true, "org.apache.deltaspike")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return jar;
    }

    @Inject
    private MySimpleConversationScopedBean mySimpleConversationBean;

    private ContextControl ctxCtrl = BeanProvider.getContextualReference(ContextControl.class);

    @After
    public void stopContexts() {
        ctxCtrl.stopContexts();
    }

    @Test
    public void helloSimpleConversationScoped() {
        Assert.assertNotNull(mySimpleConversationBean);

        ctxCtrl.startContext(SessionScoped.class);
        ctxCtrl.startContext(ConversationScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        mySimpleConversationBean.begin();
        int myBeanHashcode = mySimpleConversationBean.getMyBean().hashCode();

        String result = mySimpleConversationBean.doSomething("hello 0");
        Assert.assertEquals("hello 0 processed!", result);

        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        Assert.assertEquals(myBeanHashcode, mySimpleConversationBean.getMyBean().hashCode());
        

        myBeanHashcode = mySimpleConversationBean.getMyBean().hashCode();
        

        result = mySimpleConversationBean.doSomething("hello 1");
        Assert.assertEquals("hello 1 processed!", result);
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        Assert.assertEquals(myBeanHashcode, mySimpleConversationBean.getMyBean().hashCode());

        result = mySimpleConversationBean.doSomething("hello 2");
        Assert.assertEquals("hello 2 processed!", result);

        myBeanHashcode = mySimpleConversationBean.getMyBean().hashCode();
        

        mySimpleConversationBean.end();
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.stopContext(ConversationScoped.class);

        ctxCtrl.startContext(ConversationScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        mySimpleConversationBean.begin();
        
        Assert.assertNotEquals(myBeanHashcode, mySimpleConversationBean.getMyBean().hashCode());

        result = mySimpleConversationBean.doSomething("hello 3");
        Assert.assertEquals("hello 3 processed!", result);

        myBeanHashcode = mySimpleConversationBean.getMyBean().hashCode();
        
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        Assert.assertEquals(myBeanHashcode, mySimpleConversationBean.getMyBean().hashCode());
        

        myBeanHashcode = mySimpleConversationBean.getMyBean().hashCode();
        

        result = mySimpleConversationBean.doSomething("hello 4");
        Assert.assertEquals("hello 4 processed!", result);
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        Assert.assertEquals(myBeanHashcode, mySimpleConversationBean.getMyBean().hashCode());
        

        result = mySimpleConversationBean.doSomething("hello 5");
        Assert.assertEquals("hello 5 processed!", result);

        myBeanHashcode = mySimpleConversationBean.getMyBean().hashCode();
        
        mySimpleConversationBean.end();
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.stopContext(ConversationScoped.class);

        ctxCtrl.startContext(ConversationScoped.class);
        ctxCtrl.startContext(RequestScoped.class);
        
        
        mySimpleConversationBean.begin();
        Assert.assertNotEquals(myBeanHashcode, mySimpleConversationBean.getMyBean().hashCode());
        
        mySimpleConversationBean.end();
    }

}
