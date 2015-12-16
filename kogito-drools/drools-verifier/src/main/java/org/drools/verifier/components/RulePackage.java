/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.verifier.data.VerifierComponent;

public class RulePackage extends VerifierComponent<PackageDescr> {

    private int                       offset    = 0;
    private String                    name;
    private Set<VerifierRule>         rules     = new HashSet<VerifierRule>();
    private List<String>              globals   = new ArrayList<String>();
    private String                    description;
    private List<String>              metadata  = new ArrayList<String>();
    private Map<String, List<String>> otherInfo = new HashMap<String, List<String>>();

    
    public RulePackage(PackageDescr descr) {
       super(descr);
    }
    public int getOffset() {
        offset++;
        return offset % 2;
    }

    @Override
    public String getPath() {
        return String.format( "package[@name=%s]",
                              getName() );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<VerifierRule> getRules() {
        return rules;
    }

    public void setRules(Set<VerifierRule> rules) {
        this.rules = rules;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.RULE_PACKAGE;
    }

    public List<String> getGlobals() {
        return globals;
    }

    public List<String> getMetadata() {
        return metadata;
    }

    public Map<String, List<String>> getOtherInfo() {
        return otherInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
