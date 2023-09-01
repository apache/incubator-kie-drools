package org.drools.model.codegen.project;

import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;

/**
 * Utility class to discover/interact with KieModuleModel.
 *
 */
public class KieModuleModelWrapper {
    private KieModuleModel kieModuleModel;

    public KieModuleModelWrapper(KieModuleModel kieModuleModel) {
        this.kieModuleModel = kieModuleModel;
        setDefaultsforEmptyKieModule(kieModuleModel);
    }

    static KieModuleModelWrapper fromResourcePaths(Path[] resourcePaths) {
        return new KieModuleModelWrapper(lookupKieModuleModel(resourcePaths));
    }

    private static KieModuleModel lookupKieModuleModel(Path[] resourcePaths) {
        for (Path resourcePath : resourcePaths) {
            if (resourcePath.toString().endsWith(".jar")) {
                InputStream inputStream = fromJarFile(resourcePath);
                if (inputStream != null) {
                    return KieModuleModelImpl.fromXML(inputStream);
                }
            } else {
                Path moduleXmlPath = resourcePath.resolve(KieModuleModelImpl.KMODULE_JAR_PATH.asString());
                if (Files.exists(moduleXmlPath)) {
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(Files.readAllBytes(moduleXmlPath))) {
                        return KieModuleModelImpl.fromXML(bais);
                    } catch (IOException e) {
                        throw new UncheckedIOException("Impossible to open " + moduleXmlPath, e);
                    }
                }
            }
        }

        return new KieModuleModelImpl();
    }

    /*
     * This is really a modified duplicate of org.drools.drl.quarkus.deployment.ResourceCollector#fromJarFile(java.nio.file.Path).
     * TODO: Refactor https://issues.redhat.com/browse/DROOLS-7254
     */
    public static InputStream fromJarFile(Path jarPath) {
        try (ZipFile zipFile = new ZipFile(jarPath.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().endsWith("kmodule.xml")) {
                    InputStream inputStream = zipFile.getInputStream(entry);
                    return new ByteArrayInputStream(inputStream.readAllBytes());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return null; // cannot find such file
    }

    Map<String, KieBaseModel> kieBaseModels() {
        return kieModuleModel.getKieBaseModels();
    }


}
