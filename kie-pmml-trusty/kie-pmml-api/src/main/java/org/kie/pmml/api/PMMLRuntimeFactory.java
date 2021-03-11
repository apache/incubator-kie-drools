/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.api;

import java.io.File;

import org.kie.api.KieBase;
import org.kie.pmml.api.runtime.PMMLRuntime;

public interface PMMLRuntimeFactory {

    /**
     * Retrieve a <code>PMMLRuntime</code> bound to the <code>org.dmg.pmml.PMMLModel</code>
     * that should be present in the given <code><File</code>
     * @param pmmlFile
     * @return
     */
    PMMLRuntime getPMMLRuntimeFromFile(File pmmlFile);

    /**
     * Retrieve a <code>PMMLRuntime</code> bound to the <code>org.dmg.pmml.PMMLModel</code>
     * that should be present in the <code><File</code> with the given <b>pmmlFileName</b>.
     * Such file will be looked for in the classpath
     * (e.g. provided by <b>Maven</b> dependency)
     *
     * @param pmmlFileName
     * @return
     */
    PMMLRuntime getPMMLRuntimeFromClasspath(String pmmlFileName);

    /**
     * Retrieve a <code>PMMLRuntime</code> bound to the <code>org.dmg.pmml.PMMLModel</code>
     * that should be present in the <code><File</code> with the given <b>pmmlFileName</b>.
     * Such file will be looked for in the <code>kjar</code> loaded inside the <code>KieContainer</code>
     * with the given <b>gav</b>
     *
     * @param pmmlFileName
     * @param kieBase the name of the Kiebase configured inside the <b>kmodule.xml</b> of the loaded <b>kjar</b>
     * @param gav
     * @return
     */
    PMMLRuntime getPMMLRuntimeFromKieContainerByKieBase(String kieBase, String pmmlFileName, String gav);

    /**
     * Retrieve a <code>PMMLRuntime</code> bound to the <code>org.dmg.pmml.PMMLModel</code>
     * that should be present in the <code><File</code> with the given <b>pmmlFileName</b>.
     * Such file will be looked for in the <code>kjar</code> loaded inside the <code>KieContainer</code>
     * with the given <b>gav</b>.
     * It will use the <b>default</b> Kiebase defined inside the <b>kmodule.xml</b> of the loaded <b>kjar</b>
     *
     * @param pmmlFileName
     * @param gav
     * @return
     */
    PMMLRuntime getPMMLRuntimeFromKieContainerByDefaultKieBase(String pmmlFileName, String gav);

    /**
     * Retrieve a <code>PMMLRuntime</code> bound to the <code>org.dmg.pmml.PMMLModel</code>
     * with the given <b>pmmlModelName</b> that should be present in the <code><File</code>
     * with the given <b>pmmlFileName</b>.
     * Such file will be looked for in the given <code>KieBase</code>
     *
     * @param pmmlFileName
     * @param pmmlModelName
     * @param kieBase
     * @return
     */
    PMMLRuntime getPMMLRuntimeFromFileNameModelNameAndKieBase(String pmmlFileName, String pmmlModelName, KieBase kieBase);

}
