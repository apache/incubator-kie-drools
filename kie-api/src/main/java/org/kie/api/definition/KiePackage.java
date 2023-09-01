package org.kie.api.definition;

import java.util.Collection;

import org.kie.api.definition.process.Process;
import org.kie.api.definition.rule.Global;
import org.kie.api.definition.rule.Query;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;

/**
 * This provides a collection of knowledge definitions that can be given to a {@link org.kie.api.KieBase}.
 * The name is used to provide "namespace" separation of those definitions.
 */
public interface KiePackage {

    /**
     * @return namespace for this package
     */
    String getName();

    /**
     * @return immutable collection of rule definitions for this package.
     */
    Collection<Rule> getRules();

    /**
     * Return the process definitions for this package.
     * The collection is immutable.
     *
     * @return a Collection of Processes for this package.
     */
    Collection<Process> getProcesses();

    /**
     * Return the fact types declared in this package
     * The collection is immutable.
     *
     * @return a Collection of FactType for this package
     */
    Collection<FactType> getFactTypes();

    /**
     * Return the query definitions for this package.
     * The collection is immutable.
     *
     * @return a Collection of Query for this package
     */
    Collection<Query> getQueries();

    /**
     * Return the names of the functions defined in this package.
     * The collection is immutable.
     *
     * @return a Collection of Function names for this package
     */
    Collection<String> getFunctionNames();

    /**
     * Return the names of the globals defined in this package.
     * The collection is immutable.
     *
     * @return a Collection of Global names for this package
     */
    Collection<Global> getGlobalVariables();

}
