/*
 * Copyright 2012 Red Hat
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
package org.drools.compiler.beliefsystem.defeasible;

import org.drools.core.BeliefSystemType;
import org.drools.core.SessionConfiguration;
import org.drools.core.beliefsystem.defeasible.DefeasibilityStatus;
import org.drools.core.beliefsystem.defeasible.DefeasibleBeliefSet;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.rule.EntryPointId;
import org.drools.core.util.Entry;
import org.drools.core.util.ObjectHashMap.ObjectEntry;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DefeasibilityTest {


    private void checkDefeasibilityByHandleId( EqualityKey key, DefeasibilityStatus status, int insertSize, int undefeatedSize ) {
        DefeasibleBeliefSet dbs = (DefeasibleBeliefSet) key.getBeliefSet();

        assertEquals( status, dbs.getStatus() );

        switch ( status ) {
            case DEFINITELY:
//                assertTrue( dbs.getDeliberation() == BeliefSet.FACT_WMSTATE.IN );
//                assertTrue( dbs.isDefinitelyProvable() );
//                assertTrue( dbs.isDefeasiblyProvable() );
//                assertFalse( dbs.isUndecidable() );
//                assertFalse( dbs.isDefeated() );
                break;

            case DEFEASIBLY:
//                assertTrue( dbs.getDeliberation() == BeliefSet.FACT_WMSTATE.IN );
//                assertFalse( dbs.isDefinitelyProvable() );
//                assertTrue( dbs.isDefeasiblyProvable() );
//                assertFalse( dbs.isUndecidable() );
//                assertFalse( dbs.isDefeated() );
                break;

            case DEFEATEDLY:
//                assertTrue( dbs.getDeliberation() == BeliefSet.FACT_WMSTATE.HELD );
//                assertFalse( dbs.isDefinitelyProvable() );
//                assertFalse( dbs.isDefeasiblyProvable() );
//                assertFalse( dbs.isUndecidable() );
//                assertTrue( dbs.isDefeated() );
                break;

            case UNDECIDABLY:
//                assertTrue( dbs.getDeliberation() == BeliefSet.FACT_WMSTATE.HELD );
//                assertFalse( dbs.isDefinitelyProvable() );
//                assertFalse( dbs.isDefeasiblyProvable() );
//                assertTrue( dbs.isUndecidable() );
//                assertFalse( dbs.isDefeated() );
                break;
        }

        assertEquals( insertSize, dbs.size() );
        assertEquals( undefeatedSize, dbs.undefeatdSize() );
    }


    protected StatefulKnowledgeSession getSession( String ruleFile  ) {
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add( ResourceFactory.newClassPathResource(ruleFile, getClass()), ResourceType.DRL );
        if ( kBuilder.hasErrors() ) {
            System.err.println( kBuilder.getErrors() );
            fail();
        }

        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );

        KieSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();

        ((SessionConfiguration) ksConf).setBeliefSystemType( BeliefSystemType.DEFEASIBLE );


        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession( ksConf, null );
        return kSession;
    }

    @Test
    public void strictEntailment() {
        StatefulKnowledgeSession kSession = getSession( "strict.drl" );
        Map<String, FactHandle> handles = new HashMap<String, FactHandle>();
        kSession.fireAllRules();

        EntryPoint ep = kSession.getEntryPoint(EntryPointId.DEFAULT.getEntryPointId());
        TruthMaintenanceSystem tms = ((NamedEntryPoint)ep).getTruthMaintenanceSystem();

        assertEquals(2, tms.getEqualityKeyMap().size());
        assertEquals( 5, kSession.getObjects().size() );

        checkDefeasibilityByHandleId( getEqualityKey("C( id=99 )", tms), DefeasibilityStatus.DEFINITELY, 2, 2 );
        checkDefeasibilityByHandleId( getEqualityKey("D( id=-5 )", tms), DefeasibilityStatus.DEFINITELY, 1, 1);
    }

    @Test
    public void defeasibleEntailment( ) {
        StatefulKnowledgeSession kSession = getSession( "strictOverride.drl" );
        kSession.fireAllRules();

        EntryPoint ep = kSession.getEntryPoint(EntryPointId.DEFAULT.getEntryPointId());
        TruthMaintenanceSystem tms = ((NamedEntryPoint)ep).getTruthMaintenanceSystem();

        assertEquals( 2, tms.getEqualityKeyMap().size());
        assertEquals( 5, kSession.getObjects().size() );

        checkDefeasibilityByHandleId( getEqualityKey("X( id=-1 )", tms), DefeasibilityStatus.DEFINITELY, 3, 2);
        checkDefeasibilityByHandleId( getEqualityKey("C( id=99 )", tms), DefeasibilityStatus.DEFINITELY, 1, 1);
    }

    @Test
    public void defeasibleEntailmentMultiActivation( ) {
        StatefulKnowledgeSession kSession = getSession( "defeat.drl" );
        kSession.fireAllRules();

        EntryPoint ep = kSession.getEntryPoint(EntryPointId.DEFAULT.getEntryPointId());
        TruthMaintenanceSystem tms = ((NamedEntryPoint)ep).getTruthMaintenanceSystem();

        assertEquals( 1, tms.getEqualityKeyMap().size() );
        assertEquals( 4, kSession.getObjects().size() );

        //checkDefeasibilityByHandleId( 4, DefeasibilityStatus.UNDECIDABLY, 3, 3, tms );
        checkDefeasibilityByHandleId( getEqualityKey("X( id=-1 )", tms), DefeasibilityStatus.UNDECIDABLY, 3, 3 );
    }


//    @Test
//    public void testDefeasibleEntailmentMultiActivationWithDefeatComposite() {
//        defeasibleEntailmentMultiActivationWithDefeat( true );
//    }
//
//
//    @Test
//    public void testDefeasibleEntailmentMultiActivationWithDefeat() {
//        defeasibleEntailmentMultiActivationWithDefeat( false );
//    }
//
//
//    public void defeasibleEntailmentMultiActivationWithDefeat( boolean composite ) {
//        StatefulKnowledgeSession kSession = getSession( "org/drools/beliefsystem/defeasible/defeatDefeater.drl", composite );
//        ArrayList list = new ArrayList();
//        kSession.setGlobal( "list", list );
//        kSession.fireAllRules();
//
//        TruthMaintenanceSystem tms = ( (StatefulKnowledgeSessionImpl) kSession ).session.getTruthMaintenanceSystem();
//        assertEquals( 1, tms.getJustifiedMap().size() );
//
//        checkDefeasibilityByHandleId( 4, DefeasibilityStatus.DEFEATEDLY, 3, 1, tms );
//
//        assertEquals( 3, kSession.getObjects().size() );
//
//        assertEquals( 1, list.size() );
//        assertTrue( list.contains( "Stage1" ) );
//
//        kSession.insert( "go" );
//        kSession.fireAllRules();
//
//        checkDefeasibilityByHandleId( 4, DefeasibilityStatus.DEFEASIBLY, 4, 1, tms );
//
//        for ( Object o : kSession.getObjects() ) {
//            System.out.println( o );
//        }
//        System.out.println( list );
//
////        assertEquals( 5, kSession.getFactCount() );
//
//        assertEquals( 3, list.size() );
//        assertTrue( list.contains( "Stage1" ) );
//        assertTrue( list.contains( "Stage2" ) );
//
//    }
//
//
//    @Test
//    public void testDefeaterNeutrality() {
//        defeaterNeutrality( false );
//    }
//
//    @Test
//    public void testDefeaterNeutralityComposite() {
//        defeaterNeutrality( true );
//    }
//
//    public void defeaterNeutrality( boolean composite ) {
//        StatefulKnowledgeSession kSession = getSession( "org/drools/beliefsystem/defeasible/defeaterOnly.drl", composite );
//        ArrayList list = new ArrayList();
//        kSession.setGlobal( "list", list );
//        kSession.fireAllRules();
//
//        // TruthMaintenanceSystem tms = ( (StatefulKnowledgeSessionImpl) kSession ).session.getTruthMaintenanceSystem();
//
//        for ( Object o : kSession.getObjects() ) {
//            System.out.println( o );
//        }
//
//        assertEquals( 0, list.size() );
//        assertEquals( 2, kSession.getFactCount() );
//    }
//
//
//    @Test
//    public void testMultipleDefeats() {
//        multipleDefeats( false );
//    }
//
//    @Test
//    public void testMultipleDefeatsComposite() {
//        multipleDefeats( true );
//    }
//
//    public void multipleDefeats( boolean composite ) {
//        StatefulKnowledgeSession kSession = getSession( "org/drools/beliefsystem/defeasible/multiDefeat.drl", composite );
//        kSession.fireAllRules();
//
//        TruthMaintenanceSystem tms = ( (StatefulKnowledgeSessionImpl) kSession ).session.getTruthMaintenanceSystem();
//        assertEquals( 1, tms.getJustifiedMap().size() );
//        assertEquals( 3, kSession.getFactCount() );
//
//        checkDefeasibilityByHandleId( 3, DefeasibilityStatus.DEFEATEDLY, 3, 2, tms );
//    }
//
//
//    @Test
//    public void testRemoveDefiniteJustifier() {
//        removeDefiniteJustifier( false );
//    }
//
//    @Test
//    public void testRemoveDefiniteJustifierComposite() {
//        removeDefiniteJustifier( true );
//    }
//
//    public void removeDefiniteJustifier( boolean composite ) {
//        StatefulKnowledgeSession kSession = getSession( "org/drools/beliefsystem/defeasible/strictRetract.drl", composite );
//
//        FactHandle h = kSession.insert( "go" );
//        kSession.fireAllRules();
//
//        TruthMaintenanceSystem tms = ( (StatefulKnowledgeSessionImpl) kSession ).session.getTruthMaintenanceSystem();
//
//        checkDefeasibilityByHandleId( 3, DefeasibilityStatus.DEFINITELY, 2, 2, tms );
//
//        kSession.retract( h );
//        kSession.fireAllRules();
//
//        checkDefeasibilityByHandleId( 3, DefeasibilityStatus.DEFEASIBLY, 1, 1, tms );
//
//    }
//
//    @Test
//    public void testRemoveDefeasibleJustifier() {
//        removeDefeasibleJustifier( false );
//    }
//
//    @Test
//    public void testRemoveDefeasibleJustifierComposite() {
//        removeDefeasibleJustifier( true );
//    }
//
//    public void removeDefeasibleJustifier( boolean composite ) {
//        StatefulKnowledgeSession kSession = getSession( "org/drools/beliefsystem/defeasible/defeaterRetract.drl", composite );
//
//        FactHandle h = kSession.insert( "go" );
//        kSession.fireAllRules();
//
//        TruthMaintenanceSystem tms = ( (StatefulKnowledgeSessionImpl) kSession ).session.getTruthMaintenanceSystem();
//
//        checkDefeasibilityByHandleId( 3, DefeasibilityStatus.DEFINITELY, 2, 2, tms );
//
//        kSession.retract( h );
//        kSession.fireAllRules();
//
//        checkDefeasibilityByHandleId( 3, DefeasibilityStatus.DEFINITELY, 1, 1, tms );
//
//    }
//
//
//
//    @Test
//    public void testRemoveDefeasibleEntailmentMultiActivationWithDefeat() {
//        removeDefeasibleEntailmentMultiActivationWithDefeat( false );
//    }
//
//    @Test
//    public void testRemoveDefeasibleEntailmentMultiActivationWithDefeatComposite() {
//        removeDefeasibleEntailmentMultiActivationWithDefeat( true );
//    }
//
//    public void removeDefeasibleEntailmentMultiActivationWithDefeat( boolean composite ) {
//        StatefulKnowledgeSession kSession = getSession( "org/drools/beliefsystem/defeasible/defeatDefeaterRetract.drl", composite );
//        ArrayList list = new ArrayList();
//        kSession.setGlobal( "list", list );
//        kSession.fireAllRules();
//
//        TruthMaintenanceSystem tms = ( (StatefulKnowledgeSessionImpl) kSession ).session.getTruthMaintenanceSystem();
//
//        FactHandle h = kSession.insert( "go" );
//        kSession.fireAllRules();
//
//        kSession.retract( h );
//        kSession.fireAllRules();
//
//        checkDefeasibilityByHandleId(4, DefeasibilityStatus.DEFEATEDLY, 3, 1, tms);
//
//        assertEquals( 3, kSession.getObjects().size() );
//
//    }
//
//
//
//    @Test
//    public void testDefeaterPositiveVsNegative() {
//
//
//        StatefulKnowledgeSession kSession = getSession( "org/drools/beliefsystem/defeasible/defeatersPosNeg.drl", false );
//        ArrayList list = new ArrayList();
//        kSession.setGlobal( "list", list );
//        kSession.fireAllRules();
//
//        TruthMaintenanceSystem tms = ( (StatefulKnowledgeSessionImpl) kSession ).session.getTruthMaintenanceSystem();
//
//        // -35
//        checkDefeasibilityByHandleId( 3, DefeasibilityStatus.DEFEASIBLY, 3, 3, tms );
//        // 44
//        checkDefeasibilityByHandleId( 4, DefeasibilityStatus.DEFEASIBLY, 2, 2, tms );
//        // 3
//        checkDefeasibilityByHandleId( 5, DefeasibilityStatus.DEFEATEDLY, 2, 1, tms );
//        // -1
//        checkDefeasibilityByHandleId( 6, DefeasibilityStatus.UNDECIDABLY, 2, 2, tms );
//
//        for ( Object o : kSession.getObjects() ) {
//            System.out.println( o );
//        }
//
//        assertEquals( 2, list.size() );
//        assertTrue( list.contains( 44 ) );
//        assertTrue( list.contains( -35 ) );
//        assertEquals( 4, kSession.getFactCount() );
//    }
//
//    @Test
//    public void testDefeaterPositiveVsNegativeComposite() {
//
//        StatefulKnowledgeSession kSession = getSession( "org/drools/beliefsystem/defeasible/defeatersPosNeg.drl", true );
//        ArrayList list = new ArrayList();
//        kSession.setGlobal( "list", list );
//        kSession.fireAllRules();
//
//
//        TruthMaintenanceSystem tms = ( (StatefulKnowledgeSessionImpl) kSession ).session.getTruthMaintenanceSystem();
//
//        // -35
//        checkDefeasibilityByHandleId( 3, DefeasibilityStatus.DEFEASIBLY, 3, 3, tms );
//        // 44
//        checkDefeasibilityByHandleId( 4, DefeasibilityStatus.DEFEASIBLY, 2, 2, tms );
//        // 3
//        checkDefeasibilityByHandleId( 5, DefeasibilityStatus.DEFEATEDLY, 2, 1, tms );
//        // -1
//        checkDefeasibilityByHandleId( 6, DefeasibilityStatus.UNDECIDABLY, 2, 2, tms );
//
//        for ( Object o : kSession.getObjects() ) {
//            System.out.println( o );
//        }
//
//        System.out.println( list );
//        assertEquals( 1, list.size() );
//        assertTrue( list.contains( -44 ) );
//        assertEquals( 2, kSession.getFactCount() );
//        assertEquals( 1, ((StatefulKnowledgeSessionImpl) kSession).getEntryPoint("neg").getFactCount() );
//
//        StatefulKnowledgeSession kSession2 = kSession.getKieBase().newStatefulKnowledgeSession();
//
////        NegatableBeliefSystem.STRICT = true;
////
////        try {
////            kSession2.fireAllRules();
////            fail( "X(-35) should have been asserted in both positive and negated form" );
////        } catch ( Exception e ) {
////
////        } finally {
////            NegatableBeliefSystem.STRICT = false;
////        }
//
//        kSession.dispose();
//        kSession2.dispose();
//    }
//
//
//
//
//    @Test
//    public void testDefeatOutcomePosNeg() {
//
//        StatefulKnowledgeSession kSession = getSession( "org/drools/beliefsystem/defeasible/negDefeatPos.drl", false );
//        ArrayList list = new ArrayList();
//        kSession.setGlobal( "list", list );
//        kSession.fireAllRules();
//
//        TruthMaintenanceSystem tms = ( (StatefulKnowledgeSessionImpl) kSession ).session.getTruthMaintenanceSystem();
//
//        for ( Object o : kSession.getObjects() ) {
//            System.out.println( o );
//        }
//
//        checkDefeasibilityByHandleId( 3, DefeasibilityStatus.DEFEASIBLY, 2, 1, tms );
//
//        System.out.println( list );
//
//        // Since the NEG is not plugged in, the DFL just inserts this fact
//        assertEquals( 1, list.size() );
//        assertTrue( list.contains( "+1" ) );
//
//    }
//
//
//    @Test
//    public void testDefeatOutcomePosNegComposite() {
//
//        StatefulKnowledgeSession kSession = getSession( "org/drools/beliefsystem/defeasible/negDefeatPos.drl", true );
//        ArrayList list = new ArrayList();
//        kSession.setGlobal( "list", list );
//        kSession.fireAllRules();
//
//        TruthMaintenanceSystem tms = ( (StatefulKnowledgeSessionImpl) kSession ).session.getTruthMaintenanceSystem();
//
//        for ( Object o : kSession.getObjects() ) {
//            System.out.println( o );
//        }
//
//        checkDefeasibilityByHandleId( 3, DefeasibilityStatus.DEFEASIBLY, 2, 1, tms );
//
//        System.out.println( list );
//
//        assertEquals( 1, list.size() );
//        assertTrue( list.contains( "-1" ) );
//    }
//
//
//
//
////    @Test
////    public void testFactOverrideByDefeat() {
////        StatefulKnowledgeSession kSession = getSession("org/drools/beliefsystem/defeasible/defeatFactOverride.drl");
////        ArrayList list = new ArrayList();
////        kSession.setGlobal( "list", list );
////        kSession.fireAllRules();
////
////        TruthMaintenanceSystem tms = ( (StatefulKnowledgeSessionImpl) kSession ).session.getTruthMaintenanceSystem();
////
////
////    }


    public EqualityKey getEqualityKey(String id, TruthMaintenanceSystem tms) {
        Entry[] entries = (Entry[]) tms.getEqualityKeyMap().toArray();
        for ( Entry e : entries ) {
            EqualityKey key = (EqualityKey)((ObjectEntry)e).getValue();
            if ( key.getFactHandle().getObject().toString().equals(id)) {
                return key;
            }
        }
        fail("Unable to find Equality Key" + id );
        return null;
    }


}
