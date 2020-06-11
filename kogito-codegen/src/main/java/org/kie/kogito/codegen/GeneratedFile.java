/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen;

import java.nio.charset.StandardCharsets;

public class GeneratedFile {

    public enum Type {
        APPLICATION( false ),
        APPLICATION_SECTION( false ),
        APPLICATION_CONFIG( false ),
        PROCESS( false ),
        PROCESS_INSTANCE( false ),
        REST( true ),
        JSON_MAPPER( false ),
        RULE( false ),
        DECLARED_TYPE( true ),
        DTO( true ),
        QUERY( true ),
        MODEL( false ),
        CLASS( false ),
        MESSAGE_CONSUMER( false ),
        MESSAGE_PRODUCER( false ),
        RESOURCE( false );

        private final boolean customizable;

        Type( boolean customizable ) {
            this.customizable = customizable;
        }

        public boolean isCustomizable() {
            return customizable;
        }
    }

    private final String relativePath;
    private final byte[] contents;
    private final Type type;

    public GeneratedFile(Type type, String relativePath, String contents) {
        this(type, relativePath, contents.getBytes(StandardCharsets.UTF_8));
    }

    public GeneratedFile(Type type, String relativePath, byte[] contents) {
        this.type = type;
        this.relativePath = relativePath;
        this.contents = contents;
    }

    public String relativePath() {
        return relativePath;
    }

    public byte[] contents() {
        return contents;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "GeneratedFile{" +
                "type=" + type +
                ", relativePath='" + relativePath + '\'' +
                '}';
    }
}
