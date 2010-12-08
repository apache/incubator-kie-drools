/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.repository;

import java.io.File;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

/**
 * This is a utility to simulate session behavior for the test suite.
 * 
 * @author Michael Neale
 */
public class RepositorySessionUtil {

    private static ThreadLocal<RulesRepository> repo = new ThreadLocal<RulesRepository>();
    private static Repository multiThreadedRepository;
    private static Session session = null;

    // private static final Logger log = Logger.getLogger( RepositorySessionUtil.class );

    public static boolean deleteDir( File dir ) {

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                if (!deleteDir(new File(dir, children[i]))) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public static RulesRepository getRepository() throws RulesRepositoryException {
        RulesRepository repoInstance = repo.get();
        // System.out.println("----------getRepository");
        if (repoInstance == null) {

            // System.out.println("----------repoInstance == null");

            System.out.println("----------repoInstance == null");

            File dir = new File("repository");
            System.out.println("DELETING test repo: " + dir.getAbsolutePath());
            deleteDir(dir);
            System.out.println("TEST repo was deleted.");

            try {
            	//configurator = new JackrabbitRepository
            	// create a repo instance (startup)
            
	            multiThreadedRepository = RulesRepositoryConfigurator.getInstance(null).getJCRRepository();
	
	            // create a session
	            //Session session;
            
                session = multiThreadedRepository.login(new SimpleCredentials("alan_parsons", "password".toCharArray()));
                RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator(session);

                // clear out and setup
                if (admin.isRepositoryInitialized()) {
                    admin.clearRulesRepository();
                }
                RulesRepositoryConfigurator.getInstance(null).setupRepository(session);
                repoInstance = new RulesRepository(session);

                multiThreadedRepository.login(new SimpleCredentials("ADMINISTRATOR", "password".toCharArray()));
                // loonie hack
                // DroolsRepositoryAccessManager.adminThreadlocal.set( adminSession );
                repo.set(repoInstance);
            } catch (Exception e) {
                throw new RulesRepositoryException(e);
            }
        }

        return repoInstance;
    }

    public static synchronized RulesRepository getMultiThreadedRepository() throws RulesRepositoryException {
        if (multiThreadedRepository == null) {
            // System.out.println("----------repoInstance == null");

            File dir = new File("repository");
            System.out.println("DELETING test repo: " + dir.getAbsolutePath());
            deleteDir(dir);
            System.out.println("TEST repo was deleted.");

            try {
            	// create a repo instance (startup)
            	multiThreadedRepository = RulesRepositoryConfigurator.getInstance(null).getJCRRepository();

                // create a session to config repo
                Session session = multiThreadedRepository.login(new SimpleCredentials("alan_parsons", "password".toCharArray()));
                RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator(session);

                // clear out and setup
                if (admin.isRepositoryInitialized()) {
                    admin.clearRulesRepository();
                }
                RulesRepositoryConfigurator.getInstance(null).setupRepository( session);
            } catch (Exception e) {
                throw new RulesRepositoryException(e);
            }
        }

        // associate this repo instance with thread specific sessions every time.
        Session session;
        try {
            session = multiThreadedRepository.login(new SimpleCredentials("alan_parsons", "password".toCharArray()));
            RulesRepository threadLocalRepo = new RulesRepository(session);
            return threadLocalRepo;
        } catch (LoginException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static void shutdown() throws RepositoryException {
    	RulesRepositoryConfigurator.getInstance(null).shutdown();
        repo.set(null);
        multiThreadedRepository = null;
    }

}
