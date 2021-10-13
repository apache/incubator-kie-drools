/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.internal.runtime.manager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated
 * Use <code>org.kie.api.runtime.manager.RuntimeManagerFactory</code>
 */
public interface RuntimeManagerFactory extends org.kie.api.runtime.manager.RuntimeManagerFactory {


    /**
     * @deprecated
     * Use <code>org.kie.api.runtime.manager.RuntimeManagerFactory</code>
     */
    public static class Factory {
        private static RuntimeManagerFactory INSTANCE;
        private static Logger logger = LoggerFactory.getLogger(Factory.class);

        static {
            try {
                final Object delegate =
                        Class.forName( System.getProperty("org.jbpm.runtime.manager.class",
                                "org.jbpm.runtime.manager.impl.RuntimeManagerFactoryImpl") ).newInstance();
                INSTANCE = (RuntimeManagerFactory) Proxy.newProxyInstance(RuntimeManagerFactory.class.getClassLoader(),
                        new Class[]{RuntimeManagerFactory.class}, new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {

                        return method.invoke(delegate, arguments);
                    }
                });
            } catch (Exception e) {
                logger.error("Unable to instance RuntimeManagerFactory due to " + e.getMessage());
            }
        }

        /**
         * Returns a reference to the RuntimeManagerFactory singleton
         */
        public static RuntimeManagerFactory get() {
            if (INSTANCE == null) {
                throw new RuntimeException("RuntimeManagerFactory was not initialized, see previous errors");
            }
            return INSTANCE;
        }
    }
}
