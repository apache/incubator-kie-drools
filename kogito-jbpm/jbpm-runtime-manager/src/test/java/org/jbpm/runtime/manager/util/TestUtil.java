package org.jbpm.runtime.manager.util;

import java.io.File;
import java.io.FilenameFilter;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class TestUtil {

    public static PoolingDataSource setupPoolingDataSource() {
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName("jdbc/jbpm-ds");
        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        pds.setMaxPoolSize(50);
        pds.setAllowLocalTransactions(true);
        pds.getDriverProperties().put("user", "sa");
        pds.getDriverProperties().put("password", "");
        pds.getDriverProperties().put("url", "jdbc:h2:mem:test;MVCC=true");
        pds.getDriverProperties().put("driverClassName", "org.h2.Driver");
        pds.init();
        return pds;
    }
    
    public static void cleanupSingletonSessionId() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (tempDir.exists()) {
            
            String[] jbpmSerFiles = tempDir.list(new FilenameFilter() {
                
                @Override
                public boolean accept(File dir, String name) {
                    
                    return name.endsWith("-jbpmSessionId.ser");
                }
            });
            for (String file : jbpmSerFiles) {
                System.out.println(tempDir + file);
                new File(tempDir, file).delete();
            }
        }
    }
    
    public static void main(String[] args) {
        cleanupSingletonSessionId();
    }
}
