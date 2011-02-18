/*
 * Copyright 2010 JBoss Inc
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

/*
 * J A V A  C O M M U N I T Y  P R O C E S S
 *
 * J S R  9 4
 *
 * Test Compatability Kit
 */
package org.jcp.jsr94.tck;

// java imports
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.drools.jsr94.rules.Jsr94FactHandle;
import org.jcp.jsr94.tck.admin.LocalRuleExecutionSetProviderTest;
import org.jcp.jsr94.tck.admin.RuleAdministrationExceptionTest;
import org.jcp.jsr94.tck.admin.RuleAdministratorTest;
import org.jcp.jsr94.tck.admin.RuleExecutionSetCreateExceptionTest;
import org.jcp.jsr94.tck.admin.RuleExecutionSetDeregistrationExceptionTest;
import org.jcp.jsr94.tck.admin.RuleExecutionSetRegisterExceptionTest;
import org.jcp.jsr94.tck.admin.RuleExecutionSetTest;
import org.jcp.jsr94.tck.admin.RuleTest;

/**
 * Run all the test suites in the Test Compatability Kit.
 * Output is directed to System.out (textui).
 */
public class AllTests extends TestSuite {

    public static Test suite() {
        setTckConf();

        final TestSuite suite = new TestSuite( "JSR 94 Test Compatability Kit" );
        suite.addTestSuite( ApiSignatureTest.class );
        suite.addTestSuite( ClassLoaderTest.class );
        suite.addTestSuite( ConfigurationExceptionTest.class );
        suite.addTestSuite( HandleTest.class );
        suite.addTestSuite( InvalidHandleExceptionTest.class );
        suite.addTestSuite( InvalidRuleSessionExceptionTest.class );
        suite.addTestSuite( ObjectFilterTest.class );
        suite.addTestSuite( RuleExceptionTest.class );
        suite.addTestSuite( RuleExecutionExceptionTest.class );
        suite.addTestSuite( RuleExecutionSetMetadataTest.class );
        suite.addTestSuite( RuleExecutionSetNotFoundExceptionTest.class );
        suite.addTestSuite( RuleRuntimeTest.class );
        suite.addTestSuite( RuleServiceProviderManagerTest.class );
        suite.addTestSuite( RuleServiceProviderTest.class );
        suite.addTestSuite( RuleSessionCreateExceptionTest.class );
        suite.addTestSuite( RuleSessionTest.class );
        suite.addTestSuite( RuleSessionTypeUnsupportedExceptionTest.class );
        suite.addTestSuite( StatefulRuleSessionTest.class );
        suite.addTestSuite( StatelessRuleSessionTest.class );
        suite.addTestSuite( LocalRuleExecutionSetProviderTest.class );
        suite.addTestSuite( RuleAdministrationExceptionTest.class );
        suite.addTestSuite( RuleAdministratorTest.class );
        suite.addTestSuite( RuleExecutionSetCreateExceptionTest.class );
        //     suite.addTestSuite(RuleExecutionSetProviderTest.class);
        suite.addTestSuite( RuleExecutionSetRegisterExceptionTest.class );
        suite.addTestSuite( RuleExecutionSetTest.class );
        suite.addTestSuite( RuleExecutionSetDeregistrationExceptionTest.class );
        suite.addTestSuite( RuleTest.class );
        return suite;
    }

    /**
     * Because the tck.conf relies on directory paths that are not universally the same from eclipse, maven
     * and ant this method create a tck.conf on the fly with the correct locations, it also create a jar
     * on the fly which it uses as the jar url location.
     */
    private static void setTckConf() {
        File jarFile = null;
        ZipOutputStream zos = null;
        File rootDirectory = null;
        try {
            jarFile = File.createTempFile( "drools-jsr94",
                                           ".jar" );

            URL url = Jsr94FactHandle.class.getResource( "Jsr94FactHandle.class" );
            rootDirectory = new File( url.getFile() ).getParentFile().getParentFile().getParentFile().getParentFile();

            zos = new ZipOutputStream( new FileOutputStream( jarFile ) );
            zipDir( rootDirectory,
                    zos );
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            if ( zos != null ) {
                try {
                    zos.close();
                } catch ( IOException e ) {
                }
            }
        }

        String conf = "";
        conf += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        conf += "<tck-configuration>\n";
        conf += "<test-factory>org.jcp.jsr94.tck.util.TestFactory</test-factory>\n";
        conf += "<rule-service-provider>org.drools.jsr94.rules.RuleServiceProviderImpl</rule-service-provider>\n";
        conf += "<rule-service-provider-jar-url>file://" + jarFile.getAbsolutePath() + "</rule-service-provider-jar-url>\n";

        URL url = AllTests.class.getResource( "AllTests.class" );
        String setLocation = new File( url.getFile() ).getParentFile().getAbsolutePath().replaceAll( "\\\\",
                                                                                                     "/" );
        // the tck needs an asbolute path, with no drive letters
        if ( setLocation.charAt( 1 ) == ':' ) {
            setLocation = setLocation.substring( 2 );
        }
        conf += "<rule-execution-set-location>" + setLocation + "</rule-execution-set-location>\n";
        conf += "</tck-configuration>";

        url = AllTests.class.getResource( "tck.conf" );
        File tckConf = new File( url.getFile() );
        BufferedWriter buffWriter = null;
        try {
            FileWriter fileWriter = new FileWriter( tckConf );
            buffWriter = new BufferedWriter( fileWriter );
            buffWriter.write( conf );
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            try {
                buffWriter.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        System.setProperty( "jsr94.tck.configuration",
                            tckConf.getParent() );

    }

    public static void zipDir(File zipDir,
                              ZipOutputStream zos) {
        try {
            String[] dirList = zipDir.list();
            byte[] readBuffer = new byte[2156];
            int bytesIn = 0;
            for ( int i = 0; i < dirList.length; i++ ) {
                File f = new File( zipDir,
                                   dirList[i] );
                if ( f.isDirectory() ) {
                    zipDir( f,
                            zos );
                    continue;
                }
                FileInputStream fis = new FileInputStream( f );
                ZipEntry anEntry = new ZipEntry( f.getPath() );
                zos.putNextEntry( anEntry );
                while ( (bytesIn = fis.read( readBuffer )) != -1 ) {
                    zos.write( readBuffer,
                               0,
                               bytesIn );
                }
                fis.close();
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
