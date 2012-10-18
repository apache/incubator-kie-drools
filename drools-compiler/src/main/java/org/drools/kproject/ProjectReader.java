package org.drools.kproject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.drools.core.util.StringUtils;

public class ProjectReader {
    private FileSystem fs;

    private KProject   kproject;

    public ProjectReader(FileSystem fs) {
        this.fs = fs;
        this.kproject = new KProjectImpl();
    }    
    
    public KProject getKproject() {
        return kproject;
    }

    public void read() {
        readKBasePaths();
        //readKBases();
    }

    public void readKBasePaths() {
        Properties props = null;
        try {
            props = loadProperties( fs.getRootFolder().getFile( "kbasePaths.properties" ) );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        
        kproject.setKProjectPath( props.getProperty( "kproject" ).trim() );
        
//        Map<String, String> map = new HashMap<String, String>();
//        mapStartsWith( map, props, "kbase" );
//        
//        Map<String, String> kbasePaths = kproject.getKbasePaths();
//        for ( String kbaseQName : map.keySet() ) {
//            if ( StringUtils.isEmpty( kbaseQName ) ) {
//                continue;
//            }             
//            kbaseQName = kbaseQName.trim();
//            kbasePaths.put( kbaseQName,  map.get( kbaseQName ).trim() );
//        }
    }  
    
//    public void readKBases() {        
//        for ( Entry<String, String> entry : kproject.getKbasePaths().entrySet() ) {
//            readKBase( entry );
//         }
//    }

    public void readKBase(Entry<String, String> entry) {
        Folder folder = fs.getFolder( entry.getValue() );
        String kbaseQName = entry.getKey();
        File file = folder.getFile( kbaseQName + ".properties" );
        Properties props = null;
        try {
            props = loadProperties( file );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        
        String namespace = props.getProperty( "namespace" );
        String name = props.getProperty( "name" );
        String filesStr = props.getProperty( "files" );
        List<String> files = new ArrayList<String>();
        for ( String str : filesStr.split( "," ) ) {
            if ( StringUtils.isEmpty( str ) ) {
                continue;
            }            
            files.add(  str.trim() );
        }
        
//        KBaseImpl kbase = new KBaseImpl( namespace, name, files );
//        
//        kbase.setEventProcessingMode( EventProcessingOption.determineEventProcessingMode(  props.getProperty( "eventProcessingMode" ) ) );
//        
//        kbase.setEqualsBehavior( AssertBehaviorOption.valueOf( props.getProperty( "equalsBehavior" ) ) );
//        List<String> list = Arrays.asList( props.getProperty( "annotations" ).split( "," ) );
//        kbase.setAnnotations( list );
//        
//        list = Arrays.asList( props.getProperty( "ksessions" ).split( "," ) );
//        for ( String ksessionQName : list ) {
//            if ( StringUtils.isEmpty( ksessionQName ) ) {
//                continue;
//            }
//            ksessionQName = ksessionQName.trim();            
//            Map<String, String> map = new HashMap<String, String>();            
//            mapStartsWith( map, props, ksessionQName );
//            readKSession(ksessionQName, map, kbase, kproject);
//        }
//                
//        kproject.getKBases().put( kbaseQName, kbase );
    }
    
    public void readKSession(String ksessionQName, Map<String, String> map, KBase kbase, KProject kproject) {
//        KSessionImpl ksession = new KSessionImpl( map.get( "namespace" ), map.get( "name ") );
//        ksession.setType( map.get( "type" ) );
//        
//        List<String> list = Arrays.asList( map.get( "annotations" ).split( "," ) );
//        ksession.setAnnotations( list );
//        
//        ksession.setClockType( ClockTypeOption.get( map.get( "clockType" ) ) );
//        kbase.getKSessions().put( ksessionQName, ksession );
    }

        
    private static Properties loadProperties(File file) throws IOException {
        Properties props = new Properties();
        if ( file.exists() ) {
            InputStream is = null;
            try {
                is = file.getContents();
                props.load( is );
            } finally {
                if ( is != null ) {
                    is.close();
                }
            }
        }
        return props;
    }
    
    private void mapStartsWith(Map<String, String> map,
                               Properties properties,
                               String startsWith) {
        Enumeration< ? > enumeration = properties.propertyNames();
        while ( enumeration.hasMoreElements() ) {
            String key = (String) enumeration.nextElement();
            if ( key.startsWith( startsWith ) ) {
                if ( !map.containsKey( key ) ) {
                    map.put( key.substring( startsWith.length() + 1),
                             properties.getProperty( key ) );
                }

            }
        }
    }    
}
