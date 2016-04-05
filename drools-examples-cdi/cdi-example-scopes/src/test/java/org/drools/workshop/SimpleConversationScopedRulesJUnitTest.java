/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;

import javax.inject.Inject;
import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author salaboy
 */
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
//        System.out.println(war.toString(true));
        return jar;
    }

    @Inject
    private MySimpleConversationScopedBean mySimpleConversationBean;

    @Test
    public void helloSimpleConversationScoped() {
        Assert.assertNotNull(mySimpleConversationBean);
        ContextControl ctxCtrl = BeanProvider.getContextualReference(ContextControl.class);

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
        
        
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.stopContext(ConversationScoped.class);
        ctxCtrl.stopContext(SessionScoped.class);

    }

}
