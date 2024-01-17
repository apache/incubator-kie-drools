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
package org.drools.model.prototype;

import org.drools.base.prototype.PrototypeFieldExtractor;
import org.drools.base.prototype.PrototypeObjectType;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.accessor.ReadAccessor;
import org.junit.Test;
import org.kie.api.prototype.PrototypeFact;
import org.kie.api.prototype.PrototypeFactInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.api.prototype.PrototypeBuilder.prototype;

public class PrototypeFieldExtractorTest {
    @Test
    public void testExtractor() {
        PrototypeFact cheesePrototype = prototype("org.sore.Cheese").withField("name", String.class).withField("price", Integer.class).asFact();

        final ReadAccessor extractName = new PrototypeFieldExtractor(cheesePrototype, "name" );
        final ReadAccessor extractPrice = new PrototypeFieldExtractor(cheesePrototype, "price" );

        PrototypeFactInstance stilton = cheesePrototype.newInstance();
        stilton.put("name", "stilton" );
        stilton.put("price", 200 );

        assertThat(extractName.getValue(null, stilton)).isEqualTo("stilton");

        assertThat(extractPrice.getValue(null, stilton)).isEqualTo(200);

        assertThat(extractName.isNullValue(null, stilton)).isFalse();
        
        stilton.put("name", null );

        assertThat(extractName.isNullValue(null, stilton)).isTrue();
        assertThat(extractPrice.isNullValue(null, stilton)).isFalse();

        PrototypeFactInstance brie = cheesePrototype.newInstance();
        brie.put("name", "brie" );
        brie.put("price", 55 );

        assertThat(extractName.getValue(null, brie)).isEqualTo("brie");

        assertThat(extractPrice.getValue(null, brie)).isEqualTo(55);

        assertThat(extractName.isNullValue(null, brie)).isFalse();
        
        brie.put("name", null );

        assertThat(extractName.isNullValue(null, brie)).isTrue();
        assertThat(extractPrice.isNullValue(null, stilton)).isFalse();
    }

    @Test
    public void testDeclaration() {
        PrototypeFact cheesePrototype = prototype("org.sore.Cheese").withField("name", String.class).withField("price", Integer.class).asFact();
        ReadAccessor extractName = new PrototypeFieldExtractor(cheesePrototype, "name" );
        Pattern pattern = new Pattern( 0, new PrototypeObjectType(cheesePrototype) );
        Declaration declaration = new Declaration( "typeOfCheese", extractName, pattern );

        PrototypeFactInstance brie = cheesePrototype.newInstance();
        brie.put("name", "brie" );
        brie.put("price", 55 );

        // Check we can extract Declarations correctly 
        assertThat(declaration.getValue(null, brie)).isEqualTo("brie");
    }
}
