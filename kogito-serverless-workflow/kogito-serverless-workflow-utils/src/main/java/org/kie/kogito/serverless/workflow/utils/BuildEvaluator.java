/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.serverless.workflow.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils.SECRET_MAGIC;
import static org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils.getSecret;

public class BuildEvaluator {

    private BuildEvaluator() {
    }

    private static final Pattern SECRET_PATTERN = Pattern.compile("\\$" + SECRET_MAGIC + "\\.(\\w+)");

    public static String eval(String value) {
        Matcher matcher = SECRET_PATTERN.matcher(value);
        return matcher.matches() ? getSecret(matcher.group(1)) : value;
    }
}
