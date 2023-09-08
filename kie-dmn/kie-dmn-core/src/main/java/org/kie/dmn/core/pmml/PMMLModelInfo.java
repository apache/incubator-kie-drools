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
package org.kie.dmn.core.pmml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PMMLModelInfo {

    protected final String name;
    protected final String className;
    protected final Collection<String> inputFieldNames;
    protected final Collection<String> outputFieldNames;
    protected final Collection<String> targetFieldNames;

    public PMMLModelInfo(String name, String className, Collection<String> inputFieldNames, Collection<String> targetFieldNames, Collection<String> outputFieldNames) {
        this.name = name;
        this.className = className;
        this.inputFieldNames = Collections.unmodifiableList(new ArrayList<>(inputFieldNames));
        this.targetFieldNames = Collections.unmodifiableList(new ArrayList<>(targetFieldNames));
        this.outputFieldNames = Collections.unmodifiableList(new ArrayList<>(outputFieldNames));
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public Collection<String> getInputFieldNames() {
        return inputFieldNames;
    }

    public Collection<String> getOutputFieldNames() {
        return outputFieldNames;
    }

    public Collection<String> getTargetFieldNames() {
        return targetFieldNames;
    }

}
