/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.audit.strategy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;


public enum PersistenceStrategyType {

    KIE_SESSION,
    STANDALONE_JTA,
    STANDALONE_JTA_SPRING_SHARED_EM,
    STANDALONE_LOCAL_SPRING_SHARED_EM,
    STANDALONE_LOCAL;

    public static PersistenceStrategy getPersistenceStrategy(PersistenceStrategyType type, Environment env ) { 
        return getPersistenceStrategy(type, env, null, null);
    }
            
    public static PersistenceStrategy getPersistenceStrategy(PersistenceStrategyType type, EntityManagerFactory emf ) { 
        return getPersistenceStrategy(type, null, emf, null);
    }
            
    public static PersistenceStrategy getPersistenceStrategy(PersistenceStrategyType type, String peristenceUnitName ) { 
        return getPersistenceStrategy(type, null, null, peristenceUnitName);
    }
            
    public static PersistenceStrategy getPersistenceStrategy(PersistenceStrategyType type, 
            Environment env, EntityManagerFactory emf, 
            String persistenceUnitName) { 
        EntityManager em = null;
    	if( env != null ) { 
            emf = (EntityManagerFactory) env.get(EnvironmentName.ENTITY_MANAGER_FACTORY); 
            // in case there is entity manager already available and the type is shared entity manager then use this
            em = (EntityManager) env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
        }
        
        PersistenceStrategy persistenceStrategy;
        switch(type) { 
        case KIE_SESSION: 
            persistenceStrategy = new KieSessionCommandScopedStrategy(env);
            break;
        case STANDALONE_JTA: 
            if( emf != null ) { 
                persistenceStrategy = new StandaloneJtaStrategy(emf);
            } else if(persistenceUnitName != null) { 
                persistenceStrategy = new StandaloneJtaStrategy(Persistence.createEntityManagerFactory(persistenceUnitName));
            } else { 
                throw new IllegalArgumentException(
                        "Either the EnvironmentName.ENTITY_MANAGER_FACTORY should be set in the given Environment, or a valid peristence unit name is required.");
            }
            break;
        case STANDALONE_JTA_SPRING_SHARED_EM:
        	if( em != null ) { 
                persistenceStrategy = new SpringStandaloneJtaSharedEntityManagerStrategy(em);
            } else if( emf != null ) { 
                persistenceStrategy = new SpringStandaloneJtaSharedEntityManagerStrategy(emf);
            } else { 
                throw new IllegalArgumentException(
                        "The EnvironmentName.ENTITY_MANAGER_FACTORY should be set "
                                + "with a valid EntityManagerFactory instance in the given Environment instance.");
            }
            break;
        case STANDALONE_LOCAL_SPRING_SHARED_EM:
        	if( em != null ) { 
                persistenceStrategy = new SpringStandaloneLocalSharedEntityManagerStrategy(em);
            } else if( emf != null ) { 
                persistenceStrategy = new SpringStandaloneLocalSharedEntityManagerStrategy(emf);
            } else { 
                throw new IllegalArgumentException(
                        "The EnvironmentName.ENTITY_MANAGER_FACTORY should be set "
                                + "with a valid EntityManagerFactory instance in the given Environment instance.");
            }
            break;
        case STANDALONE_LOCAL:
            if( emf != null ) { 
                persistenceStrategy = new StandaloneLocalStrategy(emf);
            } else { 
                throw new IllegalArgumentException( "This strategy requires a EntityManagerFactory instance!");
            }
            break;
        default:
            throw new IllegalArgumentException("Unknown " + PersistenceStrategyType.class.getSimpleName() + " type: " + type );
        }
        return persistenceStrategy;
    }
}
