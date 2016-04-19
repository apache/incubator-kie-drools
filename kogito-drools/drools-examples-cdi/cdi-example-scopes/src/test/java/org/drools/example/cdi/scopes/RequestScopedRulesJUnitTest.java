/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import static org.junit.Assert.assertNotEquals;

@RunWith(Arquillian.class)
public class RequestScopedRulesJUnitTest {

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .addClasses(MyBean.class, MyRequestScopedBean.class)
                .addPackages(true, "org.apache.deltaspike")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return jar;
    }

    @Inject
    private MyRequestScopedBean myRequestBean;

    private ContextControl ctxCtrl = BeanProvider.getContextualReference(ContextControl.class);

    @After
    public void stopContexts() {
        ctxCtrl.stopContexts();
    }

    @Test
    public void helloRequestScoped() {
        Assert.assertNotNull(myRequestBean);

        ctxCtrl.startContext(RequestScoped.class);


        String myBeanId = myRequestBean.getMyBean().getId();
        long myKieSessionId = myRequestBean.getkSession().getIdentifier();

        int result = myRequestBean.doSomething("hello 0");
        Assert.assertEquals(1, result);

        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);
        

        assertNotEquals(myBeanId, myRequestBean.getMyBean().getId());
        assertNotEquals(myKieSessionId, myRequestBean.getkSession().getIdentifier());


        myBeanId = myRequestBean.getMyBean().getId();
        myKieSessionId = myRequestBean.getkSession().getIdentifier();

        result = myRequestBean.doSomething("hello 1");
        Assert.assertEquals(1, result);
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);


        assertNotEquals(myBeanId, myRequestBean.getMyBean().getId());
        assertNotEquals(myKieSessionId, myRequestBean.getkSession().getIdentifier());

        result = myRequestBean.doSomething("hello 2");
        Assert.assertEquals(1, result);

        myBeanId = myRequestBean.getMyBean().getId();
        myKieSessionId = myRequestBean.getkSession().getIdentifier();
        
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        assertNotEquals(myBeanId, myRequestBean.getMyBean().getId());
        assertNotEquals(myKieSessionId, myRequestBean.getkSession().getIdentifier());

        ctxCtrl.stopContext(RequestScoped.class );
    }
}
