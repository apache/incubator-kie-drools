package org.drools.repository.db;

import org.hibernate.tool.hbm2ddl.SchemaExport;

public class DDLGenerator {

    /**
     * This will generate DDL for the current config.
     * No args are required, will spit it to standard out.
     */
    public static void main(String[] args) {
        SchemaExport exporter = new SchemaExport(HibernateUtil.getConfiguration());
        exporter.create(true, false);
    }

}
