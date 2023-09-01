package org.kie.api.fluent;

/**
 * Contains common operations for all nodes, basically naming, metadata and definition completion.
 * 
 * @param <T> concrete node instance
 * @param <P> container parent node
 */
public interface NodeBuilder<T extends NodeBuilder<T, P>, P extends NodeContainerBuilder<P, ?>> {

    /** 
     * Method to notify that definition of this node is done
     * @return container parent node 
    */
    P done();

    T name(String name);

    T setMetadata(String key, Object value);
}
