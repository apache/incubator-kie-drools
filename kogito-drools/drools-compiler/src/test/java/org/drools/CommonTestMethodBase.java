package org.drools;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
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

    protected KnowledgeBase loadKnowledgeBaseFromString( String... drlContentStrings ) {
        return loadKnowledgeBaseFromString( null, drlContentStrings );
    }

    protected KnowledgeBase loadKnowledgeBaseFromString( KnowledgeBuilderConfiguration config, String... drlContentStrings ) {
        KnowledgeBuilder kbuilder = config == null ? KnowledgeBuilderFactory.newKnowledgeBuilder() : KnowledgeBuilderFactory.newKnowledgeBuilder(config);
        for ( String drlContentString : drlContentStrings ) {
            kbuilder.add( ResourceFactory.newByteArrayResource(drlContentString.getBytes()),
                    ResourceType.DRL );
        }

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }

    protected KnowledgeBase loadKnowledgeBase( KnowledgeBuilderConfiguration kbuilderConf, 
                                               KnowledgeBaseConfiguration kbaseConf, 
                                               String... classPathResources ) {
        if( kbuilderConf == null ) {
            kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        }
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbuilderConf);
        for ( String classPathResource : classPathResources ) {
            kbuilder.add( ResourceFactory.newClassPathResource( classPathResource,
                    getClass() ),
                    ResourceType.DRL );
        }
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        if( kbaseConf == null ) {
            kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbaseConf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }
    
    protected KnowledgeBase loadKnowledgeBase( KnowledgeBuilderConfiguration kbuilderConf, String... classPathResources ) {
        return loadKnowledgeBase( kbuilderConf,  null, classPathResources );
    }

    protected KnowledgeBase loadKnowledgeBase( KnowledgeBaseConfiguration kbaseConf, String... classPathResources ) {
        return loadKnowledgeBase( null,  kbaseConf, classPathResources );
    }

    protected KnowledgeBase loadKnowledgeBase( String... classPathResources ) {
        return loadKnowledgeBase( null,  null, classPathResources );
    }

    protected org.drools.rule.Package loadPackage( final String classPathResource ) throws DroolsParserException,
            IOException {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( classPathResource ) ) );

        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        final Package pkg = builder.getPackage();
        return pkg;
    }

    protected RuleBase loadRuleBase( final Reader reader ) throws IOException,
            DroolsParserException,
            Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr packageDescr = parser.parse( reader );
        if ( parser.hasErrors() ) {
            fail( "Error messages in parser, need to sort this our (or else collect error messages):\n"
                    + parser.getErrors() );
        }
        // pre build the package
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );

        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        Package pkg = builder.getPackage();
        pkg = SerializationHelper.serializeObject(pkg);

        // add the package to a rulebase
        RuleBase ruleBase = getSinglethreadRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        // load up the rulebase
        return ruleBase;
    }

}
