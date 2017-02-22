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

package org.drools.core.rule.constraint;

import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DroolsQuery;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.phreak.ReactiveObject;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.From;
import org.drools.core.rule.MutableTypeConstraint;
import org.drools.core.spi.AcceptsClassObjectType;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.PatternExtractor;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.ClassUtils;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.drools.core.util.ClassUtils.areNullSafeEquals;
import static org.drools.core.util.ClassUtils.convertFromPrimitiveType;

public class XpathConstraint extends MutableTypeConstraint {

    private LinkedList<XpathChunk> chunks;

    private Declaration declaration;
    private Declaration xpathStartDeclaration;

    public XpathConstraint() {
        this(new LinkedList<XpathChunk>());
    }

    private XpathConstraint(LinkedList<XpathChunk> chunks) {
        this.chunks = chunks;
    }

    public XpathChunk addChunck(Class<?> clazz, String field, int index, boolean iterate, boolean lazy) {
        XpathChunk chunk = XpathChunk.get(clazz, field, index, iterate, lazy);
        if (chunk != null) {
            chunks.add(chunk);
        }
        return chunk;
    }

    @Override
    public ConstraintType getType() {
        return ConstraintType.XPATH;
    }

    @Override
    public Declaration[] getRequiredDeclarations() {
        // TODO ?
        return new Declaration[0];
    }

    @Override
    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XpathConstraint clone() {
        XpathConstraint clone = new XpathConstraint(this.chunks);
        if (declaration != null) {
            clone.setDeclaration( declaration.clone() );
        }
        if (xpathStartDeclaration != null) {
            clone.setXpathStartDeclaration( xpathStartDeclaration.clone() );
        }
        return clone;
    }

    @Override
    public boolean isTemporal() {
        // TODO ?
        return false;
    }

    @Override
    public boolean isAllowedCachedLeft(ContextEntry context, InternalFactHandle handle) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAllowedCachedRight(Tuple tuple, ContextEntry context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ContextEntry createContextEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        chunks =  (LinkedList<XpathChunk>) in.readObject();
        declaration = (Declaration) in.readObject();
        xpathStartDeclaration = (Declaration) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(chunks);
        out.writeObject(declaration);
        out.writeObject(xpathStartDeclaration);
    }

    public LinkedList<XpathChunk> getChunks() {
        return chunks;
    }

    public Class<?> getResultClass() {
        return chunks.isEmpty() ? Object.class : chunks.getLast().getReturnedClass();
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(Declaration declaration) {
        declaration.setReadAccessor( getReadAccessor() );
        this.declaration = declaration;
    }

    public InternalReadAccessor getReadAccessor() {
        return new PatternExtractor( new ClassObjectType( getResultClass() ) );
    }

    public Declaration getXpathStartDeclaration() {
        return xpathStartDeclaration;
    }

    public void setXpathStartDeclaration( Declaration xpathStartDeclaration ) {
        this.xpathStartDeclaration = xpathStartDeclaration;
        chunks.get(0).declaration = xpathStartDeclaration;
    }

    private interface XpathEvaluator {
        Iterable<?> evaluate(InternalWorkingMemory workingMemory, Tuple leftTuple, Object object);
    }

    @Override
    public String toString() {
        return chunks.toString();
    }

    private static class SingleChunkXpathEvaluator implements XpathEvaluator {
        private final XpathChunk chunk;

        private SingleChunkXpathEvaluator(XpathChunk chunk) {
            this.chunk = chunk;
        }

        public Iterable<?> evaluate(InternalWorkingMemory workingMemory, Tuple leftTuple, Object object) {
            return evaluateObject(workingMemory, leftTuple, chunk, new ArrayList<Object>(), object);
        }

        private List<Object> evaluateObject(InternalWorkingMemory workingMemory, Tuple leftTuple, XpathChunk chunk, List<Object> list, Object object) {
            Object result = chunk.evaluate(object);
            if (!chunk.lazy && result instanceof ReactiveObject) {
                ((ReactiveObject) result).addLeftTuple(leftTuple);
            }
            if (chunk.iterate && result instanceof Iterable) {
                for (Object value : (Iterable<?>) result) {
                    if (!chunk.lazy && value instanceof ReactiveObject) {
                        ((ReactiveObject) value).addLeftTuple(leftTuple);
                    }
                    if (value != null) {
                        list.add(value);
                    }
                }
            } else if (result != null) {
                list.add(result);
            }
            return list;
        }

        @Override
        public String toString() {
            return chunk.toString();
        }

        @Override
        public boolean equals( Object obj ) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !(obj instanceof SingleChunkXpathEvaluator)) {
                return false;
            }

            SingleChunkXpathEvaluator other = (SingleChunkXpathEvaluator) obj;

            return chunk.equals( other.chunk );
        }

