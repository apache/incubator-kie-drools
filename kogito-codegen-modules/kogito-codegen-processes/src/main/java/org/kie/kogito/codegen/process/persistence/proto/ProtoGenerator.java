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
package org.kie.kogito.codegen.process.persistence.proto;

import java.util.Collection;
import java.util.List;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;

public interface ProtoGenerator {

    GeneratedFileType PROTO_TYPE = GeneratedFileType.of("PROTO", GeneratedFileType.Category.STATIC_HTTP_RESOURCE);
    String INDEX_COMMENT = "@Field(index = Index.YES, store = Store.YES) @SortableField";
    String KOGITO_JAVA_CLASS_OPTION = "kogito_java_class";
    String KOGITO_SERIALIZABLE = "kogito.Serializable";
    String ARRAY = "Array";
    String COLLECTION = "Collection";

    Proto protoOfDataClasses(String packageName, String... headers);

    Collection<GeneratedFile> generateProtoFiles();

    List<String> protoBuiltins();

    interface Builder<E, T extends ProtoGenerator> {

        Builder<E, T> withDataClasses(Collection<E> dataClasses);

        T build(Collection<E> modelClasses);
    }
}
