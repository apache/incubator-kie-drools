/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.integrationtests;

import java.io.IOException;

import org.drools.core.util.DroolsStreamUtils;

/**
 * Marshalling helper class to perform serialize/de-serialize a given object
 */
public class SerializationHelper {
    public static <T> T serializeObject(T obj) throws IOException, ClassNotFoundException {
        return serializeObject(obj, null);
    }

    public static <T> T serializeObject(T obj, ClassLoader classLoader) throws IOException, ClassNotFoundException {
        return (T)DroolsStreamUtils.streamIn(DroolsStreamUtils.streamOut(obj), classLoader);
    }
}
