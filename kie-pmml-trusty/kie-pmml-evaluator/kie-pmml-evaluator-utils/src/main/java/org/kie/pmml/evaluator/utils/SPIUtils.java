package org.kie.pmml.evaluator.utils;

import org.kie.pmml.api.PMMLRuntimeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SPIUtils {

    private SPIUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(SPIUtils.class.getName());

    private static final ServiceLoader<PMMLRuntimeFactory> pmmlRuntimeFactoryLoader = ServiceLoader.load(PMMLRuntimeFactory.class);


    public static PMMLRuntimeFactory getPMMLRuntimeFactory(boolean refresh) {
        logger.debug("getRuntimeManager {}", refresh);
        List<PMMLRuntimeFactory> toReturn = new ArrayList<>();
        Iterator<PMMLRuntimeFactory> managers = getFactories(refresh);
        managers.forEachRemaining(toReturn::add);
        return toReturn.stream().findFirst().orElse(new PMMLRuntimeFactoryImpl());
    }


    private static Iterator<PMMLRuntimeFactory> getFactories(boolean refresh) {
        if (refresh) {
            pmmlRuntimeFactoryLoader.reload();
        }
        return pmmlRuntimeFactoryLoader.iterator();
    }
}
