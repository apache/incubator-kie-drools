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
package org.drools.verifier.visitor;


import org.drools.drl.ast.descr.PackageDescr;
import org.drools.verifier.TestBase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PatternDescrVisitorTest extends TestBase {


    @Test
    void testVisitFieldVariableRestriction() throws Exception {

        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("PatternDescrVisitorTest.drl"));

        assertThat(packageDescr).isNotNull();

        packageDescrVisitor.visitPackageDescr(packageDescr);

        assertContainsFields(1);

        assertContainsField("age");
        assertContainsVariable("Test", "var");
    }

    @Test
    void testVisitPatternVariableRestriction() throws Exception {

        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("PatternDescrVisitorTest2.drl"));

        assertThat(packageDescr).isNotNull();

        packageDescrVisitor.visitPackageDescr(packageDescr);

        assertContainsVariable("Test", "var");
    }

}
