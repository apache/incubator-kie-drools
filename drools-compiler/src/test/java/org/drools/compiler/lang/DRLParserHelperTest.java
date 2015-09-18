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

package org.drools.compiler.lang;


import static org.junit.Assert.*;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

public class DRLParserHelperTest {

    /**
     * this test will throw a {@link RuntimeException} caused 
     * by an {@link IndexOutOfBoundsException}<br>
     * 
     */
    @Test
    public void test() throws Exception {
        byte[] content=new byte[]{0x04,0x44,0x00,0x00,0x60,0x00,0x00,0x00};
        //"content" may come from any .DS_Store file of Mac,
        //i just use this 8-Byte instead to reproduce the case
        
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();
        try {
            knowledgeBuilder.add(ResourceFactory.newByteArrayResource(content), 
                    ResourceType.DRL);
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
            e.printStackTrace();
        }
    }
}