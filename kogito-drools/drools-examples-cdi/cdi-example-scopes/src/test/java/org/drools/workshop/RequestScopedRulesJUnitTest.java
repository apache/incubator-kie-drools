/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop;

import javax.enterprise.context.RequestScoped;

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
public class RequestScopedRulesJUnitTest {

    public RequestScopedRulesJUnitTest() {
    }

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .addClasses(MyBean.class, MyRequestScopedBean.class)
                .addPackages(true, "org.apache.deltaspike")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
//        System.out.println(war.toString(true));
        return jar;
    }
    
    @Inject
    private MyRequestScopedBean myRequestBean;

    @Test
    @Ignore
    public void helloRequestScoped() {
        Assert.assertNotNull(myRequestBean);
        ContextControl ctxCtrl = BeanProvider.getContextualReference(ContextControl.class);

        ctxCtrl.startContext(RequestScoped.class);


        int myBeanHashcode = myRequestBean.getMyBean().hashCode();
        int myKieSessionHashcode = myRequestBean.getkSession().hashCode();

        int result = myRequestBean.doSomething("hello 0");
        Assert.assertEquals(1, result);

        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);
        

        Assert.assertNotEquals(myBeanHashcode, myRequestBean.getMyBean().hashCode());
        Assert.assertNotEquals(myKieSessionHashcode, myRequestBean.getkSession().hashCode());


        myBeanHashcode = myRequestBean.getMyBean().hashCode();
        myKieSessionHashcode = myRequestBean.getkSession().hashCode();

        result = myRequestBean.doSomething("hello 1");
        Assert.assertEquals(1, result);
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        
        Assert.assertNotEquals(myBeanHashcode, myRequestBean.getMyBean().hashCode());
        Assert.assertNotEquals(myKieSessionHashcode, myRequestBean.getkSession().hashCode());

        result = myRequestBean.doSomething("hello 2");
        Assert.assertEquals(1, result);
       
        myBeanHashcode = myRequestBean.getMyBean().hashCode();
        myKieSessionHashcode = myRequestBean.getkSession().hashCode();
        
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        Assert.assertNotEquals(myBeanHashcode, myRequestBean.getMyBean().hashCode());
        Assert.assertNotEquals(myKieSessionHashcode, myRequestBean.getkSession().hashCode());

    }

  
    
}
