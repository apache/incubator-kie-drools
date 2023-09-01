/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.internal.pmml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to provide utility methods to manage implementation to be invoked
 * at runtime
 */
public class PMMLImplementationsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PMMLImplementationsUtil.class);

    private static final String JPMML_IMPL = "org.kie.dmn.jpmml.DMNjPMMLInvocationEvaluator";

    private PMMLImplementationsUtil() {
    }

    /**
     * @param classLoader
     * @return <code>true</code> if <b>org.kie.dmn.jpmml.DMNjPMMLInvocationEvaluator</b> is found in the given
     * <code>ClassLoader</code>,
     * <code>false</code> otherwise
     */
    public static boolean isjPMMLAvailableToClassLoader(final ClassLoader classLoader) {
        try {
            classLoader.loadClass(JPMML_IMPL);
            LOGGER.info("jpmml libraries available on classpath");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
