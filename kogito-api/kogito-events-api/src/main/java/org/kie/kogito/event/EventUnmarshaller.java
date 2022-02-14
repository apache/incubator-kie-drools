/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.event;

import java.io.IOException;

public interface EventUnmarshaller<S> {

    /**
     * Converts input object to output object
     * 
     * @param input value to be converted
     * @param outputClass type of the value getting generated
     * @return ouput object
     * @throws IOException if conversion cannot be performed. IMPORTANT!!!! any other exception will be considered unexpected, so this implementation should not willingly throw any runtime exception
     */
    <T> T unmarshall(S input, Class<T> outputClass, Class<?>... parametrizedClasses) throws IOException;
}
