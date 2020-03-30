/*
 *
 *   Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.jbpm.serverless.workflow.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.jbpm.serverless.workflow.api.ExpressionEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressionEvaluatorProvider {

    private Map<String, ExpressionEvaluator> expressionEvaluatorMap = new HashMap<>();
    private static Logger logger = LoggerFactory.getLogger(ExpressionEvaluatorProvider.class);

    public ExpressionEvaluatorProvider() {
        ServiceLoader<ExpressionEvaluator> foundExpressionEvaluators = ServiceLoader.load(ExpressionEvaluator.class);
        foundExpressionEvaluators.forEach(expressionEvaluator -> {
            expressionEvaluatorMap.put(expressionEvaluator.getName(),
                                       expressionEvaluator);
            logger.info("Found expression evaluator with name: " + expressionEvaluator.getName());
        });
    }

    private static class LazyHolder {

        static final ExpressionEvaluatorProvider INSTANCE = new ExpressionEvaluatorProvider();
    }

    public static ExpressionEvaluatorProvider getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Map<String, ExpressionEvaluator> get() {
        return expressionEvaluatorMap;
    }
}