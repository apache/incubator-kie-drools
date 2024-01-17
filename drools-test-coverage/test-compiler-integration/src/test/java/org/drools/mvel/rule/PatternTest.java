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
package org.drools.mvel.rule;

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ObjectType;
import org.drools.base.prototype.PrototypeObjectType;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.core.test.model.Cheese;
import org.junit.Test;
import org.kie.api.prototype.PrototypeFact;
import org.kie.api.prototype.PrototypeFactInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.api.prototype.PrototypeBuilder.prototype;

public class PatternTest {

    @Test
    public void testDeclarationsObjectType() {
        final ObjectType type = new ClassObjectType( Cheese.class );
        final Pattern col = new Pattern( 0, type, "foo" );
        final Declaration dec = col.getDeclaration();
        final ReadAccessor ext = dec.getExtractor();
        assertThat(ext.getExtractToClass()).isEqualTo(Cheese.class);

        final Cheese stilton = new Cheese( "stilton", 42 );

        assertThat(dec.getValue(null, stilton)).isEqualTo(stilton);

    }

    @Test
    public void testDeclarationsPrototype() {
        PrototypeFact cheese = prototype("org.store.Cheese").withField("name", String.class).withField("price", Integer.class).asFact();

        final ObjectType type = new PrototypeObjectType(cheese );

        final Pattern col = new Pattern( 0, type, "foo" );
        final Declaration dec = col.getDeclaration();
        final ReadAccessor ext = dec.getExtractor();
        assertThat(ext.getExtractToClass()).isEqualTo(PrototypeFactInstance.class);

        PrototypeFactInstance stilton = cheese.newInstance();
        stilton.put("name", "stilton" );
        stilton.put("price", Integer.valueOf(200 ) );

        assertThat(dec.getValue(null, stilton)).isEqualTo(stilton);
    }

}
