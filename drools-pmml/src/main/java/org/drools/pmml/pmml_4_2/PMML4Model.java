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

import org.dmg.pmml.pmml_4_2.descr.DataField;
import org.dmg.pmml.pmml_4_2.descr.MiningField;
import org.dmg.pmml.pmml_4_2.descr.MiningSchema;
import org.dmg.pmml.pmml_4_2.descr.Output;
import org.dmg.pmml.pmml_4_2.descr.OutputField;
import org.drools.pmml.pmml_4_2.model.PMML4ModelType;
import org.drools.pmml.pmml_4_2.model.PMMLDataField;

public interface PMML4Model {
    public String getModelId();
    public PMML4ModelType getModelType();
    public List<MiningField> getRawMiningFields();
    public List<OutputField> getRawOutputFields();
    public List<PMMLDataField> getMiningFields();
    public List<PMMLDataField> getOutputFields();
    public PMML4Unit getOwner();
    public MiningSchema getMiningSchema();
    public Output getOutput();
}
