/*
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
package org.kie.kogito.codegen.process.util;

import java.util.function.Function;

import org.kie.kogito.codegen.api.Generator;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CodegenUtil {

    private static final Logger LOG = LoggerFactory.getLogger(CodegenUtil.class);
    /**
     * Flag used to configure transaction enabling. Default to <code>true</code>
     */
    public static final String TRANSACTION_ENABLED = "transactionEnabled";

    private CodegenUtil() {
        // do nothing
    }

    /**
     * Creates the property for a certain generator.
     * 
     * @param generator
     * @param propertyName
     * @return returns the property for certain generator
     */
    public static String generatorProperty(Generator generator, String propertyName) {
        return String.format("kogito.%s.%s", generator.name(), propertyName);
    }

    /**
     * Creates the property for global application
     * 
     * @param propertyName
     * @return
     */
    public static String globalProperty(String propertyName) {
        return String.format("kogito.%s", propertyName);
    }

    /**
     * This computes the boolean value of the transaction being enabled based on the logic specified
     * the property. Default value it is true
     * 
     * @see CodegenUtil#getProperty
     */
    public static boolean isTransactionEnabled(Generator generator, KogitoBuildContext context) {
        return isTransactionEnabled(generator, context, true);
    }

    public static boolean isTransactionEnabled(Generator generator, KogitoBuildContext context, boolean defaultValue) {
        boolean propertyValue = getProperty(generator, context, TRANSACTION_ENABLED, Boolean::parseBoolean, defaultValue);
        LOG.debug("Compute property {} for generator {} property with value {}", TRANSACTION_ENABLED, generator.name(), propertyValue);
        // java implementation does not have transactions
        return !JavaKogitoBuildContext.CONTEXT_NAME.equals(context.name()) && propertyValue;
    }

    /**
     * This method is a generic method to compute certain property of the given type.
     * 1. we compute the global property applicable for all the application.
     * 2. we compute the property only applicable for certain generator.
     * 
     * @see CodegenUtil#getApplicationProperty
     * @see CodegenUtil#globalProperty
     * @see CodegenUtil#generatorProperty
     */
    public static <T> T getProperty(Generator generator, KogitoBuildContext context, String propertyName, Function<String, T> converter, T defaultValue) {

        String generatorProperty = generatorProperty(generator, propertyName);
        if (isApplicationPropertyDefined(context, generatorProperty)) {
            return converter.apply(getApplicationProperty(context, generatorProperty));
        }

        String globalProperty = globalProperty(propertyName);

        if (isApplicationPropertyDefined(context, globalProperty)) {
            return converter.apply(getApplicationProperty(context, globalProperty));
        }

        return defaultValue;
    }

    private static boolean isApplicationPropertyDefined(KogitoBuildContext context, String property) {
        return context.getApplicationProperty(property).isPresent();
    }

    private static String getApplicationProperty(KogitoBuildContext context, String property) {
        return context.getApplicationProperty(property).orElseThrow(() -> new IllegalArgumentException("Property " + property + " defined but does not contain proper value"));
    }
}
