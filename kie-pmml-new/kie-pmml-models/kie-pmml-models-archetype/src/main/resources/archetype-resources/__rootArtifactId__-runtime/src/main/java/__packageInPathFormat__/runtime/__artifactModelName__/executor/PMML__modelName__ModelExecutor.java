#set($str="")
        #set($dt=$str.getClass().forName("java.util.Date").newInstance())
        #set($year=$dt.getYear()+1900)
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
        {artifactModelName}.KiePMML${modelName}Model;

/**
 * Default <code>PMMLModelExecutor</code> for <b>${modelName}</b>
 */
public class PMML$ {

    modelName
}ModelExecutor implements PMMLModelExecutor{

private static final Logger log=Logger.getLogger(PMML${modelName}ModelExecutor.class.getName());

@Override
public PMML_MODEL getPMMLModelType(){
        return PMML_MODEL.${modelNameUppercase}_MODEL;
        }

@Override
public PMML4Result evaluate(KiePMMLModel model,PMMLContext context)throws KiePMMLException{
        log.info("evaluate "+model+" "+context);
        if(!(model instanceof KiePMML${modelName}Model)){
        throw new KiePMMLModelException("Expected a KiePMML${modelName}Model, received a "+model.getClass().getName());
        }
        // TODO
        return null;
        }

        }
