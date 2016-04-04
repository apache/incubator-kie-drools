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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author salaboy
 */
@RunWith(Arquillian.class)
public class ConversationScopedRulesJUnitTest {

    public ConversationScopedRulesJUnitTest() {
    }

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .addClasses(MyBean.class, MyConversationScopedBean.class)
                .addPackages(true, "org.apache.deltaspike")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
//        System.out.println(war.toString(true));
        return jar;
    }

    @Inject
    private MyConversationScopedBean myConversationBean;

    @Test
    @Ignore
    public void helloConversationScoped() {
        Assert.assertNotNull(myConversationBean);
        ContextControl ctxCtrl = BeanProvider.getContextualReference(ContextControl.class);

        ctxCtrl.startContext(SessionScoped.class);
        ctxCtrl.startContext(ConversationScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        myConversationBean.begin();
        int myBeanHashcode = myConversationBean.getMyBean().hashCode();
        int myKieSessionHashcode = myConversationBean.getkSession().hashCode();

        int result = myConversationBean.doSomething("hello 0");
        Assert.assertEquals(1, result);

        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        Assert.assertEquals(myBeanHashcode, myConversationBean.getMyBean().hashCode());
        Assert.assertEquals(myKieSessionHashcode, myConversationBean.getkSession().hashCode());

        myBeanHashcode = myConversationBean.getMyBean().hashCode();
        myKieSessionHashcode = myConversationBean.getkSession().hashCode();

        result = myConversationBean.doSomething("hello 1");
        Assert.assertEquals(1, result);
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        Assert.assertEquals(myBeanHashcode, myConversationBean.getMyBean().hashCode());
        Assert.assertEquals(myKieSessionHashcode, myConversationBean.getkSession().hashCode());

        result = myConversationBean.doSomething("hello 2");
        Assert.assertEquals(1, result);

        myBeanHashcode = myConversationBean.getMyBean().hashCode();
        myKieSessionHashcode = myConversationBean.getkSession().hashCode();

        myConversationBean.end();
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.stopContext(ConversationScoped.class);

        ctxCtrl.startContext(ConversationScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        myConversationBean.begin();
        
        Assert.assertNotEquals(myBeanHashcode, myConversationBean.getMyBean().hashCode());
        Assert.assertNotEquals(myKieSessionHashcode, myConversationBean.getkSession().hashCode());
        result = myConversationBean.doSomething("hello 3");
        Assert.assertEquals(1, result);

        myBeanHashcode = myConversationBean.getMyBean().hashCode();
        myKieSessionHashcode = myConversationBean.getkSession().hashCode();
        
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        Assert.assertEquals(myBeanHashcode, myConversationBean.getMyBean().hashCode());
        Assert.assertEquals(myKieSessionHashcode, myConversationBean.getkSession().hashCode());

        myBeanHashcode = myConversationBean.getMyBean().hashCode();
        myKieSessionHashcode = myConversationBean.getkSession().hashCode();

        result = myConversationBean.doSomething("hello 4");
        Assert.assertEquals(1, result);
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        Assert.assertEquals(myBeanHashcode, myConversationBean.getMyBean().hashCode());
        Assert.assertEquals(myKieSessionHashcode, myConversationBean.getkSession().hashCode());

        result = myConversationBean.doSomething("hello 5");
        Assert.assertEquals(1, result);

        myBeanHashcode = myConversationBean.getMyBean().hashCode();
        myKieSessionHashcode = myConversationBean.getkSession().hashCode();
        myConversationBean.end();
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.stopContext(ConversationScoped.class);

        ctxCtrl.startContext(ConversationScoped.class);
        ctxCtrl.startContext(RequestScoped.class);
        
        
        myConversationBean.begin();
        Assert.assertNotEquals(myBeanHashcode, myConversationBean.getMyBean().hashCode());
        Assert.assertNotEquals(myKieSessionHashcode, myConversationBean.getkSession().hashCode());
        myConversationBean.end();
        
        
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.stopContext(ConversationScoped.class);
        ctxCtrl.stopContext(SessionScoped.class);

    }

}
