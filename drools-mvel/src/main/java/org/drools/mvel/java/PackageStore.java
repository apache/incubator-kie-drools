package org.drools.mvel.java;

import java.util.List;

import org.drools.core.rule.JavaDialectRuntimeData;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.drools.util.PortablePath;
import org.kie.memorycompiler.resources.ResourceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PackageStore
    implements
    ResourceStore {
    private static final Logger LOG = LoggerFactory.getLogger(PackageStore.class);
    private JavaDialectRuntimeData       javaDialectRuntimeData;

    private List<KnowledgeBuilderResult> errors;

    public PackageStore() {
    }

    public PackageStore(final JavaDialectRuntimeData javaDialectRuntimeData,
                        final List<KnowledgeBuilderResult> errors) {
        this.javaDialectRuntimeData = javaDialectRuntimeData;
        this.errors = errors;
    }

    public void setPackageCompilationData(final JavaDialectRuntimeData javaDialectRuntimeData) {
        this.javaDialectRuntimeData = javaDialectRuntimeData;
    }

    public void write(final PortablePath resourceName,
                      final byte[] clazzData) {
        try {
            this.javaDialectRuntimeData.write( resourceName.asString(),
                                               clazzData );
        } catch ( final Exception e ) {
            LOG.error("Exception", e);
            this.errors.add( new JavaDialectError( "PackageStore was unable to write resourceName='" + resourceName.asString() + "'" ) );
        }
    }

    public void write(final PortablePath resourceName,
                      final byte[] clazzData,
                      boolean createFolder) {
        write(resourceName, clazzData);
    }

    public byte[] read(final PortablePath resourceName) {
        byte[] clazz = null;
        try {
            clazz = this.javaDialectRuntimeData.read( resourceName.asString() );
        } catch ( final Exception e ) {
            this.errors.add( new JavaDialectError( "PackageStore was unable to read resourceName='" + resourceName.asString() + "'" ) );
        }
        return clazz;
    }

    public void remove(final PortablePath resourceName) {
        try {
            this.javaDialectRuntimeData.remove( resourceName.asString() );
        } catch ( final Exception e ) {
            this.errors.add( new JavaDialectError( "PackageStore was unable to remove resourceName='" + resourceName.asString() + "'"  ) );
        }
    }

}
