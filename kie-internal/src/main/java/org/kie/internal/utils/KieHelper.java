package org.kie.internal.utils;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;

import java.io.InputStream;

import static org.kie.api.io.ResourceType.determineResourceType;

public class KieHelper {

    public final KieServices ks = KieServices.Factory.get();

    public final KieFileSystem kfs = ks.newKieFileSystem();

    private int counter = 0;

    public KieContainer build() {
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException(results.getMessages().toString());
        }
        return ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
    }

    public KieHelper addContent(String content, ResourceType type) {
        kfs.write(generateResourceName(type), content);
        return this;
    }

    public KieHelper addFromClassPath(String name) {
        return addFromClassPath(name, null);
    }

    public KieHelper addFromClassPath(String name, String encoding) {
        InputStream input = getClass().getResourceAsStream(name);
        if (input == null) {
            throw new IllegalArgumentException("The file (" + name + ") does not exist as a classpath resource.");
        }
        ResourceType type = determineResourceType(name);
        kfs.write(generateResourceName(type), ks.getResources().newInputStreamResource(input, encoding));
        return this;
    }

    public KieHelper addResource(Resource resource) {
        kfs.write(resource);
        return this;
    }

    public KieHelper addResource(Resource resource, ResourceType type) {
        if (resource.getSourcePath() == null && resource.getTargetPath() == null) {
            resource.setSourcePath(generateResourceName(type));
        }
        return addResource(resource);
    }

    private String generateResourceName(ResourceType type) {
        return "src/main/resources/file" + counter++ + "." + type.getDefaultExtension();
    }
}
