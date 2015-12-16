/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.cdi;

import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import java.io.File;

import org.drools.core.command.impl.CommandFactoryServiceImpl;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.core.util.FileManager;
import org.drools.core.io.impl.ResourceFactoryServiceImpl;
import org.drools.compiler.kproject.AbstractKnowledgeTest;
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
import org.kie.api.KieServices;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieRepositoryImpl;
import org.drools.compiler.kie.builder.impl.KieServicesImpl;
import org.kie.api.runtime.KieContainer;

public class CDITestRunner extends BlockJUnit4ClassRunner {

    public static volatile Weld          weld;
    public static volatile WeldContainer container;

    public static volatile FileManager   fileManager;

    public static volatile ClassLoader   origCl;
    
    public static void setUp(File... files) {
        fileManager = new FileManager();
        fileManager.setUp();
        origCl = Thread.currentThread().getContextClassLoader();
        
        ((KieServicesImpl) KieServices.Factory.get()).nullKieClasspathContainer();

        // hack to ensure atleast one beans.xml can be found, which is needed for Weld initialization
        MemoryFileSystem mfs = new MemoryFileSystem();
        mfs.write( "META-INF/beans.xml",
                   AbstractKnowledgeTest.generateBeansXML().getBytes() );
        mfs.writeAsJar( CDITestRunner.fileManager.getRootDirectory(),
                        "emptyCDIJar" );
        File file1 = CDITestRunner.fileManager.newFile( "emptyCDIJar.jar" );

        URLClassLoader urlClassLoader;
        try {
            List<URL> urls = new ArrayList<URL>();
            urls.add( file1.toURI().toURL() );            
            if ( files != null && files.length > 0 ) {
                for (  File file : files) {
                    urls.add( file.toURI().toURL() );
                }                
            }
            urlClassLoader = new URLClassLoader( urls.toArray( new URL[urls.size()] ),
                                                 Thread.currentThread().getContextClassLoader() );
            Thread.currentThread().setContextClassLoader( urlClassLoader );
        } catch ( MalformedURLException e ) {
            fail( e.getMessage() );
        }
    }

    public static void tearDown() {
        try {
            if ( CDITestRunner.weld != null ) {
                CDITestRunner.weld.shutdown();
             
                CDITestRunner.weld = null;
            }
            if ( CDITestRunner.container != null ) {
                CDITestRunner.container = null; 
            }
        } finally {
            try {
                // FIXME Java 7+
                // on Windows, the URLClassLoader will not release all resources,
                // so the attempt to delete the temporary files will fail.
                // an explicit dispose call is needed, but it has not been introduced until Java7+
                // http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4950148

                /*
                try {
                    ((URLClassLoader) Thread.currentThread().getContextClassLoader()).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */

                Thread.currentThread().setContextClassLoader( origCl );
            } finally {
                fileManager.tearDown();
            }            
        }        
    }
    
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
        //        list.add( KieCDIExtension.class.getName() );
        //        list.add( KBase.class.getName() );
        //        list.add( KSession.class.getName() );
        //        list.add( KReleaseId.class.getName() );
        //        list.add( KieServices.class.getName() );
               list.add( KieServicesImpl.class.getName() );
               list.add( KieContainer.class.getName() );
               list.add( KieContainerImpl.class.getName() );
        //        list.add( KieRepository.class.getName() );
                list.add( KieRepositoryImpl.class.getName() );
        //        list.add( KieCommands.class.getName() );
                list.add( CommandFactoryServiceImpl.class.getName() );
        //        list.add( KieResources.class.getName() );
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

        public TestWeldSEDeployment(final ResourceLoader resourceLoader,
                                    Bootstrap bootstrap,
                                    List<String> classes) {
            super( bootstrap );
            //            ResourceLoader interceptor = new ResourceLoader() {
            //                
            //                @Override
            //                public void cleanup() {
            //                    resourceLoader.cleanup();
            //                    //WeldSEUrlDeployment.BEANS_XML
            //                }
            //                
            //                @Override
            //                public Collection<URL> getResources(String name) {
            //                    resourceLoader.getResources( name );
            //                    return null;
            //                }
            //                
            //                @Override
            //                public URL getResource(String name) {
            //                    if ( name.equals( WeldSEUrlDeployment.BEANS_XML ) ) {
            //                        try {
            //                            return new URL("http://www.redhat.com");
            //                        } catch ( MalformedURLException e ) {
            //                            //fail("");
            //                        }
            //                    }
            //                    return resourceLoader.getResource( name );
            //                }
            //                
            //                @Override
            //                public Class< ? > classForName(String name) {
            //                    return resourceLoader.classForName( name );
            //                }
            //            };
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
