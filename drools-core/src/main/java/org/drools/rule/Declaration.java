package org.drools.rule;

/*
 * $Id: Declaration.java,v 1.1 2005/07/26 01:06:31 mproctor Exp $
 *
 * Copyright 2001-2003 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.io.Serializable;

import org.drools.base.ValueType;
import org.drools.spi.Extractor;

/*
 * Copyright 2005 JBoss Inc
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
 */

/**
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 *
 */
public class Declaration
    implements
    Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     * 
     */
    private static final long serialVersionUID = 248608383490386902L;

    /** The identifier for the variable. */
    private final String      identifier;

    private final Extractor   extractor;

    private Column            column;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param identifier
     *            The name of the variable.
     * @param objectType
     *            The type of this variable declaration.
     * @param order
     *            The index within a rule.
     */
    public Declaration(final String identifier,
                       final Extractor extractor,
                       final Column column) {
        this.identifier = identifier;
        this.extractor = extractor;
        this.column = column;
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the variable's identifier.
     * 
     * @return The variable's identifier.
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Retrieve the <code>ValueType</code>.
     * 
     * @return The ValueType.
     */
    public ValueType getValueType() {
        return this.extractor.getValueType();
    }

    /**
     * Returns the index of the column
     * 
     * @return the column
     */
    public Column getColumn() {
        return this.column;
    }

    public void setColumn(final Column column) {
        this.column = column;
    }

    /**
     * Returns the Extractor expression
     * 
     * @return
     */
    public Extractor getExtractor() {
        return this.extractor;
    }

    public Object getValue(final Object object) {
        return this.extractor.getValue( object );
    }

    public char getCharValue(Object object) {
        return this.extractor.getCharValue( object );
    }

    public int getIntValue(Object object) {
        return this.extractor.getIntValue( object );
    }

    public byte getByteValue(Object object) {
        return this.extractor.getByteValue( object );
    }

    public short getShortValue(Object object) {
        return this.extractor.getShortValue( object );
    }

    public long getLongValue(Object object) {
        return this.extractor.getLongValue( object );
    }

    public float getFloatValue(Object object) {
        return this.extractor.getFloatValue( object );
    }

    public double getDoubleValue(Object object) {
        return this.extractor.getDoubleValue( object );
    }

    public boolean getBooleanValue(Object object) {
        return this.extractor.getBooleanValue( object );
    }

    public int getHashCode(Object object) {
        return this.extractor.getHashCode( object );
    }
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public String toString() {
        return "[Declaration: type=" + this.extractor.getValueType() + " identifier=" + this.identifier + "]";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * this.column.getOffset();
        result = PRIME * this.extractor.hashCode();
        result = PRIME * this.identifier.hashCode();
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        final Declaration other = (Declaration) object;

        return this.column.getOffset() == other.column.getOffset() && this.identifier.equals( other.identifier ) && this.extractor.equals( other.extractor );
    }

}
