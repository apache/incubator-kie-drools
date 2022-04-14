package org.drools.compiler.builder.impl;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.ParserError;
import org.drools.compiler.lang.ExpanderException;
import org.drools.compiler.lang.dsl.DSLMappingFile;
import org.drools.compiler.lang.dsl.DSLTokenizedMappingFile;
import org.drools.compiler.lang.dsl.DefaultExpander;
import org.drools.core.util.IoUtils;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public abstract class Processor {

    protected KnowledgeBuilderConfigurationImpl configuration;
    protected Collection<KnowledgeBuilderResult> results = new ArrayList<>();
    protected ReleaseId releaseId;
    protected List<DSLTokenizedMappingFile> dslFiles;
    protected static final transient Logger logger = LoggerFactory.getLogger(KnowledgeBuilderImpl.class);


    public Processor(KnowledgeBuilderConfigurationImpl configuration, ReleaseId releaseId) {
        this.configuration = configuration;
        this.releaseId = releaseId;
    }

    public Processor(KnowledgeBuilderConfigurationImpl configuration) {
        this.configuration = configuration;
    }

    public Collection<KnowledgeBuilderResult> getResults(){
        return this.results;
    }

    public abstract PackageDescr process(Resource resource)throws DroolsParserException, IOException;

    protected PackageDescr generatedDrlToPackageDescr(Resource resource, String generatedDrl) throws DroolsParserException {
        // dump the generated DRL if the dump dir was configured
        if (this.configuration.getDumpDir() != null) {
            dumpDrlGeneratedFromDTable(this.configuration.getDumpDir(), generatedDrl, resource.getSourcePath());
        }

        DrlParser parser = new DrlParser(configuration.getLanguageLevel());
        PackageDescr pkg = parser.parse(resource, new StringReader(generatedDrl));
        this.results.addAll(parser.getErrors());
        if (pkg == null) {
            //addBuilderResult(new ParserError(resource, "Parser returned a null Package", 0, 0));
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
            logger.warn("Can't write the DRL generated from decision table to file " + dumpFile.getAbsolutePath() + "!\n" +
                    Arrays.toString(ex.getStackTrace()));
        }
    }

    protected File createDumpDrlFile(File dumpDir, String fileName, String extension) {
        return new File(dumpDir, fileName.replaceAll("[^a-zA-Z0-9\\.\\-_]+", "_") + extension);
    }

    protected PackageDescr dslrReaderToPackageDescr(Resource resource, Reader dslrReader) throws DroolsParserException {
        boolean hasErrors;
        PackageDescr pkg;

        DrlParser parser = new DrlParser(configuration.getLanguageLevel());
        DefaultExpander expander = getDslExpander();

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

    protected DefaultExpander getDslExpander() {
        DefaultExpander expander = new DefaultExpander();
        if (this.dslFiles == null || this.dslFiles.isEmpty()) {
            return null;
        }
        for (DSLMappingFile file : this.dslFiles) {
            expander.addDSLMapping(file.getMapping());
        }
        return expander;
    }

}
