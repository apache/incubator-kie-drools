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
package org.kie.kogito.serverless.workflow.parser.handlers.openapi;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class MediaTypeUtils {

    private MediaTypeUtils() {
    }

    private static final Collection<String> textMedia = Set.of("text/plain", "text/csv", "text/html");
    private static final Collection<String> bytesMedia = Set.of("application/octet-stream", "application/zip");

    public static Optional<Class<?>> fromMedia(String type) {
        if (isText(type)) {
            return Optional.of(String.class);
        } else if (isBytes(type)) {
            return Optional.of(byte[].class);
        } else {
            return Optional.empty();
        }

    }

    public static boolean isText(String type) {
        if (type == null)
            return false;
        return textMedia.contains(type.toLowerCase());
    }

    public static boolean isBytes(String type) {
        if (type == null)
            return false;
        return bytesMedia.contains(type.toLowerCase());
    }
}
