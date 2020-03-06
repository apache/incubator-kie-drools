/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.models.regression.compiler.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class KiePMMLCode extends SimpleJavaFileObject {

    private String contents = null;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private String className;

    /**
     * Constructor for <code>Kind.CLASS</code> <code>KiePMMLCode</code>
     * @param className
     * @throws Exception
     */
    public KiePMMLCode(String className) throws Exception {
        super(new URI(className), Kind.CLASS);
        this.className = className;
    }

    /**
     * Constructor for <code>Kind.SOURCE</code> <code>KiePMMLCode</code>
     * @param className
     * @param contents
     */
    public KiePMMLCode(String className, String contents) {
        super(URI.create("string:///" + className.replace('.', '/')
                                 + Kind.SOURCE.extension), Kind.SOURCE);
        this.contents = contents;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return baos;
    }

    public byte[] getByteCode() {
        return baos.toByteArray();
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors)
            throws IOException {
        return contents;
    }
}