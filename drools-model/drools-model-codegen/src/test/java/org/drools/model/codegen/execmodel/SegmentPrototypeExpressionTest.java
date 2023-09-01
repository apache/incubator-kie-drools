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
package org.drools.model.codegen.execmodel;

import org.drools.model.Prototype;
import org.drools.model.PrototypeExpression;
import org.drools.model.PrototypeFact;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.PrototypeDSL.prototype;
import static org.drools.model.PrototypeExpression.fixedValue;
import static org.drools.model.PrototypeExpression.prototypeField;
import static org.drools.modelcompiler.facttemplate.FactFactory.createMapBasedFact;

public class SegmentPrototypeExpressionTest {

    @Test
    public void testExpression() {
        PrototypeExpression expr1 = prototypeField("fieldA");
        PrototypeExpression expr2 = prototypeField("fieldB").add(prototypeField("fieldC")).sub(fixedValue(1));

        Prototype prototype = prototype("test");
        PrototypeFact testFact = (PrototypeFact) createMapBasedFact(prototype);
        testFact.set( "fieldA", 12 );
        testFact.set( "fieldB", 8 );
        testFact.set( "fieldC", 5 );

        assertThat(expr1.asFunction(prototype).apply(testFact)).isEqualTo(expr2.asFunction(prototype).apply(testFact));
        assertThat(expr1.getImpactedFields()).containsExactly("fieldA");
        assertThat(expr2.getImpactedFields()).containsExactlyInAnyOrder("fieldB", "fieldC");
    }
}