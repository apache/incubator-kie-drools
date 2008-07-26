package org.drools.verifier;

import java.util.Collection;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.drools.verifier.dao.VerifierResult;
import org.drools.verifier.dao.VerifierResultFactory;
import org.drools.verifier.report.ReportModeller;
import org.drools.verifier.report.html.ComponentsReportModeller;

/**
 * This is the main user class for verifier.
 * This will use rules to validate rules, caching the "knowledge base" of verifier rules.
 *
 * @author Toni Rikkola
 */
public class Verifier {

    static RuleBase        verifierKnowledgeBase;

    private VerifierResult result = VerifierResultFactory.createVerifierResult();

    public void addPackageDescr(PackageDescr descr) {
        try {

            PackageDescrFlattener ruleFlattener = new PackageDescrFlattener();

            ruleFlattener.addPackageDescrToData( descr,
                                                 result.getVerifierData() );

        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

    /**
     * As the analyzer uses rules itself, this will reload the knowledge base.
     * @throws Exception
     */
    public synchronized void reloadAnalysisKnowledgeBase() throws Exception {
        verifierKnowledgeBase = createRuleBase();
    }

    /**
     * 
     * This will run the verifier.
     * 
     * @return true if everything worked.
     */
    public boolean fireAnalysis() {
        try {

            if ( this.verifierKnowledgeBase == null ) {
                synchronized ( this.getClass() ) {
                    verifierKnowledgeBase = createRuleBase();
                }
            }

            WorkingMemory workingMemory = verifierKnowledgeBase.newStatefulSession();

            Collection< ? extends Object> c = result.getVerifierData().getAll();

            for ( Object o : c ) {
                workingMemory.insert( o );
            }

            // Object that returns the results.
            workingMemory.setGlobal( "result",
                                     result );
            workingMemory.fireAllRules();

        } catch ( Throwable t ) {
            t.printStackTrace();

            return false;
        }

        return true;
    }

    /**
     * Returns the verifier results as plain text.
     *
     * @return Analysis results as plain text.
     */
    public String getResultAsPlainText() {
        return ReportModeller.writePlainText( result );
    }

    /**
     * Returns the verifier results as XML.
     *
     * @return Analysis results as XML
     */
    public String getResultAsXML() {
        return ReportModeller.writeXML( result );
    }

    /**
     * Returns the verifier results as HTML.
     *
     * @return Analysis results as HTML
     */
    public void writeComponentsHTML(String path) {
        ComponentsReportModeller.writeHTML( path,
                                            result );
    }

    /**
     * Returns the verifier results as <code>AnalysisResult</code> object.
     *
     * @return Analysis result
     */
    public VerifierResult getResult() {
        return result;
    }

    private static RuleBase createRuleBase() throws Exception {

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        Collection<Package> packages = RuleLoader.loadPackages();
        for ( Package pkg : packages ) {
            try {
                ruleBase.addPackage( pkg );
            } catch ( Exception e ) {
                throw new Exception( "Adding package " + pkg.getName() + " caused an error.",
                                     e );
            }
        }

        return ruleBase;
    }
}
