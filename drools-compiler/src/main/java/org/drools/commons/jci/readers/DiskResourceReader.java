package org.drools.commons.jci.readers;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;

public class DiskResourceReader implements ResourceReader {
    private final File root;

    public DiskResourceReader( final File pRoot ) {
        root = pRoot;        
    }
    
    public boolean isAvailable( final String pResourceName ) {
        return new File(root, pResourceName).exists();
    }

    public byte[] getBytes( final String pResourceName ) {
        try {
            return readBytesFromInputStream(new FileInputStream(new File(root, pResourceName)));
        } catch(Exception e) {
            return null;
        }
    }
    
    public Collection<String> getFileNames() {
        List<String> list = new ArrayList();
        list(root, list);        
        return list;
    }
    
    /**
     * @deprecated
     */
    public String[] list() {
        final List<String> files = new ArrayList<String>();
        list(root, files);
        return (String[]) files.toArray(new String[files.size()]);
    }

    /**
     * @deprecated
     */
    private void list( final File pFile, final List pFiles ) {
        if (pFile.isDirectory()) {
            final File[] directoryFiles = pFile.listFiles();
            for (int i = 0; i < directoryFiles.length; i++) {
                list(directoryFiles[i], pFiles);
            }
        } else {
            pFiles.add(pFile.getAbsolutePath().substring(root.getAbsolutePath().length()+1));
        }
    }   
    
}
