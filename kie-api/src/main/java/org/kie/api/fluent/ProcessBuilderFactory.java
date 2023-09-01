package org.kie.api.fluent;

import org.kie.api.definition.process.Process;

/**
 * Factory to create process builder instance.<br>
 * It is also a convenient place holder for additional utility methods
 */
public interface ProcessBuilderFactory {

    /**
     * Returns a <code>ProcessBuilder</code> that can be used to create a process definition
     * @param processId the unique id of the process being defined
     * @return builder instance to create process definition
     */
    ProcessBuilder processBuilder(String processId);

    /**
     * Converts process definition into an array of bytes.<br>
     * Typically this array will be converted to a Resource for a KIE base
     * @param builder process definition to be converted to byte[]
     * @return byte[] containing process definition
     */
    byte[] toBytes(Process builder);
}
