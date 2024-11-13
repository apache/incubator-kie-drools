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

package org.kie.kogito.codegen.usertask;

import java.nio.file.Path;
import java.util.Arrays;

import org.jbpm.process.core.Work;
import org.kie.kogito.internal.utils.ConversionUtils;

public final class UserTaskCodegenHelper {

    private UserTaskCodegenHelper() {
        // do nothing;
    }

    public static String processId(Work descriptor) {
        return ConversionUtils.sanitizeClassName((String) descriptor.getParameter("ProcessId"));
    }

    public static String className(Work descriptor) {
        return processId(descriptor) + "_" + ConversionUtils.sanitizeClassName((String) descriptor.getParameter(Work.PARAMETER_UNIQUE_TASK_ID));
    }

    public static String packageName(Work descriptor) {
        return (String) descriptor.getParameter("PackageName");
    }

    public static Path path(Work descriptor) {
        return path(packageName(descriptor));
    }

    public static Path path(String packageName) {
        String[] pathFragments = packageName.split("\\.");

        if (pathFragments.length == 1) {
            return Path.of(pathFragments[0]);
        }
        String[] children = Arrays.copyOfRange(pathFragments, 1, pathFragments.length);
        return Path.of(pathFragments[0], children);
    }

    public static String fqnClassName(Work descriptor) {
        return packageName(descriptor) + "." + className(descriptor);
    }
}
