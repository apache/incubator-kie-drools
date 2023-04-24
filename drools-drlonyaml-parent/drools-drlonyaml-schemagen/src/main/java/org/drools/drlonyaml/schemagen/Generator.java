package org.drools.drlonyaml.schemagen;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;

public class Generator {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Missing one argument, the drools-drlonyaml-model project.basedir");
            System.exit(-1);
        }
        final String projectBaseDir = args[0];
        
        final Class<?> ROOT_CLASS = Class.forName("org.drools.drlonyaml.model.Package");
        JacksonModule module = new JacksonModule(
                JacksonOption.FLATTENED_ENUMS_FROM_JSONVALUE,
                JacksonOption.RESPECT_JSONPROPERTY_REQUIRED,
                JacksonOption.RESPECT_JSONPROPERTY_ORDER
        );
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_7,
                OptionPreset.PLAIN_JSON)
                        .with(Option.DEFINITIONS_FOR_ALL_OBJECTS)
                        .with(module);
        configBuilder.forTypesInGeneral()
            .withCustomDefinitionProvider(new AtomicTypeJsonValueDefinitionProvider());
        configBuilder.forFields().withDefaultResolver(field -> {
            JsonProperty annotation = field.getAnnotationConsideringFieldAndGetter(JsonProperty.class);
            return annotation == null || annotation.defaultValue().isEmpty() ? null : annotation.defaultValue();
        });
        configBuilder.forTypesInGeneral().withTypeAttributeOverride((node, scope, context) -> {
            if (scope.getType().getErasedType().equals(ROOT_CLASS)) {
                node.put("$comment", "This provisional schema is automatically (re-)generated from Java class definitions.");
                }
            });
        SchemaGeneratorConfig config = configBuilder.build();
        SchemaGenerator generator = new SchemaGenerator(config);
        ObjectNode jsonSchema = generator.generateSchema(ROOT_CLASS);
        
        final String jsonSchemaAsString = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
        Files.write(Paths.get(projectBaseDir + "/src/main/resources/drlonyaml-schema.json"), jsonSchemaAsString.getBytes());
    }
}
