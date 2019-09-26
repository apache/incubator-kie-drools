/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder;

import java.util.List;
import java.util.stream.Collectors;

import static org.drools.modelcompiler.builder.PackageModel.DOMAIN_CLASSESS_METADATA_FILE_NAME;

public class PackageModelWriter {

    private final PackageModel packageModel;
    private final List<DeclaredTypeWriter> declaredTypes;
    private final List<AccumulateClassWriter> accumulateClasses;
    private final RuleWriter ruleWriter;
    private final DomainClassesMetadata domainClassesMetadata;

    public PackageModelWriter(PackageModel packageModel, boolean oneClassPerRule) {
        this.packageModel = packageModel;
        this.declaredTypes = toDeclaredTypeWriters(packageModel);
        this.accumulateClasses = toAccumulateClassWriters(packageModel);
        this.ruleWriter = new RuleWriter(packageModel.getRulesFileName(), packageModel.getRulesSource(oneClassPerRule), packageModel);
        this.domainClassesMetadata = new DomainClassesMetadata(packageModel);
    }

    public List<DeclaredTypeWriter> getDeclaredTypes() {
        return declaredTypes;
    }

    public List<AccumulateClassWriter> getAccumulateClasses() {
        return accumulateClasses;
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

    private static List<DeclaredTypeWriter> toDeclaredTypeWriters(PackageModel packageModel) {
        return packageModel.getGeneratedPOJOsSource().stream().map(pojo -> new DeclaredTypeWriter(pojo, packageModel)).collect(Collectors.toList());
    }

    static class DomainClassesMetadata {

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
