/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.serverless.workflow.utils;

import java.net.URI;
import java.nio.file.Path;
import java.util.function.Predicate;

import org.drools.util.StringUtils;

public class OpenAPIOperationId {

    static final String OPENAPI_OPERATION_SEPARATOR = "#";
    private static final String REGEX_NO_EXT = "[.][^.]+$";

    public static OpenAPIOperationId fromOperation(String operation) {
        int indexOf = operation.indexOf(OPENAPI_OPERATION_SEPARATOR);
        if (indexOf == -1) {
            throw new IllegalArgumentException("Operation " + operation + " should contain " + OPENAPI_OPERATION_SEPARATOR + " to differentiate between URI and operation id");
        }
        return new OpenAPIOperationId(operation.substring(0, indexOf), operation.substring(indexOf + OPENAPI_OPERATION_SEPARATOR.length()));
    }

    private final URI uri;
    private final String operationId;
    private final String fileName;
    private final String className;
    private final String serviceName;

    private OpenAPIOperationId(String uri, String operationId) {
        this.uri = URI.create(uri);
        this.operationId = operationId;
        this.fileName = Path.of(uri).getFileName().toString();
        this.className = getClassName(fileName, operationId);
        this.serviceName = onlyChars(removeExt(fileName.toLowerCase()));
    }

    public URI getUri() {
        return uri;
    }

    public String getOperationId() {
        return operationId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getFileName() {
        return fileName;
    }

    public String geClassName() {
        return className;
    }

    public static String getClassName(String fileName, String operationId) {
        return StringUtils.ucFirst(getClassIdentifier(removeExt(fileName.toLowerCase())) + "_" + onlyChars(operationId));
    }

    public static String removeExt(String fileName) {
        return fileName.replaceFirst(REGEX_NO_EXT, "");
    }

    private static String onlyChars(String name) {
        return filterString(name, Character::isLetter);
    }

    private static String getClassIdentifier(String name) {
        return filterString(name, Character::isJavaIdentifierPart);
    }

    private static String filterString(String str, Predicate<Character> p) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (p.test(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "OpenAPIOperationId [uri=" + uri + ", operationId=" + operationId + ", fileName=" + fileName +
                ", className=" + className + ", serviceName=" + serviceName + "]";
    }

}
