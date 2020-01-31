#set( $str = "" )
#set( $dt = $str.getClass().forName("java.util.Date").newInstance() )
#set( $year = $dt.getYear() + 1900 )
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
package org.kie.pmml.runtime.${artifactModelName}.executor;

import java.util.logging.Logger;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.api.model.enums.PMML_MODEL;
import org.kie.pmml.api.model.${artifactModelName}.KiePMML${modelName}Model;
import org.kie.pmml.runtime.api.exceptions.KiePMMLModelException;
import org.kie.pmml.runtime.api.executor.PMMLContext;
import org.kie.pmml.runtime.core.executor.PMMLModelExecutor;


/**
 * Default <code>PMMLModelExecutor</code> for <b>${modelName}</b>
 */
public class PMML${modelName}ModelExecutor implements PMMLModelExecutor {

    private static final Logger log = Logger.getLogger(PMML${modelName}ModelExecutor.class.getName());

     @Override
     public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.${modelNameUppercase}_MODEL;
     }

    @Override
    public PMML4Result evaluate(KiePMMLModel model, PMMLContext context) throws KiePMMLException {
        log.info("evaluate " + model + " " + context);
         if (!(model instanceof KiePMML${modelName}Model)) {
             throw new KiePMMLModelException("Expected a KiePMML${modelName}Model, received a " + model.getClass().getName());
        }
        // TODO
        return null;
     }

}
