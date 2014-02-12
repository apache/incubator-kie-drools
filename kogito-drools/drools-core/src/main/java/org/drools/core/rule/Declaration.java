package org.drools.core.rule;

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
 */

import org.drools.core.base.ValueType;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.spi.AcceptsReadAccessor;
import org.drools.core.spi.InternalReadAccessor;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;

import static org.drools.core.util.ClassUtils.canonicalName;
import static org.drools.core.util.ClassUtils.convertFromPrimitiveType;

public class Declaration
    implements
    Externalizable,
    AcceptsReadAccessor,
    Cloneable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private static final long    serialVersionUID = 510l;

    /** The identifier for the variable. */
    private String               identifier;

    private String               bindingName;

    private InternalReadAccessor readAccessor;

    private Pattern              pattern;

    private boolean              internalFact;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public Declaration() {
        this( null,
              null,
              null );
    }

    /**
     * Construct.
     *
     * @param identifier
     *            The name of the variable.
     * @param pattern
     *            The pattern this variable is declared in
     */
    public Declaration(final String identifier,
                       final Pattern pattern) {
        this( identifier,
              null,
              pattern,
              false );
    }

    /**
     * Construct.
     *
     * @param identifier
     *            The name of the variable.
     * @param extractor
     *            The extractor for this variable
     * @param pattern
     *            The pattern this variable is declared in
     */
    public Declaration(final String identifier,
                       final InternalReadAccessor extractor,
                       final Pattern pattern) {
        this( identifier,
              extractor,
              pattern,
              false );
    }

    /**
     * Construct.
     *
     * @param identifier
     *            The name of the variable.
     * @param identifier
     *            The name of the variable.
     * @param extractor
     *            The extractor for this variable
     * @param internalFact
     *            True if this is an internal fact created by the engine, like a collection result
     *            of a collect CE
     */
    public Declaration(final String identifier,
                       final InternalReadAccessor extractor,
                       final Pattern pattern,
                       final boolean internalFact) {
        this.identifier = identifier;
        this.readAccessor = extractor;
        this.pattern = pattern;
        this.internalFact = internalFact;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        identifier = (String) in.readObject();
        readAccessor = (InternalReadAccessor) in.readObject();
        pattern = (Pattern) in.readObject();
        internalFact = in.readBoolean();
        bindingName = (String) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( identifier );
        out.writeObject( readAccessor );
        out.writeObject( pattern );
        out.writeBoolean( internalFact );
        out.writeObject( bindingName );
    }

    public void setReadAccessor(InternalReadAccessor readAccessor) {
        this.readAccessor = readAccessor;
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

    public String getBindingName() {
        return bindingName != null ? bindingName : identifier;
    }

    public void setBindingName(String bindingName) {
        this.bindingName = bindingName;
    }

    /**
     * Retrieve the <code>ValueType</code>.
     *
     * @return The ValueType.
     */
    public ValueType getValueType() {
        return this.readAccessor.getValueType();
    }

    /**
     * Returns the index of the pattern
     *
     * @return the pattern
     */
    public Pattern getPattern() {
        return this.pattern;
    }

    public void setPattern(final Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Returns true if this declaration is a pattern declaration
     * @return
     */
    public boolean isPatternDeclaration() {
        return ( this.pattern != null && this.pattern.getDeclaration() == this ) || this.getIdentifier().equals( "this" ) ;
    }

    /**
     * Returns the Extractor expression
     *
     * @return
     */
    public InternalReadAccessor getExtractor() {
        return this.readAccessor;
    }

    public Object getValue(InternalWorkingMemory workingMemory,
                           final Object object) {
        return this.readAccessor.getValue( workingMemory, object );
    }

    public char getCharValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        return this.readAccessor.getCharValue(workingMemory, object);
    }

    public int getIntValue(InternalWorkingMemory workingMemory,
                           final Object object) {
        return this.readAccessor.getIntValue(workingMemory, object);
    }

    public byte getByteValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        return this.readAccessor.getByteValue(workingMemory, object);
    }

    public short getShortValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        return this.readAccessor.getShortValue(workingMemory, object);
    }

    public long getLongValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        return this.readAccessor.getLongValue(workingMemory, object);
    }

    public float getFloatValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        return this.readAccessor.getFloatValue(workingMemory, object);
    }

    public double getDoubleValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
        return this.readAccessor.getDoubleValue(workingMemory, object);
    }

    public boolean getBooleanValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
        return this.readAccessor.getBooleanValue(workingMemory, object);
    }

    public int getHashCode(InternalWorkingMemory workingMemory,
                           final Object object) {
        return this.readAccessor.getHashCode(workingMemory, object);
    }

    public boolean isGlobal() {
        return this.readAccessor != null && this.readAccessor.isGlobal();
    }

    public Method getNativeReadMethod() {
        if ( this.readAccessor != null ) {
            return this.readAccessor.getNativeReadMethod();
        } else {
            // This only happens if there was an error else where, such as building the initial declaration binding
            // return getValue to avoid null pointers, so rest of drl can attempt to build
            try {
                return this.getClass().getDeclaredMethod( "getValue",
                                                          new Class[]{InternalWorkingMemory.class, Object.class} );
            } catch ( final Exception e ) {
                throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                            e );
            }
        }
    }

    public String getNativeReadMethodName() {
        return readAccessor != null ? readAccessor.getNativeReadMethodName() : "getValue";
    }

    private transient String cachedTypeName;
    public String getTypeName() {
        if (cachedTypeName == null) {
        // we assume that null extractor errors are reported else where
            cachedTypeName = ( getExtractor() != null && getExtractor().getExtractToClass() != null )
                             ? canonicalName(getExtractor().getExtractToClass())
                             : "java.lang.Object";
        }
        return cachedTypeName;
    }

    private transient String cachedBoxedTypeName;
    public String getBoxedTypeName() {
        if (cachedBoxedTypeName == null) {
            // we assume that null extractor errors are reported else where
            cachedBoxedTypeName = getExtractor() != null ? canonicalName(convertFromPrimitiveType(getExtractor().getExtractToClass())) : "java.lang.Object";
        }
        return cachedBoxedTypeName;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public String toString() {
        return "(" + this.readAccessor.getValueType() + ") " + this.identifier;
    }

    public int hashCode() {
        int result = 29 * this.pattern.getOffset();
        result += 31 * this.readAccessor.hashCode();
        result += 37 * this.identifier.hashCode();
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

        return this.pattern.getOffset() == other.pattern.getOffset() && this.identifier.equals( other.identifier ) && this.readAccessor.equals( other.readAccessor );
    }

    public boolean isInternalFact() {
        return internalFact;
    }

    public Declaration clone() {
        return new Declaration( this.identifier,
                                this.readAccessor,
                                this.pattern );
    }
}
