#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
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
package  ${package}.${packageModelName}.model;

import java.util.Collections;
import java.util.Map;

import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.commons.model.KiePMMLModel;

public class KiePMML${modelName}Model extends KiePMMLModel {


    public KiePMML${modelName}Model(String modelName) {
        super(modelName, Collections.emptyList());
    }

    @Override
    public Object evaluate(final Object knowledgeBase, final Map<String, Object> requestData,
                            final PMMLContext context) {
        // TODO
        throw new UnsupportedOperationException();
    }

}
