package org.drools.compiler.rule.builder.dialect.java.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * A descriptor class for a local variable declaration in a java code block
 */
public class JavaLocalDeclarationDescr {
    private int start;
    private int end;
    private String type;
    private List<String> modifiers;
    private List<IdentifierDescr> identifiers;
    
    public JavaLocalDeclarationDescr() {
        this( -1, -1, "" );
    }

    public JavaLocalDeclarationDescr( int start, int end, String type ) {
        this.start = start;
        this.end = end;
        this.type = type;
        this.modifiers = new ArrayList<>();
        this.identifiers = new ArrayList<>();
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int finalOffset) {
        this.end = finalOffset;
    }

    public List<IdentifierDescr> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<IdentifierDescr> identifiers) {
        this.identifiers = identifiers;
    }
    
    public void addIdentifier(IdentifierDescr identifier) {
        this.identifiers.add( identifier );
    }
    public void addIdentifier(String identifier, int start, int end) {
        this.identifiers.add( new IdentifierDescr( identifier, start, end ) );
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<String> modifiers) {
        this.modifiers = modifiers;
    }
    
    public void addModifier(String modifier) {
        this.modifiers.add( modifier );
    }

    public int getStart() {
        return start;
    }

    public void setStart(int startingOffset) {
        this.start = startingOffset;
    }
    
    public void updateStart( int start ) {
        if( this.start == -1 ) {
            this.setStart( start );
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRawType() {
        int genericStart = type.indexOf('<');
        return genericStart < 0 ? type : type.substring(0, genericStart);
    }

    @Override
    public String toString() {
        return "[Declaration type="+this.type + " identifiers=" + this.identifiers.toString() + "]";
    }
    
    public static class IdentifierDescr {
        private String identifier;
        private int start;
        private int end;
        
        public IdentifierDescr() {
            this( "", -1, -1 );
        }
        public IdentifierDescr( String identifier, int start, int end ) {
            this.identifier = identifier;
            this.start = start;
            this.end = end;
        }
        public int getEnd() {
            return end;
        }
        public void setEnd(int end) {
            this.end = end;
        }
        public String getIdentifier() {
            return identifier;
        }
        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }
        public int getStart() {
            return start;
        }
        public void setStart(int start) {
            this.start = start;
        }
        @Override
        public String toString() {
            return this.identifier;
        }
    }

}
