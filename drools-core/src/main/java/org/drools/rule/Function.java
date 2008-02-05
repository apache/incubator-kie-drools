package org.drools.rule;

import java.io.Serializable;

public class Function implements Dialectable, Serializable {
    private String name;
    private String dialect;
        
    public Function(String name,
                    String dialect) {
        this.name = name;
        this.dialect = dialect;
    }

    public String getName() {
        return this.name;
    }

    public String getDialect() {
        return this.dialect;
    }
}
