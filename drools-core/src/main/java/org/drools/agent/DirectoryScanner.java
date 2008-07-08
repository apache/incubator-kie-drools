package org.drools.agent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.drools.RuleBase;
import org.drools.rule.Package;

/**
 * This will scan a directory for files to watch for a change. It will update
 * the list of files only if they number of files in a directory changes.
 * 
 * @author Michael Neale
 */
public class DirectoryScanner extends PackageProvider {

	private FileScanner scanner;
	private File dir;

	void configure(Properties config) {
		String d = config.getProperty(RuleAgent.DIRECTORY);

		// now check to see whats in them dir...
		dir = new File(d);
		if (!(dir.isDirectory() && dir.exists())) {
			throw new IllegalArgumentException("The directory " + d
					+ "is not valid.");
		}
		
		scanner = new FileScanner();
		scanner.setFiles( dir.listFiles() );

	}

	PackageChangeInfo loadPackageChanges() {
		
		Map<String, String> pathToPackage = scanner.pathToPackage;
		Collection<String> removedPackageNames = new ArrayList<String>();
		
		if( pathToPackage != null ) {
			Map<String,String> tempPathToPackage = new HashMap<String, String>();
			
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				File f = files[i];
				String path = f.getPath();
				
				if (pathToPackage.containsKey(path)) {

					tempPathToPackage.put(path, pathToPackage.get(path));
					pathToPackage.remove(path);
				} 
			}
			
			if( pathToPackage.size() > 0 || files.length != tempPathToPackage.size() ) {
				removedPackageNames.addAll( pathToPackage.values() );
				
				listener.info("File changes detected in the directory "
						+ dir.getPath());
				
//				scanner = new FileScanner();
				scanner.setFiles( files );			
			}
		
			scanner.pathToPackage = tempPathToPackage;
		}
				
		PackageChangeInfo info = scanner.loadPackageChanges();
		
		info.addRemovedPackages(removedPackageNames);
		
		return info;
	}
	

	public String toString() {
		String s = "DirectoryScanner";
		if (dir != null) {
			s = s + " scanning dir: " + dir.getPath();
		}
		if (scanner != null && scanner.files != null) {
			s = s + " found " + scanner.files.length + " file(s).";
		}
		return s;
	}

}
