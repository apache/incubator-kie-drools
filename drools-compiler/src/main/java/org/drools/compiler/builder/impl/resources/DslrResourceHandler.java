package org.drools.compiler.builder.impl.resources;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.drl.parser.lang.dsl.DefaultExpander;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

import java.io.IOException;
import java.util.function.Supplier;

public class DslrResourceHandler extends ResourceHandler {
    private Supplier<DefaultExpander> expander;

    public DslrResourceHandler(KnowledgeBuilderConfigurationImpl configuration, Supplier<DefaultExpander> expander) {
        super(configuration);
        this.expander = expander;
    }

    @Override
    public boolean handles(ResourceType type) {
        return type == ResourceType.DSLR || type == ResourceType.RDSLR;
    }

    @Override
    public PackageDescr process(Resource resource, ResourceConfiguration configuration) throws DroolsParserException, IOException {
        return dslrReaderToPackageDescr(resource, resource.getReader(), expander.get());
    }
}