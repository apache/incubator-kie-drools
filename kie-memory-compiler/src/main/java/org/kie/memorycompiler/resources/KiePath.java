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
import java.io.Serializable;
import java.util.Objects;

public class KiePath implements Serializable {

    private static final boolean IS_WINDOWS_SEPARATOR = File.separatorChar == '\\';

    public static final KiePath ROOT_PATH = new KiePath("");

    private String path;

    public KiePath() { }

    private KiePath(String path) {
        this.path = path;
    }

    public static String normalizePath(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        return trimLeadingAndTrailing( IS_WINDOWS_SEPARATOR ? s.replace('\\', '/') : s );
    }

    public static KiePath of(String s) {
        String normalized = normalizePath(s);
        return normalized == null ? ROOT_PATH : new KiePath( normalized );
    }

    public KiePath getParent() {
        int lastSlash = path.lastIndexOf( '/' );
        return lastSlash >= 0 ? new KiePath(path.substring( 0, lastSlash )) : ROOT_PATH;
    }

    public boolean isEmpty() {
        return path.isEmpty();
    }

    public KiePath resolve(KiePath kiePath) {
        return isEmpty() ? kiePath : new KiePath(path + "/" + kiePath);
    }

    public KiePath resolve(String name) {
        return resolve(of(name));
    }

    public String getFileName() {
        int lastSlash = path.lastIndexOf( '/' );
        return lastSlash >= 0 ? path.substring( lastSlash+1 ) : path;
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KiePath)) return false;
        KiePath kiePath = (KiePath) o;
        return Objects.equals(path, kiePath.path);
    }

    public boolean endsWith(String s) {
        return path.endsWith(s);
    }

    public boolean startsWith(String s) {
        return path.startsWith(s);
    }

    public KiePath substring(int beginIndex) {
        return of(path.substring(beginIndex));
    }

    public KiePath substring(int beginIndex, int endIndex) {
        return of(path.substring(beginIndex, endIndex));
    }

    public static String trimLeadingAndTrailing(String p) {
        if ( p.charAt( 0 ) == '/') {
            p = p.substring( 1 );
        }
        if ( p.charAt( p.length() -1 ) == '/') {
            p = p.substring( 0, p.length() -1 );
        }
        return p;
    }
}
