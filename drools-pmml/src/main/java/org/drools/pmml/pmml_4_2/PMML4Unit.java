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
package org.drools.pmml.pmml_4_2;

import java.util.List;
import java.util.Map;

import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.drools.pmml.pmml_4_2.model.PMML4ModelType;
import org.drools.pmml.pmml_4_2.model.PMMLDataField;

public interface PMML4Unit {
    public PMML getRawPMML();
    public List<PMML4Model> getModels();
    public List<PMMLDataField> getDataDictionaryFields();
    public Map<String,PMML4Model> getRootModels();
    public <T extends PMML4Model> Map<String,T> getModels(PMML4ModelType modelTypeFilter, PMML4Model parent);
    public boolean containsMiningModel();
    public Map<String, PMMLDataField> getDataDictionaryMap();
}
