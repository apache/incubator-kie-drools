package org.kie.dmn.core.compiler;

import org.kie.dmn.api.core.DMNType;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class DMNCompilerContext {
    private Stack<DMNScope> stack = new Stack();

    public DMNCompilerContext() {
        this.stack.push( new DMNScope(  ) );
    }

    public void enterFrame() {
        this.stack.push( new DMNScope( this.stack.peek() ) );
    }

    public void exitFrame() {
        this.stack.pop();
    }

    public DMNType resolve( String name ) {
        return this.stack.peek().resolve( name );
    }

    public void setVariable( String name, DMNType type ) {
        this.stack.peek().setVariable( name, type );
    }

    public Map<String, DMNType> getVariables() {
        Map<String, DMNType> variables = new HashMap<>(  );
        for( DMNScope scope : stack ) {
            variables.putAll( scope.getVariables() );
        }
        return variables;
    }
}
