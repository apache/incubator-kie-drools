package org.drools.lang.descr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This holds the value of an argument that has been parsed. 
 * The argument would then be passed to a method, or function etc. 
 * 
 * @author Michael Neale
 *
 */
public class ArgumentValueDescr
    implements
    Serializable {

    private static final long serialVersionUID = 320L;

    /** Obviously if it was in quotes, its a string literal (which could be anything) */
    public static final int   STRING           = 1;

    /** Means true integer, not Javas interpretation of it */
    public static final int   INTEGRAL         = 2;

    /** Means a decimal number, which may or may not be floating */
    public static final int   DECIMAL          = 4;

    /** If its none of the above, then its a variable */
    public static final int   VARIABLE         = 8;

    public static final int   BOOLEAN          = 16;

    public static final int   NULL             = 32;

    public static final int   MAP              = 64;
    
    public static final int   LIST             = 128;

    private final int         type;
    private final Object      value;

    /**
     * @param type One of the constant types.
     * @param value
     */
    public ArgumentValueDescr(int type,
                              Object value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public static class MapDescr {

        private List list;

        public MapDescr() {
            list = new ArrayList();
        }

        public void add(KeyValuePairDescr pair) {
            this.list.add( pair );
        }

        public KeyValuePairDescr[] getKeyValuePairs() {
            return ( KeyValuePairDescr[] ) this.list.toArray( new KeyValuePairDescr[ this.list.size() ] );
        }
    }

    public static class KeyValuePairDescr {
        private ArgumentValueDescr key;
        private ArgumentValueDescr value;

        public KeyValuePairDescr(ArgumentValueDescr key,
                            ArgumentValueDescr value) {
            this.key = key;
            this.value = value;
        }

        public ArgumentValueDescr getKey() {
            return this.key;
        }

        public ArgumentValueDescr getValue() {
            return this.value;
        }                
    }

}
