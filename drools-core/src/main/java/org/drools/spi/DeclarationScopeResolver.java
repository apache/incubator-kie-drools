package org.drools.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.drools.rule.Declaration;
import org.drools.rule.RuleConditionElement;

public class DeclarationScopeResolver {
    private static final Stack EMPTY_STACK = new Stack(  );
    private Map[] maps;
    private Stack buildStack; 

    public DeclarationScopeResolver(final Map[] maps ) {
        this( maps, EMPTY_STACK );
    }

    public DeclarationScopeResolver(final Map[] maps, final Stack buildStack ) {
        this.maps = maps;
        if( buildStack == null ) {
            this.buildStack = EMPTY_STACK;
        } else {
            this.buildStack = buildStack;
        }
    }

    public Class getType(final String name) {
        for ( int i = 0, length = this.maps.length; i < length; i++ ) {
            final Object object = this.maps[i].get( name );
            if ( object != null ) {
                if ( object instanceof Declaration ) {
                    return ((Declaration) object).getExtractor().getExtractToClass();
                } else {
                    return (Class) object;
                }
            }
        }
        for( int i = this.buildStack.size()-1; i >= 0; i-- ) {
            Declaration declaration = ( Declaration ) (( RuleConditionElement ) this.buildStack.get( i )).getInnerDeclarations().get( name );
            if( declaration != null ) {
                return declaration.getExtractor().getExtractToClass();
            }
        }
        return null;
    }
    
    public Declaration getDeclaration( final String name ) {
        for( int i = this.buildStack.size()-1; i >= 0; i-- ) {
            Declaration declaration = ( Declaration ) (( RuleConditionElement ) this.buildStack.get( i )).getInnerDeclarations().get( name );
            if( declaration != null ) {
                return declaration;
            }
        }
        return null;
    }

    public boolean available(final String name) {
        for ( int i = 0, length = this.maps.length; i < length; i++ ) {
            if ( this.maps[i].containsKey( (name) ) ) {
                return true;
            }
        }
        for( int i = this.buildStack.size()-1; i >= 0; i-- ) {
            Declaration declaration = ( Declaration ) (( RuleConditionElement ) this.buildStack.get( i )).getInnerDeclarations().get( name );
            if( declaration != null ) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Return all declarations scoped to the current
     * RuleConditionElement in the build stack
     * 
     * @return
     */
    public Map getDeclarations() {
        Map declarations = new HashMap();
        for( int i = 0; i < this.buildStack.size(); i++ ) {
            // this may be optimized in the future to only re-add elements at 
            // scope breaks, like "NOT" and "EXISTS"
            declarations.putAll( ((RuleConditionElement) this.buildStack.get( i )).getInnerDeclarations() );
        }
        return declarations;
    }
}
