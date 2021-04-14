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
package org.kie.pmml.compiler.executor;

import java.io.InputStream;
import java.util.List;

import org.kie.pmml.api.exceptions.ExternalException;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.commons.model.KiePMMLModel;

/**
 * Actual implementations are required to convert a <b>PMML</b> xml to
 * to a <code>List&lt;KiePMMLModel&gt;</code>
 */
public interface PMMLCompiler {

    /**
     * Read the given <code>InputStream</code> to return a <code>List&lt;KiePMMLModel&gt;</code>
     * @param packageName the package into which put all the generated classes out of the given <code>InputStream</code>
     * @param inputStream
     * @param fileName
     * @param hasClassloader Using <code>HasClassloader</code> to avoid coupling with drools
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     * @throws ExternalException if any other kind of <code>Exception</code> has been thrown during execution
     */
    List<KiePMMLModel> getKiePMMLModels(final String packageName, final InputStream inputStream, final String fileName, final HasClassLoader hasClassloader);

    /**
     * Read the given <code>InputStream</code> to return a <code>List&lt;KiePMMLModel&gt;</code> following a
     * <b>kie-maven-plugin</b> invocation
     * @param factoryClassName the name of the <b>Factory</b> class to generate, containing the generated <code>KiePmmlModel</code>s
     * @param packageName the package into which put all the generated classes out of the given <code>InputStream</code>
     * @param inputStream
     * @param fileName
     * @param hasClassloader Using <code>HasClassloader</code> to avoid coupling with drools
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     * @throws ExternalException if any other kind of <code>Exception</code> has been thrown during execution
     */
    List<KiePMMLModel> getKiePMMLModelsWithSources(final String factoryClassName, final String packageName, final InputStream inputStream, final String fileName, final HasClassLoader hasClassloader);
}
