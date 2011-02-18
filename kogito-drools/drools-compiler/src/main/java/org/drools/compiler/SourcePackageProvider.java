package org.drools.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.drools.RuntimeDroolsException;
import org.drools.agent.FileLoader;
import org.drools.rule.Package;

/**
 * This is used by the agent when a source file is encountered.
 */
public class SourcePackageProvider implements FileLoader {

    public Package loadPackage(File drl) throws IOException {
        FileInputStream fin = new FileInputStream(drl);

        PackageBuilder b = new PackageBuilder();
        try {
            b.addPackageFromDrl(new InputStreamReader(fin));

            fin.close();

            if (b.hasErrors()) {
                throw new RuntimeDroolsException(
                        "Error building rules from source: " + b.getErrors());
            } else {
                return b.getPackage();
            }
        } catch (DroolsParserException e) {
            throw new RuntimeException(e);
        }

    }

}
