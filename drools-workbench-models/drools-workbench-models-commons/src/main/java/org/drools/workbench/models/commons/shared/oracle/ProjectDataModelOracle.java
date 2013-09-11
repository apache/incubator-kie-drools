/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.models.commons.shared.oracle;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.commons.shared.oracle.model.Annotation;
import org.drools.workbench.models.commons.shared.oracle.model.DropDownData;
import org.drools.workbench.models.commons.shared.oracle.model.FieldAccessorsAndMutators;
import org.drools.workbench.models.commons.shared.oracle.model.MethodInfo;
import org.drools.workbench.models.commons.shared.oracle.model.ModelField;
import org.drools.workbench.models.commons.shared.oracle.model.TypeSource;

public interface ProjectDataModelOracle {

    //Fact and Field related methods
    String[] getFactTypes();

    List<String> getRuleNames();

    String getFactNameFromType( final String classType );

    boolean isFactTypeRecognized( final String factType );

    boolean isFactTypeAnEvent( final String factType );

    TypeSource getTypeSource( final String factType );

    String getSuperType( final String factType );

    Set<Annotation> getTypeAnnotations( final String factType );

    Map<String, Set<Annotation>> getTypeFieldsAnnotations( final String factType );

    Map<String, ModelField[]> getModelFields();

    String[] getFieldCompletions( final String factType );

    String[] getFieldCompletions( final FieldAccessorsAndMutators accessor,
                                  final String factType );

    String getFieldType( final String variableClass,
                         final String fieldName );

    String getFieldClassName( final String factName,
                              final String fieldName );

    String getParametricFieldType( final String factType,
                                   final String fieldName );

    String[] getOperatorCompletions( final String factType,
                                     final String fieldName );

    String[] getConnectiveOperatorCompletions( final String factType,
                                               final String fieldName );

    List<String> getMethodNames( final String factType );

    List<String> getMethodNames( final String factName,
                                 final int i );

    List<String> getMethodParams( final String factType,
                                  final String methodNameWithParams );

    MethodInfo getMethodInfo( final String factName,
                              final String methodName );

    // Enumeration related methods
    String[] getEnumValues( final String factType,
                            final String factField );

    boolean hasEnums( final String qualifiedFactField );

    boolean hasEnums( final String factType,
                      final String factField );

    boolean isDependentEnum( final String factType,
                             final String factField,
                             final String field );

    DropDownData getEnums( final String type,
                           final String field );

    DropDownData getEnums( final String factType,
                           final String factField,
                           final Map<String, String> currentValueMap );

    List<String> getPackageNames();
}
