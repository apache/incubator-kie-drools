package org.drools.reteoo;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.base.evaluators.StringFactory;
import org.drools.base.field.ObjectFieldImpl;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.PredicateConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.PropagationContext;

public class CompositeObjectSinkAdapterTest extends TestCase {

    public int la;
    public int blah;
    public String wah;
    



    public void testBeta() {
        CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        MockBetaNode beta = new MockBetaNode(0, null, null);
        ad.addObjectSink( beta );
        assertEquals(1, ad.getSinks().length);
        assertEquals(beta, ad.getSinks()[0]);
        
        assertEquals(1, ad.otherSinks.size());
        assertEquals(beta, ad.otherSinks.getFirst());
        
        assertNull(ad.hashableSinks);
        assertNull(ad.hashedFieldIndexes);
        assertNull(ad.hashedSinkMap);
        
        ad.removeObjectSink( beta );
        assertNull(ad.otherSinks);
        assertEquals(0, ad.getSinks().length);
    }
    
    
    public void testAlphaWithPredicate() {
        CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        AlphaNode al = new AlphaNode(0, new PredicateConstraint(null, (Declaration[]) null), null);
        ad.addObjectSink( al );
        
        assertEquals(1, ad.getSinks().length);      
        assertEquals(1, ad.otherSinks.size());
        assertEquals(al, ad.otherSinks.getFirst());
        
        ad.removeObjectSink( al );
        assertEquals(0, ad.getSinks().length);
        assertNull(ad.otherSinks);
        
    }
    
    public void testSingleAlpha() {
        
        CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        LiteralConstraint lit = new LiteralConstraint(new MockExtractor(),
                                                      StringFactory.getInstance().getEvaluator( Operator.EQUAL ),
                                                      new ObjectFieldImpl("stilton"));
        AlphaNode al = new AlphaNode(0, lit,  new MockObjectSource(0) );
        
        ad.addObjectSink( al );
        
        assertNull(ad.otherSinks);
        assertNotNull(ad.hashedFieldIndexes);
        assertEquals(1, ad.hashableSinks.size());
        assertEquals(al, ad.getSinks()[0]);
        
        
        ad.removeObjectSink( al );
        assertNull(ad.otherSinks);
        assertNull(ad.hashableSinks);
        
        
    }
    
    public void testDoubleAlphaWithBeta() {
        
        CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        LiteralConstraint lit = new LiteralConstraint(new MockExtractor(),
                                                      StringFactory.getInstance().getEvaluator( Operator.EQUAL ),
                                                      new ObjectFieldImpl("stilton"));
        AlphaNode al = new AlphaNode(0, lit,  new MockObjectSource(0) );
        
        ad.addObjectSink( al );
        
        assertNull(ad.otherSinks);
        assertNotNull(ad.hashedFieldIndexes);
        assertEquals(1, ad.hashableSinks.size());
        assertEquals(al, ad.getSinks()[0]);
        

        LiteralConstraint lit2 = new LiteralConstraint(new MockExtractor(),
                                                      StringFactory.getInstance().getEvaluator( Operator.EQUAL ),
                                                      new ObjectFieldImpl("cheddar"));
        AlphaNode al2 = new AlphaNode(1, lit2,  new MockObjectSource(0) );
        
        ad.addObjectSink( al2 );
        
        assertNull(ad.otherSinks);
        assertEquals(2, ad.hashableSinks.size());
        assertEquals(al, ad.getSinks()[0]);        
        assertEquals(al2, ad.getSinks()[1]);
        
        //add a beta, just for good measure, make sure it leaves others alone
        MockBetaNode beta = new MockBetaNode(0, null, null);
        ad.addObjectSink( beta );
        assertNotNull(ad.otherSinks);
        assertEquals(2, ad.hashableSinks.size());

        
        assertEquals(1, ad.otherSinks.size());
        assertEquals(beta, ad.otherSinks.getFirst());
        
        ad.removeObjectSink( beta );
        assertNull(ad.otherSinks);
        assertEquals(2, ad.hashableSinks.size());
        
    }    
    
    
    public void testTripleAlpha() {
        CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        LiteralConstraint lit = new LiteralConstraint(new MockExtractor(),
                                                      StringFactory.getInstance().getEvaluator( Operator.EQUAL ),
                                                      new ObjectFieldImpl("stilton"));
        AlphaNode al = new AlphaNode(0, lit,  new MockObjectSource(0) );
        
        ad.addObjectSink( al );
        
        assertNull(ad.otherSinks);
        assertNotNull(ad.hashedFieldIndexes);
        assertEquals(1, ad.hashableSinks.size());
        assertEquals(al, ad.getSinks()[0]);

        
        LiteralConstraint lit2 = new LiteralConstraint(new MockExtractor(),
                                                      StringFactory.getInstance().getEvaluator( Operator.EQUAL ),
                                                      new ObjectFieldImpl("cheddar"));
        AlphaNode al2 = new AlphaNode(1, lit2,  new MockObjectSource(1) );
        
        ad.addObjectSink( al2 );
        
        assertNull(ad.hashedSinkMap);
        assertEquals(2, ad.hashableSinks.size());
        
        LiteralConstraint lit3 = new LiteralConstraint(new MockExtractor(),
                                                       StringFactory.getInstance().getEvaluator( Operator.EQUAL ),
                                                       new ObjectFieldImpl("stinky"));
        AlphaNode al3 = new AlphaNode(1, lit3,  new MockObjectSource(2) );
        ad.addObjectSink( al3 );
        
        //this should now be nicely hashed.
        assertNotNull( ad.hashedSinkMap );        
        assertNull(ad.hashableSinks);
        
        
        
        //now remove one, check the hashing is undone
        ad.removeObjectSink( al2 );
        assertNotNull(ad.hashableSinks);
        assertEquals(2, ad.hashableSinks.size());
        assertNull(ad.hashedSinkMap);
        
        
    }
    
 
    
    
    
    static class MockExtractor implements FieldExtractor {

        public int getIndex() {
            //  Auto-generated method stub
            return 0;
        }

        public boolean getBooleanValue(Object object) {
            //  Auto-generated method stub
            return false;
        }

        public byte getByteValue(Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public char getCharValue(Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public double getDoubleValue(Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public Class getExtractToClass() {
            //  Auto-generated method stub
            return null;
        }

        public float getFloatValue(Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public int getIntValue(Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public long getLongValue(Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public Method getNativeReadMethod() {
            //  Auto-generated method stub
            return null;
        }

        public short getShortValue(Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public Object getValue(Object object) {
            //  Auto-generated method stub
            return null;
        }

        public ValueType getValueType() {
            //  Auto-generated method stub
            return null;
        }
        
    }
    
    static class MockBetaNode extends BetaNode {

        
        MockBetaNode(int id,
                     TupleSource leftInput,
                     ObjectSource rightInput) {
            super( id,
                   leftInput,
                   rightInput );
            //  Auto-generated constructor stub
        }

        public void updateSink(TupleSink sink,
                               PropagationContext context,
                               InternalWorkingMemory workingMemory) {
            //  Auto-generated method stub
            
        }

        public void assertTuple(ReteTuple tuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
            //  Auto-generated method stub
            
        }

        public void retractTuple(ReteTuple tuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
            //  Auto-generated method stub
            
        }

        public void assertObject(InternalFactHandle handle,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
            //  Auto-generated method stub
            
        }

        public void retractObject(InternalFactHandle handle,
                                  PropagationContext context,
                                  InternalWorkingMemory workingMemory) {
            //  Auto-generated method stub
            
        }
        
    }
}
