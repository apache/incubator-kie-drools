package org.drools.pmml_4_0;

import org.drools.compiler.compiler.PMMLCompiler;
import org.drools.compiler.compiler.PackageRegistry;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.io.InputStream;
import java.util.List;
import java.util.Map;


/**
 *  This class mocks the previous pmml_4_0 compiler expected in 5.5Final and used in a few
 *  experimental products.
 */

@Deprecated
public class PMML4Compiler implements PMMLCompiler {

    private PMMLCompiler compiler = new org.drools.pmml.pmml_4_1.PMML4Compiler();

    public String compile( InputStream inputStream, Map<String, PackageRegistry> stringPackageRegistryMap ) {
        return compiler.compile( inputStream, stringPackageRegistryMap );
    }

    public List<KnowledgeBuilderResult> getResults() {
        return compiler.getResults();
    }

    @Override
    public void clearResults() {
        compiler.clearResults();
    }

}
