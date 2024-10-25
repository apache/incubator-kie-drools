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

package org.kie.dmn.feel.lang.ast;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.types.GenRangeType;
import org.kie.dmn.feel.util.EvaluationContextTestUtil;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.mockito.Mockito;

class RangeTypeNodeTest {

    @Test
    void evaluate() {
        final CTypeNode typeNode = new CTypeNode(BuiltInType.BOOLEAN);
        final RangeTypeNode rangeTypeNode = new RangeTypeNode(typeNode, "sometext");
        Assertions.assertThat(rangeTypeNode.evaluate(EvaluationContextTestUtil.newEmptyEvaluationContext())).isInstanceOf(GenRangeType.class);
    }

    @Test
    void accept() {
        final Visitor visitor = Mockito.spy(Visitor.class);
        final RangeTypeNode rangeTypeNode = new RangeTypeNode(null, "sometext");
        rangeTypeNode.accept(visitor);
        Mockito.verify(visitor).visit(rangeTypeNode);
    }

    @Test
    void getGenTypeNode() {
        final CTypeNode typeNode = new CTypeNode(BuiltInType.BOOLEAN);
        final RangeTypeNode rangeTypeNode = new RangeTypeNode(typeNode, "sometext");
        Assertions.assertThat(rangeTypeNode.getGenericTypeNode()).isSameAs(typeNode);
    }
}