        @Override
        public int hashCode() {
            return chunk.hashCode();
        }
    }

    public static class XpathChunk implements AcceptsClassObjectType, Externalizable {

        private String field;
        private int index;
        private boolean iterate;
        private boolean lazy;
        private boolean array;

        private List<Constraint> constraints;
        private Declaration declaration;
        private ClassObjectType classObjectType;
        private ClassObjectType returnedType;
        private Method accessor;
        
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(field);
            out.writeInt(index);
            out.writeBoolean(iterate);
            out.writeBoolean(lazy);
            out.writeBoolean(array);
            
            out.writeObject(constraints);
            out.writeObject(declaration);
            out.writeObject(classObjectType);
            out.writeObject(returnedType);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            field = (String) in.readObject();
            index = in.readInt();
            iterate = in.readBoolean();
            lazy = in.readBoolean();
            array = in.readBoolean();
            
            constraints = (List<Constraint>) in.readObject();
            declaration = (Declaration) in.readObject();
            classObjectType = (ClassObjectType) in.readObject();
            returnedType = (ClassObjectType) in.readObject();
        }
        
        /**
         * NOT INTENDED FOR ACTUAL USE, only for Java Serialization mechanism purpose only.
         */
        public XpathChunk() {
            // for serialization only purposes.
        }

        private XpathChunk(String field, int index, boolean iterate, boolean lazy, boolean array) {
            this.field = field;
            this.index = index;
            this.iterate = iterate;
            this.lazy = lazy;
            this.array = array;
        }

        private static XpathChunk get(Class<?> clazz, String field, int index, boolean iterate, boolean lazy) {
            Method accessor = ClassUtils.getAccessor( clazz, field );
            if (accessor == null) {
                return null;
            }
            return new XpathChunk(accessor.getName(), index, iterate, lazy, iterate && accessor.getReturnType().isArray());
        }

        private Method getAccessor() {
            if (accessor == null) {
                try {
                    accessor = classObjectType.getClassType().getMethod( field );
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException( e );
                }
            }
            return accessor;
        }

        public void addConstraint(Constraint constraint) {
            if (constraints == null) {
                constraints = new ArrayList<Constraint>();
            }
            setConstraintType((MutableTypeConstraint)constraint);
            constraints.add(constraint);
        }

        private void setConstraintType(final MutableTypeConstraint constraint) {
            final Declaration[] declarations = constraint.getRequiredDeclarations();

            boolean isAlphaConstraint = true;
            for ( int i = 0; isAlphaConstraint && i < declarations.length; i++ ) {
                if ( !declarations[i].isGlobal() ) {
                    isAlphaConstraint = false;
                }
            }

            ConstraintType type = isAlphaConstraint ? ConstraintType.ALPHA : ConstraintType.BETA;
            constraint.setType(type);
        }

        public Object evaluate(Object obj) {
            try {
                Object result = getAccessor().invoke( obj );
                if (array) {
                    result = Arrays.asList( (Object[]) result );
                }
                if (index >= 0) {
                    result = ((List)result).subList( index, index+1 );
                }
                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Class<?> getReturnedClass() {
            if (returnedType != null) {
                return returnedType.getClassType();
            }
            try {
                Method accessor = classObjectType.getClassType().getMethod( field );
                return convertFromPrimitiveType( iterate ? getItemClass(accessor) : accessor.getReturnType() );
            } catch (NoSuchMethodException e) {
                throw new RuntimeException( e );
            }
        }

        public void setReturnedType( ClassObjectType returnedType ) {
            this.returnedType = returnedType;
        }

        private Class<?> getItemClass(Method accessor) {
            Class<?> lastReturnedClass = accessor.getReturnType();
            if (Iterable.class.isAssignableFrom(lastReturnedClass)) {
                return getParametricType(accessor);
            }
            if (lastReturnedClass.isArray()) {
                return lastReturnedClass.getComponentType();
            }
            return lastReturnedClass;
        }

        private Class<?> getParametricType(Method accessor) {
            Type returnedType = accessor.getGenericReturnType();
            if (returnedType instanceof ParameterizedType) {
                Type[] parametricType = ((ParameterizedType) returnedType).getActualTypeArguments();
                if (parametricType.length > 0) {
                    return parametricType[0] instanceof Class ?
                           (Class<?>) parametricType[0] :
                           (Class<?>) ( (ParameterizedType) parametricType[0] ).getRawType();
                }
            }
            return Object.class;
        }

        public From asFrom() {
            From from = new From( new XpathDataProvider(new SingleChunkXpathEvaluator(this), declaration ) );
            from.setResultClass(getReturnedClass());
            return from;
        }

        public List<AlphaNodeFieldConstraint> getAlphaConstraints() {
            return getConstraintsByType(ConstraintType.ALPHA);
        }

        public List<BetaNodeFieldConstraint> getBetaConstraints() {
            return getConstraintsByType(ConstraintType.BETA);
        }

        public List<XpathConstraint> getXpathConstraints() {
            return getConstraintsByType(ConstraintType.XPATH);
        }

        private <T> List<T> getConstraintsByType(ConstraintType constraintType) {
            if (constraints == null) {
                return Collections.emptyList();
            }
            List<T> typedConstraints = new ArrayList<T>();
            for (Constraint constraint : constraints) {
                if (constraint.getType() == constraintType) {
                    typedConstraints.add( (T) constraint );
                }
            }
            return typedConstraints;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder( (lazy ? "?" : "") + classObjectType.getClassType().getSimpleName() );
            if (iterate) {
                sb.append( "/" );
            } else {
                sb.append( "." );
            }
            sb.append( field );
            if (index >= 0) {
                sb.append( "[" ).append( index ).append( "]" );
            }
            if (constraints != null && !constraints.isEmpty()) {
                sb.append( "{" );
                sb.append( constraints.get(0) );
                for (int i = 1; i < constraints.size(); i++) {
                    sb.append( ", " ).append( constraints.get( i ) );
                }
                sb.append( "}" );
            }
            return sb.toString();
        }

        @Override
        public void setClassObjectType( ClassObjectType classObjectType ) {
            this.classObjectType = classObjectType;
        }

        @Override
        public boolean equals( Object obj ) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !(obj instanceof XpathChunk)) {
                return false;
            }

            XpathChunk other = (XpathChunk) obj;

            return field.equals( other.field ) &&
                   index == other.index &&
                   iterate == other.iterate &&
                   lazy == other.lazy &&
                   array == other.array &&
                   areNullSafeEquals(declaration, other.declaration);
        }

        @Override
        public int hashCode() {
            int hash = 23 * field.hashCode() + 29 * index;
            if (declaration != null) {
                hash += 31 * declaration.hashCode();
            }
            if (iterate) {
                hash += 37;
            }
            if (lazy) {
                hash += 41;
            }
            if (array) {
                hash += 43;
            }
            return hash;
        }
    }

    public static class XpathDataProvider implements DataProvider {

        private final XpathEvaluator xpathEvaluator;
        private final Declaration declaration;

        public XpathDataProvider( XpathEvaluator xpathEvaluator, Declaration declaration ) {
            this.xpathEvaluator = xpathEvaluator;
            this.declaration = declaration;
        }

        @Override
        public Declaration[] getRequiredDeclarations() {
            return new Declaration[0];
        }

        @Override
        public Object createContext() {
            return null;
        }

        @Override
        public Iterator getResults(Tuple leftTuple, InternalWorkingMemory wm, PropagationContext ctx, Object providerContext) {
            InternalFactHandle fh = leftTuple.getFactHandle();
            Object obj = fh.getObject();

            if (obj instanceof DroolsQuery) {
                obj = ((DroolsQuery)obj).getElements()[declaration.getPattern().getOffset()];
            }

            return xpathEvaluator.evaluate(wm, leftTuple, obj).iterator();
        }

        @Override
        public DataProvider clone() {
            return this;
        }

        @Override
        public void replaceDeclaration(Declaration declaration, Declaration resolved) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return xpathEvaluator.toString();
        }

        @Override
        public boolean equals( Object obj ) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !(obj instanceof XpathChunk)) {
                return false;
            }

            XpathDataProvider other = (XpathDataProvider) obj;

            return xpathEvaluator.equals( other.xpathEvaluator ) &&
                   areNullSafeEquals(declaration, other.declaration);
        }

        @Override
        public int hashCode() {
            int hash = 31 * xpathEvaluator.hashCode();
            if (declaration != null) {
                hash += 37 * declaration.hashCode();
            }
            return hash;
        }

        @Override
        public boolean isReactive() {
            return true;
        }
    }
}
