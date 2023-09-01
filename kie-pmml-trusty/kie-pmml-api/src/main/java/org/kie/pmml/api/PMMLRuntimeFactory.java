package org.kie.pmml.api;

import java.io.File;

import org.kie.pmml.api.runtime.PMMLRuntime;

public interface PMMLRuntimeFactory {

    /**
     * Retrieve a <code>PMMLRuntime</code> bound to the given <code><File</code>
     * with an <i>on-the-fly</i> compilation.
     * @param pmmlFile
     * @return
     */
    PMMLRuntime getPMMLRuntimeFromFile(File pmmlFile);

    /**
     * Retrieve a <code>PMMLRuntime</code> bound to the given <b>pmmlFileName</b>
     * with an <i>on-the-fly</i> compilation.
     * Such file will be looked for in the classpath
     * (e.g. provided by <b>Maven</b> dependency)
     *
     * @param pmmlFileName
     * @return
     */
    PMMLRuntime getPMMLRuntimeFromClasspath(String pmmlFileName);
}
