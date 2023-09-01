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
package org.drools.model.codegen.execmodel;

import java.util.List;
import java.util.stream.Collectors;

import static org.drools.model.codegen.execmodel.PackageModel.DOMAIN_CLASSESS_METADATA_FILE_NAME;

public class PackageModelWriter {

    private final PackageModel packageModel;
    private final List<DeclaredTypeWriter> declaredTypes;
    private final List<AccumulateClassWriter> accumulateClasses;
    private final List<RuleUnitWriter> ruleUnitWriters;
    private final RuleWriter ruleWriter;
    private final DomainClassesMetadata domainClassesMetadata;

    public PackageModelWriter(PackageModel packageModel) {
        this.packageModel = packageModel;
        this.declaredTypes = toDeclaredTypeWriters(packageModel);
        this.accumulateClasses = toAccumulateClassWriters(packageModel);
        PackageModel.RuleSourceResult ruleSourceResult = packageModel.getRulesSource();
        this.ruleWriter = new RuleWriter(packageModel.getRulesFileName(), ruleSourceResult, packageModel);
        this.ruleUnitWriters = toRuleUnitWriters(packageModel, ruleSourceResult);
        this.domainClassesMetadata = new DomainClassesMetadata(packageModel);
    }

    public List<DeclaredTypeWriter> getDeclaredTypes() {
        return declaredTypes;
    }

    public List<AccumulateClassWriter> getAccumulateClasses() {
        return accumulateClasses;
    }

    public List<RuleUnitWriter> getRuleUnitWriters() {
        return ruleUnitWriters;
    }

    public RuleWriter getRules() {
        return ruleWriter;
    }

    public DomainClassesMetadata getDomainClassesMetadata() {
        return domainClassesMetadata;
    }

    private List<AccumulateClassWriter> toAccumulateClassWriters(PackageModel packageModel) {
        return packageModel.getGeneratedAccumulateClasses().stream().map(pojo -> new AccumulateClassWriter(pojo, packageModel)).collect(Collectors.toList());
    }

    private List<RuleUnitWriter> toRuleUnitWriters(PackageModel packageModel, PackageModel.RuleSourceResult ruleSourceResult) {
        return packageModel.getRuleUnits().stream().map(ruleUnitDescr -> new RuleUnitWriter(packageModel, ruleSourceResult, ruleUnitDescr)).collect(Collectors.toList());
    }

    private static List<DeclaredTypeWriter> toDeclaredTypeWriters(PackageModel packageModel) {
        return packageModel.getGeneratedPOJOsSource().stream().map(pojo -> new DeclaredTypeWriter(pojo, packageModel)).collect(Collectors.toList());
    }

    public PackageModel getPackageModel() {
        return packageModel;
    }

    public static class DomainClassesMetadata {

        private final String name, source;

        DomainClassesMetadata(PackageModel packageModel) {
            this(packageModel.getPathName() + "/" + DOMAIN_CLASSESS_METADATA_FILE_NAME + packageModel.getPackageUUID() + ".java",
                    packageModel.getDomainClassesMetadataSource());
        }

        DomainClassesMetadata(String name, String source) {
            this.name = name;
            this.source = source;
        }

        public String getName() {
            return name;
        }

        public String getSource() {
            return source;
        }
    }
}
