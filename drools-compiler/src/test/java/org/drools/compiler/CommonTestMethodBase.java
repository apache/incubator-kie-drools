package org.drools.compiler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

import org.drools.core.RuleBase;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.RuleBaseFactory;
import org.drools.core.common.InternalAgenda;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.runtime.rule.impl.AgendaImpl;
import org.junit.Assert;
import org.kie.api.KieBaseConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.PhreakOption;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.KieSessionOption;

/**
 * This contains methods common to many of the tests in drools-compiler. 
 * </p>
 * The {@link #createKnowledgeSession(KnowledgeBase)} method has been made
 * common so that tests in drools-compiler can be reused (with persistence)
 * in drools-persistence-jpa.
 */
public class CommonTestMethodBase extends Assert {
    public static PhreakOption preak = PhreakOption.ENABLED;

    // ***********************************************
    // METHODS TO BE REMOVED FOR 6.0.0

    protected RuleBase getRuleBase() throws Exception {
        return RuleBaseFactory.newRuleBase(RuleBase.RETEOO,
                                           null);
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {
        return RuleBaseFactory.newRuleBase(RuleBase.RETEOO,
                                           config);
    }

    protected RuleBase getSinglethreadRuleBase() throws Exception {
        RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setMultithreadEvaluation(false);
        return RuleBaseFactory.newRuleBase(RuleBase.RETEOO,
                                           config);
    }

    protected org.drools.core.rule.Package loadPackage(final String classPathResource) throws DroolsParserException,
            IOException {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl(new InputStreamReader(getClass().getResourceAsStream(classPathResource)));

        if (builder.hasErrors()) {
            fail(builder.getErrors().toString());
        }

        final org.drools.core.rule.Package pkg = builder.getPackage();
        return pkg;
    }

    protected RuleBase loadRuleBase(final Reader reader) throws IOException,
            DroolsParserException,
            Exception {
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        final PackageDescr packageDescr = parser.parse(reader);
        if (parser.hasErrors()) {
            fail("Error messages in parser, need to sort this our (or else collect error messages):\n"
                  + parser.getErrors() );
        }
        // pre build the package
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );

        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        org.drools.core.rule.Package pkg = builder.getPackage();
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

    protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase, KieSessionOption option) {
        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( option );
        return kbase.newStatefulKnowledgeSession( ksconf, null );
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
        return loadKnowledgeBaseFromString( null, null, preak, drlContentStrings );
    }

    protected KnowledgeBase loadKnowledgeBaseFromString(PhreakOption phreak, String... drlContentStrings) {
        return loadKnowledgeBaseFromString( null, null, phreak, drlContentStrings );
    }

    protected KnowledgeBase loadKnowledgeBaseFromString(KnowledgeBuilderConfiguration config,
                                                        String... drlContentStrings) {
        return loadKnowledgeBaseFromString( config, null, preak, drlContentStrings );
    }

    protected KnowledgeBase loadKnowledgeBaseFromString(KieBaseConfiguration kBaseConfig,
                                                        String... drlContentStrings) {
        return loadKnowledgeBaseFromString( null, kBaseConfig, preak, drlContentStrings );
    }

    protected KnowledgeBase loadKnowledgeBaseFromString(KnowledgeBuilderConfiguration config,
                                                        KieBaseConfiguration kBaseConfig,
                                                        PhreakOption phreak,
                                                        String... drlContentStrings) {
        KnowledgeBuilder kbuilder = config == null ? KnowledgeBuilderFactory.newKnowledgeBuilder() : KnowledgeBuilderFactory.newKnowledgeBuilder( config );
        for ( String drlContentString : drlContentStrings ) {
            kbuilder.add( ResourceFactory.newByteArrayResource(drlContentString.getBytes()),
                          ResourceType.DRL );
        }

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        if ( kBaseConfig == null ) {
            kBaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        }
        kBaseConfig.setOption( phreak );        
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
        kbaseConf.setOption( preak );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbaseConf );
        kbase.addKnowledgePackages( knowledgePackages );
        try {
            kbase = SerializationHelper.serializeObject( kbase );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        kbaseConf.setOption( preak );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbaseConf );
        kbase.addKnowledgePackages( knowledgePackages );
        try {
            kbase = SerializationHelper.serializeObject( kbase );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
