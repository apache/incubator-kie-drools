/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.compiler.compiler;

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
import org.drools.drl.parser.DrlParser;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.io.InputStreamResource;
import org.junit.Test;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DescrResourceSetTest {

    protected static final transient Logger logger = LoggerFactory.getLogger( KnowledgeBuilderImpl.class);

    private static final PackageDescrResourceVisitor visitor = new PackageDescrResourceVisitor();
    private static final KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();

    @Test
    public void drlFilesTest() throws Exception {
        Set<File> drlFiles = getDrlFiles();
        for( File drl : drlFiles ) {
            final DrlParser parser = new DrlParser(conf.getOption(LanguageLevelOption.KEY));
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
        assertThat(dir.exists()).as("Does not exist: " + url.toString()).isTrue();

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
