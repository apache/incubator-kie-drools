package org.kie.dmn.feel.jandex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.assertj.core.api.Assertions;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;
import org.junit.Test;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractJandexTest {

    public static final Logger LOG = LoggerFactory.getLogger(AbstractJandexTest.class);
    private final String moduleName;

    protected AbstractJandexTest(String moduleName) {
        this.moduleName = moduleName;
    }

    @Test
    public void testReflectConfigJSON() throws Exception {
        Indexer indexer = new Indexer();
        InputStream stream = getClass().getClassLoader()
                                       .getResourceAsStream("org/kie/dmn/feel/runtime/FEELFunction.class");
        indexer.index(stream);
        stream.close();
        stream = getClass().getClassLoader()
                           .getResourceAsStream("org/kie/dmn/feel/runtime/functions/BaseFEELFunction.class");
        indexer.index(stream);
        scanFile(new File("./target/classes"), indexer);
        Index index = indexer.complete();

        Set<ClassInfo> founds = index.getAllKnownImplementors(DotName.createSimple(FEELFunction.class.getCanonicalName()));
        LOG.debug("founds: \n{}", founds);
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        List<Object> results = new ArrayList<>();
        for (ClassInfo found : founds.stream().sorted(Comparator.comparing(ClassInfo::name)).collect(Collectors.toList())) {
            results.add(toReflectConfigMap(found));
        }
        LOG.debug("results: \n{}", results);
        String json = jsonb.toJson(results);
        LOG.debug("Expected reflect-config.json: \n{}", json);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> fromJson = jsonb.fromJson(new FileReader("src/main/resources/META-INF/native-image/org.kie/" + moduleName + "/reflect-config.json"),
                                                            List.class);
        List<DotName> dotNamesInJSON = fromJson.stream().map(m -> DotName.createSimple((String) m.get("name"))).collect(Collectors.toList());

        Set<DotName> foundsViaJandex = founds.stream().map(ClassInfo::name).collect(Collectors.toSet());
        Set<DotName> foundsViaJSON = dotNamesInJSON.stream().collect(Collectors.toSet());
        Assertions.assertThat(foundsViaJandex)
                  .as("List of classes found via Jandex during test and listed in JSON file must be same.")
                  .isEqualTo(foundsViaJSON)
        ;
    }

    private Map<String, Object> toReflectConfigMap(ClassInfo found) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", found.name().toString());
        result.put("allDeclaredConstructors" , true);
        result.put("allPublicConstructors"   , true);
        result.put("allDeclaredMethods"      , true);
        result.put("allPublicMethods"        , true);
        result.put("allDeclaredClasses"      , true);
        result.put("allPublicClasses"        , true);
        return result;
    }

    private void scanFile(File source, Indexer indexer) throws Exception {
        if (source.isDirectory()) {
            File[] children = source.listFiles();
            if (children == null)
                throw new FileNotFoundException("Source directory disappeared: " + source);

            for (File child : children)
                scanFile(child, indexer);

            return;
        }

        if (!source.getName().endsWith(".class")) {
            return;
        }

        LOG.trace("scan: {}", source);
        try (FileInputStream input = new FileInputStream(source);) {
            ClassInfo info = indexer.index(input);
        }
    }
}
