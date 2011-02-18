package org.drools.decisiontable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.drools.RuntimeDroolsException;
import org.drools.agent.FileLoader;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

/**
 * This is used by the agent when a source file is encountered.
 * 
 *
 */
public class SourcePackageProvider
    implements
    FileLoader {

    public Package loadPackage(File rm) throws IOException {
        final FileInputStream fin = new FileInputStream( rm );

        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile( fin,
                                        InputType.XLS );

        fin.close();
        
        PackageBuilder b = new PackageBuilder();
        try {
            b.addPackageFromDrl( new StringReader( drl ) );
            if ( b.hasErrors() ) {
                throw new RuntimeDroolsException( "Error building rules from source: " + b.getErrors() );
            } else {
                return b.getPackage();
            }
        } catch ( DroolsParserException e ) {
            throw new RuntimeException( e );
        }

    }

}
