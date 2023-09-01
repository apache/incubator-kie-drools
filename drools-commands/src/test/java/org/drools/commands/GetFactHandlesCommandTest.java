/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.commands;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import org.drools.commands.runtime.rule.GetFactHandlesCommand;
import org.drools.core.common.InternalFactHandle;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SuppressWarnings("unchecked")
public class GetFactHandlesCommandTest {

    private KieSession ksession;
    private ExecutableRunner runner;
    private Context context;
    private Random random = new Random();
    
    @Before
    public void setup() { 
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ksession = kbase.newKieSession();
        runner = ExecutableRunner.create();
        context = ( (RegistryContext) runner.createContext() ).register( KieSession.class, ksession );
    }
    
    @After
    public void cleanUp() { 
       ksession.dispose(); 
    }
    
    @Test
    public void getEmptyFactHandlesTest() { 
        GetFactHandlesCommand command = new GetFactHandlesCommand();
        Object result = runner.execute(command, context);
        if( result instanceof Collection<?> ) { 
            assertThat(result).isNotNull();
            assertThat(((Collection<?>) result).isEmpty()).isTrue();
        }
        else { 
           fail("result of command was NOT a collection of FactHandles"); 
        }
    }
    
    @Test
    public void getOneFactHandleTest() { 
        String randomFact = "" + random.nextLong();
        ksession.insert(randomFact);
        GetFactHandlesCommand command = new GetFactHandlesCommand();
        Object result = runner.execute(command, context);
        
        verifyThatCollectionContains1FactHandleWithThisFact(randomFact, result);
    }

    @Test
    public void getMultipleFactHandleTest() { 
        HashSet<String> factSet = new HashSet<String>();
        int numFacts = 4;
        for( int i = 0; i < numFacts; ++i ) { 
            factSet.add("" + random.nextInt());
        }
        for( String fact : factSet ) { 
            ksession.insert(fact);
        }
        
        GetFactHandlesCommand command = new GetFactHandlesCommand();
        Object result = runner.execute(command, context);
        
        verifyThatCollectionContainsTheseFactHandle(factSet, result);
    }

    @Test
    public void getEmptyDisconnectedFactHandlesTest() { 
        GetFactHandlesCommand command = new GetFactHandlesCommand(true);
        Object result = runner.execute(command, context);
        if( result instanceof Collection<?> ) { 
            assertThat(result).isNotNull();
            assertThat(((Collection<?>) result).isEmpty()).isTrue();
        }
        else { 
           fail("result of command was NOT a collection of FactHandles"); 
        }
    }

    @Test
    public void getOneDisconnectedFactHandleTest() { 
        System.out.println( Thread.currentThread().getStackTrace()[1].getMethodName() );
        String randomFact = "" + random.nextLong();
        ksession.insert(randomFact);
        
        // Retrieve and verify fact handle collections
        GetFactHandlesCommand command = new GetFactHandlesCommand(false);
        Object result = runner.execute(command, context);
        verifyThatCollectionContains1FactHandleWithThisFact(randomFact, result);
        FactHandle factHandle = (FactHandle) ((Collection<FactHandle>) result).toArray()[0];
        
        command = new GetFactHandlesCommand(false);
        result = runner.execute(command, context);
        verifyThatCollectionContains1FactHandleWithThisFact(randomFact, result);
        FactHandle connectedFactHandle = (FactHandle) ((Collection<FactHandle>) result).toArray()[0];
        
        command = new GetFactHandlesCommand(true);
        result = runner.execute(command, context);
        verifyThatCollectionContains1FactHandleWithThisFact(randomFact, result);
        FactHandle disconnectedFactHandle = (FactHandle) ((Collection<FactHandle>) result).toArray()[0];

        // Test fact handle collections
        assertThat(factHandle == connectedFactHandle).isTrue();
        assertThat(!(factHandle == disconnectedFactHandle)).isTrue();
    }

