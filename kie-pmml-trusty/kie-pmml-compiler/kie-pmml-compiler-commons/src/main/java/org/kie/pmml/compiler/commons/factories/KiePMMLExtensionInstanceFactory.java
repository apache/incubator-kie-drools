package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.dmg.pmml.Extension;
import org.kie.pmml.commons.model.KiePMMLExtension;

public class KiePMMLExtensionInstanceFactory {

    private KiePMMLExtensionInstanceFactory() {
    }

    public static List<KiePMMLExtension> getKiePMMLExtensions(List<Extension> extensions) {
        return extensions != null ? extensions.stream().map(KiePMMLExtensionInstanceFactory::getKiePMMLExtension).collect(Collectors.toList()) : Collections.emptyList();
    }

    public static KiePMMLExtension getKiePMMLExtension(Extension extension) {
        return new KiePMMLExtension(extension.getExtender(), extension.getName(), extension.getValue(), extension.getContent());
    }
}
