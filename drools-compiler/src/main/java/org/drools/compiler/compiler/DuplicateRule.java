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
package org.drools.compiler.compiler;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

public class DuplicateRule extends ConfigurableSeverityResult {
    
    public static final String KEY = "duplicateRule";
    
    private String rule;
    
    private PackageDescr pkgDescr;
    
    private int[] line;
    
    public DuplicateRule(RuleDescr ruleDescr, PackageDescr pkg, KnowledgeBuilderConfiguration config) {
        super(ruleDescr.getResource(), config, getSpecificMessage(ruleDescr, pkg));
        rule = ruleDescr.getName();
        pkgDescr = pkg;
        line = new int[1];
        line[0] = ruleDescr.getLine();
    }

    @Override
    public int[] getLines() {
        return line;
    }

    @Override
    protected String getOptionKey() {
        return KEY;
    }
    private static String getSpecificMessage(RuleDescr ruleDescr, PackageDescr pkg) {
        return "Rule name " + ruleDescr.getName()
        + " already exists in package  " + pkg.getName();
    }

}
