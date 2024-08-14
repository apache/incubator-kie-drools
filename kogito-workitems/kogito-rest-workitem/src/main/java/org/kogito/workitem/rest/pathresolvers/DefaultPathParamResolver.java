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
package org.kogito.workitem.rest.pathresolvers;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultPathParamResolver implements PathParamResolver {

    @Override
    public String apply(String endPoint, Map<String, Object> parameters) {
        Set<String> toRemove = new HashSet<>();
        int start = endPoint.indexOf('{');
        if (start == -1) {
            return endPoint;
        }
        StringBuilder sb = new StringBuilder(endPoint);
        while (start != -1) {
            int end = sb.indexOf("}", start);
            if (end == -1) {
                throw new IllegalArgumentException("malformed endpoint should contain enclosing '}' " + endPoint);
            }
            final String key = sb.substring(start + 1, end);
            final Object value = getParam(parameters, key);
            if (value == null) {
                throw new IllegalArgumentException("missing parameter " + key);
            }
            toRemove.add(key);
            sb.replace(start, end + 1, URLEncoder.encode(value.toString(), Charset.defaultCharset()));
            start = sb.indexOf("{");
        }
        parameters.keySet().removeAll(toRemove);
        return sb.toString();
    }

    protected Object getParam(Map<String, Object> parameters, String key) {
        return parameters.get(key);
    }
}
