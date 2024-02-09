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
package org.kie.pmml.models.tree.compiler.utils;

import java.util.UUID;

import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

/**
 * Class meant to provide utility methods for <b>KiePMMLTree</b> model
 */
public class KiePMMLTreeModelUtils {

    private KiePMMLTreeModelUtils() {
    }

    public static String createNodeFullClassName(final String packageName) {
        String nodeClassName = createNodeClassName();
        return String.format(PACKAGE_CLASS_TEMPLATE, packageName, nodeClassName);
    }

    public static String createNodeClassName() {
        String rawName = "Node" + UUID.randomUUID();
        return getSanitizedClassName(rawName);
    }
}
