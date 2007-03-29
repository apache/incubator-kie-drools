package org.drools.brms.client.modeldriven.brxml;


/**
 * This holds values for rule attributes (eg salience, agenda-group etc).
 * @author Michael Neale
 */
public class RuleAttribute
    implements
    PortableObject {
    
    public RuleAttribute(String name,
                         String value) {
        this.attributeName = name;
        this.value = value;
    }
    public String attributeName;
    public String value;

    public RuleAttribute() {}
    
}
