package org.drools.cdi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.command.impl.CommandFactoryServiceImpl;
import org.drools.io.impl.ResourceFactoryServiceImpl;
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
import org.kie.KieServices;
import org.kie.builder.KieRepository;
import org.kie.builder.impl.KieRepositoryImpl;
import org.kie.builder.impl.KieServicesImpl;
import org.kie.cdi.KBase;
import org.kie.cdi.KReleaseId;
import org.kie.cdi.KSession;
import org.kie.command.KieCommands;
import org.kie.io.KieResources;

public class CDITestRunner extends BlockJUnit4ClassRunner {

    public static volatile Weld          weld;
    public static volatile WeldContainer container;

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
        list.add( KReleaseId.class.getName() );
        list.add( KieServices.class.getName() );
        list.add( KieServicesImpl.class.getName() );
        list.add( KieRepository.class.getName() );
        list.add( KieRepositoryImpl.class.getName() );
        list.add( KieCommands.class.getName() );
        list.add( CommandFactoryServiceImpl.class.getName() );
        list.add( KieResources.class.getName() );
        list.add( ResourceFactoryServiceImpl.class.getName() );        
        
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