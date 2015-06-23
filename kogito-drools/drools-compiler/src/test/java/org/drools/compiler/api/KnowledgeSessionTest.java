/*
 * Copyright 2015 JBoss Inc
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

package org.drools.compiler.api;

import java.util.Collection;

import org.junit.Test;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;


public class KnowledgeSessionTest {
    @Test
    public void testKnowledgeProviderWithRules() {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        String str = "";
        str += "package org.drools.compiler.test1\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "then\n";
        str += "end\n\n";
        str += "rule rule2\n";
        str += "when\n";
        str += "then\n";
        str += "end\n";
        builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        str = "package org.drools.compiler.test2\n";
        str += "rule rule3\n";
        str += "when\n";
        str += "then\n";
        str += "end\n\n";
        str += "rule rule4\n";
        str += "when\n";
        str += "then\n";
        str += "end\n";
        builder.add( ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL );

        Collection<KnowledgePackage> pkgs = builder.getKnowledgePackages();


    }
}
