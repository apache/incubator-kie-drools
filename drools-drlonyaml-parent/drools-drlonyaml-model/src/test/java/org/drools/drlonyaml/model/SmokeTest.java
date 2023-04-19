package org.drools.drlonyaml.model;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;

public class SmokeTest {
    private static final Logger LOG = LoggerFactory.getLogger(SmokeTest.class);
    private static final DrlParser drlParser = new DrlParser();
    private static final ObjectMapper mapper;
    static {
        YAMLFactory yamlFactory = YAMLFactory.builder()
                .enable(Feature.MINIMIZE_QUOTES)
                .build();
        mapper = new ObjectMapper(yamlFactory);
    }

    @Test
    public void smokeTestFromDRL1() throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource("/smoke1.drl.txt").toURI()));
        PackageDescr pkgDescr = drlParser.parse(new StringReader(content));
        Package model = Package.from(pkgDescr);
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, model);
        LOG.info("{}", writer.toString());
    }
    
    @Test
    public void smokeTestFromDRL2() throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource("/smoke2.drl.txt").toURI()));
        PackageDescr pkgDescr = drlParser.parse(new StringReader(content));
        Package model = Package.from(pkgDescr);
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, model);
        LOG.info("{}", writer.toString());
    }
    
    @Test
    public void smokeTestFromDRL3() throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource("/smoke3.drl.txt").toURI()));
        PackageDescr pkgDescr = drlParser.parse(new StringReader(content));
        Package model = Package.from(pkgDescr);
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, model);
        LOG.info("{}", writer.toString());
    }
    
    @Test
    public void smokeTestFromDRL4() throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource("/smoke4.drl.txt").toURI()));
        PackageDescr pkgDescr = drlParser.parse(new StringReader(content));
        Package model = Package.from(pkgDescr);
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, model);
        LOG.info("{}", writer.toString());
    }
    
    @Test
    public void smokeTestFromDRL5() throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource("/smoke5.drl.txt").toURI()));
        PackageDescr pkgDescr = drlParser.parse(new StringReader(content));
        Package model = Package.from(pkgDescr);
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, model);
        LOG.info("{}", writer.toString());
    }
    
    @Test
    public void smokeTestFromDRL6() throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource("/smoke6.drl.txt").toURI()));
        PackageDescr pkgDescr = drlParser.parse(new StringReader(content));
        Package model = Package.from(pkgDescr);
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, model);
        LOG.info("{}", writer.toString());
    }
    
    @Test
    public void smokeTestFromDRL7() throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource("/smoke7.drl.txt").toURI()));
        PackageDescr pkgDescr = drlParser.parse(new StringReader(content));
        Package model = Package.from(pkgDescr);
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, model);
        LOG.info("{}", writer.toString());
    }
    
    @Test
    public void smokeTestFromDRL8() throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource("/smoke8.drl.txt").toURI()));
        PackageDescr pkgDescr = drlParser.parse(new StringReader(content));
        Package model = Package.from(pkgDescr);
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, model);
        LOG.info("{}", writer.toString());
    }
    
    @Test
    public void smokeTestFromDRL9() throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource("/smoke9.drl.txt").toURI()));
        PackageDescr pkgDescr = drlParser.parse(new StringReader(content));
        Package model = Package.from(pkgDescr);
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, model);
        LOG.info("{}", writer.toString());
    }
    
    @Test
    public void smokeTestFromDRL10() throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource("/smoke10.drl.txt").toURI()));
        PackageDescr pkgDescr = drlParser.parse(new StringReader(content));
        Package model = Package.from(pkgDescr);
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, model);
        LOG.info("{}", writer.toString());
    }
    
    @Test
    public void smokeTestFromYAML1() throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource("/yamlfirst_smoke1.yml").toURI()));
        Package result = mapper.readValue(content, Package.class);
        LOG.info("{}", result);
    }
    
    @Ignore("additional RHS types not supported.")
    @Test
    public void smokeTestFromYAML2() throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource("/yamlfirst_smoke2.yml").toURI()));
        Package result = mapper.readValue(content, Package.class);
        LOG.info("{}", result);
    }
}
