package org.drools.traits.persistence;

import java.util.Map;

import javax.transaction.UserTransaction;

import org.drools.core.base.MapGlobalResolver;
import org.drools.core.impl.EnvironmentFactory;
import org.kie.api.runtime.Environment;
import org.kie.test.util.db.PersistenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.api.runtime.EnvironmentName.GLOBALS;
import static org.kie.api.runtime.EnvironmentName.TRANSACTION;

public class DroolsPersistenceUtil extends PersistenceUtil {

    private static Logger logger = LoggerFactory.getLogger(DroolsPersistenceUtil.class);

    // Persistence and data source constants
    public static final String DROOLS_PERSISTENCE_UNIT_NAME = "org.drools.persistence.jpa";

    public static String OPTIMISTIC_LOCKING = "optimistic";
    public static String PESSIMISTIC_LOCKING = "pessimistic";


    public static Environment createEnvironment(Map<String, Object> context) {
        Environment env = EnvironmentFactory.newEnvironment();

        UserTransaction ut = (UserTransaction) context.get(TRANSACTION);
        if (ut != null) {
            env.set(TRANSACTION, ut);
        }

        env.set(ENTITY_MANAGER_FACTORY, context.get(ENTITY_MANAGER_FACTORY));
        env.set(GLOBALS, new MapGlobalResolver());

        return env;
    }

}
