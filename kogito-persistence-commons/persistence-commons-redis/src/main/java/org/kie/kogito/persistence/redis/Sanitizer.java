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
package org.kie.kogito.persistence.redis;

public class Sanitizer {
    // Special chars to be escaped https://oss.redislabs.com/redisearch/Escaping/
    // asterisk ("*") is valid instead
    private static final String ILLEGAL_CHARS_REGEX = "[,.<>{}\\[\\]\"':;!@#$%^&()\\-+=~ ]";

    public static Object sanitize(Object o) {
        if (o instanceof String) {
            return ((String) o).replaceAll(ILLEGAL_CHARS_REGEX, "\\\\$0");
        }
        return o;
    }
}
