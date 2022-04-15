package org.drools.compiler.builder.impl;

import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.xml.XmlPackageReader;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.api.io.Resource;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.Reader;

public class ProcessorXml extends Processor {

    public ProcessorXml(KnowledgeBuilderConfigurationImpl configuration){
        super(configuration);
    }

    public PackageDescr process(Resource resource) throws DroolsParserException, IOException {
        final XmlPackageReader xmlReader = new XmlPackageReader(this.configuration.getSemanticModules());
        xmlReader.getParser().setClassLoader(this.configuration.getClassLoader()); //(this.rootClassLoader);

        try (Reader reader = resource.getReader()) {
            xmlReader.read(reader);
        } catch (final SAXException e) {
            throw new DroolsParserException(e.toString(),
                    e.getCause());
        }
        return xmlReader.getPackageDescr();
    }

}