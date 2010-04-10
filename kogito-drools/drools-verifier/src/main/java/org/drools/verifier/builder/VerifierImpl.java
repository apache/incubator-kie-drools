package org.drools.verifier.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarInputStream;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsError;
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
import org.drools.verifier.visitor.PackageDescrVisitor;

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

            PackageDescrVisitor ruleFlattener = new PackageDescrVisitor( result.getVerifierData(),
                                                                         jars );

            ruleFlattener.visitPackageDescr( descr );

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

        if (kbuilder.hasErrors()){
            Iterator<KnowledgeBuilderError> errors = kbuilder.getErrors().iterator();
            while(errors.hasNext()){
                this.errors.add(new VerifierError("Error compiling verifier rules: "+errors.next().getMessage()));
            }
            throw new IllegalStateException("Error compiling verifier rules");
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
            DrlParser drlParser = new DrlParser();

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

                PackageDescr pkg = drlParser.parse( drl.toString() );

                if ( drlParser.hasErrors() ) {
                    addVerifierErrors( drlParser );
                } else if ( pkg == null ) {
                    errors.add( new VerifierError( "Verifier could not form a PackageDescr from the resources that it was trying to verify." ) );
                } else {
                    addPackageDescr( pkg );

                    addDrlData( drl.toString() );
                }

                reader.close();

            } catch ( DroolsParserException e ) {
                errors.add( new VerifierError( e.getMessage() ) );
            } catch ( IOException e ) {
                errors.add( new VerifierError( e.getMessage() ) );
            }
        }
    }

    private void addVerifierErrors(DrlParser p) {
        for ( DroolsError droolsError : p.getErrors() ) {
            errors.add( new VerifierError( droolsError.getMessage() ) );
        }
    }

    /**
     * 
     * Adds meta data from DRL to package and rule.
     * 
     * @param drl Package DRL
     */
    private void addDrlData(String drl) {

        List<DrlRuleParser> rules;

        try {
            DrlPackageParser pData = addDrlPackageData( drl );
            rules = pData.getRules();
        } catch ( ParseException e ) {
            rules = DrlRuleParser.findRulesDataFromDrl( drl );
        }

        addDrlRulesData( rules );
    }

    private void addDrlRulesData(List<DrlRuleParser> rules) {
        for ( DrlRuleParser rData : rules ) {
            VerifierRule rule = this.result.getVerifierData().getRuleByName( rData.getName() );

            if ( rule != null ) {
                rule.getHeader().addAll( rData.getHeader() );
                rule.getLhsRows().addAll( rData.getLhs() );
                rule.getRhsRows().addAll( rData.getRhs() );
                rule.setDescription( rData.getDescription() );
                rule.getCommentMetadata().addAll( rData.getMetadata() );
                rule.getOtherInfo().putAll( rData.getOtherInformation() );
            }
        }
    }

    private DrlPackageParser addDrlPackageData(String drl) throws ParseException {
        DrlPackageParser pData = DrlPackageParser.findPackageDataFromDrl( drl );

        RulePackage rPackage = this.result.getVerifierData().getPackageByName( pData.getName() );

        rPackage.getGlobals().addAll( pData.getGlobals() );
        rPackage.setDescription( pData.getDescription() );
        rPackage.getMetadata().addAll( pData.getMetadata() );
        rPackage.getOtherInfo().putAll( pData.getOtherInformation() );

        return pData;
    }

    public List<VerifierError> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
