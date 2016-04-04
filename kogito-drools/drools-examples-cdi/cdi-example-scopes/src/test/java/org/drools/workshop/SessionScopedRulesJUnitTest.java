/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop;

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
public class SessionScopedRulesJUnitTest {

    public SessionScopedRulesJUnitTest() {
    }

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .addClasses(MyBean.class, MySessionScopedBean.class)
                .addPackages(true, "org.apache.deltaspike")
//                .addAsServiceProvider(KieBusinessScopeExtension.class, KieBusinessScopeContext.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
//        System.out.println(war.toString(true));
        return jar;
    }
    
    @Inject
    private MySessionScopedBean mySessionBean;

    @Test
    @Ignore
    public void helloSessionScoped() {
        Assert.assertNotNull(mySessionBean);
        ContextControl ctxCtrl = BeanProvider.getContextualReference(ContextControl.class);

        ctxCtrl.startContext(SessionScoped.class);
        ctxCtrl.startContext(RequestScoped.class);


        int myBeanHashcode = mySessionBean.getMyBean().hashCode();
        int myKieSessionHashcode = mySessionBean.getkSession().hashCode();

        int result = mySessionBean.doSomething("hello 0");
        Assert.assertEquals(1, result);

        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);
        

        Assert.assertEquals(myBeanHashcode, mySessionBean.getMyBean().hashCode());
        Assert.assertEquals(myKieSessionHashcode, mySessionBean.getkSession().hashCode());


        myBeanHashcode = mySessionBean.getMyBean().hashCode();
        myKieSessionHashcode = mySessionBean.getkSession().hashCode();

        result = mySessionBean.doSomething("hello 1");
        Assert.assertEquals(1, result);
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        
        Assert.assertEquals(myBeanHashcode, mySessionBean.getMyBean().hashCode());
        Assert.assertEquals(myKieSessionHashcode, mySessionBean.getkSession().hashCode());

        result = mySessionBean.doSomething("hello 2");
        Assert.assertEquals(1, result);
       
        myBeanHashcode = mySessionBean.getMyBean().hashCode();
        myKieSessionHashcode = mySessionBean.getkSession().hashCode();
        
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.stopContext(SessionScoped.class);
        
        
        ctxCtrl.startContext(SessionScoped.class);
        ctxCtrl.startContext(RequestScoped.class);
        
        Assert.assertNotEquals(myBeanHashcode, mySessionBean.getMyBean().hashCode());
        Assert.assertNotEquals(myKieSessionHashcode, mySessionBean.getkSession().hashCode());

    }

  
    
}
