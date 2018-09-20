/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.verifier.visitor;


import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.verifier.TestBase;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class PatternDescrVisitorTest extends TestBase {


    @Test
    public void testVisitFieldVariableRestriction() throws Exception {

        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("PatternDescrVisitorTest.drl"));

        assertNotNull(packageDescr);

        packageDescrVisitor.visitPackageDescr(packageDescr);

        assertContainsFields(1);

        assertContainsField("age");
        assertContainsVariable("Test", "var");
    }

    @Test
    public void testVisitPatternVariableRestriction() throws Exception {

        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("PatternDescrVisitorTest2.drl"));

        assertNotNull(packageDescr);

        packageDescrVisitor.visitPackageDescr(packageDescr);

        assertContainsVariable("Test", "var");
    }

}
