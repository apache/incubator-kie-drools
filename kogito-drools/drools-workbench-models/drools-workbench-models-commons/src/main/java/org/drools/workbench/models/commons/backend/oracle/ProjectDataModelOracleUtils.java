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
package org.drools.workbench.models.commons.backend.oracle;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.datamodel.oracle.Annotation;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.TypeSource;

/**
 * Utilities to query ProjectDMO content
 */
public class ProjectDataModelOracleUtils {

    public static String[] getFactTypes( final ProjectDataModelOracle dmo ) {
        final Map<String, ModelField[]> modelFields = dmo.getProjectModelFields();
        final String[] types = modelFields.keySet().toArray( new String[ modelFields.size() ] );
        Arrays.sort( types );
        return types;
    }

    public static List<String> getSuperType( final ProjectDataModelOracle dmo,
                                       final String factType ) {
        return dmo.getProjectSuperTypes().get( factType );
    }

    public static Set<Annotation> getTypeAnnotations( final ProjectDataModelOracle dmo,
                                                      final String factType ) {
        final Map<String, Set<Annotation>> typeAnnotations = dmo.getProjectTypeAnnotations();
        if ( !typeAnnotations.containsKey( factType ) ) {
            return Collections.EMPTY_SET;
        }
        return typeAnnotations.get( factType );
    }

    public static Map<String, Set<Annotation>> getTypeFieldsAnnotations( final ProjectDataModelOracle dmo,
                                                                         final String factType ) {
        final Map<String, Map<String, Set<Annotation>>> typeFieldsAnnotations = dmo.getProjectTypeFieldsAnnotations();
        if ( !typeFieldsAnnotations.containsKey( factType ) ) {
            return Collections.EMPTY_MAP;
        }
        return typeFieldsAnnotations.get( factType );
    }

    public static String getFieldClassName( final ProjectDataModelOracle dmo,
                                            final String factType,
                                            final String fieldName ) {
        final ModelField field = getField( dmo,
                                           factType,
                                           fieldName );
        return field == null ? null : field.getClassName();
    }

    private static ModelField getField( final ProjectDataModelOracle dmo,
                                        final String factType,
                                        final String fieldName ) {
        final String shortName = getFactNameFromType( dmo,
                                                      factType );
        final ModelField[] fields = dmo.getProjectModelFields().get( shortName );
        if ( fields == null ) {
            return null;
        }
        for ( ModelField modelField : fields ) {
            if ( modelField.getName().equals( fieldName ) ) {
                return modelField;
            }
        }
        return null;
    }

    private static String getFactNameFromType( final ProjectDataModelOracle dmo,
                                               final String factType ) {
        if ( factType == null ) {
            return null;
        }
        if ( dmo.getProjectModelFields().containsKey( factType ) ) {
            return factType;
        }
        for ( Map.Entry<String, ModelField[]> entry : dmo.getProjectModelFields().entrySet() ) {
            for ( ModelField mf : entry.getValue() ) {
                if ( DataType.TYPE_THIS.equals( mf.getName() ) && factType.equals( mf.getClassName() ) ) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public static String getParametricFieldType( final ProjectDataModelOracle dmo,
                                                 final String factType,
                                                 final String fieldName ) {
        final String qualifiedFactFieldName = factType + "#" + fieldName;
        return dmo.getProjectFieldParametersType().get( qualifiedFactFieldName );
    }

    public static TypeSource getTypeSource( final ProjectDataModelOracle dmo,
                                            final String factType ) {
        return dmo.getProjectTypeSources().get( factType );
    }

}
