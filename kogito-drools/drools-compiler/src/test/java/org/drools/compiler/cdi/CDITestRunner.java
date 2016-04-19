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

import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieRepositoryImpl;
import org.drools.compiler.kie.builder.impl.KieServicesImpl;
import org.drools.core.command.impl.CommandFactoryServiceImpl;
import org.drools.core.io.impl.ResourceFactoryServiceImpl;
import org.drools.core.util.FileManager;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.kie.api.KieServices;
import org.kie.api.builder.KieRepository;
import org.kie.api.cdi.KBase;
import org.kie.api.cdi.KReleaseId;
import org.kie.api.cdi.KSession;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.KieContainer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

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

        URLClassLoader urlClassLoader;
        try {
            List<URL> urls = new ArrayList<>();
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
                try {
                    ((URLClassLoader) Thread.currentThread().getContextClassLoader()).close();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to close custom URLClassLoader (this is a test error)!", e);
                }
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
        list.addAll(Arrays.asList(classes));
        list.add(KieCDIExtension.class.getName());
        list.add(KBase.class.getName());
        list.add(KSession.class.getName());
        list.add(KReleaseId.class.getName());
        list.add(KieServices.class.getName());
        list.add(KieServicesImpl.class.getName());
        list.add(KieContainer.class.getName());
        list.add(KieContainerImpl.class.getName());
        list.add(KieRepository.class.getName());
        list.add(KieRepositoryImpl.class.getName());
        list.add(KieCommands.class.getName());
        list.add(CommandFactoryServiceImpl.class.getName());
        list.add(ResourceFactoryServiceImpl.class.getName());

        Weld weld = new Weld();
        List<Class<?>> classList = new ArrayList<>();
        for (String className : list) {
            try {
                Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                classList.add(clazz);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        weld.beanClasses(classList.toArray(new Class[0]));
        weld.addExtension(new KieCDIExtension());
        // bean discovery needs to be disabled, otherwise Weld will scan and find beans from multiple tests, which
        // then results in ambiguity as multiple tests define multiple @Default kbases and/or ksessions
        weld.disableDiscovery();
        return weld;
    }
}
