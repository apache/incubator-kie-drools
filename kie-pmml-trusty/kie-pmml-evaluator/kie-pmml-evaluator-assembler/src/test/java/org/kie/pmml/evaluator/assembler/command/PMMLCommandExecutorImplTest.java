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

package org.kie.pmml.evaluator.assembler.command;

import org.junit.Test;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.junit.Assert.*;

public class PMMLCommandExecutorImplTest {

    @Test(expected = KiePMMLException.class)
    public void validateNoSource() {
        PMMLRequestData pmmlRequestData = new PMMLRequestData();
        pmmlRequestData.setModelName("modelName");
        PMMLCommandExecutorImpl cmdExecutor = new PMMLCommandExecutorImpl();
        cmdExecutor.validate(pmmlRequestData);
    }

    @Test(expected = KiePMMLException.class)
    public void validateEmptySource() {
        PMMLRequestData pmmlRequestData = new PMMLRequestData();
        pmmlRequestData.setModelName("modelName");
        pmmlRequestData.setSource("");
        PMMLCommandExecutorImpl cmdExecutor = new PMMLCommandExecutorImpl();
        cmdExecutor.validate(pmmlRequestData);
    }

    @Test(expected = KiePMMLException.class)
    public void validateNoModelName() {
        PMMLRequestData pmmlRequestData = new PMMLRequestData();
        pmmlRequestData.setSource("source");
        PMMLCommandExecutorImpl cmdExecutor = new PMMLCommandExecutorImpl();
        cmdExecutor.validate(pmmlRequestData);
    }

    @Test(expected = KiePMMLException.class)
    public void validateEmptyModelName() {
        PMMLRequestData pmmlRequestData = new PMMLRequestData();
        pmmlRequestData.setSource("source");
        pmmlRequestData.setModelName("");
        PMMLCommandExecutorImpl cmdExecutor = new PMMLCommandExecutorImpl();
        cmdExecutor.validate(pmmlRequestData);
    }
}