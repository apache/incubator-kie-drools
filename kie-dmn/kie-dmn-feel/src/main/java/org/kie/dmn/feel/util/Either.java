/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.util;

import java.util.Optional;
import java.util.function.Function;

public class Either<L,R> {
    private final Optional<L> left;
    private final Optional<R> right;
    
    protected Either(Optional<L> left, Optional<R> right) {
      this.left = left;
      this.right = right;
    }

    public static <L,R> Either<L,R> ofLeft(L value) {
        return new Either<>(Optional.of(value), Optional.empty());
    }
    
    public static <L,R> Either<L,R> ofRight(R value) {
        return new Either<>(Optional.empty(), Optional.of(value));
    }
    
    public boolean isLeft() {
        return this.left.isPresent();
    }
    
    public boolean isRight() {
        return ! isLeft();
    }
    
    public <X> X cata(Function<L,X> left, Function<R,X> right) {
        // warning: left.invoke, because of FEEL specs, could return null. The below is the safest way to implement cata over this Either.
        return isLeft() ? left.apply( this.left.get() ) : right.apply( this.right.orElse( null ) );
    }
}