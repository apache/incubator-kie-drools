/*
 * Copyright 2015 JBoss Inc
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

package org.drools.workbench.models.guided.dtree.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.MethodInfo;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class AbstractGuidedDecisionTreeDRLPersistenceUnmarshallingTest {

    protected static final String SEPARATOR_PARAM = "{separator}";
    protected static final String VALUE_PARAM = "{value}";

    protected PackageDataModelOracle dmo;
    protected Map<String, ModelField[]> packageModelFields = new HashMap<String, ModelField[]>();
    protected Map<String, String[]> projectJavaEnumDefinitions = new HashMap<String, String[]>();
    protected Map<String, List<MethodInfo>> projectMethodInformation = new HashMap<String, List<MethodInfo>>();

    @Before
    public void setUp() throws Exception {
        dmo = mock( PackageDataModelOracle.class );
        when( dmo.getProjectModelFields() ).thenReturn( packageModelFields );
        when( dmo.getProjectJavaEnumDefinitions() ).thenReturn( projectJavaEnumDefinitions );
        when( dmo.getProjectMethodInformation() ).thenReturn( projectMethodInformation );
    }

    @After
    public void cleanUp() throws Exception {
        packageModelFields.clear();
        projectJavaEnumDefinitions.clear();
        projectMethodInformation.clear();
    }

    protected void addModelField( final String factName,
                                  final String fieldName,
                                  final String clazz,
                                  final String type ) {
        ModelField[] modelFields = new ModelField[ 1 ];
        modelFields[ 0 ] = new ModelField( fieldName,
                                           clazz,
                                           ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           type );
        if ( packageModelFields.containsKey( factName ) ) {
            final List<ModelField> existingModelFields = new ArrayList<ModelField>( Arrays.asList( packageModelFields.get( factName ) ) );
            existingModelFields.add( modelFields[ 0 ] );
            modelFields = existingModelFields.toArray( modelFields );
        }
        packageModelFields.put( factName,
                                modelFields );
    }

    protected void addJavaEnumDefinition( final String factName,
                                          final String fieldName,
                                          final String[] values ) {
        final String key = factName + "#" + fieldName;
        projectJavaEnumDefinitions.put( key,
                                        values );
    }

    protected GuidedDecisionTree getAndTestUnmarshalledModel( final String drl,
                                                              final String baseFileName,
                                                              final int expectedParseErrorsSize ) {
        final GuidedDecisionTree model = GuidedDecisionTreeDRLPersistence.getInstance().unmarshal( drl,
                                                                                                   baseFileName,
                                                                                                   dmo );

        assertNotNull( model );
        assertEquals( expectedParseErrorsSize,
                      model.getParserErrors().size() );
        return model;
    }

    protected void assertEqualsIgnoreWhitespace( final String expected,
                                                 final String actual ) {
        final String cleanExpected = expected.replaceAll( "\\s+", "" );
        final String cleanActual = actual.replaceAll( "\\s+", "" );

        assertEquals( cleanExpected, cleanActual );
    }
}