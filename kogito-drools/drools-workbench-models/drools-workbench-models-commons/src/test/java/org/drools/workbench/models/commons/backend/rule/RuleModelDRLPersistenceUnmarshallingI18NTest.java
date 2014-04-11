package org.drools.workbench.models.commons.backend.rule;
/*
 * Copyright 2013 JBoss Inc
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.MethodInfo;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RuleModelDRLPersistenceUnmarshallingI18NTest {

    private PackageDataModelOracle dmo;
    private Map<String, ModelField[]> packageModelFields = new HashMap<String, ModelField[]>();
    private Map<String, String[]> projectJavaEnumDefinitions = new HashMap<String, String[]>();
    private Map<String, List<MethodInfo>> projectMethodInformation = new HashMap<String, List<MethodInfo>>();

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

    private void addModelField( final String factName,
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

    @Test
    public void testI18N_US_InsertFact() {
        final String drl = "package org.test;\n" +
                "rule \"r1\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "then\n" +
                "Applicant fact0 = new Applicant();\n" +
                "fact0.setAge( 55 );\n" +
                "insert( fact0 );\n" +
                "end";

        addModelField( "Applicant",
                       "age",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC_INTEGER );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.rhs.length );
        assertTrue( m.rhs[ 0 ] instanceof ActionInsertFact );
        final ActionInsertFact aif = (ActionInsertFact) m.rhs[ 0 ];
        assertEquals( "Applicant",
                      aif.getFactType() );
        assertEquals( "fact0",
                      aif.getBoundName() );

        assertEquals( 1,
                      aif.getFieldValues().length );
        final ActionFieldValue afv = aif.getFieldValues()[ 0 ];
        assertEquals( "age",
                      afv.getField() );
        assertEquals( "55",
                      afv.getValue() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      afv.getType() );
        assertEquals( FieldNatureType.TYPE_LITERAL,
                      afv.getNature() );
    }

    @Test
    public void testI18N_JP_InsertFact() {
        final String drl = "package org.test;\n" +
                "rule \"r1\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "then\n" +
                "帽子 fact0 = new 帽子();\n" +
                "fact0.setサイズ( 55 );\n" +
                "insert( fact0 );\n" +
                "end";

        addModelField( "帽子",
                       "サイズ",
                       "java.lang.Integer",
                       DataType.TYPE_NUMERIC_INTEGER );

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 new ArrayList<String>(),
                                                                                 dmo );

        assertNotNull( m );

        assertEquals( 1,
                      m.rhs.length );
        assertTrue( m.rhs[ 0 ] instanceof ActionInsertFact );
        final ActionInsertFact aif = (ActionInsertFact) m.rhs[ 0 ];
        assertEquals( "帽子",
                      aif.getFactType() );
        assertEquals( "fact0",
                      aif.getBoundName() );

        assertEquals( 1,
                      aif.getFieldValues().length );
        final ActionFieldValue afv = aif.getFieldValues()[ 0 ];
        assertEquals( "サイズ",
                      afv.getField() );
        assertEquals( "55",
                      afv.getValue() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      afv.getType() );
        assertEquals( FieldNatureType.TYPE_LITERAL,
                      afv.getNature() );

    }

}
