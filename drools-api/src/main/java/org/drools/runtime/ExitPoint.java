package org.drools.runtime;

/**
 * <p>
 * An exit point provides a mechanism to send objects from the working memory to some external process 
 * or function.  For instance, an exit point can be used to inform some piece of code that an object 
 * matches a rule.  
 * </p>
 *
 * <p>
 * To create an exit point, implement the interface and register it with the KnowledgeRuntime:
 * </p>
 * <pre>
 * ...
 * ksession.registerExitPoint("my-exit-point", new MyExitPointImpl());
 * </pre>
 * 
 * <p>
 * Exit points are invoked from the consequence side of a rule:
 * </p>
 * <pre>
 * when
 *   ...
 * then
 *   exitPoint["my-exit-point"].insert(...);
 * </pre>
 */

public interface ExitPoint {
    void insert(Object object);
}
