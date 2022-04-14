package org.drools.compiler.builder.impl;

import org.drools.compiler.compiler.DroolsParserException;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.api.io.Resource;
import java.io.IOException;


public class ProcessorDslr extends Processor{

    public ProcessorDslr(KnowledgeBuilderConfigurationImpl configuration){
        super(configuration);
    }

    public PackageDescr process(Resource resource) throws DroolsParserException, IOException {
        return dslrReaderToPackageDescr(resource, resource.getReader());
    }

}
