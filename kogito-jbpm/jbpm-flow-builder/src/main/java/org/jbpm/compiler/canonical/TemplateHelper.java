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
package org.jbpm.compiler.canonical;

import java.io.InputStream;

import org.jbpm.util.JbpmClassLoaderUtil;

public class TemplateHelper {

    public static InputStream findTemplate(String template) {
        return findTemplate(JbpmClassLoaderUtil.findClassLoader(), template);
    }

    public static InputStream findTemplate(ClassLoader contextClassLoader, String template) {
        InputStream is = contextClassLoader.getResourceAsStream(template);
        if (is != null) {
            return is;
        }

        is = contextClassLoader.getResourceAsStream(template.substring(1));
        if (is != null) {
            return is;
        }

        is = contextClassLoader.getResourceAsStream(String.format("class-templates/%s", template));
        if (is != null) {
            return is;
        }
        return is = contextClassLoader.getResourceAsStream(String.format("/class-templates/%s", template));
    }
}
