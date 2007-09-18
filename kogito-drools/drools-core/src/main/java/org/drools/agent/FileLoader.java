package org.drools.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;



/**
 * This interface is mostly provided so a DRL provider can live in
 * drools-compiler, without it, we would have circular references.
 * @author Michael Neale
 *
 */
public interface FileLoader {

	public org.drools.rule.Package loadPackage(File drl) throws FileNotFoundException, IOException;

}
