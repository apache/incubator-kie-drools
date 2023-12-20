/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.base.rule.constraint;

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
import java.util.Objects;

import org.drools.base.base.AcceptsClassObjectType;
import org.drools.base.base.ClassObjectType;
import org.drools.base.base.DroolsQuery;
import org.drools.base.base.ObjectType;
import org.drools.base.base.ValueResolver;
import org.drools.base.phreak.ReactiveObject;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.ContextEntry;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.From;
import org.drools.base.rule.MutableTypeConstraint;
import org.drools.base.rule.accessor.DataProvider;
import org.drools.base.rule.accessor.PatternExtractor;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.util.ClassUtils;
import org.kie.api.runtime.rule.FactHandle;

import static org.drools.util.ClassUtils.convertFromPrimitiveType;

public class XpathConstraint extends MutableTypeConstraint<ContextEntry> {

    private LinkedList<XpathChunk> chunks;

    private Declaration declaration;
    private Declaration xpathStartDeclaration;

    public XpathConstraint() {
        this(new LinkedList<>());
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
    public boolean isAllowedCachedLeft(ContextEntry context, FactHandle handle) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAllowedCachedRight(BaseTuple tuple, ContextEntry context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ContextEntry createContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAllowed(FactHandle handle, ValueResolver valueResolver) {
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

    public ReadAccessor getReadAccessor() {
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
        Iterable<?> evaluate(ValueResolver valueResolver, BaseTuple leftBaseTuple, Object object);
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

        public Iterable<?> evaluate(ValueResolver valueResolver, BaseTuple leftBaseTuple, Object object) {
            return evaluateObject(valueResolver, leftBaseTuple, chunk, new ArrayList<>(), object);
        }

        private List<Object> evaluateObject(ValueResolver valueResolver, BaseTuple leftBaseTuple, XpathChunk chunk, List<Object> list, Object object) {
            Object result = chunk.evaluate(object);
            if (!chunk.lazy && result instanceof ReactiveObject ro) {
                ro.addTuple(leftBaseTuple);
            }
            if (chunk.iterate && result instanceof Iterable i) {
                for (Object value : i) {
                    if (!chunk.lazy && value instanceof ReactiveObject ro) {
                        ro.addTuple(leftBaseTuple);
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
            if (!(obj instanceof SingleChunkXpathEvaluator)) {
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
        private ObjectType returnedType;
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
                constraints = new ArrayList<>();
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
                return ((ClassObjectType) returnedType).getClassType();
            }
            try {
                Method accessor = classObjectType.getClassType().getMethod( field );
                return convertFromPrimitiveType( iterate ? getItemClass(accessor) : accessor.getReturnType() );
            } catch (NoSuchMethodException e) {
                throw new RuntimeException( e );
            }
        }

        public void setReturnedType( ObjectType returnedType ) {
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
            if (returnedType instanceof ParameterizedType pt) {
                Type[] parametricType = pt.getActualTypeArguments();
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

        public List<BetaConstraint> getBetaConstraints() {
            return getConstraintsByType(ConstraintType.BETA);
        }

        public List<XpathConstraint> getXpathConstraints() {
            return getConstraintsByType(ConstraintType.XPATH);
        }

        private <T> List<T> getConstraintsByType(ConstraintType constraintType) {
            if (constraints == null) {
                return Collections.emptyList();
            }
            List<T> typedConstraints = new ArrayList<>();
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
            if (!(obj instanceof XpathChunk)) {
                return false;
            }

            XpathChunk other = (XpathChunk) obj;

            return field.equals( other.field ) &&
                   index == other.index &&
                   iterate == other.iterate &&
                   lazy == other.lazy &&
                   array == other.array &&
                   Objects.equals(declaration, other.declaration);
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
        public Iterator getResults(BaseTuple tuple,
                                   ValueResolver valueResolver,
                                   Object providerContext) {
            FactHandle fh = tuple.getFactHandle();
            Object obj = fh.getObject();

            if (obj instanceof DroolsQuery) {
                obj = declaration.getValue(null, obj);
            }

            return xpathEvaluator.evaluate(valueResolver, tuple, obj).iterator();
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
            if (!(obj instanceof XpathChunk)){
                return false;
            }

            XpathDataProvider other = (XpathDataProvider) obj;

            return xpathEvaluator.equals( other.xpathEvaluator ) &&
                   Objects.equals(declaration, other.declaration);
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
