package org.kie.dmn.core.compiler;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

public class DMNCompilerContext {

    private final DMNFEELHelper feelHelper;
    private Stack<DMNScope> stack = new Stack();
    private Function<String, Reader> relativeResolver;

    public DMNCompilerContext(DMNFEELHelper feelHelper) {
        this.feelHelper = feelHelper;
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

    public CompilerContext toCompilerContext() {
        CompilerContext compilerContext = feelHelper.newCompilerContext();
        compilerContext.getListeners().clear();
        for ( Map.Entry<String, DMNType> entry : this.getVariables().entrySet() ) {
            compilerContext.addInputVariableType(
                    entry.getKey(),
                    dmnToFeelType((BaseDMNTypeImpl) entry.getValue())
            );
        }
        return compilerContext;
    }

    private static Type dmnToFeelType(BaseDMNTypeImpl v) {
        if (v.isCollection()) return BuiltInType.LIST;
        else return v.getFeelType();
    }

    public DMNFEELHelper getFeelHelper() {
        return feelHelper;
    }

    public void setRelativeResolver(Function<String, Reader> relativeResolver) {
        this.relativeResolver = relativeResolver;
    }

    public Function<String, Reader> getRelativeResolver() {
        return relativeResolver;
    }

}
