package org.kie.kogito.codegen;

public class ConfigBeanGenerator extends TemplatedGenerator {

    private static final String RESOURCE_CDI = "/class-templates/config/CdiConfigBeanTemplate.java";
    private static final String RESOURCE_SPRING = "/class-templates/config/SpringConfigBeanTemplate.java";

    public ConfigBeanGenerator(String packageName) {
        super(packageName,
              "ConfigBean",
              RESOURCE_CDI,
              RESOURCE_SPRING);
    }

}
