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
package org.kie.dmn.feel.parser.feel11;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class ScopeHelper<T> {

    Deque<Map<String, T>> stack;
    
    public ScopeHelper() {
        this.stack = new ArrayDeque<>();
        this.stack.push(new HashMap<>());
    }
    
    public void addInScope(Map<String, T> inputTs) {
        stack.peek().putAll(inputTs);
    }
    
    public void addInScope(String name, T T) {
        stack.peek().put(name, T);
    }
    
    public void pushScope() {
        stack.push(new HashMap<>());
    }
    
    public void popScope() {
        stack.pop();
    }
    
    public Optional<T> resolve(String name) {
        return stack.stream()
            .map( scope -> Optional.ofNullable( scope.get( name )) )
            .flatMap( o -> o.isPresent() ? Stream.of( o.get() ) : Stream.empty() )
            .findFirst()
            ;
    }
}