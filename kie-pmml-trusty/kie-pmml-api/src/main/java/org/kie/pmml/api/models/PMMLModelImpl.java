/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.api.models;

import java.util.List;

/**
 * User-friendly representation of a <b>PMML</b> model
 */
public class PMMLModelImpl implements PMMLModel {

    private final String fileName;
    private final String name;
    private final List<MiningField> miningFields;
    private final List<OutputField> outputFields;

    public PMMLModelImpl(String fileName, String name, List<MiningField> miningFields, List<OutputField> outputFields) {
        this.fileName = fileName;
        this.name = name;
        this.miningFields = miningFields;
        this.outputFields = outputFields;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<MiningField> getMiningFields() {
        return miningFields;
    }

    @Override
    public List<OutputField> getOutputFields() {
        return outputFields;
    }
}
