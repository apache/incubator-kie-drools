/*
 * Copyright 2015 JBoss Inc
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
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.From;
import org.drools.core.rule.MutableTypeConstraint;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.PatternExtractor;
import org.drools.core.spi.PropagationContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.drools.core.util.ClassUtils.getAccessor;

public class XpathConstraint extends MutableTypeConstraint {

    private final LinkedList<XpathChunk> chunks;

    private Declaration declaration;
    private Declaration xpathStartDeclaration;

    public XpathConstraint() {
        this(new LinkedList<XpathChunk>());
    }

    private XpathConstraint(LinkedList<XpathChunk> chunks) {
        this.chunks = chunks;
        setType(ConstraintType.XPATH);
    }

    public XpathChunk addChunck(Class<?> clazz, String field, int index, boolean iterate) {
        XpathChunk chunk = XpathChunk.get(clazz, field, index, iterate);
        if (chunk != null) {
            chunks.add(chunk);
        }
        return chunk;
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
    public boolean isAllowedCachedRight(LeftTuple tuple, ContextEntry context) {
        throw new UnsupportedOperationException();

    }

    @Override
    public ContextEntry createContextEntry() {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        throw new UnsupportedOperationException();
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
        Iterable<?> evaluate(InternalWorkingMemory workingMemory, LeftTuple leftTuple, Object object);
    }

    private static class SingleChunkXpathEvaluator implements XpathEvaluator {
        private final XpathChunk chunk;

        private SingleChunkXpathEvaluator(XpathChunk chunk) {
            this.chunk = chunk;
        }

        public Iterable<?> evaluate(InternalWorkingMemory workingMemory, LeftTuple leftTuple, Object object) {
            return evaluateObject(workingMemory, leftTuple, chunk, new ArrayList<Object>(), object);
        }

        private List<Object> evaluateObject(InternalWorkingMemory workingMemory, LeftTuple leftTuple, XpathChunk chunk, List<Object> list, Object object) {
            Object result = chunk.evaluate(object);
            if (result instanceof ReactiveObject) {
                ((ReactiveObject) result).addLeftTuple(leftTuple);
            }
            if (chunk.iterate && result instanceof Iterable) {
                for (Object value : (Iterable<?>) result) {
                    if (value instanceof ReactiveObject) {
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
    }

    public static class XpathChunk {
        
        private final Class<?> clazz;
        private final String field;
        private final int index;
        private final boolean iterate;
        private final Method accessor;
        private List<Constraint> constraints;
        private Declaration declaration;

        private XpathChunk(Class<?> clazz, String field, int index, boolean iterate, Method accessor) {
            this.clazz = clazz;
            this.field = field;
            this.index = index;
            this.iterate = iterate;
            this.accessor = accessor;
            this.accessor.setAccessible(true);
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

        public <T> T evaluate(Object obj) {
            try {
                T result = (T) accessor.invoke(obj);
                if (index >= 0) {
                    result = (T) ((List)result).subList( index, index+1 );
                }
                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static XpathChunk get(Class<?> clazz, String field, int index, boolean iterate) {
            Method accessor = getAccessor(clazz, field);
            if (accessor == null) {
                return null;
            }
            return new XpathChunk(clazz, field, index, iterate, accessor);
        }

        public Class<?> getReturnedClass() {
            Class<?> lastReturnedClass = accessor.getReturnType();
            return iterate && Iterable.class.isAssignableFrom(lastReturnedClass) ?
                   getParametricType() :
                   lastReturnedClass;
        }

        public Class<?> getParametricType() {
            Type returnedType = accessor.getGenericReturnType();
            if (returnedType instanceof ParameterizedType) {
                Type[] parametricType = ((ParameterizedType) returnedType).getActualTypeArguments();
                if (parametricType.length > 0) {
                    return (Class<?>)parametricType[0];
                }
            }
            return Object.class;
        }

        public From asFrom() {
            From from = new From( new XpathDataProvider(new SingleChunkXpathEvaluator(this), declaration ) );
            from.setResultClass(getReturnedClass());
            return from;
        }

        public List<BetaNodeFieldConstraint> getBetaConstraints() {
            if (constraints == null) {
                return Collections.emptyList();
            }
            List<BetaNodeFieldConstraint> betaConstraints = new ArrayList<BetaNodeFieldConstraint>();
            for (Constraint constraint : constraints) {
                if (constraint.getType() == ConstraintType.BETA) {
                    betaConstraints.add(((BetaNodeFieldConstraint) constraint));
                }
            }
            return betaConstraints;
        }

        public List<AlphaNodeFieldConstraint> getAlphaConstraints() {
            if (constraints == null) {
                return Collections.emptyList();
            }
            List<AlphaNodeFieldConstraint> alphaConstraints = new ArrayList<AlphaNodeFieldConstraint>();
            for (Constraint constraint : constraints) {
                if (constraint.getType() == ConstraintType.ALPHA) {
                    alphaConstraints.add(((AlphaNodeFieldConstraint) constraint));
                }
            }
            return alphaConstraints;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder( clazz.getSimpleName() );
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
        public Iterator getResults(LeftTuple leftTuple, InternalWorkingMemory wm, PropagationContext ctx, Object providerContext) {
            InternalFactHandle fh = leftTuple.getHandle();
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
    }
}
