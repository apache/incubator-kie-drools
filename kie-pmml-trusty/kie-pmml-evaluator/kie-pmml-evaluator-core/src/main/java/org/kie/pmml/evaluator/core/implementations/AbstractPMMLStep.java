/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.evaluator.core.implementations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kie.pmml.api.models.PMMLStep;

/**
 * Common abstract implementation of <code>PMMLStep</code>
 */
public class AbstractPMMLStep implements PMMLStep {

    private static final long serialVersionUID = -7633308400272166095L;

    private final Map<String, Object> info = new HashMap<>();

    @Override
    public void addInfo(String infoName, Object infoValue) {
        info.put(infoName, infoValue);
    }

    /**
     * Returns an <b>unmodifiable map</b> of <code>info</code>
     * @return
     */
    @Override
    public Map<String, Object> getInfo() {
        return Collections.unmodifiableMap(info);
    }
}
