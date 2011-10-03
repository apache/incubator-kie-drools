/*
* Copyright 2011 JBoss Inc
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

package org.drools.compiler;

import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.lang.descr.PackageDescr;

public class DuplicateRule extends ConfigurableSeverityResult {
    
    public static final String KEY = "duplicateRule";
    
    private String rule;
    
    private PackageDescr pkgDescr;
    
    public DuplicateRule(String ruleName, PackageDescr pkg, KnowledgeBuilderConfiguration config) {
        super(config);
        rule = ruleName;
        pkgDescr = pkg;
    }

	@Override
	public String getMessage() {
		return "Rule name " + rule 
        + " already exists in package  " + pkgDescr.getName();
	}

	@Override
	public int[] getLines() {
		return null;
	}

    @Override
    String getOptionKey() {
        return KEY;
    }

}
