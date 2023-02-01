/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;

import org.drools.core.base.AccessorKeySupplier;
import org.drools.core.base.ValueType;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.rule.accessor.AcceptsReadAccessor;
import org.drools.core.rule.accessor.ReadAccessor;
import org.drools.core.reteoo.Tuple;
import org.drools.core.rule.accessor.TupleValueExtractor;

import static org.drools.util.ClassUtils.canonicalName;
import static org.drools.util.ClassUtils.convertFromPrimitiveType;

public class Declaration implements Externalizable, AcceptsReadAccessor, TupleValueExtractor {

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private static final long    serialVersionUID = 510l;

    /** The identifier for the variable. */
    private String               identifier;

    private String               bindingName;

    private ReadAccessor         readAccessor;

    private Pattern              pattern;

    private boolean              internalFact;

    private transient Class<?>   declarationClass;

    private int xPathOffset = 0;

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
                       final ReadAccessor extractor,
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
                       final ReadAccessor extractor,
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
        ( (DroolsObjectInputStream) in ).readExtractor( this::setReadAccessor );
        pattern = (Pattern) in.readObject();
        internalFact = in.readBoolean();
        bindingName = (String) in.readObject();
        xPathOffset = in.readInt();

    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( identifier );

        if (readAccessor instanceof AccessorKeySupplier) {
            out.writeObject( ( (AccessorKeySupplier) readAccessor ).getAccessorKey() );
        } else {
            out.writeObject( readAccessor );
        }

        out.writeObject( pattern );
        out.writeBoolean( internalFact );
        out.writeObject( bindingName );
        out.writeInt(xPathOffset);
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
    @Override
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

    public int getObjectIndex() {
        return pattern.getObjectIndex() + xPathOffset;
    }

    public int getTupleIndex() {
        return pattern.getTupleIndex() + xPathOffset;
    }

    public void setxPathOffset( int xPathOffset ) {
        this.xPathOffset = xPathOffset;
    }

    public int getxPathOffset() {
        return xPathOffset;
    }

    public boolean isFromXpathChunk() {
        return xPathOffset >= 1;
    }

    /**
     * Returns true if this declaration is a pattern declaration
     */
    public boolean isPatternDeclaration() {
        return ( this.pattern != null && this.pattern.getDeclaration() == this ) || this.getIdentifier().equals( "this" ) ;
    }

    public void setReadAccessor(ReadAccessor readAccessor) {
        this.readAccessor = readAccessor;
    }

    /**
     * Returns the Extractor expression
     */
    public ReadAccessor getExtractor() {
        return this.readAccessor;
    }

    public Class<?> getDeclarationClass() {
        if (declarationClass == null) {
            declarationClass = readAccessor != null ? readAccessor.getExtractToClass() : null;
        }
        return declarationClass;
    }

    public void setDeclarationClass( Class<?> declarationClass ) {
        this.declarationClass = declarationClass;
    }

    @Override
    public Object getValue(ReteEvaluator reteEvaluator, Tuple tuple) {
        return getValue( reteEvaluator, tuple.get( this ) );
    }

    public Object getValue(ReteEvaluator reteEvaluator, InternalFactHandle fh) {
        return getValue( reteEvaluator, fh.getObject() );
    }

    public Object getValue(ReteEvaluator reteEvaluator,
                           final Object object) {
        return this.readAccessor.getValue( reteEvaluator, object );
    }

    public char getCharValue(ReteEvaluator reteEvaluator,
                           final Object object) {
        return this.readAccessor.getCharValue(reteEvaluator, object);
    }

    public int getIntValue(ReteEvaluator reteEvaluator,
                           final Object object) {
        return this.readAccessor.getIntValue(reteEvaluator, object);
    }

    public byte getByteValue(ReteEvaluator reteEvaluator,
                             final Object object) {
        return this.readAccessor.getByteValue(reteEvaluator, object);
    }

    public short getShortValue(ReteEvaluator reteEvaluator,
                               final Object object) {
        return this.readAccessor.getShortValue(reteEvaluator, object);
    }

    public long getLongValue(ReteEvaluator reteEvaluator,
                             final Object object) {
        return this.readAccessor.getLongValue(reteEvaluator, object);
    }

    public float getFloatValue(ReteEvaluator reteEvaluator,
                               final Object object) {
        return this.readAccessor.getFloatValue(reteEvaluator, object);
    }

    public double getDoubleValue(ReteEvaluator reteEvaluator,
                                 final Object object) {
        return this.readAccessor.getDoubleValue(reteEvaluator, object);
    }

    public boolean getBooleanValue(ReteEvaluator reteEvaluator,
                                   final Object object) {
        return this.readAccessor.getBooleanValue(reteEvaluator, object);
    }

    public int getHashCode(ReteEvaluator reteEvaluator,
                           final Object object) {
        return this.readAccessor.getHashCode(reteEvaluator, object);
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
                return this.getClass().getDeclaredMethod("getValue",
                                                         ReteEvaluator.class, Object.class);
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
            cachedTypeName = ( getExtractor() != null && getDeclarationClass() != null )
                             ? canonicalName(getDeclarationClass())
                             : "java.lang.Object";
        }
        return cachedTypeName;
    }

    private transient String cachedBoxedTypeName;
    public String getBoxedTypeName() {
        if (cachedBoxedTypeName == null) {
            // we assume that null extractor errors are reported else where
            cachedBoxedTypeName = getExtractor() != null ? canonicalName(convertFromPrimitiveType(getDeclarationClass())) : "java.lang.Object";
        }
        return cachedBoxedTypeName;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public String toString() {
        return "(" + (this.readAccessor != null ? this.readAccessor.getValueType() : "null accessor") + ") " + this.identifier;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.pattern.getTupleIndex();
        result = PRIME * result + this.readAccessor.hashCode();
        result = PRIME * result + this.identifier.hashCode();
        result = PRIME * result + this.xPathOffset;
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

        return this.pattern.getPatternId() == other.pattern.getPatternId() &&
               this.identifier.equals(other.identifier) && this.readAccessor.equals(other.readAccessor) &&
               this.xPathOffset == other.xPathOffset;
    }

    public boolean isInternalFact() {
        return internalFact;
    }

    @Override
    public Declaration clone() {
        Declaration declr = new Declaration( this.identifier, this.readAccessor, this.pattern );
        declr.setBindingName(this.bindingName);
        declr.setxPathOffset(this.xPathOffset);
        return declr;
    }

    public Declaration cloneWithPattern(Pattern pattern) {
        Declaration declr = new Declaration( this.identifier, this.readAccessor, pattern );
        declr.setBindingName(this.bindingName);
        declr.setxPathOffset(this.xPathOffset);
        return declr;
    }
}
