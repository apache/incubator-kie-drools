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
package org.kie.pmml.compiler.testingutils;

import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.MathContext;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.Model;
import org.dmg.pmml.Visitor;
import org.dmg.pmml.VisitorAction;

public class TestingModel extends Model {

    private static final long serialVersionUID = 2465202344031699917L;

    private String modelName;

    @Override
    public MiningFunction getMiningFunction() {
        return null;
    }

    @Override
    public Model setMiningFunction(MiningFunction miningFunction) {
        return null;
    }

    @Override
    public String getAlgorithmName() {
        return null;
    }

    @Override
    public Model setAlgorithmName(String algorithmName) {
        return null;
    }

    @Override
    public boolean isScorable() {
        return false;
    }

    @Override
    public Model setScorable(Boolean scorable) {
        return null;
    }

    @Override
    public MathContext getMathContext() {
        return null;
    }

    @Override
    public Model setMathContext(MathContext mathContext) {
        return null;
    }

    @Override
    public MiningSchema getMiningSchema() {
        return null;
    }

    @Override
    public Model setMiningSchema(MiningSchema miningSchema) {
        return null;
    }

    @Override
    public LocalTransformations getLocalTransformations() {
        return null;
    }

    @Override
    public Model setLocalTransformations(LocalTransformations localTransformations) {
        return null;
    }

    @Override
    public VisitorAction accept(Visitor visitor) {
        return null;
    }

    @Override
    public Model setModelName(String modelName) {
        this.modelName = modelName;
        return this;
    }

    public String getModelName() {
        return modelName;
    }

    @Override
    public MiningFunction requireMiningFunction() {
        return null;
    }

    @Override
    public MiningSchema requireMiningSchema() {
        return null;
    }


}
