package org.drools.cdi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.discovery.AbstractWeldSEDeployment;
import org.jboss.weld.environment.se.discovery.ImmutableBeanDeploymentArchive;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.junit.Ignore;
import org.junit.Test;

public class KProjectCDITest {
    
    @Test @Ignore
    public void test1() {
        final List<String> classes = new ArrayList<String>();
        classes.add( TestClass.class.getName() );
        classes.add( TestClassImpl.class.getName() );
        
        Weld weldContainer = new Weld() {
            @Override
            protected Deployment createDeployment(ResourceLoader resourceLoader,
                                                  Bootstrap bootstrap) {
                return new TestWeldSEDeployment(resourceLoader, bootstrap, classes);
            }
        };
        
        WeldContainer weld = weldContainer.initialize();
        TestClass bean = weld.instance().select(TestClass.class).get();
        
        System.out.println(bean.getKBase1());
    }
    
    
    public static class TestWeldSEDeployment extends AbstractWeldSEDeployment {
        private final BeanDeploymentArchive beanDeploymentArchive;

        public TestWeldSEDeployment(ResourceLoader resourceLoader,
                                    Bootstrap bootstrap,
                                    List<String> classes) {
            super(bootstrap);
            beanDeploymentArchive = new ImmutableBeanDeploymentArchive("classpath", classes, null);

        }

        public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
            return Collections.singletonList(beanDeploymentArchive);
        }

        public BeanDeploymentArchive loadBeanDeploymentArchive(Class<?> beanClass) {
            return beanDeploymentArchive;
        }

    }    
}
