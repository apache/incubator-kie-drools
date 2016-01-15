package org.drools.compiler.compiler;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.io.impl.InputStreamResource;
import org.junit.Test;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DescrResourceSetTest {

    protected static final transient Logger logger = LoggerFactory.getLogger(KnowledgeBuilderImpl.class);

    private static final PackageDescrResourceVisitor visitor = new PackageDescrResourceVisitor();
    private static final KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();

    @Test
    public void drlFilesTest() throws Exception {
        Set<File> drlFiles = getDrlFiles();
        for( File drl : drlFiles ) {
            final DrlParser parser = new DrlParser(((KnowledgeBuilderConfigurationImpl)conf).getLanguageLevel());
            InputStreamResource resource = new InputStreamResource(new FileInputStream(drl));
            PackageDescr pkgDescr = parser.parse(resource);
            if( parser.hasErrors() ) {
                continue;
            }
            visitor.visit(pkgDescr);
        }
        logger.debug( drlFiles.size() + " drl tested.");
    }

    private Set<File> getDrlFiles() throws Exception {
        URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
        File dir = new File(url.toURI());
        assertTrue("Does not exist: " + url.toString(), dir.exists());

        final FileFilter drlFilter = new FileFilter() {
            @Override
            public boolean accept( File file ) {
                return file.getName().endsWith(".drl");
            }
        };
        final FileFilter dirFilter = new FileFilter() {
            @Override
            public boolean accept( File file ) {
                return file.isDirectory();
            }
        };

        Set<File> drls = new TreeSet<File>(new Comparator<File>() {

            @Override
            public int compare( File o1, File o2 ) {
                if( o1 == o2 ) {
                    return 0;
                } else if( o1 == null ) {
                    return 1;
                } else if( o2 == null ) {
                   return -1;
                } else {
                   return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
                }
            }
        });

        // BFS recursive search
        Queue<File> dirs = new LinkedList<File>();
        dirs.add(dir);

        while( ! dirs.isEmpty() ) {
            dir = dirs.poll();
            drls.addAll(Arrays.asList(dir.listFiles(drlFilter)));
            dirs.addAll(Arrays.asList(dir.listFiles(dirFilter)));
        }

        return drls;
    }
}
