/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.common.DisconnectedWorkingMemoryEntryPoint;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.CompositeObjectSinkAdapter.HashKey;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ReteooFactHandleFactory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.PredicateConstraint;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.mvel.model.Cheese;
import org.drools.mvel.model.MockObjectSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class CompositeObjectSinkAdapterTest {
    private InternalKnowledgeBase        kBase;
    private BuildContext                 buildContext;
	private CompositeObjectSinkAdapter ad;
	private InternalReadAccessor extractor;

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    private final boolean useLambdaConstraint;

    public CompositeObjectSinkAdapterTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;
    }

    @Parameterized.Parameters(name = "useLambdaConstraint={0}")
    public static Collection<Object[]> getParameters() {
        Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{false});
        parameters.add(new Object[]{true});
        return parameters;
    }

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
        this.kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();

        this.buildContext = new BuildContext( kBase );
        this.buildContext.setRule(new RuleImpl("test"));
        this.ad = new CompositeObjectSinkAdapter();
    }


    @Test
    public void testAddBeta() {
        final MockBetaNode beta = createBetaNode();
        
        ad.addObjectSink( beta );
        
        sinksAre(beta);
        otherSinksAre(beta);
        hashableSinksAreEmpty();
        hashedFieldIndexesAreEmpty();
        hashedSinkMapIsEmpty();
    }
    
    @Test
    public void testAddBetaRemoveBeta() {
        final MockBetaNode beta = createBetaNode();
        ad.addObjectSink( beta );

        ad.removeObjectSink( beta );
        
        sinksAreEmpty();
        otherSinksAreEmpty();
    }


    @Test
    public void testAddOneAlphaNotHashable() {
        final AlphaNode al = createAlphaNode(new PredicateConstraint( null, null ));
        ad.addObjectSink( al );

        sinksAre(al);
        otherSinksAre(al);
    }
    
    @Test
    public void testAddOneAlphaNotHashableRemoveOneAlpha() {
        final AlphaNode al = createAlphaNode(new PredicateConstraint( null, null ));
        ad.addObjectSink( al );

        ad.removeObjectSink( al );
        
        sinksAreEmpty();
        otherSinksAreEmpty();
    }

    @Test
    public void testAddOneAlpha() {
        extractor = store.getReader( Cheese.class, "type" );
        final AlphaNode al = createAlphaNode(cheeseTypeEqualsTo("stilton"));

        ad.addObjectSink( al );

        sinksAre(al);
        otherSinksAreEmpty();
        hashedFieldIndexesAre(extractor.getIndex());
        hashableSinksAre(al);
    }
    
    @Test
    public void testAddOneAlphaRemoveOneAlpha() {
        extractor = store.getReader( Cheese.class, "type" );
        final AlphaNode al = createAlphaNode(cheeseTypeEqualsTo("stilton"));
        ad.addObjectSink( al );
 
        ad.removeObjectSink( al );
        
        otherSinksAreEmpty();
        hashableSinksAreEmpty();
    }
    
    @Test
    public void testAddTwoAlphas() {
        extractor = store.getReader( Cheese.class, "type" );
        final AlphaNode al = createAlphaNode(cheeseTypeEqualsTo("stilton"));
        ad.addObjectSink( al );
        final AlphaNode al2 = createAlphaNode(cheeseTypeEqualsTo("cheddar"));

        ad.addObjectSink( al2 );

        sinksAre(al, al2);
        otherSinksAreEmpty();
        hashableSinksAre(al, al2);
    }
    
    @Test
    public void testAddTwoAlphasAddOneBeta() {
        extractor = store.getReader( Cheese.class, "type" );
        final AlphaNode al = createAlphaNode(cheeseTypeEqualsTo("stilton"));
        ad.addObjectSink( al );
        final AlphaNode al2 = createAlphaNode(cheeseTypeEqualsTo("cheddar"));
        ad.addObjectSink( al2 );
        final BetaNode beta = createBetaNode();
        
        ad.addObjectSink( beta );
        
        otherSinksAre(beta);
        hashableSinksAre(al, al2);
    }
    
    @Test
    public void testAddTwoAlphasAddOneBetaRemoveOneBeta() {
        extractor = store.getReader( Cheese.class, "type" );
        final AlphaNode al = createAlphaNode(cheeseTypeEqualsTo("stilton"));
        ad.addObjectSink( al );
        final AlphaNode al2 = createAlphaNode(cheeseTypeEqualsTo("cheddar"));
        ad.addObjectSink( al2 );
        final BetaNode beta = createBetaNode();
        ad.addObjectSink( beta );

        ad.removeObjectSink( beta );
        
        otherSinksAreEmpty();
        hashableSinksAre(al, al2);
    }

    @Test
    public void testAddThreeAlphas() {
        extractor = store.getReader( Cheese.class, "type" );
        final AlphaNode al = createAlphaNode(cheeseTypeEqualsTo("stilton"));
        ad.addObjectSink( al );
        final AlphaNode al2 = createAlphaNode(cheeseTypeEqualsTo("cheddar"));
        ad.addObjectSink( al2 );
        final AlphaNode al3 = createAlphaNode(cheeseTypeEqualsTo("stinky"));

        ad.addObjectSink( al3 );

        //this should now be nicely hashed.
        hashableSinksAreEmpty();
        hashedSinkMapIs(al, al2, al3);
    }
    
    @Test
    public void testAddThreeAlphasRemoveOneAlpha() {
        extractor = store.getReader( Cheese.class, "type" );
        final AlphaNode al = createAlphaNode(cheeseTypeEqualsTo("stilton"));
        ad.addObjectSink( al );
        final AlphaNode al2 = createAlphaNode(cheeseTypeEqualsTo("cheddar"));
        ad.addObjectSink( al2 );
        final AlphaNode al3 = createAlphaNode(cheeseTypeEqualsTo("stinky"));
        ad.addObjectSink( al3 );

        ad.removeObjectSink( al2 );
        
        hashableSinksAre(al, al3);
        hashedSinkMapIsEmpty();
    }
    

    @Test
    public void testTripleAlphaCharacterConstraint() {
        extractor = store.getReader( Cheese.class, "charType" );
        final AlphaNode al = createAlphaNode(cheeseCharTypeEqualsTo(65));
        ad.addObjectSink( al );
        final AlphaNode al2 = createAlphaNode(cheeseCharTypeEqualsTo(66));
        ad.addObjectSink( al2 );
        final AlphaNode al3 = createAlphaNode(cheeseCharTypeEqualsTo(67));
        ad.addObjectSink( al3 );

        assertSame( al2, ad.getHashedSinkMap().get(keyForCheeseCharType('B')));
        assertNull( ad.getHashedSinkMap().get(keyForCheeseCharType('X')));
    }

 
    @Test
    public void testTripleAlphaObjectCharacterConstraint() {
    	extractor = store.getReader( Cheese.class, "charObjectType" );
        final AlphaNode al = createAlphaNode(cheeseCharObjectTypeEqualsTo(65));
        ad.addObjectSink( al );
        final AlphaNode al2 = createAlphaNode(cheeseCharObjectTypeEqualsTo(66));
        ad.addObjectSink( al2 );
        final AlphaNode al3 = createAlphaNode(cheeseCharObjectTypeEqualsTo(67));
        ad.addObjectSink( al3 );

        assertSame( al2, ad.getHashedSinkMap().get(keyForCheeseCharObjectType('B')));
        assertNull( ad.getHashedSinkMap().get(keyForCheeseCharObjectType('X')));
    }

    @Test
    public void testAddOneAlphaForRanges() {
        extractor = store.getReader( Cheese.class, "price" );
        final AlphaNode al1 = createAlphaNode(cheesePriceGreaterThan(10));

        ad.addObjectSink( al1 );

        sinksAre(al1);
        otherSinksAreEmpty();
        assertNotNull( ad.getRangeIndexedFieldIndexes() );
        rangeIndexableSinksAre(al1);
    }
 
    
    @Test
    public void testTwoAlphasForRanges() {
        extractor = store.getReader( Cheese.class, "price" );
        final AlphaNode al1 = createAlphaNode(cheesePriceGreaterThan(10));
        ad.addObjectSink( al1 );
        final AlphaNode al2 = createAlphaNode(cheesePriceGreaterThan(20));

        ad.addObjectSink( al2 );

        assertNull( ad.getRangeIndexMap() );
        rangeIndexableSinksAre(al1, al2);
    }


    @Test
    public void testThreeAlphasForRanges() {
        extractor = store.getReader( Cheese.class, "price" );
        final AlphaNode al1 = createAlphaNode(cheesePriceGreaterThan(10));
        ad.addObjectSink( al1 );
        final AlphaNode al2 = createAlphaNode(cheesePriceGreaterThan(20));
        ad.addObjectSink( al2 );
        final AlphaNode al3 = createAlphaNode(cheesePriceGreaterThan(30));

        ad.addObjectSink( al3 );

        //this should now be nicely indexed.
        assertNotNull( ad.getRangeIndexMap() );
        rangeIndexableSinksIsEmpty();
    }    
    
    
    @Test
    public void testAddThreeAlphasRemoveOneAlphaForRanges() {
        extractor = store.getReader( Cheese.class, "price" );
        final AlphaNode al1 = createAlphaNode(cheesePriceGreaterThan(10));
        ad.addObjectSink( al1 );
        final AlphaNode al2 = createAlphaNode(cheesePriceGreaterThan(20));
        ad.addObjectSink( al2 );
        final AlphaNode al3 = createAlphaNode(cheesePriceGreaterThan(30));
        ad.addObjectSink( al3 );

        ad.removeObjectSink( al2 );
        
        rangeIndexableSinksAre(al1, al3);
        assertNull( ad.getRangeIndexMap() );
    }
    
    
    @Test
    public void testAddThreeAlphasVerifyRangeQuery() {
        extractor = store.getReader( Cheese.class, "price" );
        final AlphaNode al1 = createAlphaNode(cheesePriceGreaterThan(10));
        ad.addObjectSink( al1 );
        final AlphaNode al2 = createAlphaNode(cheesePriceGreaterThan(20));
        ad.addObjectSink( al2 );
        final AlphaNode al3 = createAlphaNode(cheesePriceGreaterThan(30));
        ad.addObjectSink( al3 );

        // test propagation
        CompositeObjectSinkAdapter.FieldIndex fieldIndex = ad.getRangeIndexedFieldIndexes().get(0);

        Cheese cheese = new Cheese();
        cheese.setPrice(25);

        Collection<AlphaNode> matchingAlphaNodes = ad.getRangeIndexMap().get(fieldIndex).getMatchingAlphaNodes(cheese);
        assertEquals(2, matchingAlphaNodes.size());
        assertTrue(matchingAlphaNodes.contains(al1));
        assertTrue(matchingAlphaNodes.contains(al2));

        // should not find this one
        cheese.setPrice(5);

        matchingAlphaNodes = ad.getRangeIndexMap().get(fieldIndex).getMatchingAlphaNodes(cheese);
        assertTrue(matchingAlphaNodes.isEmpty());
    }

    

    @Test(expected = IllegalStateException.class)
    public void testRangeIndexConflictKey() {
        extractor = store.getReader( Cheese.class, "price" );
        final AlphaNode al1 = createAlphaNode(cheesePriceGreaterThan(10));
        ad.addObjectSink( al1 );
        final AlphaNode al2 = createAlphaNode(cheesePriceGreaterThan(20));
        ad.addObjectSink( al2 );
        final AlphaNode al3 = createAlphaNode(cheesePriceGreaterThan(30));
        ad.addObjectSink( al3 );
        final AlphaNode al4 = createAlphaNode(cheesePriceGreaterThan(30));

        ad.addObjectSink( al4 ); // throws IllegalStateException
    }

    @Test
    public void testPropagationWithNullValue() {
        extractor = store.getReader( Cheese.class, "type" );

        final AlphaNode al1 = createAlphaNode(cheeseTypeEqualsTo("stilton"));
        final AlphaNode al2 = createAlphaNode(cheeseTypeEqualsTo("brie"));
        final AlphaNode al3 = createAlphaNode(cheeseTypeEqualsTo("muzzarela"));

        ad.addObjectSink( al1 );
        ad.addObjectSink( al2 );
        ad.addObjectSink( al3 );

        InternalFactHandle handle = new ReteooFactHandleFactory().newFactHandle( new Cheese(),
                                                                                 null,
                                                                                 null,
                                                                                 new DisconnectedWorkingMemoryEntryPoint( "DEFAULT" ) );
        try {
            ad.propagateAssertObject( handle,
                                      null,
                                      null );
        } catch ( RuntimeException e ) {
            fail( "Not supposed to throw any exception: " + e.getMessage() );
        }

    }

	private AlphaNodeFieldConstraint cheeseTypeEqualsTo(String value) {
		return ConstraintTestUtil.createCheeseTypeEqualsConstraint(extractor, value, useLambdaConstraint);
	}
    
	private AlphaNodeFieldConstraint cheeseCharTypeEqualsTo(int value) {
		return ConstraintTestUtil.createCheeseCharTypeEqualsConstraint(extractor, value, useLambdaConstraint);
	}
	
	private AlphaNodeFieldConstraint cheeseCharObjectTypeEqualsTo(int value) {
		return ConstraintTestUtil.createCheeseCharObjectTypeEqualsConstraint(extractor, value, useLambdaConstraint);
	}
	
	private AlphaNodeFieldConstraint cheesePriceGreaterThan(int value) {
		return ConstraintTestUtil.createCheesePriceGreaterConstraint(extractor, value, useLambdaConstraint);
	}

	private AlphaNode createAlphaNode(AlphaNodeFieldConstraint lit) {
		return new AlphaNode( buildContext.getNextId(),
                                            lit,
                                            new MockObjectSource( buildContext.getNextId() ),
                                            buildContext );
	}
   

	private MockBetaNode createBetaNode() {
		return new MockBetaNode( buildContext.getNextId(),
                                                    new MockBetaNode( ),
                                                    new MockObjectSource(),
                                                    buildContext );
	}
	

	private HashKey keyForCheeseCharType(char c) {
        Cheese cheese = new Cheese();
        cheese.setCharType( c );
		HashKey hashKey = new CompositeObjectSinkAdapter.HashKey();
		hashKey.setValue( extractor.getIndex(),
                cheese,
                extractor );
		return hashKey;
	}
	
	private HashKey keyForCheeseCharObjectType(char c) {
        Cheese cheese = new Cheese();
        cheese.setCharObjectType( c );
		HashKey hashKey = new CompositeObjectSinkAdapter.HashKey();
		hashKey.setValue( extractor.getIndex(),
                cheese,
                extractor );
		return hashKey;
	}
	
	private void sinksAre(ObjectSink... objectSinks) {
		assertEquals(objectSinks.length, ad.getSinks().length);
		for (int i = 0; i < objectSinks.length; i++) {
			assertEquals(objectSinks[i], ad.getSinks()[i]);
		}
	}
	
	private void sinksAreEmpty() {
		assertEquals(0, ad.getSinks().length);
	}
	
	private void otherSinksAre(ObjectSink... objectSinks) {
		assertEquals(objectSinks.length, ad.getOtherSinks().size());
		for (int i = 0; i < objectSinks.length; i++) {
			assertEquals(objectSinks[i], ad.getOtherSinks().get(i));
		}
	}

	private void otherSinksAreEmpty() {
		assertNull(ad.getOtherSinks());
	}
	
	private void hashableSinksAre(ObjectSink... objectSinks) {
		assertEquals(objectSinks.length, ad.getHashableSinks().size());
		for (int i = 0; i < objectSinks.length; i++) {
			assertTrue(ad.getHashableSinks().contains(objectSinks[i]));
		}
	}
	
	private void rangeIndexableSinksAre(ObjectSink... rangeIndexableSinks) {
		assertEquals(rangeIndexableSinks.length, ad.getRangeIndexableSinks().size());
		for (int i = 0; i < rangeIndexableSinks.length; i++) {
			assertTrue(ad.getRangeIndexableSinks().contains(rangeIndexableSinks[i]));
		}
	}
	
	private void rangeIndexableSinksIsEmpty(ObjectSink... rangeIndexableSinks) {
		assertNull(ad.getRangeIndexableSinks());
		
	}

	private void hashedFieldIndexesAre(Integer... fieldIndexes) {
		assertEquals(fieldIndexes.length, ad.getHashedFieldIndexes().size());
		List<Integer> hashedFieldIndexes = Arrays.asList(fieldIndexes);
		for (int i = 0; i < fieldIndexes.length; i++) {
			assertTrue(hashedFieldIndexes.contains(ad.getHashedFieldIndexes().get(i).getIndex()));
		}
	}
	
	private void hashedFieldIndexesAreEmpty() {
		assertNull(ad.getHashedFieldIndexes());
	}
	

	private void hashableSinksAreEmpty() {
		assertNull(ad.getHashableSinks());
	}
	
	private void hashedSinkMapIs(AlphaNode... nodes) {
		assertEquals(nodes.length, ad.getHashedSinkMap().size());
		for (int i = 0; i < nodes.length; i++) {
			assertTrue(ad.getHashedSinkMap().containsValue(nodes[i]));
		}
	}
	
	private void hashedSinkMapIsEmpty() {
		assertNull(ad.getHashedSinkMap());
	}
}
