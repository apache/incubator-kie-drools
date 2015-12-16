/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.models.commons.backend.oracle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.rule.DSLSentence;

/**
 * Default implementation of DataModelOracle
 */
public class PackageDataModelOracleImpl extends ProjectDataModelOracleImpl implements PackageDataModelOracle {

    //Package for which this DMO relates
    private String packageName = "";

    // Package-level enumeration definitions derived from "Workbench" enumerations.
    private Map<String, String[]> packageWorkbenchEnumDefinitions = new HashMap<String, String[]>();

    // Package-level DSL language extensions.
    private List<DSLSentence> packageDSLConditionSentences = new ArrayList<DSLSentence>();
    private List<DSLSentence> packageDSLActionSentences = new ArrayList<DSLSentence>();

    // Package-level map of Globals {alias, class name}.
    private Map<String, String> packageGlobalTypes = new HashMap<String, String>();

    @Override
    public void setPackageName( final String packageName ) {
        this.packageName = packageName;
    }

    @Override
    public void addPackageWorkbenchEnumDefinitions( final Map<String, String[]> dataEnumLists ) {
        this.packageWorkbenchEnumDefinitions.putAll( dataEnumLists );
    }

    @Override
    public void addPackageDslConditionSentences( final List<DSLSentence> dslConditionSentences ) {
        this.packageDSLConditionSentences.addAll( dslConditionSentences );
    }

    @Override
    public void addPackageDslActionSentences( final List<DSLSentence> dslActionSentences ) {
        this.packageDSLActionSentences.addAll( dslActionSentences );
    }

    @Override
    public void addPackageGlobals( final Map<String, String> packageGlobalTypes ) {
        this.packageGlobalTypes.putAll( packageGlobalTypes );
    }

    @Override
    public String getPackageName() {
        return this.packageName;
    }

    @Override
    public Map<String, String[]> getPackageWorkbenchDefinitions() {
        return this.packageWorkbenchEnumDefinitions;
    }

    @Override
    public List<DSLSentence> getPackageDslConditionSentences() {
        return this.packageDSLConditionSentences;
    }

    @Override
    public List<DSLSentence> getPackageDslActionSentences() {
        return this.packageDSLActionSentences;
    }

    @Override
    public Map<String, String> getPackageGlobals() {
        return this.packageGlobalTypes;
    }
}

