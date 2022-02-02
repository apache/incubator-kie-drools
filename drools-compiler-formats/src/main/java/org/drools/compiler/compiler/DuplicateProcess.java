/*
* Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.compiler;

import org.kie.api.definition.process.Process;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

public class DuplicateProcess extends ConfigurableSeverityResult {
    
    public static final String KEY = "duplicateProcess";
    private static final int[] line = new int[0];
    
    private String processId;
    
    public DuplicateProcess(Process process, KnowledgeBuilderConfiguration config) {
        super(process.getResource(), config);
        processId = process.getId();
    }

	@Override
	public String getMessage() {
		return "Process with same id already exists: " + processId;
	}

	@Override
	public int[] getLines() {
		return line;
	}

    @Override
    String getOptionKey() {
        return KEY;
    }

}
