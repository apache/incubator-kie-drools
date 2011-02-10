/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.builder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilderErrors;
import org.drools.io.Resource;
import org.drools.lang.descr.PackageDescr;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.verifier.DefaultVerifierConfiguration;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.VerifierError;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierComponent;
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

    private List<VerifierError>         errors                 = new ArrayList<VerifierError>();

    private VerifierReport              result                 = VerifierReportFactory.newVerifierReport();

    private List<JarInputStream>        jars                   = new ArrayList<JarInputStream>();

    private VerifierPackageBuilder      verifierPackageBuilder = new VerifierPackageBuilder();

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
        return analyse( new ScopesAgendaFilter( true,
                                                ScopesAgendaFilter.ALL_SCOPES ) );
    }

    public boolean fireAnalysis(ScopesAgendaFilter scopesAgendaFilter) {
        return analyse( scopesAgendaFilter );
    }

    private boolean analyse(ScopesAgendaFilter scopesAgendaFilter) {
        try {

            if ( this.verifierKnowledgeBase == null ) {
                synchronized ( this.getClass() ) {
                    updateRuleBase();
                    updateKnowledgeSession();
                }
            }

            for ( Object object : result.getVerifierData().getAll() ) {
                ksession.insert( object );
            }

            // Object that returns the results.
            ksession.setGlobal( "result",
                                result );

            ksession.fireAllRules( scopesAgendaFilter );

        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

        return true;
    }

    public void flushKnowledgeSession() {
        updateKnowledgeSession();
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

    private void updateRuleBase() {

        VerifierKnowledgeBaseBuilder verifierKnowledgeBaseBuilder = new VerifierKnowledgeBaseBuilder();

        verifierKnowledgeBase = verifierKnowledgeBaseBuilder.newVerifierKnowledgeBase( conf );

        if ( verifierKnowledgeBaseBuilder.hasErrors() ) {
            this.errors.addAll( verifierKnowledgeBaseBuilder.getErrors() );
        }
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

    
    
    public void addResourcesToVerify(Resource resource, ResourceType type,
            ResourceConfiguration config) {
        verifierPackageBuilder.addKnowledgeResource( resource,
                type,
                config );

            if ( verifierPackageBuilder.hasErrors() ) {
                addVerifierErrors( verifierPackageBuilder.getErrors() );
            }

            PackageDescr pkg = verifierPackageBuilder.getPackageDescr();
            if ( pkg != null ) {
                addPackageDescr( pkg );
            } else {
                errors.add( new VerifierError( "Verifier could not form a PackageDescr from the resources that it was trying to verify." ) );
            }
        
    }
    
    public void addResourcesToVerify(Resource resource,
                                     ResourceType type) {

        verifierPackageBuilder.addKnowledgeResource( resource,
                                                     type,
                                                     null );

        if ( verifierPackageBuilder.hasErrors() ) {
            addVerifierErrors( verifierPackageBuilder.getErrors() );
        }

        PackageDescr pkg = verifierPackageBuilder.getPackageDescr();
        if ( pkg != null ) {
            addPackageDescr( pkg );

        } else {
            errors.add( new VerifierError( "Verifier could not form a PackageDescr from the resources that it was trying to verify." ) );
        }
    }

    private void addVerifierErrors(PackageBuilderErrors packageBuilderErrors) {
        for ( KnowledgeBuilderError knowledgeBuilderError : packageBuilderErrors ) {
            errors.add( new VerifierError( knowledgeBuilderError.getMessage() ) );
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
