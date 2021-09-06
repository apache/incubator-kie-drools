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
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtils {

    public static final Path JAVA_ROOT = Paths.get("src", "main", "java");

    private static final boolean IS_WINDOWS_FS = File.separatorChar == '\\';

    public static Path string2Path(String s) {
        return string2Path(s, IS_WINDOWS_FS ? '/' : '\\');
    }

    public static Path string2Path(String s, char separator) {
        return Paths.get(s.replace(separator, File.separatorChar));
    }

    public static String path2String(Path p, char separator) {
        return p.toString().replace(File.separatorChar, separator);
    }

    public static String toClassName( Path path ) {
        return toDotNameWithoutExtension(path, ".class");
    }

    public static String toClassNameFromSource( Path path ) {
        return toDotNameWithoutExtension(path, ".java");
    }

    private static String toDotNameWithoutExtension(Path path, String ext) {
        String className = path2String(path, '.');
        if (className.endsWith(ext)) {
            className = className.substring(0, className.length() - ext.length());
        }
        return className;
    }

    public static Path toJavaSourcePath( String className ) {
        return getPathWithExtension(className, ".java");
    }

    public static Path toClassSourcePath( String className ) {
        return getPathWithExtension(className, ".class");
    }

    private static Path getPathWithExtension(String className, String s) {
        return Paths.get(className.replace('.', File.separatorChar) + s);
    }
}
