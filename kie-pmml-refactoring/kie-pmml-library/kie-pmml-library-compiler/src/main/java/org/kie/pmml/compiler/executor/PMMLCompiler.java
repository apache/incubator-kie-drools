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

import org.kie.pmml.api.model.KiePMMLModel;
/**
 * Actual implementations are required to convert a <b>PMML</b> xml to
 * to a <code>List&lt;KiePMMLModel&gt;</code>
 */
public interface PMMLCompiler {

    /**
     * Read the given <code>InputStream</code> to return a <code>List&lt;KiePMMLModel&gt;</code>
     * @param inputStream
     * @param kbuilder Using <code>Object</code> to avoid coupling with drools
     * @return
     */
    List<KiePMMLModel> getResults(InputStream inputStream, Object kbuilder) throws Exception;

}
