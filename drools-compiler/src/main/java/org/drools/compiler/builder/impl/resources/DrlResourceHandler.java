package org.drools.compiler.builder.impl.resources;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.io.impl.DescrResource;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.drools.drl.parser.DroolsParserException;
import org.drools.drl.parser.ParserError;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Translates a Resource into a PackageDescr
 */
public class DrlResourceHandler {
    private final KnowledgeBuilderConfigurationImpl configuration;
    private final Collection<KnowledgeBuilderResult> results = new ArrayList<>();

    public DrlResourceHandler(KnowledgeBuilderConfigurationImpl configuration) {
        this.configuration = configuration;
    }

    public PackageDescr process(Resource resource) throws DroolsParserException,
            IOException {
        PackageDescr pkg;
        boolean hasErrors = false;
        if (resource instanceof DescrResource) {
            pkg = (PackageDescr) ((DescrResource) resource).getDescr();
        } else {
            final DrlParser parser = new DrlParser(configuration.getLanguageLevel());
            pkg = parser.parse(resource);
            this.results.addAll(parser.getErrors());
            if (pkg == null) {
                this.results.add(new ParserError(resource, "Parser returned a null Package", 0, 0));
            }
            hasErrors = parser.hasErrors();
        }
        if (pkg != null) {
            pkg.setResource(resource);
        }
        return hasErrors ? null : pkg;
    }

    public Collection<KnowledgeBuilderResult> getResults() {
        return results;
    }
}
