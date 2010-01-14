package org.drools.verifier.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.io.Resource;
import org.drools.lang.descr.PackageDescr;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.verifier.DefaultVerifierConfiguration;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.VerifierError;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.misc.DrlPackageParser;
import org.drools.verifier.misc.DrlRuleParser;
import org.drools.verifier.misc.PackageDescrVisitor;

/**
 * This is the main user class for verifier. This will use rules to validate
 * rules, caching the "knowledge base" of verifier rules.
 * 
 * @author Toni Rikkola
 */
public class VerifierImpl
    implements
    Verifier {

    private KnowledgeBase               verifierKnowledgeBase;
    private StatefulKnowledgeSession    ksession;

    private final VerifierConfiguration conf;

    private List<VerifierError>         errors = new ArrayList<VerifierError>();

    private VerifierReport              result = VerifierReportFactory.newVerifierReport();

    private List<JarInputStream>        jars   = new ArrayList<JarInputStream>();

    public VerifierImpl(VerifierConfiguration conf) {
        this.conf = conf;
    }

    public VerifierImpl() {
        this.conf = new DefaultVerifierConfiguration();
    }

    public StatefulKnowledgeSession getKnowledgeSession() {
        return ksession;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.drools.verifier.Verifier#addPackageDescr(org.drools.lang.descr.
     * PackageDescr)
     */
    public void addPackageDescr(PackageDescr descr) {
        try {

            PackageDescrVisitor ruleFlattener = new PackageDescrVisitor();

            ruleFlattener.addPackageDescrToData( descr,
                                                 jars,
                                                 result.getVerifierData() );

        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

    public void addObjectModel(JarInputStream jar) {
        this.jars.add( jar );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.verifier.Verifier#reloadAnalysisKnowledgeBase()
     */
    public synchronized void reloadVerifierKnowledgeBase() throws Exception {
        updateRuleBase();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.verifier.Verifier#fireAnalysis()
     */
    public boolean fireAnalysis() {
        try {

            if ( this.verifierKnowledgeBase == null ) {
                synchronized ( this.getClass() ) {
                    updateRuleBase();
                    updateKnowledgeSession();
                }
            }

            for ( Object o : result.getVerifierData().getAll() ) {
                ksession.insert( o );
            }

            // Object that returns the results.
            ksession.setGlobal( "result",
                                result );
            ksession.fireAllRules( new MetaDataAgendaFilter( conf.acceptRulesWithoutVerifiyingScope(),
                                                             "verifying_scopes",
                                                             conf.getVerifyingScopes() ) );

        } catch ( Throwable t ) {
            t.printStackTrace();

            return false;
        }

        return true;
    }

    private void updateKnowledgeSession() {
        if ( this.ksession != null ) {
            this.ksession.dispose();
        }

        ksession = verifierKnowledgeBase.newStatefulKnowledgeSession();
    }

    /**
     * Returns the verifier results as <code>AnalysisResult</code> object.
     * 
     * @return Analysis result
     */
    public VerifierReport getResult() {
        return result;
    }

    private void updateRuleBase() throws Exception {

        verifierKnowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeBuilderConfiguration kbuilderConfiguration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        kbuilderConfiguration.setProperty( "drools.dialect.java.compiler",
                                           "JANINO" );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( kbuilderConfiguration );

        if ( conf.getVerifyingResources() != null ) {
            for ( Resource resource : conf.getVerifyingResources().keySet() ) {
                kbuilder.add( resource,
                              conf.getVerifyingResources().get( resource ) );
            }
        }

        verifierKnowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.verifier.Verifier#dispose()
     */
    public void dispose() {
        if ( ksession != null ) {
            synchronized ( this.getClass() ) {
                ksession.dispose();
            }
        }
    }

    public void addResourcesToVerify(Resource resource,
                                     ResourceType type) {

        // TODO: Other than DRL
        if ( type.matchesExtension( ".drl" ) ) {
            DrlParser p = new DrlParser();

            try {

                BufferedReader reader = new BufferedReader( resource.getReader() );

                StringBuffer drl = new StringBuffer( "" );
                String line = null;
                do {
                    line = reader.readLine();
                    if ( line != null ) {
                        drl.append( line );
                        drl.append( "\n" );

                    }
                } while ( line != null );

                PackageDescr pkg = p.parse( resource.getInputStream() );

                addPackageDescr( pkg );

                addDrlData( drl.toString() );

                reader.close();

            } catch ( DroolsParserException e ) {
                errors.add( new VerifierError( e.getMessage() ) );
            } catch ( IOException e ) {
                errors.add( new VerifierError( e.getMessage() ) );
            }
        }
    }

    /**
     * 
     * Adds meta data from DRL to package and rule.
     * 
     * @param drl Package DRL
     */
    private void addDrlData(String drl) {

        DrlPackageParser pData = DrlPackageParser.findPackageDataFromDrl( drl );

        RulePackage rPackage = this.result.getVerifierData().getPackageByName( pData.getName() );

        rPackage.getGlobals().addAll( pData.getGlobals() );
        rPackage.setDescription( pData.getDescription() );
        rPackage.getMetadata().addAll( pData.getMetadata() );
        rPackage.getOtherInfo().putAll( pData.getOtherInformation() );

        for ( DrlRuleParser rData : pData.getRules() ) {
            VerifierRule rule = this.result.getVerifierData().getRuleByName( rData.getName() );

            rule.getHeader().addAll( rData.getHeader() );
            rule.getLhsRows().addAll( rData.getLhs() );
            rule.getRhsRows().addAll( rData.getRhs() );
            rule.setDescription( rData.getDescription() );
            rule.getCommentMetadata().addAll( rData.getMetadata() );
            rule.getOtherInfo().putAll( rData.getOtherInformation() );

        }
    }

    public List<VerifierError> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
