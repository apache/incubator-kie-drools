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

package org.jbpm.test.util;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.jbpm.integrationtests.JbpmSerializationHelper;
import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public abstract class AbstractBaseTest {
 
    protected KnowledgeBuilderImpl builder;
   
    @Before
    public void before() { 
        builder = new KnowledgeBuilderImpl();
    }
    
    public StatefulKnowledgeSession createKieSession(KnowledgePackage... pkg) { 
        try { 
            return createKieSession(false, pkg);
        } catch(Exception e ) { 
            String msg = "There's no reason fo an exception to be thrown here (because the kbase is not being serialized)!";
            fail( msg );
            throw new RuntimeException(msg, e);
        }
    } 
   
    public StatefulKnowledgeSession createKieSession(boolean serializeKbase, KnowledgePackage... pkg) throws Exception {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages((Collection) Arrays.asList(pkg));
        if( serializeKbase ) { 
            kbase = JbpmSerializationHelper.serializeObject( kbase );
        }

        return kbase.newStatefulKnowledgeSession();
    }
    
    @BeforeClass
    public static void configure() { 
        LoggingPrintStream.interceptSysOutSysErr();
    }
    
    @AfterClass
    public static void reset() { 
        LoggingPrintStream.resetInterceptSysOutSysErr();
    }
}
