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
package org.drools.util;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

public class PortablePath implements Serializable {

    private static final boolean IS_WINDOWS_SEPARATOR = File.separatorChar == '\\';

    public static final PortablePath ROOT_PATH = new PortablePath("");

    private String path;

    public PortablePath() { }

    private PortablePath(String path) {
        this.path = path;
    }

    public static PortablePath of(String s) {
        return of(s, IS_WINDOWS_SEPARATOR);
    }

    static PortablePath of(String s, boolean isWindowsSeparator) {
        String normalized = normalizePath(s, isWindowsSeparator);
        return normalized.isEmpty() ? ROOT_PATH : new PortablePath( normalized );
    }

    private static String normalizePath(String s, boolean isWindowsSeparator) {
        if (s == null) {
            throw new NullPointerException("A path cannot be null");
        }
        return trimTrailingSeparator( isWindowsSeparator ? s.replace('\\', '/') : s );
    }

    public PortablePath getParent() {
        int lastSlash = path.lastIndexOf( '/' );
        return lastSlash >= 0 ? new PortablePath(path.substring( 0, lastSlash )) : ROOT_PATH;
    }

    public boolean isEmpty() {
        return path.isEmpty();
    }

    public PortablePath resolve(PortablePath portablePath) {
        return isEmpty() ? portablePath : new PortablePath(path + "/" + portablePath.asString());
    }

    public PortablePath resolve(String name) {
        return resolve(of(name));
    }

    public String getFileName() {
        int lastSlash = path.lastIndexOf( '/' );
        return lastSlash >= 0 ? path.substring( lastSlash+1 ) : path;
    }

    public String asString() {
        return path;
    }

    public String asClassName() {
        return path.substring(0, path.length() - ".class".length()).replace('/', '.');
    }

    @Override
    public String toString() {
        return "KiePath{" +
                "path='" + path + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PortablePath)) return false;
        PortablePath portablePath = (PortablePath) o;
        return Objects.equals(path, portablePath.path);
    }

    public boolean endsWith(String s) {
        return path.endsWith(s);
    }

    public boolean startsWith(String s) {
        return path.startsWith(s);
    }

    public PortablePath substring(int beginIndex) {
        return of(path.substring(beginIndex));
    }

    public PortablePath substring(int beginIndex, int endIndex) {
        return of(path.substring(beginIndex, endIndex));
    }

    public static String trimTrailingSeparator(String p) {
        return !p.isEmpty() && p.charAt( p.length() -1 ) == '/' ? p.substring( 0, p.length() -1 ) : p;
    }
}
