package org.kie.pmml.evaluator.core.executor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PMMLModelEvaluatorFinderImpl implements PMMLModelEvaluatorFinder {

    private static final Logger logger = LoggerFactory.getLogger(PMMLModelEvaluatorFinderImpl.class.getName());

    private ServiceLoader<PMMLModelEvaluator> loader = ServiceLoader.load(PMMLModelEvaluator.class);

    @Override
    public List<PMMLModelEvaluator> getImplementations(boolean refresh) {
        logger.debug("getImplementations {}", refresh);
        List<PMMLModelEvaluator> toReturn = new ArrayList<>();
        Iterator<PMMLModelEvaluator> providers = getProviders(refresh);
        providers.forEachRemaining(toReturn::add);
        logger.debug("toReturn {} {}", toReturn, toReturn.size());
        if (logger.isTraceEnabled()) {
            toReturn.forEach(provider -> logger.trace("{} : {}", provider.getPMMLModelType(), provider));
        }
        return toReturn;
    }

    private Iterator<PMMLModelEvaluator> getProviders(boolean refresh) {
        if (refresh) {
            loader.reload();
        }
        return loader.iterator();
    }
}
