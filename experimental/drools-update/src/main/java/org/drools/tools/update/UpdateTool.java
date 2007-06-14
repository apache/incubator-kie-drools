/*
 * Copyright 2007 JBoss Inc
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
 *
 * Created on Jun 13, 2007
 */
package org.drools.tools.update;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.resources.FileResource;
import org.drools.compiler.DroolsParserException;
import org.drools.tools.update.drl.DRLUpdate;

/**
 * An update tool to migrate source code from Drools 3.0.x to 
 * Drools 4.0.x
 * 
 * @author etirelli
 */
public class UpdateTool {
    
    public static void main( String args[] ) {
        // create the command line parser
        CommandLineParser parser = new PosixParser();
        Options options = createOptions();
        
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args, true );

            if( line.hasOption( "f" ) ) {
                String fileMask = line.getOptionValue( "f" );
                String sufix = ".updated";
                String dir = ".";
                if( line.hasOption( "s" ) ) {
                    sufix = line.getOptionValue( "s" );
                }
                if( line.hasOption( "d" ) ) {
                    dir = line.getOptionValue( "d" );
                }
                printHeader( dir, fileMask, sufix );
                processFiles( dir, fileMask, sufix );
            } else {
                // print the help
                printHelp( options);
            }
            
        }
        catch( ParseException exp ) {
            System.err.println("Error parsing command line arguments: "+exp.getMessage());
            // print the help
            printHelp( options);
        }        
        
    }
    
    private static Options createOptions() {
        // create the Options
        Options options = new Options();
        options.addOption( "d", "dir", true, "source directory" );
        options.addOption( "f", "files", true, "pattern for the files to be updated" );
        options.addOption( "s", "sufix", true, "the sufix to be added to all updated files" );
        options.addOption( "h", "help", false, "list the usage help" );
        
        return options;
    }
    
    private static void printHelp( Options options ) {
        HelpFormatter formatter = new HelpFormatter();
        String commandLine = "org.drools.tools.update.UpdateTool -f <filemask> [-d <basedir>] [-s <sufix>]";
        formatter.printHelp( commandLine, options  );
    }
    
    private static void printHeader( String dir, String fileMask, String sufix ) {
        System.out.println("***************************************");
        System.out.println("*        Drools Update Tool           *");
        System.out.println("***************************************\n");
        System.out.println("Basedir   : "+dir);
        System.out.println("File Mask : "+fileMask);
        System.out.println("Sufix     : "+sufix+"\n");
        System.out.println("***************************************\n");
    }
    
    private static void processFiles( String dir, String fileMask, String sufix ) {
        DRLUpdate updater = new DRLUpdate();
        File curdir = new File( dir );
        Project project = new Project();
        FileSet fileset = new FileSet();
        fileset.setDir( curdir );
        fileset.setIncludes( fileMask );
        fileset.setProject( project );
        
        for( Iterator it = fileset.iterator(); it.hasNext(); ) {
            Reader reader = null;
            Writer writer = null;
            try {
                FileResource inputFile = (FileResource) it.next();
                reader = new InputStreamReader( inputFile.getInputStream() );
                String outFileName = inputFile.getName()+sufix;
                writer = new PrintWriter( new File( outFileName ) );
                
                System.out.println("Updating "+inputFile.getName()+" to "+outFileName);
                updater.updateDrl( reader, writer );
                
            } catch ( IOException e ) {
                e.printStackTrace();
            } catch ( DroolsParserException e ) {
                e.printStackTrace();
            } finally {
                try { if( reader != null ) reader.close(); } catch(Exception e) {}
                try { if( writer != null ) writer.close(); } catch(Exception e) {}
            }
        }
         
    }

}
