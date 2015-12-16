/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.guided.dtree.shared.model;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.datamodel.imports.HasImports;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.packages.HasPackageName;
import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.models.guided.dtree.shared.model.parser.GuidedDecisionTreeParserError;

public class GuidedDecisionTree implements HasImports,
                                           HasPackageName {

    private Imports imports = new Imports();
    private String packageName;
    private String treeName;
    private TypeNode root;

    private List<GuidedDecisionTreeParserError> errors = new ArrayList<GuidedDecisionTreeParserError>();

    @Override
    public Imports getImports() {
        return this.imports;
    }

    @Override
    public void setImports( final Imports imports ) {
        this.imports = PortablePreconditions.checkNotNull( "imports",
                                                           imports );
    }

    @Override
    public String getPackageName() {
        return this.packageName;
    }

    @Override
    public void setPackageName( final String packageName ) {
        this.packageName = PortablePreconditions.checkNotNull( "packageName",
                                                               packageName );
    }

    public String getTreeName() {
        return this.treeName;
    }

    public void setTreeName( final String treeName ) {
        this.treeName = PortablePreconditions.checkNotNull( "treeName",
                                                            treeName );
    }

    public TypeNode getRoot() {
        return this.root;
    }

    public void setRoot( final TypeNode root ) {
        this.root = root;
    }

    public List<GuidedDecisionTreeParserError> getParserErrors() {
        return errors;
    }
}
