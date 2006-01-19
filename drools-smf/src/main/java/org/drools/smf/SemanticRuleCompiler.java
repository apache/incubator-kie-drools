package org.drools.smf;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.jci.readers.ResourceReader;
import org.apache.commons.jci.stores.ResourceStore;
import org.drools.rule.Declaration;
import org.drools.spi.RuleComponent;

public interface SemanticRuleCompiler
    extends
    SemanticCompiler
{
    public void generate(RuleComponent[] components,
                         Declaration[] declarations,
                         Set imports,
                         Map applicationData,
                         String packageName,
                         String className,
                         String lastClassName,
                         String knowledgeHelper,
                         ResourceReader resourceReader,
                         Map files)  throws IOException;

    public void compile(String fileName,
                        ResourceReader resourceReader,
                        ResourceStore resourceStore,
                        ClassLoader classLoader);
    
    public void compile(String[] filesNames,
                        ResourceReader resourceReader,
                        ResourceStore resourceStore,
                        ClassLoader classLoader);
}
