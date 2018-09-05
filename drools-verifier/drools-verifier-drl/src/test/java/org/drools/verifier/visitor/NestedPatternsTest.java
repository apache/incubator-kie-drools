/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;

import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.data.VerifierReportFactory;
import org.junit.Test;
import org.kie.internal.builder.conf.LanguageLevelOption;

public class NestedPatternsTest {

    @Test
    public void runVisitor() throws Exception {
        VerifierData data = VerifierReportFactory.newVerifierData();
        PackageDescrVisitor visitor = new PackageDescrVisitor(data,
                Collections.EMPTY_LIST);

        assertNotNull(data);

        Reader drlReader = new InputStreamReader(getClass().getResourceAsStream("NestedPatterns.drl"));
        PackageDescr packageDescr = new DrlParser(LanguageLevelOption.DRL5).parse(drlReader);

        assertNotNull(packageDescr);

        visitor.visitPackageDescr(packageDescr);

        Collection<VerifierComponent> all = data.getAll();
        int patternCount = 0;
        for (VerifierComponent verifierComponent : all) {

            if (verifierComponent.getVerifierComponentType().equals(VerifierComponentType.PATTERN)) {
                patternCount++;
            }
        }
        assertEquals(4,
                patternCount);

        Collection<Pattern> patterns = data.getAll(VerifierComponentType.PATTERN);

//        for ( Pattern pattern : patterns ) {
//            System.out.println( pattern.getPath() + " " + pattern );
//        }

        assertNotNull(patterns);
        assertEquals(4,
                patterns.size());

        Collection<Restriction> restrictions = data.getAll(VerifierComponentType.RESTRICTION);

//        for ( Restriction restriction : restrictions ) {
//            System.out.println( restriction.getPath() + " " + restriction );
//        }

        assertNotNull(restrictions);
        assertEquals(3,
                restrictions.size());

    }
}
