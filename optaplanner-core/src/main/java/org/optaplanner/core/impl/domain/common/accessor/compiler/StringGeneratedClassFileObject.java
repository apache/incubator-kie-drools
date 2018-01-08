/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.common.accessor.compiler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;

public final class StringGeneratedClassFileObject extends SimpleJavaFileObject {

    private ByteArrayOutputStream classOutputStream;

    public StringGeneratedClassFileObject(String fullClassName) {
        super(URI.create("bytes:///" + fullClassName), Kind.CLASS);
    }

    @Override
    public InputStream openInputStream() {
        return new ByteArrayInputStream(getClassBytes());
    }

    @Override
    public OutputStream openOutputStream() {
        classOutputStream = new ByteArrayOutputStream();
        return classOutputStream;
    }

    public byte[] getClassBytes() {
        return classOutputStream.toByteArray();
    }

}
