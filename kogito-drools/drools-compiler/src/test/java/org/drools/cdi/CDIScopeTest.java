package org.drools.cdi;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.KieBase;
import org.kie.cdi.KBase;
import org.kie.cdi.KSession;
import org.kie.runtime.KieSession;

@RunWith(CDITestRunner.class)
public class CDIScopeTest {
    @Inject
    @KBase("org.kie.kbase2")
    private KieBase kBase2;

    @Inject
    @KBase("org.kie.kbase3")
    private KieBase kBase3;

    @Inject
    @KSession("org.kie.kbase3.ksession1")
    private KieSession kbase3ksession1;

    @Inject
    @KSession("org.kie.kbase3.ksession2")
    private KieSession kbase3ksession2;

    @Inject
    BeanManager     beanManager;

    
    @BeforeClass
    public static void beforeClass() {    
        CDITestRunner.weld = CDITestRunner.createWeld( CDIScopeTest.class.getName() );
        CDITestRunner.container = CDITestRunner.weld.initialize();
    }

    @AfterClass
    public static void afterClass() {
        CDITestRunner.weld.shutdown();
        CDITestRunner.container = null;
        CDITestRunner.weld = null;
    }  

    @Test
    public void testKieBaseScope() {
        Set<Bean< ? >> beans = beanManager.getBeans( KieBase.class,
                                                     new KBaseLiteral( "org.kie.kbase2" ) );
        assertEquals( 1,
                      beans.size() );

        Bean bean1 = beans.toArray( new Bean[1] )[0];
        assertEquals( ApplicationScoped.class,
                      bean1.getScope() );

        beans = beanManager.getBeans( KieBase.class,
                                      new KBaseLiteral( "org.kie.kbase3" ) );
        assertEquals( 1,
                      beans.size() );

        bean1 = beans.toArray( new Bean[1] )[0];
        assertEquals( SessionScoped.class,
                      bean1.getScope() );
    }

    @Test
    public void testKieSessionScope() {
        //org.kie.kbase3.ksession1

        Set<Bean< ? >> beans = beanManager.getBeans( KieSession.class,
                                                     new KSessionLiteral( "org.kie.kbase3.ksession1" ) );
        assertEquals( 1,
                      beans.size() );

        Bean bean1 = beans.toArray( new Bean[1] )[0];
        assertEquals( ApplicationScoped.class,
                      bean1.getScope() );

        beans = beanManager.getBeans( KieSession.class,
                                      new KSessionLiteral( "org.kie.kbase3.ksession2" ) );
        assertEquals( 1,
                      beans.size() );

        bean1 = beans.toArray( new Bean[1] )[0];
        assertEquals( SessionScoped.class,
                      bean1.getScope() );
    }

    public static class KBaseLiteral extends AnnotationLiteral<KBase>
        implements
        KBase {
        private String value;

        public KBaseLiteral(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }

    public static class KSessionLiteral extends AnnotationLiteral<KSession>
        implements
        KSession {
        private String value;

        public KSessionLiteral(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }

}
