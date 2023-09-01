package org.kie.internal.pmml;

import org.kie.api.internal.utils.KieService;

public interface PMMLCommandExecutorFactory extends KieService {

    class FactoryHolder {
        private static final PMMLCommandExecutorFactory factory = KieService.load(PMMLCommandExecutorFactory.class);
    }

    static PMMLCommandExecutorFactory get() {
        return PMMLCommandExecutorFactory.FactoryHolder.factory;
    }


    PMMLCommandExecutor newPMMLCommandExecutor();

}
