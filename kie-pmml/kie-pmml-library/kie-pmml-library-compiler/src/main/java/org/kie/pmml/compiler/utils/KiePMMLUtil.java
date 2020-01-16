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
package org.kie.pmml.compiler.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.dmg.pmml.PMML;
import org.kie.pmml.library.api.enums.PMMLModelType;
import org.xml.sax.SAXException;

import static org.kie.pmml.library.api.enums.PMMLModelType.ASSOCIATION_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.BASELINE_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.BAYESIAN_NETWORK_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.CLUSTERING_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.GAUSSIAN_PROCESS_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.GENERAL_REGRESSION_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.MINING_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.NAIVE_BAYES_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.NEAREST_NEIGHBOR_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.NEURAL_NETWORK_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.REGRESSION_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.RULESET_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.SCORECARD_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.SEQUENCE_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.SUPPORT_VECTOR_MACHINE_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.TEXT_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.TIME_SERIES_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.TREE_MODEL;
import static org.kie.pmml.library.api.enums.PMMLModelType.UNKNOWN;

/**
 * Utility class to decouple <code>PMMLCompilerExecutor</code> from actual marshalling model/implementation.
 * Currently, it directly uses {@link org.jpmml.model.PMMLUtil} and {@link org.dmg.pmml.PMML}
 */
public class KiePMMLUtil {

    /**
     *
     * @param source
     * @return
     * @throws SAXException
     * @throws JAXBException
     */
    public static PMML load(String source) throws SAXException, JAXBException {
        return load(new ByteArrayInputStream(source.getBytes()));
    }

    /**
     * Return a <code>PMMLModelType</code> mapped to a given <b>model</b> name
     *
     * @param modelName
     * @return
     *
     * @see org.dmg.pmml.PMML
     */
    public static PMMLModelType getPMMLModelType(String modelName) {
        switch (modelName) {
            case "AssociationModel":
                return ASSOCIATION_MODEL;
            case "BayesianNetworkModel":
                return BAYESIAN_NETWORK_MODEL;
            case "BaselineModel":
                return BASELINE_MODEL;
            case "ClusteringModel":
                return CLUSTERING_MODEL;
            case "GaussianProcessModel":
                return GAUSSIAN_PROCESS_MODEL;
            case "GeneralRegressionModel":
                return GENERAL_REGRESSION_MODEL;
            case "MiningModel":
                return MINING_MODEL;
            case "NaiveBayesModel":
                return NAIVE_BAYES_MODEL;
            case "NearestNeighborModel":
                return NEAREST_NEIGHBOR_MODEL;
            case "NeuralNetwork":
                return NEURAL_NETWORK_MODEL;
            case "RegressionModel":
                return REGRESSION_MODEL;
            case "RuleSetModel":
                return RULESET_MODEL;
            case "SequenceModel":
                return SEQUENCE_MODEL;
            case "Scorecard":
                return SCORECARD_MODEL;
            case "SupportVectorMachineModel":
                return SUPPORT_VECTOR_MACHINE_MODEL;
            case "TextModel":
                return TEXT_MODEL;
            case "TimeSeriesModel":
                return TIME_SERIES_MODEL;
            case "TreeModel":
                return TREE_MODEL;
            default:
                return UNKNOWN;
        }
    }

    /**
     *
     * @param is
     * @return
     * @throws SAXException
     * @throws JAXBException
     *
     * @see org.jpmml.model.PMMLUtil#unmarshal(InputStream)
     */
    private static PMML load(InputStream is) throws SAXException, JAXBException {
        return org.jpmml.model.PMMLUtil.unmarshal(is);
    }
}
