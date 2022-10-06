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
package org.kie.kogito.event;

import java.io.IOException;

/**
 * Checked version of function interface that transforms one
 * object into another. If conversion is not possible, then
 * an IOException is thrown
 * 
 * @param <T> input object
 * @param <S> output object
 */
@FunctionalInterface
public interface Converter<T, S> {
    /**
     * Converts input object into output object
     * 
     * @param value input object
     * @return output object
     * @throws IOException if there is a legit problem with the conversion.
     *         For example, a wrong format of the input. This method should not throw, willingly, any runtime exception to allow
     *         proper handling of the IOexception by the caller
     */
    S convert(T value) throws IOException;
}
