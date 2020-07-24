/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.persistence.util;

import static org.kie.api.runtime.EnvironmentName.GLOBALS;
import static org.kie.api.runtime.EnvironmentName.TRANSACTION;

import java.util.Map;
import javax.transaction.UserTransaction;
import org.drools.core.base.MapGlobalResolver;
import org.drools.core.impl.EnvironmentFactory;
import org.kie.api.runtime.Environment;
import org.kie.test.util.db.PersistenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DroolsPersistenceUtil extends PersistenceUtil {

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
