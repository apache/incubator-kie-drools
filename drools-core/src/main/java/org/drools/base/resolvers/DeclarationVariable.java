/**
 * 
 */
package org.drools.base.resolvers;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.Tuple;

public class DeclarationVariable
    implements
    ValueHandler {

    private Declaration declaration;

    public DeclarationVariable(final Declaration dec) {
        this.declaration = dec;
    }

    public Object getValue(final Tuple tuple,
                           final WorkingMemory wm) {
        return tuple.get( this.declaration ).getObject();
    }

}