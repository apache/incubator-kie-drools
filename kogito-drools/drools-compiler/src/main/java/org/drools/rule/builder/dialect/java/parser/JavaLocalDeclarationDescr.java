/*
 * Copyright 2006 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on Jun 18, 2007
 */
package org.drools.rule.builder.dialect.java.parser;

import java.util.LinkedList;
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
        this.modifiers = new LinkedList<String>();
        this.identifiers = new LinkedList<IdentifierDescr>();
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
