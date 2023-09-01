package org.drools.compiler.builder.impl.classbuilder;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.drools.base.factmodel.ClassDefinition;

/**
 * Declares an enum class to be dynamically created
 */
public class EnumClassDefinition
    extends ClassDefinition {

    private List<EnumLiteralDefinition>     enumLiterals = Collections.emptyList();

    public EnumClassDefinition() { }

    public EnumClassDefinition(String className, String fullSuperType, String[] interfax ) {
        super( className,
               fullSuperType,
               interfax );
    }



    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.enumLiterals = (List<EnumLiteralDefinition>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( enumLiterals );
    }

    public List<EnumLiteralDefinition> getEnumLiterals() {
        return enumLiterals;
    }

    public void setEnumLiterals(List<EnumLiteralDefinition> enumLiterals) {
        this.enumLiterals = enumLiterals;
    }

    public void addLiteral(EnumLiteralDefinition enumLiteralDefinition) {
        if ( enumLiterals == Collections.EMPTY_LIST ) {
            enumLiterals = new ArrayList<>();
        }
        enumLiterals.add( enumLiteralDefinition );
    }
}
