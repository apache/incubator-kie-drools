package org.drools.definition;

import java.util.Collection;

import org.drools.definition.process.Process;
import org.drools.definition.rule.Rule;

/**
 * This provides a collection of knowledge definitions that can be given to a KnowledgeBase.
 * The name is used to provide "namespace" separation of those definitions.
 * 
 *
 */
public interface KnowledgePackage {
    /**
     * The namespace for this package
     * @return
     */
    String getName();

    /**
     * Return the rule definitions for this package.
     * The collection is immutable.
     * 
     * @return
     */
    Collection<Rule> getRules();

    /**
     * Return the process definitions for this package.
     * The collection is immutable.
     * 
     * @return
     */
    Collection<Process> getProcesses();


}
