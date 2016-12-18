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
 *
 */

package org.drools.core.util;

import org.kie.internal.assembler.KieAssemblerService;
import org.kie.internal.assembler.KieAssemblers;
import org.kie.internal.runtime.KieRuntimeService;
import org.kie.internal.runtime.KieRuntimes;
import org.kie.internal.runtime.beliefs.KieBeliefService;
import org.kie.internal.runtime.beliefs.KieBeliefs;
import org.kie.internal.utils.KieService;
import org.kie.internal.utils.ServiceDiscovery;
import org.kie.internal.utils.ServiceRegistry;
import org.kie.internal.utils.ServiceRegistryImpl;
import org.kie.internal.weaver.KieWeaverService;
import org.kie.internal.weaver.KieWeavers;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class ServiceDiscoveryImpl implements ServiceDiscovery {
    private static final Logger log = LoggerFactory.getLogger( ServiceDiscovery.class );

    public void discoverFactories(Enumeration<URL> confResources, ServiceRegistry serviceRegistry) {
        // iterate urls, then for each url split the service key and attempt to register each service
        while ( confResources.hasMoreElements() ) {
            URL url = confResources.nextElement();
            java.io.InputStream is = null;
            try {
                is = url.openStream();
                log.info( "Discovered kie.conf url={} ", url );
                processKieConf( is, serviceRegistry );
            } catch ( Exception exc ) {
                throw new RuntimeException( "Unable to build kie service url = " + url.toExternalForm(), exc );
            } finally {
                try {
                    if ( is != null ) {
                        is.close();
                    } else {
                        log.error( "Unable to build kie service url={}\n", url.toExternalForm() );
                    }
                } catch (IOException e1) {
                    log.warn( "Unable to close Stream for url={} reason={}\n", url, e1.getMessage() );
                }
            }
        }
    }

    public static void processKieConf(InputStream is, ServiceRegistry serviceRegistry) throws IOException {
        String conf = readFileAsString( new InputStreamReader( is ));
        processKieConf( conf, serviceRegistry );
    }

    public static void processKieConf(String conf, ServiceRegistry serviceRegistry) {
        Map<String, List> map = ( Map<String, List> ) MVEL.eval( conf );
        processKieConf(map, serviceRegistry);
    }

    public static void processKieConf(Map<String, List> map, ServiceRegistry serviceRegistry) {
        processKieServices(map, serviceRegistry);

        processKieAssemblers(map, serviceRegistry);

        processKieWeavers(map, serviceRegistry);

        processKieBeliefs(map, serviceRegistry);

        processRuntimes(map, serviceRegistry);
    }


    private static void processRuntimes(Map<String, List> map, ServiceRegistry serviceRegistry) {
        List<KieRuntimeService> runtimeList = map.get( "runtimes" );
        if ( runtimeList != null && runtimeList.size() > 0 ) {
            KieRuntimes runtimes = serviceRegistry.get(KieRuntimes.class);

            for ( KieRuntimeService runtime : runtimeList ) {
                log.info("Adding Runtime {}\n", runtime.getServiceInterface().getName());
                runtimes.getRuntimes().put( runtime.getServiceInterface().getName(),
                                            runtime);
            }

        }
    }

    private static void processKieAssemblers(Map<String, List> map, ServiceRegistry serviceRegistry) {
        List<KieAssemblerService> assemblerList = map.get( "assemblers" );
        if ( assemblerList != null && assemblerList.size() > 0 ) {
            KieAssemblers assemblers = serviceRegistry.get(KieAssemblers.class);
            for ( KieAssemblerService assemblerFactory : assemblerList ) {
                log.info( "Adding Assembler {}\n", assemblerFactory.getClass().getName() );
                assemblers.getAssemblers().put(assemblerFactory.getResourceType(),
                                               assemblerFactory);
            }
        }
    }

    private static void processKieWeavers(Map<String, List> map, ServiceRegistry serviceRegistry) {
        List<KieWeaverService> weaverList = map.get( "weavers" );
        if ( weaverList != null && weaverList.size() > 0 ) {
            KieWeavers weavers = serviceRegistry.get(KieWeavers.class);
            for ( KieWeaverService weaver : weaverList ) {
                log.info("Adding Weaver {}\n", weavers.getClass().getName());
                weavers.getWeavers().put( weaver.getResourceType(),
                                          weaver );
            }
        }
    }

    private static void processKieBeliefs(Map<String, List> map, ServiceRegistry serviceRegistry) {
        List<KieBeliefService> beliefsList = map.get( "beliefs" );
        if ( beliefsList != null && beliefsList.size() > 0 ) {
            KieBeliefs beliefs = serviceRegistry.get(KieBeliefs.class);
            for ( KieBeliefService belief : beliefsList ) {
                log.info("Adding Belief {}\n", beliefs.getClass().getName());
                beliefs.getBeliefs().put( belief.getBeliefType(),
                                          belief );
            }
        }
    }

    private static void processKieServices(Map<String, List> map, ServiceRegistry serviceRegistry) {
        List<KieService> servicesList = map.get( "services" );
        if ( servicesList != null && servicesList.size() > 0 ) {
            for ( KieService service : servicesList ) {
                log.info( "Adding Service {}\n", service.getClass().getName() );
                serviceRegistry.registerLocator(service.getServiceInterface(), new ServiceRegistryImpl.ReturnInstance(service));
            }
        }
    }

    public static String readFileAsString(Reader reader) {
        try {
            StringBuilder fileData = new StringBuilder( 1000 );
            char[] buf = new char[1024];
            int numRead;
            while ( (numRead = reader.read( buf )) != -1 ) {
                String readData = String.valueOf( buf,
                                                  0,
                                                  numRead );
                fileData.append( readData );
                buf = new char[1024];
            }
            reader.close();
            return fileData.toString();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }
}
