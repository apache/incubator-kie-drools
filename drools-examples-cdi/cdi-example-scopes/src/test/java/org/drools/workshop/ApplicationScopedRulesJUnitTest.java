/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop;

import javax.enterprise.context.ApplicationScoped;
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
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author salaboy
 */
@RunWith(Arquillian.class)
public class ApplicationScopedRulesJUnitTest {

    public ApplicationScopedRulesJUnitTest() {
    }

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .addClasses(MyBean.class, MyApplicationScopedBean.class)
                .addPackages(true, "org.apache.deltaspike")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
//        System.out.println(war.toString(true));
        return jar;
    }
    
    @Inject
    private MyApplicationScopedBean myApplicationBean;

    @Test
    public void helloApplicationScoped() {
        Assert.assertNotNull(myApplicationBean);
        ContextControl ctxCtrl = BeanProvider.getContextualReference(ContextControl.class);

        ctxCtrl.startContext(ApplicationScoped.class);
        ctxCtrl.startContext(RequestScoped.class);


        int myBeanHashcode = myApplicationBean.getMyBean().hashCode();
        int myKieSessionHashcode = myApplicationBean.getkSession().hashCode();

        int result = myApplicationBean.doSomething("hello 0");
        Assert.assertEquals(1, result);

        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);
        

        Assert.assertEquals(myBeanHashcode, myApplicationBean.getMyBean().hashCode());
        Assert.assertEquals(myKieSessionHashcode, myApplicationBean.getkSession().hashCode());


        myBeanHashcode = myApplicationBean.getMyBean().hashCode();
        myKieSessionHashcode = myApplicationBean.getkSession().hashCode();

        result = myApplicationBean.doSomething("hello 1");
        Assert.assertEquals(1, result);
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        
        Assert.assertEquals(myBeanHashcode, myApplicationBean.getMyBean().hashCode());
        Assert.assertEquals(myKieSessionHashcode, myApplicationBean.getkSession().hashCode());


        result = myApplicationBean.doSomething("hello 2");
        Assert.assertEquals(1, result);
       
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.stopContext(ApplicationScoped.class);
        

    }

  
    
}
