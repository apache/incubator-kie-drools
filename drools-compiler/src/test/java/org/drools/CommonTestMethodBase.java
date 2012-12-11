package org.drools;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

import org.drools.common.InternalAgenda;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.integrationtests.SerializationHelper;
import org.drools.lang.descr.PackageDescr;
import org.drools.runtime.rule.impl.AgendaImpl;
import org.junit.Assert;
import org.kie.KnowledgeBase;
import org.kie.KieBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.conf.LanguageLevelOption;
import org.kie.definition.KnowledgePackage;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.Environment;
import org.kie.runtime.KieSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKnowledgeSession;

/**
 * This contains methods common to many of the tests in drools-compiler. 
 * </p>
 * The {@link #createKnowledgeSession(KnowledgeBase)} method has been made
 * common so that tests in drools-compiler can be reused (with persistence)
 * in drools-persistence-jpa.
 */
public class CommonTestMethodBase extends Assert {

    // ***********************************************
    // METHODS TO BE REMOVED FOR 6.0.0

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

    protected org.drools.rule.Package loadPackage(final String classPathResource) throws DroolsParserException,
                                                                                 IOException {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( classPathResource ) ) );

        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        final org.drools.rule.Package pkg = builder.getPackage();
        return pkg;
    }

    protected RuleBase loadRuleBase(final Reader reader) throws IOException,
                                                        DroolsParserException,
                                                        Exception {
        final DrlParser parser = new DrlParser( LanguageLevelOption.DRL5 );
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

        org.drools.rule.Package pkg = builder.getPackage();
        pkg = SerializationHelper.serializeObject( pkg );

        // add the package to a rulebase
        RuleBase ruleBase = getSinglethreadRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        // load up the rulebase
        return ruleBase;
    }
    
    // END - METHODS TO BE REMOVED FOR 6.0.0
    // ****************************************************************************************

    protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
        return kbase.newStatefulKnowledgeSession();
    }

    protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase,
                                                              KieSessionConfiguration ksconf) {
        return kbase.newStatefulKnowledgeSession( ksconf, null );
    }

    protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase,
                                                              KieSessionConfiguration ksconf,
                                                              Environment env) {
        return kbase.newStatefulKnowledgeSession( ksconf, env );
    }

    protected StatelessKnowledgeSession createStatelessKnowledgeSession(KnowledgeBase kbase) {
        return kbase.newStatelessKnowledgeSession();
    }

    protected KnowledgeBase loadKnowledgeBaseFromString(String... drlContentStrings) {
        return loadKnowledgeBaseFromString( null, null, drlContentStrings );
    }

    protected KnowledgeBase loadKnowledgeBaseFromString(KnowledgeBuilderConfiguration config,
                                                        String... drlContentStrings) {
        return loadKnowledgeBaseFromString( config, null, drlContentStrings );
    }

    protected KnowledgeBase loadKnowledgeBaseFromString(KieBaseConfiguration kBaseConfig,
                                                        String... drlContentStrings) {
        return loadKnowledgeBaseFromString( null, kBaseConfig, drlContentStrings );
    }

    protected KnowledgeBase loadKnowledgeBaseFromString(KnowledgeBuilderConfiguration config,
                                                        KieBaseConfiguration kBaseConfig,
                                                        String... drlContentStrings) {
        KnowledgeBuilder kbuilder = config == null ? KnowledgeBuilderFactory.newKnowledgeBuilder() : KnowledgeBuilderFactory.newKnowledgeBuilder( config );
        for ( String drlContentString : drlContentStrings ) {
            kbuilder.add( ResourceFactory.newByteArrayResource( drlContentString.getBytes() ),
                          ResourceType.DRL );
        }

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = kBaseConfig == null ? KnowledgeBaseFactory.newKnowledgeBase() : KnowledgeBaseFactory.newKnowledgeBase( kBaseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }

    protected KnowledgeBase loadKnowledgeBase(KnowledgeBuilderConfiguration kbuilderConf,
                                              KieBaseConfiguration kbaseConf,
                                              String... classPathResources) {
        Collection<KnowledgePackage> knowledgePackages = loadKnowledgePackages( kbuilderConf, classPathResources );

        if ( kbaseConf == null ) {
            kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbaseConf );
        kbase.addKnowledgePackages( knowledgePackages );
        return kbase;
    }

    protected KnowledgeBase loadKnowledgeBase(PackageDescr descr) {
        return loadKnowledgeBase( null, null, descr );
    }

    protected KnowledgeBase loadKnowledgeBase(KnowledgeBuilderConfiguration kbuilderConf,
                                              KieBaseConfiguration kbaseConf,
                                              PackageDescr descr) {
        Collection<KnowledgePackage> knowledgePackages = loadKnowledgePackages( kbuilderConf, descr );

        if ( kbaseConf == null ) {
            kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbaseConf );
        kbase.addKnowledgePackages( knowledgePackages );
        return kbase;
    }

    public Collection<KnowledgePackage> loadKnowledgePackages(String... classPathResources) {
        return loadKnowledgePackages( null, classPathResources );
    }

    public Collection<KnowledgePackage> loadKnowledgePackages(PackageDescr descr) {
        return loadKnowledgePackages( null, descr );
    }

    public Collection<KnowledgePackage> loadKnowledgePackages(KnowledgeBuilderConfiguration kbuilderConf,
                                                              PackageDescr descr) {
        if ( kbuilderConf == null ) {
            kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        }
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( kbuilderConf );
        kbuilder.add( ResourceFactory.newDescrResource( descr ),
                      ResourceType.DESCR );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        Collection<KnowledgePackage> knowledgePackages = kbuilder.getKnowledgePackages();
        return knowledgePackages;
    }

    public Collection<KnowledgePackage> loadKnowledgePackages(KnowledgeBuilderConfiguration kbuilderConf,
                                                              String... classPathResources) {
        if ( kbuilderConf == null ) {
            kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        }
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( kbuilderConf );
        for ( String classPathResource : classPathResources ) {
            kbuilder.add( ResourceFactory.newClassPathResource( classPathResource,
                                                                getClass() ),
                          ResourceType.DRL );
        }
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        Collection<KnowledgePackage> knowledgePackages = kbuilder.getKnowledgePackages();
        return knowledgePackages;
    }

    public Collection<KnowledgePackage> loadKnowledgePackagesFromString(String... content) {
        return loadKnowledgePackagesFromString( null,
                                                content );
    }

    public Collection<KnowledgePackage> loadKnowledgePackagesFromString(KnowledgeBuilderConfiguration kbuilderConf,
                                                                        String... content) {
        if ( kbuilderConf == null ) {
            kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        }
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( kbuilderConf );
        for ( String r : content ) {
            kbuilder.add( ResourceFactory.newByteArrayResource( r.getBytes() ),
                          ResourceType.DRL );
        }
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        Collection<KnowledgePackage> knowledgePackages = kbuilder.getKnowledgePackages();
        return knowledgePackages;
    }

    protected KnowledgeBase loadKnowledgeBase(KnowledgeBuilderConfiguration kbuilderConf,
                                              String... classPathResources) {
        return loadKnowledgeBase( kbuilderConf, null, classPathResources );
    }

    protected KnowledgeBase loadKnowledgeBase(KieBaseConfiguration kbaseConf,
                                              String... classPathResources) {
        return loadKnowledgeBase( null, kbaseConf, classPathResources );
    }

    protected KnowledgeBase loadKnowledgeBase(String... classPathResources) {
        return loadKnowledgeBase( null, null, classPathResources );
    }

    protected InternalAgenda getInternalAgenda(StatefulKnowledgeSession session) {
        return ((AgendaImpl) session.getAgenda()).getAgenda();
    }

}
