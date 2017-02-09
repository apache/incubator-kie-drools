/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.cdi.kproject;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.Set;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.util.AnnotationLiteral;

import org.drools.cdi.CDITestRunner;
import org.drools.compiler.kie.builder.impl.KieServicesImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Test;
import org.kie.api.KieServices;

import static org.junit.Assert.assertNotNull;

public class KieProjectCDITest extends AbstractKnowledgeTest {
    private static final ProtectionDomain PROTECTION_DOMAIN;

    static {
        PROTECTION_DOMAIN = (ProtectionDomain) AccessController.doPrivileged( new PrivilegedAction() {

            public Object run() {
                return JavaDialectRuntimeData.class.getProtectionDomain();
            }
        } );
    }   
    
    public static class KPTestLiteral extends AnnotationLiteral<KPTest>
            implements
            KPTest {
        private String value;

        public KPTestLiteral(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }

    }

    @Test
    public void createMultpleJarAndFileResources() throws IOException,
                       ClassNotFoundException,
                       InterruptedException {
        createKieModule( "jar1", true );
        createKieModule( "jar2", true );
        createKieModule( "jar3", true );
        createKieModule( "fol4", false );

        ClassLoader origCl = Thread.currentThread().getContextClassLoader();
        try {
            java.io.File file1 = fileManager.newFile( "jar1-1.0-SNAPSHOT.jar" );
            java.io.File file2 = fileManager.newFile( "jar2-1.0-SNAPSHOT.jar" );
            java.io.File file3 = fileManager.newFile( "jar3-1.0-SNAPSHOT.jar" );
            java.io.File fol4 = fileManager.newFile( "fol4-1.0-SNAPSHOT" );
        
            URLClassLoader urlClassLoader = new URLClassLoader( new URL[]{file1.toURI().toURL(), file2.toURI().toURL(), file3.toURI().toURL(), fol4.toURI().toURL() } );
            Thread.currentThread().setContextClassLoader( urlClassLoader );
            
            Enumeration<URL> e = urlClassLoader.getResources( KieModuleModelImpl.KMODULE_JAR_PATH );
            while ( e.hasMoreElements() ) {
                URL url = e.nextElement();
                System.out.println( url );
            }

            Class cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.compiler.cdi.test.KProjectTestClassjar1" );
            assertNotNull( cls );
            cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.compiler.cdi.test.KProjectTestClassjar2" );
            assertNotNull( cls );
            cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.compiler.cdi.test.KProjectTestClassjar3" );
            assertNotNull( cls );
            
            Weld weld = CDITestRunner.createWeld( KProjectTestClass.class.getName(),
                                                  KPTestLiteral.class.getName(),
                                                  "org.drools.compiler.cdi.test.KProjectTestClassjar1",
                                                  "org.drools.compiler.cdi.test.KProjectTestClassjar2",
                                                  "org.drools.compiler.cdi.test.KProjectTestClassjar3",
                                                  "org.drools.compiler.cdi.test.KProjectTestClassfol4" );
            ((KieServicesImpl) KieServices.Factory.get()).nullKieClasspathContainer();
            WeldContainer container = weld.initialize();            
            
            Set<Bean< ? >> beans = container.getBeanManager().getBeans( KProjectTestClass.class, new KPTestLiteral( "jar1" ) );
            Bean bean = (Bean) beans.toArray()[0];
            KProjectTestClass o1 = (KProjectTestClass) bean.create( container.getBeanManager().createCreationalContext( null ) );
            assertNotNull( o1 );            
            testEntry(o1, "jar1");
            
            beans = container.getBeanManager().getBeans( KProjectTestClass.class, new KPTestLiteral( "jar2" ) );
            bean = (Bean) beans.toArray()[0];
            KProjectTestClass o2 = (KProjectTestClass) bean.create( container.getBeanManager().createCreationalContext( null ) );
            assertNotNull( o2 );            
            testEntry(o2, "jar2");
            
            beans = container.getBeanManager().getBeans( KProjectTestClass.class, new KPTestLiteral( "jar3" ) );
            bean = (Bean) beans.toArray()[0];
            KProjectTestClass o3 = (KProjectTestClass) bean.create( container.getBeanManager().createCreationalContext( null ) );
            assertNotNull( o3 );            
            testEntry(o3, "jar3");

            beans = container.getBeanManager().getBeans( KProjectTestClass.class, new KPTestLiteral( "fol4" ) );
            bean = (Bean) beans.toArray()[0];
            KProjectTestClass o4 = (KProjectTestClass) bean.create( container.getBeanManager().createCreationalContext( null ) );
            assertNotNull( o4 );            
            testEntry(o4, "fol4");
            
            weld.shutdown();
        } finally {
            // FIXME Java 7+
            // on Windows, the URLClassLoader will not release all resources,
            // so the attempt to delete the temporary files will fail.
            // an explicit dispose call is needed, but it has not been introduced until Java7+
            // http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4950148

            /*
            ((URLClassLoader) Thread.currentThread().getContextClassLoader()).close();
            */

            Thread.currentThread().setContextClassLoader( origCl );
        }
    }

}
