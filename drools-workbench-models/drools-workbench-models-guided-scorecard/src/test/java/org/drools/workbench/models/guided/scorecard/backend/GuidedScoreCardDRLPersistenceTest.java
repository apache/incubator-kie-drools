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
package org.drools.workbench.models.guided.scorecard.backend;

import org.drools.scorecards.StringUtil;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.junit.Test;

import static junit.framework.Assert.*;

public class GuidedScoreCardDRLPersistenceTest {

    @Test
    public void testEmptyModel() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName( "test" );

        final String drl = GuidedScoreCardDRLPersistence.marshal( model );
        System.out.println( drl );
        assertNotNull( drl );

        assertFalse( drl.contains( "package" ) );
        assertEquals( 11,
                      StringUtil.countMatches( drl,
                                               "rule \"" ) );

        assertEquals( 2,
                      StringUtil.countMatches( drl,
                                               "import " ) );
    }

    @Test
    public void testEmptyModelEmptyStringPackageName() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName( "test" );
        model.setPackageName( "" );

        final String drl = GuidedScoreCardDRLPersistence.marshal( model );
        assertNotNull( drl );

        assertFalse( drl.contains( "package" ) );
        assertEquals( 11,
                      StringUtil.countMatches( drl,
                                               "rule \"" ) );

        assertEquals( 2,
                      StringUtil.countMatches( drl,
                                               "import " ) );
    }

    @Test
    public void testEmptyModelInPackage() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName( "test" );
        model.setPackageName( "org.drools.workbench.models.guided.scorecard.backend" );

        final String drl = GuidedScoreCardDRLPersistence.marshal( model );
        assertNotNull( drl );

        assertTrue( drl.contains( "package org.drools.workbench.models.guided.scorecard.backend" ) );
        assertEquals( 11,
                      StringUtil.countMatches( drl,
                                               "rule \"" ) );

        assertEquals( 2,
                      StringUtil.countMatches( drl,
                                               "import " ) );
    }

    @Test
    public void testModelWithImports() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName( "test" );
        model.setPackageName( "org.drools.workbench.models.guided.scorecard.backend" );
        model.getImports().addImport( new Import( "org.smurf.Pupa" ) );

        final String drl = GuidedScoreCardDRLPersistence.marshal( model );
        assertNotNull( drl );

        assertTrue( drl.contains( "package org.drools.workbench.models.guided.scorecard.backend" ) );
        assertEquals( 11,
                      StringUtil.countMatches( drl,
                                               "rule \"" ) );

        assertEquals( 3,
                      StringUtil.countMatches( drl,
                                               "import " ) );
    }

    @Test
    public void testModelWithImportsAndFactName() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName( "test" );
        model.setPackageName( "org.drools.workbench.models.guided.scorecard.backend" );
        model.getImports().addImport( new Import( "org.smurf.Pupa" ) );
        model.setFactName( "org.drools.MoreCheese" );

        final String drl = GuidedScoreCardDRLPersistence.marshal( model );
        assertNotNull( drl );

        assertTrue( drl.contains( "package org.drools.workbench.models.guided.scorecard.backend" ) );
        assertEquals( 11,
                      StringUtil.countMatches( drl,
                                               "rule \"" ) );

        assertEquals( 3,
                      StringUtil.countMatches( drl,
                                               "import " ) );
    }

    @Test
    public void testModelWithImportsAndFactNameDuplicatingExplicitImport() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName( "test" );
        model.setPackageName( "org.drools.workbench.models.guided.scorecard.backend" );
        model.getImports().addImport( new Import( "org.smurf.Pupa" ) );
        model.setFactName( "org.smurf.Pupa" );

        final String drl = GuidedScoreCardDRLPersistence.marshal( model );
        assertNotNull( drl );

        assertTrue( drl.contains( "package org.drools.workbench.models.guided.scorecard.backend" ) );
        assertEquals( 11,
                      StringUtil.countMatches( drl,
                                               "rule \"" ) );

        assertEquals( 3,
                      StringUtil.countMatches( drl,
                                               "import " ) );
    }

}