    @Test
    public void getMultipleDisconnectedFactHandleTest() { 
        System.out.println( "\nTest: " + Thread.currentThread().getStackTrace()[1].getMethodName() );
        HashSet<String> factSet = new HashSet<String>();
        int numFacts = 4;
        for( int i = 0; i < numFacts; ++i ) { 
            factSet.add("" + random.nextInt());
        }
        for( String fact : factSet ) { 
            ksession.insert(fact);
        }
        
        GetFactHandlesCommand command = new GetFactHandlesCommand(false);
        Object result = runner.execute(command, context);
        verifyThatCollectionContainsTheseFactHandle(factSet, result);
        Collection<FactHandle> factHandles = ((Collection<FactHandle>) result);
        
        command = new GetFactHandlesCommand(false);
        result = runner.execute(command, context);
        verifyThatCollectionContainsTheseFactHandle(factSet, result);
        Collection<FactHandle> connectedFactHandles = ((Collection<FactHandle>) result);
        
        command = new GetFactHandlesCommand(true);
        result = runner.execute(command, context);
        verifyThatCollectionContainsTheseFactHandle(factSet, result);
        Collection<FactHandle> disconnectedFactHandles = ((Collection<FactHandle>) result);
       
        // Test fact handle collections
        HashSet<FactHandle> factHandlesCopy = new HashSet<FactHandle>(factHandles);
        for( int i = 0; i < connectedFactHandles.size(); ++i ) { 
            for( Object connectedFact : connectedFactHandles ) { 
               Iterator<FactHandle> iter = factHandlesCopy.iterator();
               while(iter.hasNext() ) { 
                  Object fact = iter.next();
                  if( fact == connectedFact ) { 
                      iter.remove();
                  }
               }
            }
        }
        assertThat(factHandlesCopy.isEmpty()).isTrue();
        
        for( int i = 0; i < disconnectedFactHandles.size(); ++i ) { 
            for( Object disconnectedFact : disconnectedFactHandles ) { 
               for( Object fact : factHandles ) {
                   assertThat(!(fact == disconnectedFact)).isTrue();
               }
            }
        }
        assertThat(factHandles.size() == disconnectedFactHandles.size()).isTrue();
        
    }

    /**
     * Helper methods
     */
    private void verifyThatCollectionContains1FactHandleWithThisFact(String fact, Object collection) { 
        if( collection instanceof Collection<?> ) { 
            Collection<FactHandle> factHandles = null;
            try { 
                factHandles = (Collection<FactHandle>) collection;
            }
            catch( Exception e ) { 
               fail( "Collection was not a Colleciton<FactHandle> " + e.getMessage()); 
            }

            assertThat(!factHandles.isEmpty()).isTrue();
            assertThat(factHandles.size() == 1).isTrue();
            InternalFactHandle factHandle = (InternalFactHandle) factHandles.toArray()[0];
            assertThat(fact.equals(factHandle.getObject())).isTrue();
         }
         else { 
            fail("result of command was NOT a collection of FactHandles"); 
         }
    }

    private void verifyThatCollectionContainsTheseFactHandle(HashSet<String> factSet, Object collection) { 
        factSet = (HashSet<String>) factSet.clone();
        if( collection instanceof Collection<?> ) { 
            Collection<FactHandle> factHandles = (Collection<FactHandle>) collection;
            assertThat(!factHandles.isEmpty()).isTrue();
            assertThat(factHandles.size() == factSet.size()).as(factSet.size() + "inserted but only " + factHandles.size() + " facts retrieved").isTrue();
            Object [] internalFactHandles = factHandles.toArray();
            for( int i = 0; i < internalFactHandles.length; ++i ) { 
                Object factObject = ((InternalFactHandle) internalFactHandles[i]).getObject();
                assertThat(factSet.contains(factObject)).isTrue();
                factSet.remove(factObject);
            }
            assertThat(factSet.isEmpty()).as("Additional facts found that weren't inserted.").isTrue();
        }
        else { 
            fail("result of command was NOT a collection of FactHandles"); 
        }
    }

}
