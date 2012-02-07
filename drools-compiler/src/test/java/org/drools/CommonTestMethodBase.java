package org.drools;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.integrationtests.SerializationHelper;
import org.drools.io.ResourceFactory;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Assert;

/**
 * This contains methods common to many of the tests in drools-compiler. 
 * </p>
 * The {@link #createKnowledgeSession(KnowledgeBase)} method has been made
 * common so that tests in drools-compiler can be reused (with persistence)
 * in drools-persistence-jpa.
 */
public class CommonTestMethodBase extends Assert {

    protected RuleBase getRuleBase() throws Exception {
        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {
        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }
    
    protected RuleBase getSinglethreadRuleBase() throws Exception {
        RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setMultithreadEvaluation( false );
        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }
    
    protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) { 
        return kbase.newStatefulKnowledgeSession();
    }

    protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase, KnowledgeSessionConfiguration ksconf) { 
        return kbase.newStatefulKnowledgeSession(ksconf, null);
    }

}
