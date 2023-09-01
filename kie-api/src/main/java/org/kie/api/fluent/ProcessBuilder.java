package org.kie.api.fluent;

import java.util.Collection;
import java.util.Map;

import org.kie.api.definition.process.Process;

/**
 * Builder that contains methods to create a process definition. 
 * @see ProcessBuilderFactory
 */
public interface ProcessBuilder extends NodeContainerBuilder<ProcessBuilder, ProcessBuilder> {

    ProcessBuilder dynamic(boolean dynamic);

    ProcessBuilder version(String version);

    ProcessBuilder packageName(String packageName);

    ProcessBuilder imports(Collection<String> imports);

    ProcessBuilder functionImports(Collection<String> functionImports);

    ProcessBuilder globals(Map<String, String> globals);

    ProcessBuilder global(String name, String type);
    
    ProcessBuilder swimlane(String name);

    /**
     * Validates and returns process definition.<br>
     * Should be invoked after all other method calls.
     * @return validated process definition
     */
    Process build();
}
