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
package org.drools.mvel.compiler.beliefsystem.defeasible;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.BeliefSystemType;
import org.kie.api.runtime.ClassObjectFilter;
import org.drools.core.RuleSessionConfiguration;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.TruthMaintenanceSystemFactory;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.drools.tms.TruthMaintenanceSystemEqualityKey;
import org.drools.tms.beliefsystem.BeliefSet;
import org.drools.tms.beliefsystem.defeasible.DefeasibilityStatus;
import org.drools.tms.beliefsystem.defeasible.DefeasibleBeliefSet;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class DefeasibilityTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DefeasibilityTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs (maybe unsupported)
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    protected KieSession getSessionFromString( String drlString) {
        KieBase kBase;

        try {
            System.setProperty("drools.negatable", "on");
            KieBaseTestConfiguration equalityConfig = TestParametersUtil.getEqualityInstanceOf(kieBaseTestConfiguration);
            kBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", equalityConfig, drlString);
        } finally {
            System.setProperty("drools.negatable", "off");
        }

        KieSessionConfiguration ksConf = RuleBaseFactory.newKnowledgeSessionConfiguration();
        ksConf.as(RuleSessionConfiguration.KEY).setBeliefSystemType(BeliefSystemType.DEFEASIBLE);

        KieSession kSession = kBase.newKieSession( ksConf, null );
        return kSession;
    }


    protected KieSession getSession( String ruleFile ) {
        KieBase kBase;

        try {
            System.setProperty("drools.negatable", "on");
            KieBaseTestConfiguration equalityConfig = TestParametersUtil.getEqualityInstanceOf(kieBaseTestConfiguration);
            kBase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), equalityConfig, ruleFile);
        } finally {
            System.setProperty("drools.negatable", "off");
        }

        KieSessionConfiguration ksConf = RuleBaseFactory.newKnowledgeSessionConfiguration();
        ksConf.as(RuleSessionConfiguration.KEY).setBeliefSystemType(BeliefSystemType.DEFEASIBLE);

        KieSession kSession = kBase.newKieSession( ksConf, null );
        return kSession;
    }


    private void checkStatus( EqualityKey key, int support, DefeasibilityStatus status ) {
        assertThat(key.getStatus()).isEqualTo(EqualityKey.JUSTIFIED);
        BeliefSet set = ((TruthMaintenanceSystemEqualityKey)key).getBeliefSet();
        assertThat(set instanceof DefeasibleBeliefSet).isTrue();
        DefeasibleBeliefSet dfs = ( DefeasibleBeliefSet ) set;

        assertThat(dfs.size()).isEqualTo(support);
        assertThat(dfs.getStatus()).isEqualTo(status);
    }



    @Test(timeout = 10000 )
    public void testStrictEntailment() {
        KieSession kSession = getSession( "org/drools/mvel/compiler/beliefsystem/defeasible/strict.drl" );
        kSession.fireAllRules();

        TruthMaintenanceSystem tms = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem( (ReteEvaluator) kSession );
        FactType Ctype = kSession.getKieBase().getFactType( "org.drools.defeasible", "C" );
        FactType Dtype = kSession.getKieBase().getFactType( "org.drools.defeasible", "D" );

        for ( EqualityKey key : tms.getEqualityKeys() ) {
            Class factClass = key.getFactHandle().getObject().getClass();
            if ( factClass == Ctype.getFactClass() ) {
                checkStatus( key, 2, DefeasibilityStatus.DEFINITELY );
            } else if ( factClass == Dtype.getFactClass() ) {
                checkStatus( key, 1, DefeasibilityStatus.DEFINITELY );
            } else {
                fail( "Unrecognized object has been logically justified : " + factClass );
            }
        }

        assertThat(kSession.getObjects().size()).isEqualTo(5);
    }



    @Test(timeout = 10000 )
    public void testDefeasibleEntailmentWithStrictOverride() {
        KieSession kSession = getSession( "org/drools/mvel/compiler/beliefsystem/defeasible/strictOverride.drl" );
        kSession.fireAllRules();

        TruthMaintenanceSystem tms = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem( (ReteEvaluator) kSession );
        FactType Ctype = kSession.getKieBase().getFactType( "org.drools.defeasible", "C" );
        FactType Xtype = kSession.getKieBase().getFactType( "org.drools.defeasible", "X" );

        for ( EqualityKey key : tms.getEqualityKeys() ) {
            Class factClass = key.getFactHandle().getObject().getClass();
            if ( factClass == Ctype.getFactClass() ) {
                checkStatus( key, 1, DefeasibilityStatus.DEFINITELY );
            } else if ( factClass == Xtype.getFactClass() ) {
                checkStatus( key, 1, DefeasibilityStatus.DEFINITELY );
            } else {
                fail( "Unrecognized object has been logically justified : " + factClass );
            }
        }

        assertThat(kSession.getObjects().size()).isEqualTo(5);

    }



    @Test(timeout = 10000 )
    public void defeasibleEntailmentMultiActivation() {
        KieSession kSession = getSession( "org/drools/mvel/compiler/beliefsystem/defeasible/defeat.drl" );
        kSession.fireAllRules();

        TruthMaintenanceSystem tms = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem( (ReteEvaluator) kSession );
        FactType Xtype = kSession.getKieBase().getFactType( "org.drools.defeasible", "X" );

        for ( EqualityKey key : tms.getEqualityKeys() ) {
            Class factClass = key.getFactHandle().getObject().getClass();
            if ( factClass == Xtype.getFactClass() ) {
                checkStatus( key, 3, DefeasibilityStatus.UNDECIDABLY );
            } else {
                fail( "Unrecognized object has been logically justified : " + factClass );
            }
        }

        assertThat(kSession.getObjects().size()).isEqualTo(3);
    }

    @Test(timeout = 10000 )
    public void testDefeaterNeutrality() {
        KieSession kSession = getSession( "org/drools/mvel/compiler/beliefsystem/defeasible/defeaterOnly.drl" );
        ArrayList list = new ArrayList();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();

        TruthMaintenanceSystem tms = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem( (ReteEvaluator) kSession );
        FactType Xtype = kSession.getKieBase().getFactType( "org.drools.defeasible", "X" );

        for ( EqualityKey key : tms.getEqualityKeys() ) {
            Class factClass = key.getFactHandle().getObject().getClass();
            if ( factClass == Xtype.getFactClass() ) {
                checkStatus( key, 1, DefeasibilityStatus.DEFEATEDLY );
            } else {
                fail( "Unrecognized object has been logically justified : " + factClass );
            }
        }

        for ( Object o : kSession.getObjects() ) {
            System.out.println( o );
        }
        assertThat(list.size()).isEqualTo(0);
        assertThat(kSession.getFactCount()).isEqualTo(1);
    }


    @Test(timeout = 10000 )
    public void testMultipleDefeats() {
        KieSession kSession = getSession( "org/drools/mvel/compiler/beliefsystem/defeasible/multiDefeat.drl" );
        kSession.fireAllRules();

        TruthMaintenanceSystem tms = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem( (ReteEvaluator) kSession );
        FactType Xtype = kSession.getKieBase().getFactType( "org.drools.defeasible", "X" );

        for ( EqualityKey key : tms.getEqualityKeys() ) {
            Class factClass = key.getFactHandle().getObject().getClass();
            if ( factClass == Xtype.getFactClass() ) {
                checkStatus( key, 2, DefeasibilityStatus.DEFEATEDLY );
            } else {
                fail( "Unrecognized object has been logically justified : " + factClass );
            }
        }

        for ( Object o : kSession.getObjects() ) {
            System.out.println( o );
        }
        assertThat(kSession.getObjects().size()).isEqualTo(2);


        kSession.fireAllRules();
    }


    @Test(timeout = 10000 )
    public void testRemoveDefiniteJustifier() {
        KieSession kSession = getSession( "org/drools/mvel/compiler/beliefsystem/defeasible/strictRetract.drl" );

        FactHandle h = kSession.insert( "go" );
        kSession.fireAllRules();

        TruthMaintenanceSystem tms = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem( (ReteEvaluator) kSession );
        FactType Ctype = kSession.getKieBase().getFactType( "org.drools.defeasible", "C" );
        FactType Atype = kSession.getKieBase().getFactType( "org.drools.defeasible", "A" );

        for ( EqualityKey key : tms.getEqualityKeys() ) {
            Class factClass = key.getFactHandle().getObject().getClass();
            if ( factClass == Ctype.getFactClass() ) {
                checkStatus( key, 1, DefeasibilityStatus.DEFINITELY );
            } else if ( factClass == Atype.getFactClass() ) {
                checkStatus( key, 1, DefeasibilityStatus.DEFINITELY );
            } else {
                fail( "Unrecognized object has been logically justified : " + factClass );
            }
        }

        kSession.delete( h );
        kSession.fireAllRules();

        for ( EqualityKey key : tms.getEqualityKeys() ) {
            Class factClass = key.getFactHandle().getObject().getClass();
            if ( factClass == Ctype.getFactClass() ) {
                checkStatus( key, 1, DefeasibilityStatus.DEFEASIBLY );
            } else {
                fail( "Unrecognized object has been logically justified : " + factClass );
            }
        }

    }

    @Test(timeout = 10000 )
    public void testRemoveDefeasibleJustifier() {
        KieSession kSession = getSession( "org/drools/mvel/compiler/beliefsystem/defeasible/defeaterRetract.drl" );

        FactHandle h = kSession.insert( "go" );
        kSession.fireAllRules();

        TruthMaintenanceSystem tms = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem( (ReteEvaluator) kSession );
        FactType Ctype = kSession.getKieBase().getFactType( "org.drools.defeasible", "C" );
        FactType Atype = kSession.getKieBase().getFactType( "org.drools.defeasible", "A" );

        for ( EqualityKey key : tms.getEqualityKeys() ) {
            Class factClass = key.getFactHandle().getObject().getClass();
            if ( factClass == Ctype.getFactClass() ) {
                checkStatus( key, 1, DefeasibilityStatus.DEFINITELY );
            } else if ( factClass == Atype.getFactClass() ) {
                checkStatus( key, 1, DefeasibilityStatus.DEFINITELY );
            } else {
                fail( "Unrecognized object has been logically justified : " + factClass );
            }
        }

        kSession.delete( h );
        kSession.fireAllRules();

        for ( EqualityKey key : tms.getEqualityKeys() ) {
            Class factClass = key.getFactHandle().getObject().getClass();
            if ( factClass == Ctype.getFactClass() ) {
                checkStatus( key, 1, DefeasibilityStatus.DEFINITELY );
            } else {
                fail( "Unrecognized object has been logically justified : " + factClass );
            }
        }

    }



    @Test(timeout = 10000 ) @Ignore
    public void testRemoveDefeasibleEntailmentMultiActivationWithDefeat() {
        KieSession kSession = getSession( "org/drools/mvel/compiler/beliefsystem/defeasible/defeatDefeaterRetract.drl" );
        ArrayList list = new ArrayList();

        kSession.setGlobal( "list", list );
        kSession.fireAllRules();

        TruthMaintenanceSystem tms = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem( (ReteEvaluator) kSession );
        FactType Xtype = kSession.getKieBase().getFactType( "org.drools.defeasible", "X" );

        for ( EqualityKey key : tms.getEqualityKeys() ) {
            Class factClass = key.getFactHandle().getObject().getClass();
            if ( factClass == Xtype.getFactClass() ) {
                checkStatus( key, 1, DefeasibilityStatus.DEFEATEDLY );
            } else {
                fail( "Unrecognized object has been logically justified : " + factClass );
            }
        }

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.contains("Stage1")).isTrue();
        assertThat(kSession.getObjects().size()).isEqualTo(3);

        for ( Object o : kSession.getObjects() ) {
            System.out.println( o );
        }

        FactHandle h = kSession.insert( "go" );
        kSession.fireAllRules();

        for ( EqualityKey key : tms.getEqualityKeys() ) {
            Class factClass = key.getFactHandle().getObject().getClass();
            if ( factClass == Xtype.getFactClass() ) {
                checkStatus( key, 3, DefeasibilityStatus.DEFEASIBLY );
            } else {
                fail( "Unrecognized object has been logically justified : " + factClass );
            }
        }

        for ( Object o : kSession.getObjects() ) {
            System.out.println( o );
        }

        assertThat(kSession.getObjects().size()).isEqualTo(5); // A, A, B, X, GO
        assertThat(list.size()).isEqualTo(3);
        assertThat(list.contains("Stage1")).isTrue();
        assertThat(list.contains("Stage2")).isTrue();

        kSession.retract( h );
        kSession.fireAllRules();

        for ( Object o : kSession.getObjects() ) {
            System.out.println( o );
        }

        assertThat(kSession.getObjects().size()).isEqualTo(3); // A, A, B, X, GO

        for ( EqualityKey key : tms.getEqualityKeys() ) {
            Class factClass = key.getFactHandle().getObject().getClass();
            if ( factClass == Xtype.getFactClass() ) {
                checkStatus( key, 1, DefeasibilityStatus.DEFEATEDLY );
            } else {
                fail( "Unrecognized object has been logically justified : " + factClass );
            }
        }
    }



    @Test(timeout = 10000 )
    public void testDefeaterPositiveVsNegative() {
        KieSession kSession = getSession( "org/drools/mvel/compiler/beliefsystem/defeasible/defeatersPosNeg.drl" );
        ArrayList list = new ArrayList();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();


        TruthMaintenanceSystem tms = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem( (ReteEvaluator) kSession );
        FactType Xtype = kSession.getKieBase().getFactType( "org.drools.defeasible", "X" );

        for ( EqualityKey key : tms.getEqualityKeys() ) {
            Object fact = key.getFactHandle().getObject();
            Class factClass = fact.getClass();
            if ( factClass == Xtype.getFactClass() ) {
                Integer val = (Integer) Xtype.get( fact, "id" );
                switch ( val ) {
                    case -1 :
                        checkStatus( key, 2, DefeasibilityStatus.UNDECIDABLY );
                        break;
                    case 3 :
                        checkStatus( key, 1, DefeasibilityStatus.DEFEATEDLY );
                        break;
                    case -35 :
                        checkStatus( key, 3, DefeasibilityStatus.UNDECIDABLY );
                        break;
                    case 44 :
                        checkStatus( key, 2, DefeasibilityStatus.DEFEASIBLY );
                        break;
                    default : fail( "Unrecognized fact" );
                }
            } else {
                fail( "Unrecognized object has been logically justified : " + factClass );
            }
        }

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.contains(-44)).isTrue();
        assertThat(!list.contains(-35)).isTrue();
        assertThat(kSession.getFactCount()).isEqualTo(2);
        assertThat(getNegativeObjects(kSession).size()).isEqualTo(1);
    }

    @Test(timeout = 10000 )
    public void testDefeatOutcomePosNeg() {

        KieSession kSession = getSession( "org/drools/mvel/compiler/beliefsystem/defeasible/negDefeatPos.drl" );
        ArrayList list = new ArrayList();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();

        TruthMaintenanceSystem tms = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem( (ReteEvaluator) kSession );
        FactType Xtype = kSession.getKieBase().getFactType( "org.drools.defeasible", "X" );

        for ( EqualityKey key : tms.getEqualityKeys() ) {
            Class factClass = key.getFactHandle().getObject().getClass();
            if ( factClass == Xtype.getFactClass() ) {
                checkStatus( key, 1, DefeasibilityStatus.DEFEASIBLY );
            } else {
                fail( "Unrecognized object has been logically justified : " + factClass );
            }
        }

        assertThat(kSession.getObjects().size()).isEqualTo(2);
        assertThat(getNegativeObjects(kSession).size()).isEqualTo(1);
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.contains("-1")).isTrue();

    }


    @Test(timeout = 10000 )
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



    @Test(timeout = 10000 )
    public void testWMStatusOnNegativeDefeat() {
        String droolsSource =
                "package org.drools.tms.test; " +
                "global java.util.List posList;" +
                "global java.util.List negList;" +

                "declare Bar value : int @key end " +

                "rule Top " +
                "@Defeasible " +
                "@Defeats( 'Sub' ) " +
                "when " +
                "   $i : Integer( this < 10 ) " +
                "then " +
                "   insertLogical( new Bar( $i ) ); " +
                "end " +

                "rule Sub " +
                "@Defeasible " +
                "when " +
                "   $i : Integer() " +
                "then " +
                "   insertLogical( new Bar( $i ), $i > 10 ? 'pos' : 'neg' ); " +
                "end " +

                "rule Sup " +
                "@Defeasible " +
                "@Defeats( 'Sub' ) " +
                "when " +
                "   $i : Integer( this > 10 ) " +
                "then " +
                "   insertLogical( new Bar( $i ), 'neg' ); " +
                "end " +

                "rule React_Pos " +
                "when " +
                "   $b : Bar() " +
                "then " +
                "   posList.add( $b ); " +
                "   System.out.println( ' ++++ ' + $b ); " +
                "end " +

                "rule React_Neg " +
                "when " +
                "   $b : Bar( _.neg )" +
                "then " +
                "   negList.add( $b ); " +
                "   System.out.println( ' ---- ' + $b ); " +
                "end " +

                "";

        KieSession session = getSessionFromString( droolsSource );
        List posList = new ArrayList();
        List negList = new ArrayList();
        session.setGlobal( "posList", posList );
        session.setGlobal( "negList", negList );

        session.insert( 20 );
        session.insert( 5 );

        session.fireAllRules();

        assertThat(posList.size()).isEqualTo(1);
        assertThat(negList.size()).isEqualTo(1);

        Object posBar = posList.get( 0 );
        InternalFactHandle posHandle = (InternalFactHandle) session.getFactHandle( posBar );
        DefeasibleBeliefSet dbs = (DefeasibleBeliefSet) ((TruthMaintenanceSystemEqualityKey)posHandle.getEqualityKey()).getBeliefSet();
        assertThat(dbs.size()).isEqualTo(1);
        assertThat(dbs.isNegated()).isFalse();
        assertThat(dbs.isDecided()).isTrue();
        assertThat(dbs.isPositive()).isTrue();

        assertThat(dbs.getFactHandle()).isSameAs(posHandle);
        assertThat(posHandle.isNegated()).isFalse();
        assertThat(dbs.isDefeasiblyPosProveable()).isTrue();
        assertThat(session.getObjects().contains(posBar)).isTrue();

        Object negBar = negList.get( 0 );

        InternalFactHandle negHandle = (InternalFactHandle) getNegativeHandles(session).get(0);
        dbs = (DefeasibleBeliefSet) ((TruthMaintenanceSystemEqualityKey)negHandle.getEqualityKey()).getBeliefSet();
        assertThat(dbs.size()).isEqualTo(1);
        assertThat(dbs.isPositive()).isFalse();
        assertThat(dbs.isDecided()).isTrue();
        assertThat(dbs.isNegated()).isTrue();

        assertThat(dbs.getFactHandle()).isSameAs(negHandle);
        assertThat(negHandle.isNegated()).isTrue();

        assertThat(dbs.isDefeasiblyNegProveable()).isTrue();
        assertThat(session.getObjects().contains(negBar)).isTrue();

    }




    @Test
    public void testSelfDefeatWithRebuttal() {
        String droolsSource =
                "package org.drools.tms.test; " +
                "global java.util.List posList;" +
                "global java.util.List negList;" +

                "declare Bar value : int @key end " +

                "rule Create " +
                "@Defeasible " +
                "when " +
                "   $i : Integer() " +
                "then " +
                "   System.out.println( 'Create Bar ' + $i ); " +
                "   bolster( new Bar( $i ) ); " +
                "end " +

                "rule Defeater " +
                "@Direct " +
                "@Defeasible " +
                "@Defeats( 'Create' ) " +
                "when " +
                "   $b1 : Bar( $val1 : value ) " +
                "   $b2 : Bar( $val2 : value > $val1, value - $val1 < 15 ) " +
                "then " +
                "   System.out.println( $b2 + ' defeats ' + $b1 ); " +
                "   bolster( new Bar( $val1 ), 'neg' ); " +
                "end " +

                "rule ReactP " +
                "salience 100 " +
                "when " +
                "   $b : Bar() " +
                "then " +
                "   posList.add( $b ); " +
                "   System.out.println( ' ++++ ' + $b ); " +
                "end " +

                "rule ReactN " +
                "salience 100 " +
                "when " +
                "   $b : Bar( _.neg )  " +
                "then " +
                "   negList.add( $b ); " +
                "   System.out.println( ' ---- ' + $b ); " +
                "end " ;

        KieSession session = getSessionFromString( droolsSource );
        List posList = new ArrayList();
        List negList = new ArrayList();
        session.setGlobal( "posList", posList );
        session.setGlobal( "negList", negList );

        session.insert( 10 );
        session.insert( 30 );
        session.insert( 20 );

        session.fireAllRules();

        assertThat(posList.size()).isEqualTo(2);
        assertThat(negList.size()).isEqualTo(1);

    }


    @Test
    public void testDefeatersAndDefeasibles() {
        String droolsSource =
                "package org.drools.tms.test; " +
                "global java.util.List posList;" +

                "declare Bar value : int @key end " +

                "rule B " +
                "@Defeater " +
                "@Defeats( 'C' ) " +
                "when " +
                "   $i : Integer() " +
                "then " +
                "   insertLogical( new Bar( $i ) ); " +
                "end " +

                "rule C " +
                "@Defeasible " +
                "when " +
                "   $i : Integer() " +
                "then " +
                "   insertLogical( new Bar( $i ) ); " +
                "end " +

                "rule React " +
                "when " +
                "   $b : Bar() " +
                "then " +
                "   posList.add( $b ); " +
                "   System.out.println( ' ++++ ' + $b ); " +
                "end " ;

        KieSession session = getSessionFromString( droolsSource );
        List posList = new ArrayList();
        session.setGlobal( "posList", posList );

        session.insert( 10 );

        session.fireAllRules();
        assertThat(posList.size()).isEqualTo(1);
    }


    @Test(timeout = 10000 )
    public void testManyDefeasibles() {
        String drl = "package org.drools.defeasible; " +
                     "declare Fact " +
                     "     fact: String @key " +
                     "end " +
                     " " +
                     "rule init " +
                     "     when " +
                     "     then " +
                     "         insert( new Fact( 'one' ) ); " +
                     "         insert( new Fact( 'two' ) ); " +
                     "         insert( new Fact( 'two' ) ); " +
                     "end " +
                     " " +
                     "rule rule1 " +
                     "     @Defeasible " +
                     "     enabled true " +
                     "     when " +
                     "         Fact( \"one\"; ) " +
                     "     then " +
                     "         System.out.println(\"one causes wibble\"); " +
                     "         insertLogical( new Fact( \"wibble\") ); " +
                     "end " +
                     " " +
                     "rule rule2 " +
                     "     @Defeasible " +
                     "     when " +
                     "         Fact( \"two\"; ) " +
                     "     then " +
                     "         System.out.println(\"two causes wibble\"); " +
                     "         insertLogical( new Fact( \"wibble\") ); " +
                     "end " +
                     " " +
                     "rule rule3 " +
                     "     @Defeater " +
                     "     @Defeats( \"rule2\" ) " +
                     "     when " +
                     "         Fact( \"two\"; ) " +
                     "     then " +
                     "         System.out.println(\"two negates wibble\"); " +
                     "         insertLogical( new Fact( \"wibble\"), \"neg\" ); " +
                     "end";

        KieSession session = getSessionFromString( drl );
        session.fireAllRules();

        FactType factType = session.getKieBase().getFactType( "org.drools.defeasible", "Fact" );
        for ( Object o : session.getObjects( new ClassObjectFilter( factType.getFactClass() ) ) ) {
            if ( "wibble".equals( factType.get( o, "fact" ) ) ) {
                InternalFactHandle handle = (InternalFactHandle) session.getFactHandle( o );
                DefeasibleBeliefSet dbs = (DefeasibleBeliefSet) ((TruthMaintenanceSystemEqualityKey)handle.getEqualityKey()).getBeliefSet();

                assertThat(dbs.size()).isEqualTo(3);
                assertThat(dbs.isConflicting()).isTrue();
            }
        }

    }


    @Test(timeout = 10000 )
    public void testRetractNegativeDefeaters() {

        String drl = "declare Foo end " +

                     "rule Def " +
                     "  @Defeater " +
                     "when " +
                     "  String() " +
                     "then " +
                     "  insertLogical( new Foo(), 'neg' ); " +
                     "end ";
        KieSession session = getSessionFromString( drl );

        FactHandle h = session.insert( "foo" );

        session.fireAllRules();
        assertThat(session.getObjects().size()).isEqualTo(1);

        session.delete( h );

        session.fireAllRules();
        assertThat(session.getObjects().size()).isEqualTo(0);
    }


    public List getNegativeObjects(KieSession kSession) {
        List list = new ArrayList();
        java.util.Iterator it = ((StatefulKnowledgeSessionImpl) kSession).getObjectStore().iterateNegObjects(null);
        while ( it.hasNext() ) {
            list.add(  it.next() );
        }
        return list;
    }

    public List getNegativeHandles(KieSession kSession) {
        List list = new ArrayList();
        java.util.Iterator it = ((StatefulKnowledgeSessionImpl) kSession).getObjectStore().iterateNegFactHandles(null);
        while ( it.hasNext() ) {
            list.add(  it.next() );
        }
        return list;
    }


}
