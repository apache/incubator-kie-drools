package org.drools.compiler.xml.processes;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;


import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/*******************************************************************************
 * Class the migrates drools version 4 .rfm and .rf ruleflow files to version
 * 5 .rf and .rfm ruleflows.
 * 
 * @author <a href="mailto:author@acme.com">A.N Author</a>
 ******************************************************************************/
public class RuleFlowMigrator 
{
    /**
     * XSL file that transforms drools 4 .rfm ruleflow files to version 5
     */
    private static final String  XSL_RFM_FROM_4_TO_5 = "/org/drools/xml/processes/RuleFlowFrom4To5.xsl";
    
    /**
     * XSL file that transforms drools 4 .rf (graphical) ruleflow files to 
     * version 5
     */
    private static final String  XSL_RF_FROM_4_TO_5  = "/org/drools/xml/processes/RuleFlowGraphicalFrom4To5.xsl";
    
    /**
     * String containing namespace header for migrtated ruleflow files
     */
    private static final String  PROCESS_ELEMENT_WITH_NAMESPACE = "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                                                           + "    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n";
    
    /*************************************************************************
     * Returns a drools 5 version of a given drools 4 .rf (Graphical) ruleflow
     * in string format. Note that this method assumes the given ruleflow 
     * is a valid version 4 .rf ruleflow - this can be checked using the
     * needToMigrateRF method.
     * 
     * @param xml Drools 4 ruleflow (.rf) in xml format
     * @return drools 5 version of a given drools 4 .rf (Graphical) ruleflow
     * in string format.
     * @throws Exception
     ************************************************************************/
    public static String portRFToCurrentVersion(String xml) throws Exception 
    {
    	return portToCurrentVersion(xml, XSL_RF_FROM_4_TO_5);
    }
    
    /*************************************************************************
     * Returns a drools 5 version of a given drools 4 .rfm ruleflow
     * in string format. Note that this method assumes the given ruleflow 
     * is a valid version 4 .rfm ruleflow - this can be checked using the
     * needToMigrateRFM method. The return version 5 xml can be used as
     * a .rf (graphical) ruleflow, but its nodes do not contains
     * positional data.
     * 
     * @param xml Drools 4 ruleflow (.rfm) in xml format
     * @return drools 5 version of a given drools 4 .rfm ruleflow
     * in string format.
     * @throws Exception
     ************************************************************************/
    public static String portRFMToCurrentVersion(String xml) throws Exception 
    {
    	return portToCurrentVersion(xml, XSL_RFM_FROM_4_TO_5);
    }

    
    /*************************************************************************
     * Returns true if the given .rf (graphical) ruleflow xml is a version
     * 4 ruleflow that needs to be migrated to version 5, and returns 
     * false otherwise.
     * @param xml a .rf ruleflow in xml format
     * @return true if the given .rf (graphical) ruleflow xml is a version
     * 4 ruleflow that needs to be migrated to version 5, and returns 
     * false otherwise.
     * @throws Exception
     ************************************************************************/
    public static boolean needToMigrateRF(String xml) throws Exception {
    	return ( xml != null) && 
    	(xml.indexOf( "org.drools.eclipse.flow.ruleflow.core.RuleFlowProcessWrapper" ) >= 0 ); 
    }
    
    
    /*************************************************************************
     * Returns true if the given .rfm ruleflow xml is a version
     * 4 ruleflow that needs to be migrated to version 5, and returns 
     * false otherwise.
     * @param xml a .rfm ruleflow in xml format
     * @return true if the given .rfm graphical ruleflow xml is a version
     * 4 ruleflow that needs to be migrated to version 5, and returns 
     * false otherwise.
     * @throws Exception
     ************************************************************************/
    public static boolean needToMigrateRFM(String xml) throws Exception {
        return ( xml != null) && 
        (xml.indexOf( "org.drools.ruleflow.core.impl.RuleFlowProcessImpl" ) >= 0 ); 
    }
    
    
    /*************************************************************************
     * Utility method that applies a given xsl transform to the given xml to
     * transform a drools 4 ruleflow to version 5.
     * @param xml the ruleflow to be transformed
     * @param xsl the xsl transform to apply to the ruleflow xml
     * @return the ruleflow xml transformed from version 4 to 5 using the 
     * given xsl transformation
     * @throws Exception
     ************************************************************************/
    private static String portToCurrentVersion(String xml, String xsl) throws Exception 
    {
    	// convert it.
    	String version5XML = XSLTransformation.transform(xsl, xml, null);
    	// Add the namespace attribute to the process element as it is not added by the XSL transformation.
    	version5XML = version5XML.replaceAll( "<process ", PROCESS_ELEMENT_WITH_NAMESPACE );
    	return version5XML;
    }


