package org.kie.maven.plugin;

import org.drools.compiler.commons.jci.stores.ResourceStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;

public class DiskResourceStore implements ResourceStore {
    private final File root;

    public DiskResourceStore(File root) {
        this.root = root;
    }

    @Override
    public void write(String pResourceName, byte[] pResourceData) {
        write(pResourceName, pResourceData, false);
    }

    @Override
    public void write(String pResourceName, byte[] pResourceData, boolean createFolder) {
        File file = new File(getFilePath(pResourceName));
        if (createFolder) {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(pResourceData);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) { }
            }
        }
    }

    @Override
    public byte[] read(String pResourceName) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(getFilePath(pResourceName));
            return readBytesFromInputStream(fis);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) { }
            }
        }
    }

    @Override
    public void remove(String pResourceName) {
        File file = new File(getFilePath(pResourceName));
        if (file.exists()) {
            file.delete();
        }
    }

    private String getFilePath(String pResourceName) {
        return root.getAbsolutePath() + File.separator + pResourceName;
    }
}
