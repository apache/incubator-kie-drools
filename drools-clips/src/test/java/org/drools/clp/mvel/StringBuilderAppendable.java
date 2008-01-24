package org.drools.clp.mvel;

public class StringBuilderAppendable implements Appendable {
    public StringBuilder builder = new StringBuilder();
    

    public void append(String string) {
        this.builder.append(  string );
    }
    
    public String toString() {
        return builder.toString();
    }
}
