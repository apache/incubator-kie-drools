/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.internal.pmml;

import org.kie.api.pmml.PMMLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.api.pmml.PMMLConstants.KIE_PMML_IMPLEMENTATION;
import static org.kie.api.pmml.PMMLConstants.LEGACY;
import static org.kie.api.pmml.PMMLConstants.NEW;

/**
 * Class used to provide utility methods to manage implementation to be invoked
 * at runtime
 */
public class PMMLImplementationsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PMMLImplementationsUtil.class);

    private static final String LEGACY_IMPL = "org.kie.pmml.assembler.PMMLAssemblerService";
    private static final String TRUSTY_IMPL = "org.kie.pmml.evaluator.assembler.service.PMMLAssemblerService";
    private static final String JPMML_IMPL = "org.kie.dmn.jpmml.DMNjPMMLInvocationEvaluator";

    /**
     * @param classLoader
     * @return the <b>PMML</b> implementation to enable
     */
    public static PMMLConstants toEnable(final ClassLoader classLoader) {
        boolean isLegacyPresent = isImplementationPresent(LEGACY_IMPL, classLoader);
        boolean isTrustyPresent = isImplementationPresent(TRUSTY_IMPL, classLoader);
        if (!isLegacyPresent && !isTrustyPresent) {
            // if no pmml implementation can be loaded from the classloader we assume that we are
            // running under OSGi and use the legacy implementation which is the only one OSGi-compatible
            return LEGACY;
        }
        return getPMMLConstants(isLegacyPresent, isTrustyPresent);
    }

    protected static PMMLConstants getPMMLConstants(boolean isLegacyPresent, boolean isTrustyPresent) {
        String sysProp = System.getProperty(KIE_PMML_IMPLEMENTATION.getName());
        if (sysProp != null) {
            return getFromPropertyAndClasspath(PMMLConstants.byName(sysProp), isLegacyPresent, isTrustyPresent);
        } else {
            return getFromClassPath(isLegacyPresent, isTrustyPresent);
        }
    }

    protected static PMMLConstants getFromPropertyAndClasspath(PMMLConstants pmmlConstants, boolean isLegacyPresent, boolean isTrustyPresent) {
        switch (pmmlConstants) {
            case LEGACY:
                return returnImplementation(LEGACY, isLegacyPresent);
            case NEW:
                return returnImplementation(NEW, isTrustyPresent);
            default:
                throw new IllegalArgumentException("Unmanaged PMMLConstants " + pmmlConstants);
        }
    }

    protected static PMMLConstants getFromClassPath(boolean isLegacyPresent, boolean isTrustyPresent) {
        if (isLegacyPresent) {
            return returnImplementation(LEGACY, true);
        } else {
            return returnImplementation(NEW, isTrustyPresent);
        }
    }

    protected static PMMLConstants returnImplementation(PMMLConstants toReturn, boolean isPresent) {
        if (isPresent) {
            LOGGER.info("Using {} implementation", toReturn);
            return toReturn;
        } else {
            throw new IllegalArgumentException(String.format("Required %s PMML implementation missing in Classpath", toReturn));
        }
    }

    /**
     * @param classLoader
     * @return <code>true</code> if the <b>implementationFullName</b> is found in the given <code>ClassLoader</code>,
     * <code>false</code> otherwise
     */
    private static boolean isImplementationPresent(final String implementationFullName, final ClassLoader classLoader) {
        try {
            classLoader.loadClass(implementationFullName);
            return true;
        } catch (NoClassDefFoundError | ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * @param classLoader
     * @return <code>true</code> if <b>org.kie.dmn.jpmml.DMNjPMMLInvocationEvaluator</b> is found in the given <code>ClassLoader</code>,
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
