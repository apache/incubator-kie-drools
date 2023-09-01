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
package org.drools.mvel.compiler.beliefsystem.jtms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.drools.core.BeliefSystemType;
import org.drools.core.RuleSessionConfiguration;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.TruthMaintenanceSystemFactory;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.kiesession.entrypoints.NamedEntryPoint;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.mvel.compiler.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.drools.tms.TruthMaintenanceSystemEqualityKey;
import org.drools.tms.TruthMaintenanceSystemImpl;
import org.drools.tms.beliefsystem.jtms.JTMSBeliefSystem;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.event.rule.RuleEventListener;
import org.kie.internal.event.rule.RuleEventManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class JTMSTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public JTMSTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with testConflictToggleWithoutGoingEmpty, testPosNegNonConflictingInsertions. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false, true);
    }

    protected KieSession getSessionFromString( String drlString) {
        KieBase kBase;

        try {
            System.setProperty("drools.negatable", "on");
            kBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drlString);
        } finally {
            System.setProperty("drools.negatable", "off");
        }

        KieSessionConfiguration ksConf = RuleBaseFactory.newKnowledgeSessionConfiguration();
        ksConf.as(RuleSessionConfiguration.KEY).setBeliefSystemType(BeliefSystemType.JTMS);
        KieSession kSession = kBase.newKieSession( ksConf, null );
        return kSession;
    }
    
    protected KieSession getSessionFromFile( String ruleFile ) {
        KieBase kBase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, ruleFile);

        KieSessionConfiguration ksConf = RuleBaseFactory.newKnowledgeSessionConfiguration();
        ksConf.as(RuleSessionConfiguration.KEY).setBeliefSystemType(BeliefSystemType.JTMS);
        
        KieSession kSession = kBase.newKieSession( ksConf, null );
        return kSession;
    }    
    
    @Test(timeout = 10000 )
    public void testPosNegNonConflictingInsertions() {
        String s = "package org.drools.core.beliefsystem.jtms;\n" +
                   "\n" +
                   "import java.util.List \n" +
                   "import " + InternalMatch.class.getCanonicalName() + " \n" +
                   "global java.util.List list;\n" +
                   "\n" +
                   "rule \"go1\"\n" +
                   "when\n" +
                   "    String( this == 'go1' )\n" +
                   "then\n" +
                   "    insertLogical( 'neg', 'neg' );\n" +
                   "end\n" +
                   "\n" +
                   "rule \"go2\"\n" +
                   "when\n" +
                   "    String( this == 'go2' )\n" +
                   "then\n" +
                   "    insertLogical( 'pos' );\n" +
                   "end\n" +
                   "\n" +
                   "rule \"Positive\"\n" +
                   "when\n" +
                   "    $n : String( this != 'go1' || == 'go2' ) \n" +
                   "then\n" +
                   "    final String s = '+' + $n;" +
                   "    final List l = list;" +
                   "    l.add( s );\n" +
                   "end\n" +
                   "rule \"Negative\"\n" +
                   "when\n" +
                   "    $n : String(   _.neg, this != 'go1' || == 'go2' ) \n" +
                   "then\n" +
                   "    final String s = '-' + $n; \n" +
                   "    final List l = list; \n" +
                   "    l.add( s ); \n" +
                   "end\n";

        KieSession kSession =  getSessionFromString( s );
        List list = new ArrayList();
        kSession.setGlobal( "list", list );

        ( (RuleEventManager) kSession ).addEventListener( new RuleEventListener() {
            @Override
            public void onDeleteMatch( Match match ) {
                String rule = match.getRule().getName();
                if (rule.equals( "Positive" )) {
                    list.remove("+" + match.getDeclarationValue( "$n" ));
                } else if (rule.equals( "Negative" )) {
                    list.remove("-" + match.getDeclarationValue( "$n" ));
                }
            }
        } );

        FactHandle fhGo1 = kSession.insert( "go1" );
        kSession.fireAllRules();
        assertThat(list.contains("-neg")).isTrue();

        assertThat(kSession.getEntryPoint("DEFAULT").getObjects().size()).isEqualTo(1); //just go1
        assertThat(getNegativeObjects(kSession).size()).isEqualTo(1);
        
        FactHandle fhGo2 = kSession.insert( "go2" );
        kSession.fireAllRules();
        assertThat(list.contains("-neg")).isTrue();
        assertThat(list.contains("+pos")).isTrue();

        assertThat(kSession.getEntryPoint("DEFAULT").getObjects().size()).isEqualTo(3); //go1, go2, pos
        assertThat(getNegativeObjects(kSession).size()).isEqualTo(1);
        
        kSession.retract( fhGo1 );
        kSession.fireAllRules();
        assertThat(list.contains("-neg")).isFalse();
        assertThat(list.contains("+pos")).isTrue();
        assertThat(kSession.getEntryPoint("DEFAULT").getObjects().size()).isEqualTo(2); //go2, pos
        assertThat(getNegativeObjects(kSession).size()).isEqualTo(0);

        kSession.retract( fhGo2 );
        kSession.fireAllRules();
        assertThat(list.contains("-neg")).isFalse();
        assertThat(list.contains("+pos")).isFalse();
        assertThat(kSession.getEntryPoint("DEFAULT").getObjects().size()).isEqualTo(0);
        assertThat(getNegativeObjects(kSession).size()).isEqualTo(0);
    }

    @Test(timeout = 10000 )
    public void testConflictToggleWithoutGoingEmpty() {
        String s = "package org.drools.core.beliefsystem.jtms;\n" +
                   "\n" +
                   "import java.util.List \n" +
                   "import " + InternalMatch.class.getCanonicalName() + " \n" +
                   "global java.util.List list;\n" +
                   "\n" +
                   "rule \"go1\"\n" +
                   "when\n" +
                   "    String( this == 'go1' )\n" +
                   "then\n" +
                   "    insertLogical( 'xxx' );\n" +
                   "end\n" +
                   "rule \"go2\"\n" +
                   "when\n" +
                   "    String( this == 'go2' )\n" +
                   "then\n" +
                   "    insertLogical( 'xxx');\n" +
                   "end\n" +
                   "rule \"go3\"\n" +
                   "when\n" +
                   "    String( this == 'go3' )\n" +
                   "then\n" +
                   "    insertLogical( 'xxx');\n" +
                   "end\n" +

                   "\n" +
                   "rule \"go4\"\n" +
                   "when\n" +
                   "    String( this == 'go4' )\n" +
                   "then\n" +
                   "    insertLogical( 'xxx', 'neg' );\n" +
                   "end\n" +
                   "\n" +


                   "rule \"Positive\"\n" +
                   "when\n" +
                   "    $n : String( this == 'xxx' ) \n" +
                   "then\n" +
                   "    final String s = '+' + $n;" +
                   "    final List l = list;" +
                   "    l.add( s );\n" +
                   "end\n" +
                   "rule \"Negative\"\n" +
                   "when\n" +
                   "    $n : String( _.neg, this == 'xxx' )\n" +
                   "then\n" +
                   "    final String s = '-' + $n; \n" +
                   "    final List l = list; \n" +
                   "    l.add( s ); \n" +
                   "end\n" +
                   "";

        KieSession kSession =  getSessionFromString( s );
        List list = new ArrayList();
        kSession.setGlobal( "list", list );

        ( (RuleEventManager) kSession ).addEventListener( new RuleEventListener() {
            @Override
            public void onDeleteMatch( Match match ) {
                String rule = match.getRule().getName();
                if (rule.equals( "Positive" )) {
                    list.remove("+" + match.getDeclarationValue( "$n" ));
                } else if (rule.equals( "Negative" )) {
                    list.remove("-" + match.getDeclarationValue( "$n" ));
                }
            }
        } );

        FactHandle fhGo1 = kSession.insert( "go1" );
        FactHandle fhGo2 = kSession.insert( "go2" );
        FactHandle fhGo3 = kSession.insert( "go3" );

        kSession.fireAllRules();
        System.out.println( list );
        assertThat(list.contains("+xxx")).isTrue();

        FactHandle fhGo4 = kSession.insert( "go4" );
        kSession.fireAllRules();
        assertThat(list.isEmpty()).isTrue();

        kSession.delete(fhGo4);
        kSession.fireAllRules();
        assertThat(list.contains("+xxx")).isTrue();
    }
    
    @Test(timeout = 10000 )
    @Ignore("Currently cannot support updates")
    public void testChangeInPositivePrime() {
        String s = "package org.drools.core.beliefsystem.jtms;\n" +
                "\n" + 
                "import org.kie.internal.event.rule.ActivationUnMatchListener;\n" +
                "import java.util.List \n" +
                "import " + InternalMatch.class.getCanonicalName() + ";\n" +
                "import org.drools.mvel.compiler.Person;\n" +
                "global java.util.List list;\n" + 
                "\n" + 
                "rule \"go1\"\n" + 
                "when\n" + 
                "    String( this == 'go1' )\n" + 
                "then\n" + 
                "    Person p = new Person( 'darth' ); \n" +
                "    p.setNotInEqualTestObject(1); \n" +
                "    insertLogical( p );\n" +                
                "end\n" + 
                "rule \"go2\"\n" + 
                "when\n" + 
                "    String( this == 'go2' )\n" + 
                "then\n" + 
                "    Person p = new Person( 'darth' ); \n" +
                "    p.setNotInEqualTestObject(2); \n" +
                "    insertLogical( p );\n" +                                
                "end\n" + 
                "rule \"go3\"\n" + 
                "when\n" + 
                "    String( this == 'go3' )\n" + 
                "then\n" + 
                "    Person p = new Person( 'darth' ); \n" +
                "    p.setNotInEqualTestObject(3); \n" +
                "    insertLogical( p );\n" +                
                "end\n" +                 
                "\n";
        
        KieSession kSession =  getSessionFromString( s );
        List list = new ArrayList();
        kSession.setGlobal( "list", list );
        
        // We want to make sure go1 is prime, and then that it switches to go2
        FactHandle fhGo1 = kSession.insert( "go1" );
        kSession.fireAllRules();                
        FactHandle fhGo2 = kSession.insert( "go2" );
        kSession.fireAllRules();   
        FactHandle fhGo3 = kSession.insert( "go3" );
        kSession.fireAllRules();
        
        NamedEntryPoint ep = ( NamedEntryPoint ) ((StatefulKnowledgeSessionImpl)kSession).getEntryPoint( "DEFAULT" );
        assertThat(ep.getObjects().size()).isEqualTo(4); //just go1, go2, go3, Person(darth)
        
        int count = 0;
        for ( Object object : ep.getObjects() ) {
            if ( object instanceof Person ) {
                assertThat(((Person) object).getNotInEqualTestObject()).isEqualTo(Integer.valueOf(1));
                count++;
            }
        }
        assertThat(count).isEqualTo(1);

        TruthMaintenanceSystem tms = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem(ep);
        assertThat(tms.getEqualityKeysSize()).isEqualTo(1); // Only Person type is logical

        Iterator<EqualityKey> it = tms.getEqualityKeys().iterator();
        TruthMaintenanceSystemEqualityKey key = (TruthMaintenanceSystemEqualityKey) it.next();
        while ( !key.getFactHandle().getObject().equals( new Person( "darth") ) ) {
            key = (TruthMaintenanceSystemEqualityKey) it.next();
        }

        assertThat(key.getBeliefSet().size()).isEqualTo(3);
        assertThat(((Person) key.getBeliefSet().getFactHandle().getObject()).getNotInEqualTestObject()).isEqualTo(Integer.valueOf(1));
        
        kSession.delete( fhGo1 );
        kSession.fireAllRules();
        it = tms.getEqualityKeys().iterator();
        key = ( TruthMaintenanceSystemEqualityKey  ) it.next();
        while ( !key.getFactHandle().getObject().equals( new Person( "darth") ) ) {
            key = ( TruthMaintenanceSystemEqualityKey  ) it.next();
        }

        assertThat(key.getBeliefSet().size()).isEqualTo(2);
        assertThat(((Person) key.getBeliefSet().getFactHandle().getObject()).getNotInEqualTestObject()).isEqualTo(Integer.valueOf(3));
        
        kSession.delete( fhGo3 );
        kSession.fireAllRules();
        it = tms.getEqualityKeys().iterator();
        key = ( TruthMaintenanceSystemEqualityKey  )  it.next();
        while ( !key.getFactHandle().getObject().equals( new Person( "darth") ) ) {
            key = ( TruthMaintenanceSystemEqualityKey  )  it.next();
        }

        assertThat(key.getBeliefSet().size()).isEqualTo(1);
        assertThat(((Person) key.getBeliefSet().getFactHandle().getObject()).getNotInEqualTestObject()).isEqualTo(Integer.valueOf(2));
    }    
    
    @Test(timeout = 10000 )
    @Ignore("Currently cannot support updates")
    public void testChangeInNegativePrime() {
        String s = "package org.drools.core.beliefsystem.jtms;\n" +
                "\n" + 
                "import org.kie.internal.event.rule.ActivationUnMatchListener;\n" +
                "import java.util.List \n" +
                "import " + InternalMatch.class.getCanonicalName() + ";\n" +
                "import org.drools.mvel.compiler.Person;" +
                "global java.util.List list;\n" + 
                "\n" +
                "declare entry-point 'neg' end \n" +
                "" +
                "rule \"go1\"\n" + 
                "when\n" + 
                "    String( this == 'go1' )\n" + 
                "then\n" + 
                "    Person p = new Person( 'darth' ); \n" +
                "    p.setNotInEqualTestObject(1); \n" +
                "    insertLogical( p, 'neg' );\n" +                
                "end\n" + 
                "rule \"go2\"\n" + 
                "when\n" + 
                "    String( this == 'go2' )\n" + 
                "then\n" + 
                "    Person p = new Person( 'darth' ); \n" +
                "    p.setNotInEqualTestObject(2); \n" +
                "    insertLogical( p, 'neg' );\n" +                                
                "end\n" + 
                "rule \"go3\"\n" + 
                "when\n" + 
                "    String( this == 'go3' )\n" + 
                "then\n" + 
                "    Person p = new Person( 'darth' ); \n" +
                "    p.setNotInEqualTestObject(3); \n" +
                "    insertLogical( p, 'neg' );\n" +                
                "end\n" +                 
                "\n";

        KieSession kSession =  getSessionFromString( s );
        List list = new ArrayList();
        kSession.setGlobal( "list", list );
        
        // We want to make sure go1 is prime, and then that it switches to go2
        FactHandle fhGo1 = kSession.insert( "go1" );
        kSession.fireAllRules();                
        FactHandle fhGo2 = kSession.insert( "go2" );
        kSession.fireAllRules();   
        FactHandle fhGo3 = kSession.insert( "go3" );
        kSession.fireAllRules();
        
        NamedEntryPoint ep = ( NamedEntryPoint ) ((StatefulKnowledgeSessionImpl)kSession).getEntryPoint( "DEFAULT" );
        assertThat(ep.getObjects().size()).isEqualTo(3); //just go1, go2, go3
        assertThat(getNegativeObjects(kSession).size()).isEqualTo(1);  // Person(darth)
        
        int count = 0;
        for ( Object object : getNegativeObjects(kSession) ) {
            if ( object instanceof Person ) {
                assertThat(((Person) object).getNotInEqualTestObject()).isEqualTo(Integer.valueOf(1));
                count++;
            }
        }
        assertThat(count).isEqualTo(1);

        TruthMaintenanceSystem tms = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem(ep);

        assertThat(tms.getEqualityKeysSize()).isEqualTo(1); // Only Person type is logical
        Iterator it = tms.getEqualityKeys().iterator();
        TruthMaintenanceSystemEqualityKey key = ( TruthMaintenanceSystemEqualityKey  ) it.next() ;
        while ( !key.getFactHandle().getObject().equals( new Person( "darth") ) ) {
            key = ( TruthMaintenanceSystemEqualityKey  ) it.next() ;
        }

        assertThat(key.getBeliefSet().size()).isEqualTo(3);
        assertThat(((Person) key.getBeliefSet().getFactHandle().getObject()).getNotInEqualTestObject()).isEqualTo(Integer.valueOf(1));
        
        kSession.retract( fhGo1 );
        kSession.fireAllRules();
        it = tms.getEqualityKeys().iterator();
        key = ( TruthMaintenanceSystemEqualityKey  ) it.next() ;
        while ( !key.getFactHandle().getObject().equals( new Person( "darth") ) ) {
            key = ( TruthMaintenanceSystemEqualityKey  ) it.next() ;
        }

        assertThat(key.getBeliefSet().size()).isEqualTo(2);
        assertThat(((Person) key.getBeliefSet().getFactHandle().getObject()).getNotInEqualTestObject()).isEqualTo(Integer.valueOf(3));

        kSession.retract( fhGo3 );
        kSession.fireAllRules();
        it = tms.getEqualityKeys().iterator();
        key = ( TruthMaintenanceSystemEqualityKey  ) it.next() ;
        while ( !key.getFactHandle().getObject().equals( new Person( "darth") ) ) {
            key = ( TruthMaintenanceSystemEqualityKey  ) it.next() ;
        }

        assertThat(key.getBeliefSet().size()).isEqualTo(1);
        assertThat(((Person) key.getBeliefSet().getFactHandle().getObject()).getNotInEqualTestObject()).isEqualTo(Integer.valueOf(2));
    }
    
    @Test(timeout = 10000 )
    @Ignore("Currently cannot support updates")
    public void testRetractHandleWhenOnlyNeg() {
        String s = "package org.drools.core.beliefsystem.jtms;\n" +
                "\n" + 
                "import java.util.List \n" +
                "import " + InternalMatch.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" + 
                "\n" + 
                "rule \"go1_1\"\n" + 
                "when\n" + 
                "    String( this == 'go1' )\n" + 
                "then\n" + 
                "    insertLogical( new String( 'neg' ), 'neg' );\n" +                
                "end\n" + 
                "rule \"go1_2\"\n" + 
                "when\n" + 
                "    String( this == 'go1' )\n" + 
                "then\n" + 
                "    insertLogical( new String( 'neg' ), 'neg' );\n" +                
                "end\n" + 
                "rule \"go1_3\"\n" + 
                "when\n" + 
                "    String( this == 'go1' )\n" + 
                "then\n" + 
                "    insertLogical( new String( 'neg' ), 'neg' );\n" +                
                "end\n" +                 
                "\n" +            
                "rule \"Negative\"\n" + 
                "when\n" + 
                "    $n : String(  _.neg, this != 'go1' || == 'go2' ) \n" +
                "then\n" +  
                "    final String s = '-' + $n; \n" +
                "    final List l = list; \n" +
                "    l.add( s ); \n" +
                "end\n";
        
        KieSession kSession =  getSessionFromString( s );
        List list = new ArrayList();
        kSession.setGlobal( "list", list );

        ( (RuleEventManager) kSession ).addEventListener( new RuleEventListener() {
            @Override
            public void onDeleteMatch( Match match ) {
                String rule = match.getRule().getName();
                if (rule.equals( "Negative" )) {
                    list.remove("-" + match.getDeclarationValue( "$n" ));
                }
            }
        } );

        FactHandle fhGo1 = kSession.insert( "go1" );
        kSession.fireAllRules();
        assertThat(list.contains("-neg")).isTrue();

        assertThat(kSession.getEntryPoint("DEFAULT").getObjects().size()).isEqualTo(1); //just go1
        assertThat(getNegativeObjects(kSession).size()).isEqualTo(1);
        
        NamedEntryPoint ep = ( NamedEntryPoint ) ((StatefulKnowledgeSessionImpl)kSession).getEntryPoint( "DEFAULT" );
        TruthMaintenanceSystem tms = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem(ep);
        assertThat(tms.getEqualityKeysSize()).isEqualTo(2); // go1, neg are two different strings.
        Iterator it = tms.getEqualityKeys().iterator();
        TruthMaintenanceSystemEqualityKey key = ( TruthMaintenanceSystemEqualityKey  ) it.next() ;
        while ( !key.getFactHandle().getObject().equals( "neg") ) {
            key = ( TruthMaintenanceSystemEqualityKey  ) it.next() ;
        }

        assertThat(key.getBeliefSet().size()).isEqualTo(3);

        tms.delete( key.getLogicalFactHandle() );

        assertThat(key.getBeliefSet().size()).isEqualTo(0);

        assertThat(kSession.getEntryPoint("DEFAULT").getObjects().size()).isEqualTo(1); //just go1
        assertThat(getNegativeObjects(kSession).size()).isEqualTo(0);
        assertThat(key.getBeliefSet().size()).isEqualTo(0);
        assertThat(tms.getEqualityKeysSize()).isEqualTo(1);
    }  
    
    @Test(timeout = 10000 )
    public void testConflictStrict() {
        KieSession kSession = getSessionFromFile( "posNegConflict.drl" );

        ArrayList list = new ArrayList();
        kSession.setGlobal( "list", list );

        
        NamedEntryPoint ep = ( NamedEntryPoint ) ((StatefulKnowledgeSessionImpl)kSession).getEntryPoint( "DEFAULT" );
        JTMSBeliefSystem bs = ( JTMSBeliefSystem ) ((TruthMaintenanceSystemImpl) TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem(ep)).getBeliefSystem();
        bs.STRICT = true;

        try {
            kSession.fireAllRules();
            fail( "A fact and its negation should have been asserted, but no exception was trhown in strict mode" );
        } catch ( Exception e ) {
        } finally {
            bs.STRICT = false;
        }
    }   

    @Test(timeout = 10000 )
    @Ignore("Currently cannot support updates")
    public void testConflictTMS() {
        KieSession kSession = getSessionFromFile( "posNegTms.drl" );

        ArrayList list = new ArrayList();
        kSession.setGlobal( "list", list );

        FactHandle a = kSession.insert( "a" );
        FactHandle b = kSession.insert( "b" );
        FactHandle c = kSession.insert( "c" );
        FactHandle d = kSession.insert( "d" );

        try {
            kSession.fireAllRules();

            assertThat(kSession.getFactCount()).isEqualTo(4);
            assertThat(list.size()).isEqualTo(0);

            kSession.retract( a );
            kSession.fireAllRules();

            assertThat(kSession.getFactCount()).isEqualTo(3);
            assertThat(list.size()).isEqualTo(0);

            kSession.retract( b );
            kSession.fireAllRules();

            assertThat(kSession.getFactCount()).isEqualTo(2);
            assertThat(getNegativeObjects(kSession).size()).isEqualTo(1);
            assertThat(list.size()).isEqualTo(1);

            a = kSession.insert( "a" );
            kSession.fireAllRules();

            assertThat(kSession.getFactCount()).isEqualTo(3);
            assertThat(getNegativeObjects(kSession).size()).isEqualTo(0);
            assertThat(list.size()).isEqualTo(1);

            kSession.retract( c );
            kSession.fireAllRules();

            assertThat(kSession.getFactCount()).isEqualTo(2);
            assertThat(getNegativeObjects(kSession).size()).isEqualTo(0);
            assertThat(list.size()).isEqualTo(1);

            kSession.retract( d );
            kSession.fireAllRules();

            assertThat(kSession.getFactCount()).isEqualTo(2);
            assertThat(getNegativeObjects(kSession).size()).isEqualTo(0);
            assertThat(list.size()).isEqualTo(2);

            kSession.retract( a );
            kSession.fireAllRules();

            assertThat(kSession.getFactCount()).isEqualTo(0);
            assertThat(getNegativeObjects(kSession).size()).isEqualTo(0);
            assertThat(list.size()).isEqualTo(2);

            c = kSession.insert( "c" );
            kSession.fireAllRules();

            assertThat(kSession.getFactCount()).isEqualTo(1);
            assertThat(getNegativeObjects(kSession).size()).isEqualTo(1);
            assertThat(list.size()).isEqualTo(3);


        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "No exception should have been thrown" );
        }
    }

    public List getNegativeObjects(KieSession kSession) {
        List list = new ArrayList();
        Iterator it = ((StatefulKnowledgeSessionImpl) kSession).getObjectStore().iterateNegObjects(null);
        while ( it.hasNext() ) {
            list.add(  it.next() );
        }
        return list;
    }

    @Test
    public void testPrimeJustificationWithEqualityMode() {
        String droolsSource =
                "package org.drools.tms.test; \n" +
                "declare Bar end \n" +
                "" +
                "declare Holder x : Bar end \n" +
                "" +
                "" +
                "rule Init \n" +
                "when \n" +
                "then \n" +
                "   insert( new Holder( new Bar() ) ); \n" +
                "end \n" +

                "rule Justify \n" +
                "when \n" +
                " $s : Integer() \n" +
                " $h : Holder( $b : x ) \n" +
                "then \n" +
                " insertLogical( $b ); \n" +
                "end \n" +

                "rule React \n" +
                "when \n" +
                " $b : Bar(  ) \n" +
                "then \n" +
                " System.out.println( $b );  \n" +
                "end \n" ;

        KieSession session = getSessionFromString( droolsSource );

        FactHandle handle1 = session.insert( 10 );
        FactHandle handle2 = session.insert( 20 );

        assertThat(session.fireAllRules()).isEqualTo(4);

        session.delete( handle1 );
        assertThat(session.fireAllRules()).isEqualTo(0);
    }

}
