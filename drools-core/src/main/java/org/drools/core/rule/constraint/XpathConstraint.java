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

import org.drools.core.WorkingMemory;
import org.drools.core.base.ClassObjectType;
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
import org.drools.core.spi.Tuple;

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

    public XpathConstraint() {
        this(new LinkedList<XpathChunk>());
    }

    private XpathConstraint(LinkedList<XpathChunk> chunks) {
        this.chunks = chunks;
        setType(ConstraintType.XPATH);
    }

    public XpathChunk addChunck(Class<?> clazz, String field, boolean iterate) {
        XpathChunk chunk = XpathChunk.get(clazz, field, iterate);
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
        throw new UnsupportedOperationException("org.drools.core.rule.constraint.XpathConstraint.replaceDeclaration -> TODO");
    }

    @Override
    public XpathConstraint clone() {
        XpathConstraint clone = new XpathConstraint(this.chunks);
        clone.setDeclaration( declaration.clone() );
        return clone;
    }

    @Override
    public boolean isTemporal() {
        // TODO ?
        return false;
    }

    @Override
    public boolean isAllowedCachedLeft(ContextEntry context, InternalFactHandle handle) {
        throw new UnsupportedOperationException("org.drools.core.rule.constraint.XpathConstraint.isAllowedCachedLeft -> TODO");

    }

    @Override
    public boolean isAllowedCachedRight(LeftTuple tuple, ContextEntry context) {
        throw new UnsupportedOperationException("org.drools.core.rule.constraint.XpathConstraint.isAllowedCachedRight -> TODO");

    }

    @Override
    public ContextEntry createContextEntry() {
        throw new UnsupportedOperationException("org.drools.core.rule.constraint.XpathConstraint.createContextEntry -> TODO");

    }

    @Override
    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        throw new UnsupportedOperationException("org.drools.core.rule.constraint.XpathConstraint.isAllowed -> TODO");

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("org.drools.core.rule.constraint.XpathConstraint.readExternal -> TODO");
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        throw new UnsupportedOperationException("org.drools.core.rule.constraint.XpathConstraint.writeExternal -> TODO");
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
        private final boolean iterate;
        private final Method accessor;
        private List<Constraint> constraints;

        private XpathChunk(Class<?> clazz, String field, boolean iterate, Method accessor) {
            this.clazz = clazz;
            this.field = field;
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
                return (T)accessor.invoke(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static XpathChunk get(Class<?> clazz, String field, boolean iterate) {
            Method accessor = getAccessor(clazz, field);
            if (accessor == null) {
                return null;
            }
            return new XpathChunk(clazz, field, iterate, accessor);
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
            From from = new From( new XpathDataProvider(new SingleChunkXpathEvaluator(this)) );
            from.setResultClass(getReturnedClass());
            return from;
        }

        public List<BetaNodeFieldConstraint> getBetaaConstraints() {
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
            return clazz.getSimpleName() + (iterate ? " / " : " . ") + field + (constraints != null ? " " + constraints : "");
        }
    }

    public static class XpathDataProvider implements DataProvider {

        private final XpathEvaluator xpathEvaluator;

        public XpathDataProvider(XpathEvaluator xpathEvaluator) {
            this.xpathEvaluator = xpathEvaluator;
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
        public Iterator getResults(Tuple tuple, WorkingMemory wm, PropagationContext ctx, Object providerContext) {
            LeftTuple leftTuple = (LeftTuple) tuple;
            InternalFactHandle fh = leftTuple.getHandle();
            Object obj = fh.getObject();
            return xpathEvaluator.evaluate((InternalWorkingMemory)wm, leftTuple, obj).iterator();
        }

        @Override
        public DataProvider clone() {
            return this;
        }

        @Override
        public void replaceDeclaration(Declaration declaration, Declaration resolved) {
            throw new UnsupportedOperationException("org.drools.core.rule.constraint.XpathConstraint.XpathDataProvider.replaceDeclaration -> TODO");
        }
    }
}
