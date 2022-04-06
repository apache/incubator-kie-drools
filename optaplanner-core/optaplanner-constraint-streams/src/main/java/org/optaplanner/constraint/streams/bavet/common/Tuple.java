/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.bavet.common;

import java.util.function.Function;

import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

/**
 * A tuple must not implement equals()/hashCode() to fact equality,
 * because some stream operations ({@link UniConstraintStream#map(Function)}, ...)
 * might create 2 different tuple instances to contain the same facts.
 */
public interface Tuple {

}
