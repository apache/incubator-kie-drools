/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.example.cdi.scopes;

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

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

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
    public void helloSessionScoped() {
        Assert.assertNotNull(mySessionBean);
        ContextControl ctxCtrl = BeanProvider.getContextualReference(ContextControl.class);

        ctxCtrl.startContext(SessionScoped.class);
        ctxCtrl.startContext(RequestScoped.class);


        String myBeanId = mySessionBean.getMyBean().getId();
        long myKieSessionId = mySessionBean.getkSession().getIdentifier();

        int result = mySessionBean.doSomething("hello 0");
        Assert.assertEquals(1, result);

        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);
        

        Assert.assertEquals(myBeanId, mySessionBean.getMyBean().getId());
        Assert.assertEquals(myKieSessionId, mySessionBean.getkSession().getIdentifier());


        myBeanId = mySessionBean.getMyBean().getId();
        myKieSessionId = mySessionBean.getkSession().getIdentifier();

        result = mySessionBean.doSomething("hello 1");
        Assert.assertEquals(1, result);
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        
        Assert.assertEquals(myBeanId, mySessionBean.getMyBean().getId());
        Assert.assertEquals(myKieSessionId, mySessionBean.getkSession().getIdentifier());

        result = mySessionBean.doSomething("hello 2");
        Assert.assertEquals(1, result);
       
        myBeanId = mySessionBean.getMyBean().getId();
        myKieSessionId = mySessionBean.getkSession().getIdentifier();
        
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.stopContext(SessionScoped.class);
        
        
        ctxCtrl.startContext(SessionScoped.class);
        ctxCtrl.startContext(RequestScoped.class);
        
        Assert.assertNotEquals(myBeanId, mySessionBean.getMyBean().getId());
        Assert.assertNotEquals(myKieSessionId, mySessionBean.getkSession().getIdentifier());

        ctxCtrl.stopContext(SessionScoped.class);
        ctxCtrl.stopContext(RequestScoped.class );
    }
}