    /*************************************************************************
     * Converts the contents of the given Reader into a string.
     * WARNING: the given string is not reset (as not all 
     * readers support reset). Consequently, anny further
     * attempt to read from the given reader will return nothing,
     * unless you reset the reader.
     * @param reader
     * @return he contents of the given Reader into a string.
     * @throws IOException
     ************************************************************************/
    public static String convertReaderToString(Reader reader) throws IOException {
        final StringBuilder text = new StringBuilder();

        final char[] buf = new char[1024];
        int len = 0;

        while ( (len = reader.read( buf )) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }
        return text.toString();
    }
    
    
    /*************************************************************************
     * Test application that reads a given source 
     * file containing a drools 4 .rf and writes it to another given file
     * location as a drools 5 .rf file.
     * 
     * @param args an array whose first element is the source filename and 
     * the second element is the the destination filename to which the 
     * transformed ruleflow is written
     ************************************************************************/
    public static final void main(String[] args) 
    {
        try 
        {
            if (args.length != 2)
            {
                System.out.println("usage: RuleFileMigrator source_file dest_file");
                System.exit(1);
            }
            
        	File inFile = new File(args[0]);
        	File outFile = new File(args[1]);
        	FileReader fr = new FileReader(inFile);
        	
        	String xml = convertReaderToString(fr);
        	String result = null;
        	if (needToMigrateRF(xml))
        	{
        		result = portRFToCurrentVersion(xml);
        	}
        	
        	if (result != null)
        	{
        		System.out.println("Ruleflow migrated from version 4.0 to 5.0");
        		FileWriter fw = new FileWriter(outFile);
        		fw.write(result);
        		fw.flush();
        		fw.close();
        	}
        	else
        	{
        		System.out.println("No Ruleflow Migration Reguired - Ruleflow is version 5.0");
        	}
        } 
        catch (Throwable t) 
        {
            t.printStackTrace();
        }
    }


    /*******************************************************************************
     * This class transform a string using an XSL transform - moved verbatim
     * from the ProcessBuilder class.
     * 
     * @author 
     ******************************************************************************/
    private static class XSLTransformation {
        public static String transform(String stylesheet,
                                       String srcXMLString,
                                       HashMap<String, String> params) throws Exception {
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult( writer );
            Source src = new StreamSource( new StringReader( srcXMLString ) );
            transform( stylesheet,
                       src,
                       result,
                       params );
            return writer.toString();
        }

        public static void transform(String stylesheet,
                                     Source src,
                                     Result res,
                                     HashMap<String, String> params) throws Exception {

            Transformer transformer = getTransformer( stylesheet );

            transformer.clearParameters();

            if ( params != null && params.size() > 0 ) {
                Iterator<String> itKeys = params.keySet().iterator();

                while ( itKeys.hasNext() ) {
                    String key = itKeys.next();
                    String value = params.get( key );
                    transformer.setParameter( key,
                                              value );
                }
            }

            transformer.transform( src,
                                   res );
        }

        private static Transformer getTransformer(String stylesheet) throws Exception {
            Transformer transformer = null;
            InputStream xslStream = null;

            try {
                InputStream in = XSLTransformation.class.getResourceAsStream( stylesheet );
                xslStream = new BufferedInputStream( in );
                StreamSource src = new StreamSource( xslStream );
                src.setSystemId( stylesheet );
                transformer = TransformerFactory.newInstance().newTransformer( src );
            } finally {
                if ( xslStream != null ) xslStream.close();
            }

            return transformer;
        }
    }
}
