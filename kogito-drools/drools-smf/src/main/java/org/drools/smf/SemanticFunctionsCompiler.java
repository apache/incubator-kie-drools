package org.drools.smf;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.jci.readers.ResourceReader;
import org.apache.commons.jci.stores.ResourceStore;
import org.drools.spi.Functions;

public interface SemanticFunctionsCompiler
    extends
    SemanticCompiler
{

    public void generate(Functions functions,
                         Set imports,
                         String packageName,
                         String className,
                         String parentClass,
                         ResourceReader resourceReader,
                         Map files) throws IOException;

    public void compile(String fileName,
                        ResourceReader resourceReader,
                        ResourceStore resourceStore,
                        ClassLoader classLoader);

    public void compile(String[] filesNames,
                        ResourceReader resourceReader,
                        ResourceStore resourceStore,
                        ClassLoader classLoader);

}
