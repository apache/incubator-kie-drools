package org.drools.kproject;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.MalformedInputException;
import java.util.Properties;

import org.drools.core.util.StringUtils;
import org.kie.builder.GAV;

public class GAVImpl implements GAV {
    private final String groupId;
    private final String artifactId;
    private final String version;

    public GAVImpl(String groupId,
                                String artifactId,
                                String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public String getArtifactId() {
        return artifactId;
    }
    
    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return groupId + ":" + artifactId + ":" + version;
    }
    
    public String toExternalForm() {
        return toString();
    }
    
    public String getPomXmlPath() {
        return "META-INF/maven/" + groupId + "/" + artifactId + "/pom.xml";
    }

    public String getPomPropertiesPath() {
        return "META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties";
    }    
    
    public static GAV fromPropertiesString(String string) {
        Properties props = new Properties();
        GAV gav = null;
        try {
            props.load( new StringReader( string ) );
            String groupId = props.getProperty( "groupId" );
            String artifactId = props.getProperty( "artifactId" );
            String version = props.getProperty( "version" );            
            if ( StringUtils.isEmpty( groupId  ) || StringUtils.isEmpty( artifactId ) || StringUtils.isEmpty( version ) ) {
                throw new RuntimeException("pom.properties exists but GAV content is malformed\n" + string);
            }
            gav =  new GAVImpl( groupId, artifactId, version );
        } catch ( IOException e ) {
            throw new RuntimeException( "pom.properties was malformed\n" + string, e );
        }
        
        return gav;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GAVImpl that = (GAVImpl) o;

        if (artifactId != null ? !artifactId.equals(that.artifactId) : that.artifactId != null) return false;
        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = groupId != null ? groupId.hashCode() : 0;
        result = 31 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}