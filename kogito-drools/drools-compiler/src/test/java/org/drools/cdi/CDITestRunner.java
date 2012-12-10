package org.drools.cdi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.cdi.KProjectCDITest.TestWeldSEDeployment;
import org.drools.cdi.example.TestClass;
import org.drools.cdi.example.TestClassImpl;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.discovery.AbstractWeldSEDeployment;
import org.jboss.weld.environment.se.discovery.ImmutableBeanDeploymentArchive;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.kie.cdi.KBase;
import org.kie.cdi.KGAV;
import org.kie.cdi.KSession;

public class CDITestRunner extends BlockJUnit4ClassRunner {

    public static Weld          weld;
    public static WeldContainer container;

    public CDITestRunner(Class cls) throws InitializationError {
        super( cls );
    }

    @Override
    protected Object createTest() throws Exception {
        return container.instance().select( getTestClass().getJavaClass() ).get();
    }


    public static Weld createWeld(String... classes) {
        final List<String> list = new ArrayList<String>();
        list.addAll( Arrays.asList( classes ) );
        list.add( KieCDIExtension.class.getName() );
        list.add( KBase.class.getName() );
        list.add( KSession.class.getName() );
        list.add( KGAV.class.getName() );
        Weld weld = new Weld() {
            @Override
            protected Deployment createDeployment(ResourceLoader resourceLoader,
                                                  Bootstrap bootstrap) {
                return new TestWeldSEDeployment( resourceLoader,
                                                 bootstrap,
                                                 list );
            }
        };
        return weld;
    }

    public static class TestWeldSEDeployment extends AbstractWeldSEDeployment {
        private final BeanDeploymentArchive beanDeploymentArchive;

        public TestWeldSEDeployment(ResourceLoader resourceLoader,
                                    Bootstrap bootstrap,
                                    List<String> classes) {
            super( bootstrap );
            beanDeploymentArchive = new ImmutableBeanDeploymentArchive( "classpath",
                                                                        classes,
                                                                        null );

        }

        public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
            return Collections.singletonList( beanDeploymentArchive );
        }

        public BeanDeploymentArchive loadBeanDeploymentArchive(Class< ? > beanClass) {
            return beanDeploymentArchive;
        }
    }
}