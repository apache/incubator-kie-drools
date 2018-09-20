/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.verifier.core.index.select;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.MultiMap;
import org.drools.verifier.core.maps.MultiMapFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith( Parameterized.class )
public class SelectExactMatcherNegateTest {

    private int          amount;
    private Select<Item> select;
    private Object       firstValue;
    private Object       lastValue;

    private MultiMap<Value, Item, List<Item>> makeMap() {
        final MultiMap<Value, Item, List<Item>> itemKeyTreeMap = MultiMapFactory.make();

        itemKeyTreeMap.put( new Value( null ),
                            new Item( null ) );
        itemKeyTreeMap.put( new Value( 0 ),
                            new Item( 0 ) );
        itemKeyTreeMap.put( new Value( 56 ),
                            new Item( 56 ) );
        itemKeyTreeMap.put( new Value( 100 ),
                            new Item( 100 ) );
        itemKeyTreeMap.put( new Value( 1200 ),
                            new Item( 1200 ) );
        return itemKeyTreeMap;
    }

    public SelectExactMatcherNegateTest( final int amount,
                                         final Object firstValue,
                                         final Object lastValue,
                                         final Matcher matcher ) throws Exception {

        this.firstValue = firstValue;
        this.lastValue = lastValue;
        this.amount = amount;
        this.select = new Select<>( makeMap(),
                                    matcher );
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList( new Object[][]{
                {5, null, 1200, new ExactMatcher( KeyDefinition.newKeyDefinition().withId( "cost" ).build(),
                                                  13,
                                                  true )},
                {4, 0, 1200, new ExactMatcher( KeyDefinition.newKeyDefinition().withId( "cost" ).build(),
                                               null,
                                               true )},
        } );
    }


    @Test
    public void testAll() throws Exception {
        final Collection<Item> all = select.all();

        assertEquals( amount, all.size() );
    }

    @Test
    public void testFirst() throws Exception {
        assertEquals( firstValue,
                      select.first().cost );
    }

    @Test
    public void testLast() throws Exception {
        assertEquals( lastValue,
                      select.last().cost );
    }

    private class Item {

        private Integer cost;

        public Item( final Integer cost ) {
            this.cost = cost;
        }
    }
}