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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.datamodel.oracle.Annotation;
import org.drools.workbench.models.datamodel.oracle.MethodInfo;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.TypeSource;

/**
 * Default implementation of DataModelOracle
 */
public class ProjectDataModelOracleImpl implements ProjectDataModelOracle {

    //Project name
    protected String projectName;

    //Fact Types and their corresponding fields
    protected Map<String, ModelField[]> projectModelFields = new HashMap<String, ModelField[]>();

    //Map of the field that contains the parametrized type of a collection
    //for example given "List<String> name", key = "name" value = "String"
    protected Map<String, String> projectFieldParametersType = new HashMap<String, String>();

    //Map {factType, isEvent} to determine which Fact Type can be treated as events.
    protected Map<String, Boolean> projectEventTypes = new HashMap<String, Boolean>();

    //Map {factType, TypeSource} to determine where a Fact Type as defined.
    protected Map<String, TypeSource> projectTypeSources = new HashMap<String, TypeSource>();

    //Map {factType, superType} to determine the Super Type of a FactType.
    protected Map<String, List<String>> projectSuperTypes = new HashMap<String, List<String>>();

    //Map {factType, Set<Annotation>} containing the FactType's annotations.
    protected Map<String, Set<Annotation>> projectTypeAnnotations = new HashMap<String, Set<Annotation>>();

    //Map {factType, Map<fieldName, Set<Annotation>>} containing the FactType's Field annotations.
    protected Map<String, Map<String, Set<Annotation>>> projectTypeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();

    // Scoped (current package and imports) map of { TypeName.field : String[] } - where a list is valid values to display in a drop down for a given Type.field combination.
    protected Map<String, String[]> projectJavaEnumDefinitions = new HashMap<String, String[]>();

    //Method information used (exclusively) by ExpressionWidget and ActionCallMethodWidget
    protected Map<String, List<MethodInfo>> projectMethodInformation = new HashMap<String, List<MethodInfo>>();

    // A map of FactTypes {factType, isCollection} to determine which Fact Types are Collections.
    protected Map<String, Boolean> projectCollectionTypes = new HashMap<String, Boolean>();

    // List of available package names
    private List<String> projectPackageNames = new ArrayList<String>();

    @Override
    public void setProjectName( final String projectName ) {
        this.projectName = projectName;
    }

    @Override
    public void addProjectModelFields( final Map<String, ModelField[]> modelFields ) {
        this.projectModelFields.putAll( modelFields );
    }

    @Override
    public void addProjectFieldParametersType( final Map<String, String> fieldParametersType ) {
        this.projectFieldParametersType.putAll( fieldParametersType );
    }

    @Override
    public void addProjectEventTypes( final Map<String, Boolean> eventTypes ) {
        this.projectEventTypes.putAll( eventTypes );
    }

    @Override
    public void addProjectTypeSources( final Map<String, TypeSource> typeSources ) {
        this.projectTypeSources.putAll( typeSources );
    }

    @Override
    public void addProjectSuperTypes( final Map<String, List<String>> superTypes ) {
        this.projectSuperTypes.putAll( superTypes );
    }

    @Override
    public void addProjectTypeAnnotations( final Map<String, Set<Annotation>> annotations ) {
        this.projectTypeAnnotations.putAll( annotations );
    }

    @Override
    public void addProjectTypeFieldsAnnotations( final Map<String, Map<String, Set<Annotation>>> typeFieldsAnnotations ) {
        this.projectTypeFieldsAnnotations.putAll( typeFieldsAnnotations );
    }

    @Override
    public void addProjectJavaEnumDefinitions( final Map<String, String[]> dataEnumLists ) {
        this.projectJavaEnumDefinitions.putAll( dataEnumLists );
    }

    @Override
    public void addProjectMethodInformation( final Map<String, List<MethodInfo>> methodInformation ) {
        this.projectMethodInformation.putAll( methodInformation );
    }

    @Override
    public void addProjectCollectionTypes( final Map<String, Boolean> collectionTypes ) {
        this.projectCollectionTypes.putAll( collectionTypes );
    }

    @Override
    public void addProjectPackageNames( final List<String> packageNames ) {
        this.projectPackageNames.addAll( packageNames );
    }

    @Override
    public String getProjectName() {
        return this.projectName;
    }

    @Override
    public Map<String, ModelField[]> getProjectModelFields() {
        return this.projectModelFields;
    }

    @Override
    public Map<String, String> getProjectFieldParametersType() {
        return this.projectFieldParametersType;
    }

    @Override
    public Map<String, Boolean> getProjectEventTypes() {
        return this.projectEventTypes;
    }

    @Override
    public Map<String, TypeSource> getProjectTypeSources() {
        return this.projectTypeSources;
    }

    @Override
    public Map<String, List<String>> getProjectSuperTypes() {
        return this.projectSuperTypes;
    }

    @Override
    public Map<String, Set<Annotation>> getProjectTypeAnnotations() {
        return this.projectTypeAnnotations;
    }

    @Override
    public Map<String, Map<String, Set<Annotation>>> getProjectTypeFieldsAnnotations() {
        return this.projectTypeFieldsAnnotations;
    }

    @Override
    public Map<String, String[]> getProjectJavaEnumDefinitions() {
        return this.projectJavaEnumDefinitions;
    }

    @Override
    public Map<String, List<MethodInfo>> getProjectMethodInformation() {
        return this.projectMethodInformation;
    }

    @Override
    public Map<String, Boolean> getProjectCollectionTypes() {
        return this.projectCollectionTypes;
    }

    @Override
    public List<String> getProjectPackageNames() {
        return this.projectPackageNames;
    }

}

