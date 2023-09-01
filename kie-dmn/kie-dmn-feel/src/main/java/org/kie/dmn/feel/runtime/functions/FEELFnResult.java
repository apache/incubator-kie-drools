/**
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
package org.kie.dmn.feel.runtime.functions;

import java.util.Optional;
import java.util.function.Function;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.util.Either;

public class FEELFnResult<T> extends Either<FEELEvent, T> {

    protected FEELFnResult(Optional<FEELEvent> left, Optional<T> right) {
        super(left, right);
    }

    public static <T> FEELFnResult<T> ofError(FEELEvent event) {
        return new FEELFnResult<>(Optional.of(event), Optional.empty());
    }
    
    public static <T> FEELFnResult<T> ofResult(T value) {
        return new FEELFnResult<>(Optional.empty(), Optional.ofNullable(value));
    }
    
    public <X> FEELFnResult<X> map(Function<T, X> rightFn) {
        return isLeft()
                ? ofError(this.getLeft().get())
                : ofResult(rightFn.apply(this.getRight().get()));
    }
    
    public <X> FEELFnResult<X> flatMap(Function<T, FEELFnResult<X>> rightFn) {
        return isLeft()
                ? ofError(this.getLeft().get())
                : rightFn.apply(this.getRight().get());
    }

}