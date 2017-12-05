/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.pmml_4_2;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.pmml_4_2.descr.DataDictionary;
import org.dmg.pmml.pmml_4_2.descr.MiningField;
import org.dmg.pmml.pmml_4_2.descr.MiningSchema;
import org.dmg.pmml.pmml_4_2.descr.Output;
import org.dmg.pmml.pmml_4_2.descr.OutputField;
import org.kie.pmml.pmml_4_2.model.PMML4ModelType;
import org.kie.pmml.pmml_4_2.model.PMMLMiningField;
import org.kie.pmml.pmml_4_2.model.PMMLOutputField;

public interface PMML4Model {
    public String getModelId();
    public PMML4ModelType getModelType();
    public PMML4Model getParentModel();
    public void setParentModel(PMML4Model parentModel);
    public Map<String,PMML4Model> getChildModels();
    public List<MiningField> getRawMiningFields();
    public List<OutputField> getRawOutputFields();
    public List<PMMLMiningField> getMiningFields();
    public List<PMMLOutputField> getOutputFields();
    public Map.Entry<String, String> getMappedMiningPojo();
    public Map.Entry<String, String> getMappedOutputPojo();
    public String getMiningPojoClassName();
    public String getOutputPojoClassName();
    public PMML4Unit getOwner();
    public MiningSchema getMiningSchema();
    public Output getOutput();
    public DataDictionary getDataDictionary();
    public Serializable getRawModel();
}
