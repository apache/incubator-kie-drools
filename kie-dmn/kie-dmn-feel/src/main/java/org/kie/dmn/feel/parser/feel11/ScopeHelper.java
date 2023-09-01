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