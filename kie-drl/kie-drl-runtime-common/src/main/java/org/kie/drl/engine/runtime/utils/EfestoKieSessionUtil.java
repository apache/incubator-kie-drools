package org.kie.drl.engine.runtime.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.drools.model.Model;
import org.drools.modelcompiler.KieBaseBuilder;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EfestoKieSessionUtil {

    private static final Logger logger = LoggerFactory.getLogger(EfestoKieSessionUtil.class.getName());

    private EfestoKieSessionUtil() {
    }

    public static KieSession loadKieSession(ModelLocalUriId modelLocalUriId, EfestoRuntimeContext context) {
        logger.debug("loadKieSession {} {}", modelLocalUriId, context);
        Optional<GeneratedExecutableResource> generatedExecutableResourceOpt =
                GeneratedResourceUtils.getGeneratedExecutableResource(modelLocalUriId,
                                                                      context.getGeneratedResourcesMap());
        GeneratedExecutableResource finalResource = generatedExecutableResourceOpt.orElseThrow(
                () -> new KieRuntimeServiceException("Can not find expected GeneratedExecutableResource for " + modelLocalUriId));
        List<Model> models = new ArrayList<>();
        for (String className : finalResource.getFullClassNames()) {
            models.add(loadModel(className, context));
        }
        logger.debug("models {}", models);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(models);
        logger.debug("kieBase {}", kieBase);
        try {
            KieSession toReturn = kieBase.newKieSession();
            logger.debug("toReturn {}", toReturn);
            return toReturn;
        } catch (Exception e) {
            String errorMessage = String.format("Failed to create new session from %s due to %s", kieBase,
                                                e.getMessage());
            logger.error(errorMessage, e);
            throw new KieRuntimeServiceException(errorMessage, e);
        }
    }

    static Model loadModel(String fullModelResourcesSourceClassName, EfestoRuntimeContext context) {
        try {
            final Class<? extends Model> aClass = context.loadClass(fullModelResourcesSourceClassName);
            return aClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new KieRuntimeServiceException(e);
        }
    }
}
