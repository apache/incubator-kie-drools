package org.kie.api.definition.process;

import java.util.Map;

import org.kie.api.definition.KieDefinition;
import org.kie.api.io.Resource;

/**
 * A Process represents one modular piece of business logic that can be executed by
 * a process engine.  Different types of processes may exist.
 *
 */
public interface Process
    extends
    KieDefinition {

    /**
     * The unique id of the Process.
     *
     * @return the id
     */
    String getId();

    /**
     * The name of the Process.
     *
     * @return the name
     */
    String getName();

    /**
     * The version of the Process.
     * You may use your own versioning format
     * (as the version is not interpreted by the engine).
     *
     * @return the version
     */
    String getVersion();

    /**
     * The package name of this process.
     *
     * @return the package name
     */
    String getPackageName();

    /**
     * The type of process.
     * Different types of processes may exist.
     * This defaults to "RuleFlow".
     *
     * @return the type
     */
    String getType();

    /**
     * Meta data associated with this Node.
     */
    Map<String, Object> getMetaData();

    Resource getResource();

    void setResource( Resource res );
}
