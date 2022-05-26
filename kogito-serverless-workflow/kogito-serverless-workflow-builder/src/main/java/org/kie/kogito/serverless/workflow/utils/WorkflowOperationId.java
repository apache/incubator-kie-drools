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

public class WorkflowOperationId {

    static final String OPERATION_SEPARATOR = "#";
    private static final String REGEX_NO_EXT = "[.][^.]+$";

    public static WorkflowOperationId fromOperation(String operation) {
        String[] tokens = operation.split(OPERATION_SEPARATOR);
        if (tokens.length == 2) {
            return new WorkflowOperationId(tokens[0], tokens[1], null);
        } else if (tokens.length == 3) {
            return new WorkflowOperationId(tokens[0], tokens[2], tokens[1]);
        } else {
            throw new IllegalArgumentException(
                    "Operation " + operation + " should contain at least one and no more than two" + OPERATION_SEPARATOR + " to differentiate between URI, service and operation");
        }
    }

    private final URI uri;
    private final String operation;
    private final String fileName;
    private final String className;
    private final String packageName;
    private final String service;

    private WorkflowOperationId(String uri, String operation, String service) {
        this.uri = URI.create(uri);
        this.operation = operation;
        this.service = service;
        this.fileName = Path.of(uri).getFileName().toString();
        this.className = getClassName(fileName, service, operation);
        this.packageName = onlyChars(removeExt(fileName.toLowerCase()));
    }

    public URI getUri() {
        return uri;
    }

    public String getOperation() {
        return operation;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getService() {
        return service;
    }

    public String geClassName() {
        return className;
    }

    public static String getClassName(String fileName, String... additional) {
        StringBuilder sb = new StringBuilder(removeExt(fileName.toLowerCase()));
        for (String item : additional) {
            if (item != null) {
                sb.append('_').append(item);
            }
        }
        return StringUtils.ucFirst(getValidIdentifier(sb.toString()));
    }

    protected static String removeExt(String fileName) {
        return fileName.replaceFirst(REGEX_NO_EXT, "");
    }

    protected static String onlyChars(String name) {
        return filterString(name, Character::isLetter);
    }

    protected static String getValidIdentifier(String name) {
        return filterString(name, Character::isJavaIdentifierPart);
    }

    protected static String filterString(String str, Predicate<Character> p) {
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
        return "WorkflowOperationId [uri=" + uri + ", operation=" + operation + ", fileName=" + fileName +
                ", className=" + className + ", packageName=" + packageName + ", service=" + service + "]";
    }
}
