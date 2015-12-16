/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KieMeta {

    private final static boolean productized;

    static {
        Properties properties = new Properties();
        try {
            InputStream in = KieMeta.class.getResourceAsStream("kieMeta.properties");
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("The classpath file kieMeta.properties is missing.");
        }
        productized = Boolean.valueOf(properties.getProperty("productized"));
    }

    public static boolean isProductized() {
        return productized;
    }

}
