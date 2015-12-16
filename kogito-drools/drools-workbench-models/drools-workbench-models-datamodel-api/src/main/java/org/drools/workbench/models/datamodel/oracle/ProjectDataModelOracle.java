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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProjectDataModelOracle {

    void setProjectName( final String projectName );

    void addProjectModelFields( final Map<String, ModelField[]> modelFields );

    void addProjectFieldParametersType( final Map<String, String> fieldParametersType );

    void addProjectEventTypes( final Map<String, Boolean> eventTypes );

    void addProjectTypeSources( final Map<String, TypeSource> typeSources );

    void addProjectSuperTypes( final Map<String, List<String>> superTypes );

    void addProjectTypeAnnotations( final Map<String, Set<Annotation>> annotations );

    void addProjectTypeFieldsAnnotations( final Map<String, Map<String, Set<Annotation>>> typeFieldsAnnotations );

    void addProjectJavaEnumDefinitions( final Map<String, String[]> enumDefinitions );

    void addProjectMethodInformation( final Map<String, List<MethodInfo>> methodInformation );

    void addProjectCollectionTypes( final Map<String, Boolean> collectionTypes );

    void addProjectPackageNames( final List<String> packageNames );

    String getProjectName();

    Map<String, ModelField[]> getProjectModelFields();

    Map<String, String> getProjectFieldParametersType();

    Map<String, Boolean> getProjectEventTypes();

    Map<String, TypeSource> getProjectTypeSources();

    Map<String, List<String>> getProjectSuperTypes();

    Map<String, Set<Annotation>> getProjectTypeAnnotations();

    Map<String, Map<String, Set<Annotation>>> getProjectTypeFieldsAnnotations();

    Map<String, String[]> getProjectJavaEnumDefinitions();

    Map<String, List<MethodInfo>> getProjectMethodInformation();

    Map<String, Boolean> getProjectCollectionTypes();

    List<String> getProjectPackageNames();

}
