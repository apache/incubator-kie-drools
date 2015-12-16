/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.datamodel.oracle;

import java.util.List;
import java.util.Map;

import org.drools.workbench.models.datamodel.rule.DSLSentence;

public interface PackageDataModelOracle extends ProjectDataModelOracle {

    void setPackageName( String packageName );

    void addPackageWorkbenchEnumDefinitions( Map<String, String[]> workbenchEnumDefinitions );

    void addPackageDslConditionSentences( List<DSLSentence> dslConditionSentences );

    void addPackageDslActionSentences( List<DSLSentence> dslActionSentences );

    void addPackageGlobals( Map<String, String> globalss );

    String getPackageName();

    Map<String, String[]> getPackageWorkbenchDefinitions();

    List<DSLSentence> getPackageDslConditionSentences();

    List<DSLSentence> getPackageDslActionSentences();

    Map<String, String> getPackageGlobals();

}
