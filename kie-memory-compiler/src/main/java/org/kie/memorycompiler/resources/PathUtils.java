/*
 * Copyright (c) 2021. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.memorycompiler.resources;

import java.io.File;

public class PathUtils {

    private static final boolean IS_WINDOWS_SEPARATOR = File.separatorChar == '\\';

    public static String normalizePath(String s) {
        return IS_WINDOWS_SEPARATOR && s != null ? s.replace('\\', '/') : s;
    }

}
