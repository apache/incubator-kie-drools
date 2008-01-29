package org.drools.clips;

public class StringBuilderAppendable implements Appendable {
    public StringBuilder builder = new StringBuilder();
    

    public void append(String string) {
        this.builder.append(  string );
    }
    
    public String toString() {
        return builder.toString();
    }
}
