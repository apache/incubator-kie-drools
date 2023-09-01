package org.drools.drl.ast.descr;

/**
 * A descriptor class for globals.
 */
public class GlobalDescr extends BaseDescr {

    private static final long serialVersionUID = 510l;

    private String            identifier;
    private String            type;

    public GlobalDescr() {
        this( null,
              null );
    }

    public GlobalDescr(final String identifier,
                       final String type) {
        this.identifier = identifier;
        this.type = type;
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * @param type the type to set
     */
    public void setType(final String type) {
        this.type = type;
    }

}
