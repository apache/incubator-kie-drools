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

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.kie.pmml.api.enums.PMMLAlgorithm;
import org.kie.pmml.api.implementations.AlgorithmImplementationProviderFinder;
import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.marshaller.executor.PMMLMarshallerExecutor;
import org.kie.pmml.marshaller.model.CommonPMMLModel;

/**
 * PMMLCompilerExecutor default implementation
 */
public class PMMLCompilerExecutorImpl implements PMMLCompilerExecutor {

    private static final Logger log = Logger.getLogger(PMMLCompilerExecutorImpl.class.getName());

    private PMMLMarshallerExecutor pmmlMarshallerExecutor;
    private AlgorithmImplementationProviderFinder algorithmImplementationProviderFinder;

    public PMMLCompilerExecutorImpl(PMMLMarshallerExecutor pmmlMarshallerExecutor, AlgorithmImplementationProviderFinder algorithmImplementationProviderFinder) {
        this.pmmlMarshallerExecutor = pmmlMarshallerExecutor;
        this.algorithmImplementationProviderFinder = algorithmImplementationProviderFinder;
    }

    @Override
    public List<KiePMMLModel> getResults(String source) {
        log.info("getResults " + source);
        CommonPMMLModel commonPMMLModel = pmmlMarshallerExecutor.parse(source);
        return getResults(commonPMMLModel);
    }

    /**
     * Read the given <code>CommonPMMLModel</code> to returns a <code>List&lt;KiePMMLModel&gt;</code>
     * @param commonPMMLModel
     * @return
     */
    private List<KiePMMLModel> getResults(CommonPMMLModel commonPMMLModel) {
        log.info("getResults " + commonPMMLModel);
        return commonPMMLModel
                .getNodes()
                .stream()
                .map(this::getFromNode)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Read the given <b>node</b> to returns a <code>Optional&lt;KiePMMLModel&gt;</code>
     * @param node
     * @return
     */
    private Optional<KiePMMLModel> getFromNode(Object node) {
        log.info("getFromNode " + node);
        final PMMLAlgorithm algorithm = retrievePMMLAlgorithm(node);
        log.info("algorithm " + algorithm);
        return algorithmImplementationProviderFinder.getImplementations(false)
                .stream()
                .filter(implementation -> algorithm.equals(implementation.getPMMLAlgorithm()))
                .map(implementation -> implementation.getKiePMMLModel(node))
                .findFirst();

    }

    /**
     * Returns the <code>PMMLAlgorithm</code> of a specific <b>node</b>
     * @param node
     * @return
     */
    private PMMLAlgorithm retrievePMMLAlgorithm(Object node) {
        // TODO @gcardosi
        return PMMLAlgorithm.REGRESSION;
    }
}
