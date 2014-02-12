/*
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier;

import static org.junit.Assert.assertFalse;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.visitor.PackageDescrVisitor;
import org.junit.Before;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

abstract public class TestBaseOld {

    @Before
    public void setUp() throws Exception {

        System.setProperty( "drools.dateformat",
                            "dd-MMM-yyyy" );
    }

    public KieSession getStatelessKieSession(InputStream stream) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(stream), ResourceType.DRL);
        assertFalse(kbuilder.hasErrors());

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase.newStatefulKnowledgeSession();
    }

    /**
     * Returns true if map contains redundancy where ruleName1 is redundant to
     * ruleName2.
     * 
     * @param map
     * @param ruleName1
     * @param ruleName2
     * @return True if redundancy exists.
     */
    protected static boolean mapContains(Map<String, Set<String>> map,
                                         String ruleName1,
                                         String ruleName2) {
        if ( map.containsKey( ruleName1 ) ) {
            Set<String> set = map.get( ruleName1 );
            boolean exists = set.remove( ruleName2 );

            // If set is empty remove key from map.
            if ( set.isEmpty() ) {
                map.remove( ruleName1 );
            }
            return exists;
        }
        return false;
    }

    /**
     * Returns true if map contains redundancy where cause1 is redundant to
     * cause2.
     * 
     * @param map
     * @param cause1
     * @param cause2
     * @return True if redundancy exists.
     */
    protected static boolean causeMapContains(Map<Cause, Set<Cause>> map,
                                              Cause cause1,
                                              Cause cause2) {
        if ( map.containsKey( cause1 ) ) {
            Set<Cause> set = map.get( cause1 );
            boolean exists = set.remove( cause2 );

            // If set is empty remove key from map.
            if ( set.isEmpty() ) {
                map.remove( cause1 );
            }
            return exists;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public Collection< ? extends Object> getTestData(InputStream stream,
                                                     VerifierData data) throws Exception {
        Reader drlReader = new InputStreamReader( stream );
        PackageDescr descr = new DrlParser(LanguageLevelOption.DRL5).parse( drlReader );

        PackageDescrVisitor packageDescrVisitor = new PackageDescrVisitor( data,
                                                                           Collections.EMPTY_LIST );

        packageDescrVisitor.visitPackageDescr( descr );

        // Rules with relations
        return data.getAll();
    }
}
