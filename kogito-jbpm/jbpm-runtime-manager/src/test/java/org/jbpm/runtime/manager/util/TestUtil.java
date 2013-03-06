package org.jbpm.runtime.manager.util;

import java.io.File;

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
        File sessionIdSer = new File(System.getProperty("java.io.tmpdir") + File.separator + "jbpmSessionId.ser");
        if (sessionIdSer.exists()) {
            sessionIdSer.delete();
        }
    }
}
