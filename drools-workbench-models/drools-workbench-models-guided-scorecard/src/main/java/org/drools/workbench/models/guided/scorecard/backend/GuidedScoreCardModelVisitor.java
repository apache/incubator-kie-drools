/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.guided.scorecard.backend;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.drools.workbench.models.guided.scorecard.shared.Characteristic;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;

/**
 * A ScoreCardModel Visitor to identify fully qualified class names used by the ScoreCardModel
 */
public class GuidedScoreCardModelVisitor {

    private final ScoreCardModel model;
    private final String packageName;
    private final Imports imports;

    public GuidedScoreCardModelVisitor(final ScoreCardModel model) {
        this.model = PortablePreconditions.checkNotNull("model",
                                                        model);
        this.packageName = model.getPackageName();
        this.imports = model.getImports();
    }

    /**
     *
     * @return Imports for classes that share the package with the score card.
     */
    public Set<String> getConsumedPackageImports() {
        //Convert Fact Types into Fully Qualified Class Names
        final Set<String> fullyQualifiedClassNames = new HashSet<String>();
        for (String factType : getFactTypes()) {
            if (factType.contains(".")) {
                fullyQualifiedClassNames.add(convertToFullyQualifiedClassName(factType));
            }
        }

        return fullyQualifiedClassNames;
    }

    /**
     *
     * @return List of all imports used by the score card.
     */
    public Set<String> getConsumedModelClasses() {
        //Convert Fact Types into Fully Qualified Class Names
        final Set<String> fullyQualifiedClassNames = new HashSet<String>();
        for (String factType : getFactTypes()) {
            fullyQualifiedClassNames.add(convertToFullyQualifiedClassName(factType));
        }

        return fullyQualifiedClassNames;
    }

    private Set<String> getFactTypes() {
        final Set<String> factTypes = new HashSet<String>();
        //Extract Fact Types from model
        factTypes.add(model.getFactName());
        for (Characteristic c : model.getCharacteristics()) {
            factTypes.add(c.getFact());
        }
        return factTypes;
    }

    //Get the fully qualified class name of the fact type
    private String convertToFullyQualifiedClassName(final String factType) {
        if (factType.contains(".")) {
            return factType;
        }
        final Optional<String> fullyQualifiedClassName = findFQCNFromImports(factType);
        if (fullyQualifiedClassName.isPresent()) {
            return fullyQualifiedClassName.get();
        } else {
            return packageName + "." + factType;
        }
    }

    private Optional<String> findFQCNFromImports(final String factType) {
        for (Import imp : imports.getImports()) {
            if (imp.getType().endsWith(factType)) {
                return Optional.of(imp.getType());
            }
        }
        return Optional.empty();
    }
}
