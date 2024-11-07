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
package org.drools.mvel;

import org.drools.base.base.ValueResolver;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.base.rule.constraint.Constraint;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.CompositeObjectSinkAdapter.HashKey;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ReteooFactHandleFactory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.kiesession.entrypoints.DisconnectedWorkingMemoryEntryPoint;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.drools.mvel.model.Cheese;
import org.drools.mvel.model.MockObjectSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.rule.FactHandle;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;

public class CompositeObjectSinkAdapterTest {
    private InternalKnowledgeBase kBase;
    private BuildContext                 buildContext;
	private CompositeObjectSinkAdapter ad;
	private ReadAccessor extractor;

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    public static Stream<Boolean> parameters() {
    	return Stream.of(false, true);
    }

    @BeforeEach
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
        this.kBase = KnowledgeBaseFactory.newKnowledgeBase();

        this.buildContext = new BuildContext( kBase, Collections.emptyList() );
        this.buildContext.setRule(new RuleImpl("test"));
        this.ad = new CompositeObjectSinkAdapter();
    }


    public void testAddBeta() {
        final MockBetaNode beta = createBetaNode();
        
        ad.addObjectSink( beta );
        
        sinksAre(beta);
        otherSinksAre(beta);
        hashableSinksAreEmpty();
        hashedFieldIndexesAreEmpty();
        hashedSinkMapIsEmpty();
    }
    
    public void testAddBetaRemoveBeta() {
        final MockBetaNode beta = createBetaNode();
        ad.addObjectSink( beta );

        ad.removeObjectSink( beta );
        
        sinksAreEmpty();
        otherSinksAreEmpty();
    }


    public void testAddOneAlphaNotHashable() {
        final AlphaNode al = createAlphaNode(new AlphaNodeFieldConstraintMock());
        ad.addObjectSink( al );

        sinksAre(al);
        otherSinksAre(al);
    }
    
    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testAddOneAlphaNotHashableRemoveOneAlpha() {
        final AlphaNode al = createAlphaNode(new AlphaNodeFieldConstraintMock());
        ad.addObjectSink( al );

        ad.removeObjectSink( al );
        
        sinksAreEmpty();
        otherSinksAreEmpty();
    }

    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testAddOneAlpha(boolean useLambdaConstraint) {
        extractor = store.getReader( Cheese.class, "type" );
        final AlphaNode al = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "stilton"));

        ad.addObjectSink( al );

        sinksAre(al);
        otherSinksAreEmpty();
        hashedFieldIndexesAre(extractor.getIndex());
        hashableSinksAre(al);
    }
    
    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testAddOneAlphaRemoveOneAlpha(boolean useLambdaConstraint) {
        extractor = store.getReader( Cheese.class, "type" );
        final AlphaNode al = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "stilton"));
        ad.addObjectSink( al );
 
        ad.removeObjectSink( al );
        
        otherSinksAreEmpty();
        hashableSinksAreEmpty();
    }
    
    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testAddTwoAlphas(boolean useLambdaConstraint) {
        extractor = store.getReader( Cheese.class, "type" );
        final AlphaNode al = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "stilton"));
        ad.addObjectSink( al );
        final AlphaNode al2 = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "cheddar"));

        ad.addObjectSink( al2 );

        sinksAre(al, al2);
        otherSinksAreEmpty();
        hashableSinksAre(al, al2);
    }
    
    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testAddTwoAlphasAddOneBeta(boolean useLambdaConstraint) {
        extractor = store.getReader( Cheese.class, "type" );
        final AlphaNode al = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "stilton"));
        ad.addObjectSink( al );
        final AlphaNode al2 = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "cheddar"));
        ad.addObjectSink( al2 );
        final BetaNode beta = createBetaNode();
        
        ad.addObjectSink( beta );
        
        otherSinksAre(beta);
        hashableSinksAre(al, al2);
    }
    
    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testAddTwoAlphasAddOneBetaRemoveOneBeta(boolean useLambdaConstraint) {
        extractor = store.getReader( Cheese.class, "type" );
        final AlphaNode al = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "stilton"));
        ad.addObjectSink( al );
        final AlphaNode al2 = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "cheddar"));
        ad.addObjectSink( al2 );
        final BetaNode beta = createBetaNode();
        ad.addObjectSink( beta );

        ad.removeObjectSink( beta );
        
        otherSinksAreEmpty();
        hashableSinksAre(al, al2);
    }

    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testAddThreeAlphas(boolean useLambdaConstraint) {
        extractor = store.getReader( Cheese.class, "type" );
        final AlphaNode al = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "stilton"));
        ad.addObjectSink( al );
        final AlphaNode al2 = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "cheddar"));
        ad.addObjectSink( al2 );
        final AlphaNode al3 = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "stinky"));

        ad.addObjectSink( al3 );

        //this should now be nicely hashed.
        hashableSinksAreEmpty();
        hashedSinkMapIs(al, al2, al3);
    }
    
    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testAddThreeAlphasRemoveOneAlpha(boolean useLambdaConstraint) {
        extractor = store.getReader( Cheese.class, "type" );
        final AlphaNode al = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "stilton"));
        ad.addObjectSink( al );
        final AlphaNode al2 = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "cheddar"));
        ad.addObjectSink( al2 );
        final AlphaNode al3 = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "stinky"));
        ad.addObjectSink( al3 );

        ad.removeObjectSink( al2 );
        
        hashableSinksAre(al, al3);
        hashedSinkMapIsEmpty();
    }
    

    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testTripleAlphaCharacterConstraint(boolean useLambdaConstraint) {
        extractor = store.getReader( Cheese.class, "charType" );
        final AlphaNode al = createAlphaNode(cheeseCharTypeEqualsTo(useLambdaConstraint, 65));
        ad.addObjectSink( al );
        final AlphaNode al2 = createAlphaNode(cheeseCharTypeEqualsTo(useLambdaConstraint, 66));
        ad.addObjectSink( al2 );
        final AlphaNode al3 = createAlphaNode(cheeseCharTypeEqualsTo(useLambdaConstraint, 67));
        ad.addObjectSink( al3 );

        assertThat(ad.getHashedSinkMap().get(keyForCheeseCharType('B'))).isSameAs(al2);
        assertThat(ad.getHashedSinkMap().get(keyForCheeseCharType('X'))).isNull();
    }

 
    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testTripleAlphaObjectCharacterConstraint(boolean useLambdaConstraint) {
    	extractor = store.getReader( Cheese.class, "charObjectType" );
        final AlphaNode al = createAlphaNode(cheeseCharObjectTypeEqualsTo(useLambdaConstraint, 65));
        ad.addObjectSink( al );
        final AlphaNode al2 = createAlphaNode(cheeseCharObjectTypeEqualsTo(useLambdaConstraint, 66));
        ad.addObjectSink( al2 );
        final AlphaNode al3 = createAlphaNode(cheeseCharObjectTypeEqualsTo(useLambdaConstraint, 67));
        ad.addObjectSink( al3 );

        assertThat(ad.getHashedSinkMap().get(keyForCheeseCharObjectType('B'))).isSameAs(al2);
        assertThat(ad.getHashedSinkMap().get(keyForCheeseCharObjectType('X'))).isNull();
    }

    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testAddOneAlphaForRanges(boolean useLambdaConstraint) {
        extractor = store.getReader( Cheese.class, "price" );
        final AlphaNode al1 = createAlphaNode(cheesePriceGreaterThan(useLambdaConstraint, 10));

        ad.addObjectSink( al1 );

        sinksAre(al1);
        otherSinksAreEmpty();
        assertThat(ad.getRangeIndexedFieldIndexes()).isNotNull();
        rangeIndexableSinksAre(al1);
    }
 
    
    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testTwoAlphasForRanges(boolean useLambdaConstraint) {
        extractor = store.getReader( Cheese.class, "price" );
        final AlphaNode al1 = createAlphaNode(cheesePriceGreaterThan(useLambdaConstraint, 10));
        ad.addObjectSink( al1 );
        final AlphaNode al2 = createAlphaNode(cheesePriceGreaterThan(useLambdaConstraint, 20));

        ad.addObjectSink( al2 );

        assertThat(ad.getRangeIndexMap()).isNull();
        rangeIndexableSinksAre(al1, al2);
    }


    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testThreeAlphasForRanges(boolean useLambdaConstraint) {
        extractor = store.getReader( Cheese.class, "price" );
        final AlphaNode al1 = createAlphaNode(cheesePriceGreaterThan(useLambdaConstraint, 10));
        ad.addObjectSink( al1 );
        final AlphaNode al2 = createAlphaNode(cheesePriceGreaterThan(useLambdaConstraint, 20));
        ad.addObjectSink( al2 );
        final AlphaNode al3 = createAlphaNode(cheesePriceGreaterThan(useLambdaConstraint, 30));

        ad.addObjectSink( al3 );

        //this should now be nicely indexed.
        assertThat(ad.getRangeIndexMap()).isNotNull();
        rangeIndexableSinksIsEmpty();
    }    
    
    
    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testAddThreeAlphasRemoveOneAlphaForRanges(boolean useLambdaConstraint) {
        extractor = store.getReader( Cheese.class, "price" );
        final AlphaNode al1 = createAlphaNode(cheesePriceGreaterThan(useLambdaConstraint, 10));
        ad.addObjectSink( al1 );
        final AlphaNode al2 = createAlphaNode(cheesePriceGreaterThan(useLambdaConstraint, 20));
        ad.addObjectSink( al2 );
        final AlphaNode al3 = createAlphaNode(cheesePriceGreaterThan(useLambdaConstraint, 30));
        ad.addObjectSink( al3 );

        ad.removeObjectSink( al2 );
        
        rangeIndexableSinksAre(al1, al3);
        assertThat(ad.getRangeIndexMap()).isNull();
    }
    
    
    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testAddThreeAlphasVerifyRangeQuery(boolean useLambdaConstraint) {
        extractor = store.getReader( Cheese.class, "price" );
        final AlphaNode al1 = createAlphaNode(cheesePriceGreaterThan(useLambdaConstraint, 10));
        ad.addObjectSink( al1 );
        final AlphaNode al2 = createAlphaNode(cheesePriceGreaterThan(useLambdaConstraint, 20));
        ad.addObjectSink( al2 );
        final AlphaNode al3 = createAlphaNode(cheesePriceGreaterThan(useLambdaConstraint, 30));
        ad.addObjectSink( al3 );

        // test propagation
        CompositeObjectSinkAdapter.FieldIndex fieldIndex = ad.getRangeIndexedFieldIndexes().get(0);

        Cheese cheese = new Cheese();
        cheese.setPrice(25);

        Collection<AlphaNode> matchingAlphaNodes = ad.getRangeIndexMap().get(fieldIndex).getMatchingAlphaNodes(cheese);
        assertThat(matchingAlphaNodes.size()).isEqualTo(2);
        assertThat(matchingAlphaNodes.contains(al1)).isTrue();
        assertThat(matchingAlphaNodes.contains(al2)).isTrue();

        // should not find this one
        cheese.setPrice(5);

        matchingAlphaNodes = ad.getRangeIndexMap().get(fieldIndex).getMatchingAlphaNodes(cheese);
        assertThat(matchingAlphaNodes.isEmpty()).isTrue();
    }

    

    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testRangeIndexConflictKey(boolean useLambdaConstraint) {
        extractor = store.getReader( Cheese.class, "price" );
        final AlphaNode al1 = createAlphaNode(cheesePriceGreaterThan(useLambdaConstraint, 10));
        ad.addObjectSink( al1 );
        final AlphaNode al2 = createAlphaNode(cheesePriceGreaterThan(useLambdaConstraint, 20));
        ad.addObjectSink( al2 );
        final AlphaNode al3 = createAlphaNode(cheesePriceGreaterThan(useLambdaConstraint, 30));
        ad.addObjectSink( al3 );
        final AlphaNode al4 = createAlphaNode(cheesePriceGreaterThan(useLambdaConstraint, 30));

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> ad.addObjectSink( al4 )); // throws IllegalStateException
    }

    @ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testPropagationWithNullValue(boolean useLambdaConstraint) {
        extractor = store.getReader( Cheese.class, "type" );

        final AlphaNode al1 = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "stilton"));
        final AlphaNode al2 = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "brie"));
        final AlphaNode al3 = createAlphaNode(cheeseTypeEqualsTo(useLambdaConstraint, "muzzarela"));

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

	private AlphaNodeFieldConstraint cheeseTypeEqualsTo(boolean useLambdaConstraint, String value) {
		return ConstraintTestUtil.createCheeseTypeEqualsConstraint(extractor, value, useLambdaConstraint);
	}
    
	private AlphaNodeFieldConstraint cheeseCharTypeEqualsTo(boolean useLambdaConstraint, int value) {
		return ConstraintTestUtil.createCheeseCharTypeEqualsConstraint(extractor, value, useLambdaConstraint);
	}
	
	private AlphaNodeFieldConstraint cheeseCharObjectTypeEqualsTo(boolean useLambdaConstraint, int value) {
		return ConstraintTestUtil.createCheeseCharObjectTypeEqualsConstraint(extractor, value, useLambdaConstraint);
	}
	
	private AlphaNodeFieldConstraint cheesePriceGreaterThan(boolean useLambdaConstraint, int value) {
		return ConstraintTestUtil.createCheesePriceGreaterConstraint(extractor, value, useLambdaConstraint);
	}

	private AlphaNode createAlphaNode(AlphaNodeFieldConstraint lit) {
		return new AlphaNode( buildContext.getNextNodeId(),
                                            lit,
                                            new MockObjectSource( buildContext.getNextNodeId() ),
                                            buildContext );
	}

    private static class AlphaNodeFieldConstraintMock implements AlphaNodeFieldConstraint {

        @Override
        public boolean isAllowed(FactHandle handle, ValueResolver valueResolver) {
            return false;
        }

        @Override
        public AlphaNodeFieldConstraint cloneIfInUse() {
            return this;
        }

        @Override
        public Declaration[] getRequiredDeclarations() {
            return new Declaration[0];
        }

        @Override
        public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {

        }

        @Override
        public Constraint clone() {
            return this;
        }

        @Override
        public ConstraintType getType() {
            return null;
        }

        @Override
        public boolean isTemporal() {
            return false;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {

        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        }
    }

	private MockBetaNode createBetaNode() {
		return new MockBetaNode( buildContext.getNextNodeId(),
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
        assertThat(ad.getSinks().length).isEqualTo(objectSinks.length);
		for (int i = 0; i < objectSinks.length; i++) {
            assertThat(ad.getSinks()[i]).isEqualTo(objectSinks[i]);
		}
	}
	
	private void sinksAreEmpty() {
        assertThat(ad.getSinks().length).isEqualTo(0);
	}
	
	private void otherSinksAre(ObjectSink... objectSinks) {
        assertThat(ad.getOtherSinks().size()).isEqualTo(objectSinks.length);
		for (int i = 0; i < objectSinks.length; i++) {
            assertThat(ad.getOtherSinks().get(i)).isEqualTo(objectSinks[i]);
		}
	}

	private void otherSinksAreEmpty() {
        assertThat(ad.getOtherSinks()).isNull();
	}
	
	private void hashableSinksAre(ObjectSink... objectSinks) {
        assertThat(ad.getHashableSinks().size()).isEqualTo(objectSinks.length);
		for (int i = 0; i < objectSinks.length; i++) {
            assertThat(ad.getHashableSinks().contains(objectSinks[i])).isTrue();
		}
	}
	
	private void rangeIndexableSinksAre(ObjectSink... rangeIndexableSinks) {
        assertThat(ad.getRangeIndexableSinks().size()).isEqualTo(rangeIndexableSinks.length);
		for (int i = 0; i < rangeIndexableSinks.length; i++) {
            assertThat(ad.getRangeIndexableSinks().contains(rangeIndexableSinks[i])).isTrue();
		}
	}
	
	private void rangeIndexableSinksIsEmpty(ObjectSink... rangeIndexableSinks) {
        assertThat(ad.getRangeIndexableSinks()).isNull();
		
	}

	private void hashedFieldIndexesAre(Integer... fieldIndexes) {
        assertThat(ad.getHashedFieldIndexes().size()).isEqualTo(fieldIndexes.length);
		List<Integer> hashedFieldIndexes = Arrays.asList(fieldIndexes);
		for (int i = 0; i < fieldIndexes.length; i++) {
            assertThat(hashedFieldIndexes.contains(ad.getHashedFieldIndexes().get(i).getIndex())).isTrue();
		}
	}
	
	private void hashedFieldIndexesAreEmpty() {
        assertThat(ad.getHashedFieldIndexes()).isNull();
	}
	

	private void hashableSinksAreEmpty() {
        assertThat(ad.getHashableSinks()).isNull();
	}
	
	private void hashedSinkMapIs(AlphaNode... nodes) {
        assertThat(ad.getHashedSinkMap().size()).isEqualTo(nodes.length);
		for (int i = 0; i < nodes.length; i++) {
            assertThat(ad.getHashedSinkMap().containsValue(nodes[i])).isTrue();
		}
	}
	
	private void hashedSinkMapIsEmpty() {
        assertThat(ad.getHashedSinkMap()).isNull();
	}
}
