package org.drools.verifier.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
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
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.misc.PackageDescrVisitor;
import org.drools.verifier.misc.RuleLoader;

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
                                                 result.getVerifierData() );

        } catch ( Throwable t ) {
            t.printStackTrace();
        }
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
            ksession.fireAllRules();

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

        verifierKnowledgeBase.addKnowledgePackages( RuleLoader.loadPackages( conf.getVerifyingResources() ) );
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

        if ( type.matchesExtension( ".drl" ) ) {
            DrlParser p = new DrlParser();

            try {

                PackageDescr pkg = p.parse( resource.getInputStream() );
                addPackageDescr( pkg );

            } catch ( DroolsParserException e ) {
                errors.add( new VerifierError( e.getMessage() ) );
            } catch ( IOException e ) {
                errors.add( new VerifierError( e.getMessage() ) );
            }
        }
    }

    public List<VerifierError> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
