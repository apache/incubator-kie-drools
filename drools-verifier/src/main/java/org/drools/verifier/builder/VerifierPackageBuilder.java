package org.drools.verifier.builder;

import java.io.Reader;
import java.io.StringReader;

import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.compiler.BusinessRuleProvider;
import org.drools.compiler.BusinessRuleProviderFactory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.PackageBuilderErrors;
import org.drools.ide.common.BusinessRuleProviderDefaultImpl;
import org.drools.io.Resource;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.dsl.DefaultExpander;

/**
 * Wraps the PackageBuilder for Verifier.
 * Used to build PackageDescrs.
 * 
 * @author rikkola
 *
 */
class VerifierPackageBuilder {

    private InnerBuilder innerBuilder = new InnerBuilder();

    private PackageDescr packageDescr;

    public void addKnowledgeResource(Resource resource,
                                     ResourceType type,
                                     ResourceConfiguration configuration) {
        innerBuilder.addKnowledgeResource( resource,
                                           type,
                                           configuration );
    }

    public PackageDescr getPackageDescr() {
        return packageDescr;
    }

    public boolean hasErrors() {
        return innerBuilder.hasErrors();
    }

    public PackageBuilderErrors getErrors() {
        return innerBuilder.getErrors();
    }

    class InnerBuilder extends PackageBuilder {
        public InnerBuilder() {
            super( new PackageBuilderConfiguration() );
        }

        @Override
        public void addPackage(PackageDescr pDescr) {
            packageDescr = pDescr;
        }

        public void addPackageFromBrl(final Resource resource) throws DroolsParserException {

            try {
                BusinessRuleProvider provider = new BusinessRuleProviderDefaultImpl();

                Reader knowledge = provider.getKnowledgeReader( resource );

                DrlParser parser = new DrlParser();
                DefaultExpander expander = getDslExpander();

                if ( null != expander ) {
                    knowledge = new StringReader( expander.expand( knowledge ) );
                    if ( expander.hasErrors() ) {
                        getErrors().addAll( expander.getErrors() );
                    }
                }

                PackageDescr pkg = parser.parse( knowledge );
                if ( parser.hasErrors() ) {
                    getErrors().addAll( parser.getErrors() );
                } else {
                    addPackage( pkg );
                }

            } catch ( Exception e ) {
                throw new DroolsParserException( e );
            }
        }
    }
}