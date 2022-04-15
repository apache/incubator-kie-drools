package org.drools.compiler.builder.impl;

import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.ParserError;
import org.drools.core.io.impl.DescrResource;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.api.io.Resource;
import java.io.IOException;

public class ProcessorDrl extends Processor{

    public ProcessorDrl(KnowledgeBuilderConfigurationImpl configuration){
        super(configuration);
    }

    public PackageDescr process(Resource resource) throws DroolsParserException, IOException{
        PackageDescr pkg;
        boolean hasErrors = false;
        if (resource instanceof DescrResource){
            pkg = (PackageDescr) ((DescrResource) resource).getDescr();
        }else{
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
}