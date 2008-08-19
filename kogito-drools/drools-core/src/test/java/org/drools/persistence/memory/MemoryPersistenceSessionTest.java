package org.drools.persistence.memory;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldAccessorStore;
import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.base.evaluators.EqualityEvaluatorsDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.common.InternalWorkingMemory;
import org.drools.examples.manners.Context;
import org.drools.examples.manners.Count;
import org.drools.examples.manners.Guest;
import org.drools.examples.manners.Path;
import org.drools.examples.manners.Seating;
import org.drools.persistence.DroolsXid;
import org.drools.persistence.StatefulSessionSnapshotter;
import org.drools.persistence.Transaction;
import org.drools.persistence.memory.MemoryPersistenceManager;
import org.drools.persistence.memory.MemoryXaResource;
import org.drools.rule.Declaration;
import org.drools.rule.InvalidRuleException;
import org.drools.rule.Pattern;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.rule.VariableConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Consequence;
import org.drools.spi.ConsequenceException;
import org.drools.spi.Evaluator;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;
import org.drools.transaction.MockByteArraySnapshotter;

import junit.framework.TestCase;

public class MemoryPersistenceSessionTest extends TestCase {
    private byte[]          data1 = new byte[]{1, 1, 1, 1, 1};
    private byte[]          data2 = new byte[]{1, 1, 1, 1, 0};
    private byte[]          data3 = new byte[]{1, 1, 1, 0, 0};

    ClassFieldAccessorStore store;

    public void testSave() throws Exception {
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        Package pkg = new Package( "org.drools.test" );
        pkg.addGlobal( "list", List.class );
        pkg.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store = pkg.getClassFieldAccessorStore();
        store.setEagerWire( true );
        
        pkg.addRule( getFindPersonRule() );        
        ruleBase.addPackage( pkg );
        

        StatefulSession session = ruleBase.newStatefulSession();
        List list = new ArrayList();
        session.setGlobal( "list", list );
        Person p1 = new Person("boba fet", 500);
        session.insert( p1 );
        
        MemoryPersistenceManager pm = new MemoryPersistenceManager( new StatefulSessionSnapshotter( session ) );
        pm.save();
        
        
        Person p2 = new Person("boba fet", 500);
        Person p3 = new Person("boba fet", 500);
        session.insert( p2 );
        session.insert( p3 );        
        session.insert( new String( "boba fet" ) );
        assertEquals( 4, ((InternalWorkingMemory)session).getObjectStore().size() );
        session.fireAllRules();        
        assertEquals(3, list.size() );        
        
        pm.load();
        list.clear();
        session.insert( new String( "boba fet" ) );
        session.fireAllRules();        
        assertEquals( 1, list.size() );             
        assertEquals( 2, ((InternalWorkingMemory)session).getObjectStore().size() );
    }
    
    public void testTransactionWithRollback() throws Exception {
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        Package pkg = new Package( "org.drools.test" );
        pkg.addGlobal( "list", List.class );
        pkg.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store = pkg.getClassFieldAccessorStore();
        store.setEagerWire( true );
        
        pkg.addRule( getFindPersonRule() );        
        ruleBase.addPackage( pkg );
        

        StatefulSession session = ruleBase.newStatefulSession();
        List list = new ArrayList();
        session.setGlobal( "list", list );
        Person p1 = new Person("boba fet", 500);
        session.insert( p1 );
        
        MemoryPersistenceManager pm = new MemoryPersistenceManager( new StatefulSessionSnapshotter( session ) );
        Transaction t = pm.getTransaction();
        t.start();
        
        
        Person p2 = new Person("boba fet", 500);
        Person p3 = new Person("boba fet", 500);
        session.insert( p2 );
        session.insert( p3 );        
        session.insert( new String( "boba fet" ) );
        assertEquals( 4, ((InternalWorkingMemory)session).getObjectStore().size() );
        session.fireAllRules();        
        assertEquals(3, list.size() );        
        
        t.rollback();
        list.clear();
        session.insert( new String( "boba fet" ) );
        session.fireAllRules();        
        assertEquals( 1, list.size() );             
        assertEquals( 2, ((InternalWorkingMemory)session).getObjectStore().size() );
    }    

    private Rule getFindPersonRule() throws IntrospectionException,
                                    InvalidRuleException {
        ClassObjectType stringType = new ClassObjectType( String.class );
        ClassObjectType personType = new ClassObjectType( Person.class );

        final Rule rule = new Rule( "find person" );

        // -----------
        // $s : String( )
        // -----------
        final Pattern stringPattern = new Pattern( 0,
                                                   stringType,
                                                   "$s" );
        rule.addPattern( stringPattern );
        final Declaration sDeclaration = rule.getDeclaration( "$s" );

        // -----------
        // $p : Person( name == $s)
        // -----------
        final Pattern personPattern = new Pattern( 1,
                                                   personType,
                                                   "$p" );
        EqualityEvaluatorsDefinition evals = new EqualityEvaluatorsDefinition();
        Evaluator eval = evals.getEvaluator( ValueType.OBJECT_TYPE,
                                             Operator.EQUAL,
                                             null );
        personPattern.addConstraint( getBoundVariableConstraint( personPattern,
                                                                 "name",
                                                                 sDeclaration,
                                                                 eval ) );

        rule.addPattern( personPattern );

        final Declaration pDeclaration = rule.getDeclaration( "$p" );

        final Consequence consequence = new Consequence() {

            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Person p = (Person) drools.get( pDeclaration );

                    List list = (List) workingMemory.getGlobal( "list" );
                    list.add( p );

                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
        };

        rule.setConsequence( consequence );

        return rule;
    }

    public boolean assertEquals(byte[] bytes1,
                                byte[] bytes2) {
        if ( bytes1.length != bytes2.length ) {
            return false;
        }

        for ( int i = 0; i < bytes1.length; i++ ) {
            if ( bytes1[i] != bytes2[i] ) {
                return false;
            }
        }

        return true;
    }

    private BetaNodeFieldConstraint getBoundVariableConstraint(final Pattern pattern,
                                                               final String fieldName,
                                                               final Declaration declaration,
                                                               final Evaluator evaluator) throws IntrospectionException {
        final Class clazz = ((ClassObjectType) pattern.getObjectType()).getClassType();

        final InternalReadAccessor extractor = store.getReader( clazz,
                                                                fieldName,
                                                                getClass().getClassLoader() );

        return new VariableConstraint( extractor,
                                       declaration,
                                       evaluator );
    }
}
