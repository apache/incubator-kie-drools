/*
 * Copyright ${year} Red Hat, Inc. and/or its affiliates.
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
package  org.kie.pmml.models.naive_bayes.compiler.factories;

import java.util.Map;

import org.kie.pmml.models.naive_bayes.compiler.NaiveBayesCompilationDTO;
import org.kie.pmml.models.naive_bayes.model.KiePMMLNaiveBayesModel;

public class KiePMMLNaiveBayesModelFactory {

    private KiePMMLNaiveBayesModelFactory(){
        // Avoid instantiation
    }

    public static KiePMMLNaiveBayesModel getKiePMMLNaiveBayesModel(final NaiveBayesCompilationDTO compilationDTO) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public static Map<String, String> getKiePMMLNaiveBayesModelSourcesMap(final NaiveBayesCompilationDTO compilationDTO) {
        // TODO
        throw new UnsupportedOperationException();
    }


}
