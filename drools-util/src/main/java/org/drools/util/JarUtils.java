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

/**
 * Utility to access jar files
 */
public class JarUtils {

    private static final String SPRING_BOOT_PREFIX = "BOOT-INF/classes/"; // Actual path prefix in Spring Boot JAR
    private static final String SPRING_BOOT_URL_PREFIX = "BOOT-INF/classes!/"; // Spring Boot adds "!" to resource url as a "nest" separator

    private static final String SPRING_BOOT_NESTED_PREFIX_BEFORE_3_2 = "!/BOOT-INF/"; // Before Spring Boot 3.2
    private static final String SPRING_BOOT_NESTED_PREFIX_AFTER_3_2 = "/!BOOT-INF/"; // Since Spring Boot 3.2

    private JarUtils() {
        // Avoid instantiating class
    }

    /**
     * Spring Boot executable jar contains path "BOOT-INF/classes/org/example/MyClass.class" in the jar file.
     * However, when resource urls are acquired by spring boot classloader's getResources(),
     * "!" is added to the path prefix as a "nest" separator, resulting in "BOOT-INF/classes!/org/example/MyClass.class".
     * This method removes the "!" from the path to make it consistent with the actual path in the jar file.
     * @param resourceUrlPath resource url path
     * @return normalized resource url path
     */
    public static String normalizeSpringBootResourceUrlPath(String resourceUrlPath) {
        if (resourceUrlPath.startsWith(SPRING_BOOT_URL_PREFIX)) {
            return resourceUrlPath.replace(SPRING_BOOT_URL_PREFIX, SPRING_BOOT_PREFIX); // Remove "!"
        } else {
            return resourceUrlPath;
        }
    }

    /**
     * Replace the new spring-boot nested path representation "/!BOOT-INF/" (introduced since 3.2) with the old "!/BOOT-INF/".
     * Because the new path representation doesn't meet the path manipulation in the drools codebase.
     * See https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.2-Release-Notes#nested-jar-support
     */
    public static String replaceNestedPathForSpringBoot32(String urlPath) {
        if (urlPath.contains(SPRING_BOOT_NESTED_PREFIX_AFTER_3_2)) {
            return urlPath.replace(SPRING_BOOT_NESTED_PREFIX_AFTER_3_2, SPRING_BOOT_NESTED_PREFIX_BEFORE_3_2);
        } else {
            return urlPath;
        }
    }
}
