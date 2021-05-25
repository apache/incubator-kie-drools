/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.compiler.commons.factories;

import com.github.javaparser.ast.stmt.BlockStmt;
import org.junit.Test;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KiePMMLTruePredicateFactoryTest {

    @Test
    public void getTruePredicateBody() {
        final BlockStmt retrieved = KiePMMLTruePredicateFactory.getTruePredicateBody();
        assertNotNull(retrieved);
        BlockStmt expected = JavaParserUtils.parseBlock("{" +
                "    return true;" +
                "}");

        JavaParserUtils.equalsNode(expected, retrieved);
    }
}