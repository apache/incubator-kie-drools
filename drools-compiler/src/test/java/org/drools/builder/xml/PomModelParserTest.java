package org.drools.builder.xml;

import java.io.InputStream;

import org.drools.xml.MinimalPomParser;
import org.drools.xml.PomModel;
import org.junit.Test;

import static org.junit.Assert.*;

public class PomModelParserTest {
    
    @Test
    public void parsePom() {
        InputStream is = PomModelParserTest.class.getResourceAsStream( "pom.xml" );
        assertNotNull( is );
        
        PomModel pomModel = MinimalPomParser.parse( PomModelParserTest.class.getName().replace( '.', '/' ) + ".pom.xml", is );
        assertEquals( "groupId", pomModel.getGroupId() );
        assertEquals( "artifactId", pomModel.getArtifactId() );
        assertEquals( "version", pomModel.getVersion() );
        
        assertEquals( "parentGroupId", pomModel.getParentGroupId() );
        assertEquals( "parentArtifactId", pomModel.getParentArtifactId() );
        assertEquals( "parentVersion", pomModel.getParentVersion() );        
    }
}
