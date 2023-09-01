package org.kie.internal.io;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import org.kie.api.internal.utils.KieService;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.internal.definition.KnowledgeDescr;

/**
 * <p>
 * Convenience Factory to provide Resource implementations for the desired IO resource.
 * </p>
 *
 * <pre>
 * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
 * kbuilder.add( ResourceFactory.newUrlResource( "htp://www.domain.org/myProcess.bpmn2" ),
 *               ResourceType.BPMN2 );
 * </pre>
 *
 * <pre>
 * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
 * kbuilder.add( ResourceFactory.newClassPathResource( "htp://www.domain.org/myrules.drl", getClass() ),
 *               ResourceType.DRL );
 * </pre>
 */
public class ResourceFactory {
    private static KieResources factoryService;

    public static Resource newFileResource(File file) {
        return getFactoryService().newFileSystemResource( file );
    }

    public static Resource newFileResource(String fileName) {
        return getFactoryService().newFileSystemResource( fileName );
    }

    public static Resource newByteArrayResource(byte[] bytes) {
        return getFactoryService().newByteArrayResource( bytes );
    }

    public static Resource newInputStreamResource(InputStream stream) {
        return getFactoryService().newInputStreamResource( stream );
    }

    public static Resource newInputStreamResource(InputStream stream,
                                                  String encoding) {
        return getFactoryService().newInputStreamResource( stream,
                                                           encoding );
    }

    public static Resource newReaderResource(Reader reader) {
        return getFactoryService().newReaderResource( reader );
    }

    public static Resource newReaderResource(Reader reader,
                                             String encoding) {
        return getFactoryService().newReaderResource( reader,
                                                        encoding );
    }

    public static Resource newClassPathResource(String path) {
        return getFactoryService().newClassPathResource( path );
    }

    public static Resource newClassPathResource(String path,
                                                Class clazz) {
        return getFactoryService().newClassPathResource( path,
                                                           clazz );
    }

    public static Resource newClassPathResource(String path,
                                                ClassLoader classLoader) {
        return getFactoryService().newClassPathResource( path,
                                                           classLoader );
    }

    public static Resource newClassPathResource(String path,
                                                String encoding) {
        return getFactoryService().newClassPathResource( path,
                                                         encoding );
    }

    public static Resource newClassPathResource(String path,
                                                String encoding,
                                                Class clazz) {
        return getFactoryService().newClassPathResource( path,
                                                         encoding,
                                                         clazz );
    }

    public static Resource newClassPathResource(String path,
                                                String encoding,
                                                ClassLoader classLoader) {
        return getFactoryService().newClassPathResource( path,
                                                         encoding,
                                                         classLoader );
    }

    public static Resource newDescrResource( KnowledgeDescr descr ) {
        return getFactoryService().newDescrResource( descr );
    }

    private static synchronized KieResources getFactoryService() {
        return LazyHolder.service;
    }

    private static class LazyHolder {
        private static final KieResources service = KieService.load(KieResources.class);
    }

}
