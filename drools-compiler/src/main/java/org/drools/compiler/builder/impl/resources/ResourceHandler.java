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
package org.drools.compiler.builder.impl.resources;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.drools.drl.parser.DroolsParserException;
import org.drools.drl.parser.ParserError;
import org.drools.drl.parser.lang.ExpanderException;
import org.drools.drl.parser.lang.dsl.DefaultExpander;
import org.drools.util.IoUtils;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ResourceHandler {
    protected KnowledgeBuilderConfigurationImpl configuration;
    protected Collection<KnowledgeBuilderResult> results = new ArrayList<>();
    protected ReleaseId releaseId;
    protected static final Logger logger = LoggerFactory.getLogger(ResourceHandler.class);

    public ResourceHandler(KnowledgeBuilderConfigurationImpl configuration, ReleaseId releaseId) {
        this.configuration = configuration;
        this.releaseId = releaseId;
    }

    public ResourceHandler(KnowledgeBuilderConfigurationImpl configuration) {
        this.configuration = configuration;
    }

    public abstract PackageDescr process(Resource resource, ResourceConfiguration configuration) throws DroolsParserException, IOException;

    public final PackageDescr process(Resource resource) throws DroolsParserException, IOException {
        return process(resource, null);
    }


    public abstract boolean handles(ResourceType type);

    public Collection<KnowledgeBuilderResult> getResults(){
        return this.results;
    }

    protected PackageDescr generatedDrlToPackageDescr(Resource resource, String generatedDrl) throws DroolsParserException {
        // dump the generated DRL if the dump dir was configured
        if (this.configuration.getDumpDir() != null) {
            dumpDrlGeneratedFromDTable(this.configuration.getDumpDir(), generatedDrl, resource.getSourcePath());
        }

        DrlParser parser = new DrlParser(configuration.getOption(LanguageLevelOption.KEY));
        PackageDescr pkg = parser.parse(resource, new StringReader(generatedDrl));
        this.results.addAll(parser.getErrors());
        if (pkg == null) {
            this.results.add(new ParserError(resource, "Parser returned a null Package", 0, 0));
        } else {
            pkg.setResource(resource);
        }
        return parser.hasErrors() ? null : pkg;
    }

    protected void dumpDrlGeneratedFromDTable(File dumpDir, String generatedDrl, String srcPath) {
        String fileName = srcPath != null ? srcPath : "decision-table-" + UUID.randomUUID();
        if (releaseId != null) {
            fileName = releaseId.getGroupId() + "_" + releaseId.getArtifactId() + "_" + fileName;
        }
        File dumpFile = createDumpDrlFile(dumpDir, fileName, ".drl");
        try {
            IoUtils.write(dumpFile, generatedDrl.getBytes(IoUtils.UTF8_CHARSET));
        } catch (IOException ex) {
            // nothing serious, just failure when writing the generated DRL to file, just log the exception and continue
            logger.warn("Can't write the DRL generated from decision table to file {}!\n{}", dumpFile.getAbsolutePath(),
                    Arrays.toString(ex.getStackTrace()));
        }
    }

    protected File createDumpDrlFile(File dumpDir, String fileName, String extension) {
        return new File(dumpDir, fileName.replaceAll("[^a-zA-Z0-9\\.\\-_]+", "_") + extension);
    }

    protected PackageDescr dslrReaderToPackageDescr(Resource resource, Reader dslrReader, DefaultExpander expander) throws DroolsParserException {
        boolean hasErrors;
        PackageDescr pkg;
        this.results.clear();
        DrlParser parser = new DrlParser(configuration.getOption(LanguageLevelOption.KEY));

        try {
            try {
                if (expander == null) {
                    expander = new DefaultExpander();
                }
                String str = expander.expand(dslrReader);
                if (expander.hasErrors()) {
                    for (ExpanderException error : expander.getErrors()) {
                        error.setResource(resource);
                        this.results.add(error);
                    }
                }

                pkg = parser.parse(resource, str);
                this.results.addAll(parser.getErrors());
                hasErrors = parser.hasErrors();
            } finally {
                if (dslrReader != null) {
                    dslrReader.close();
                }
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        return hasErrors ? null : pkg;
    }

}