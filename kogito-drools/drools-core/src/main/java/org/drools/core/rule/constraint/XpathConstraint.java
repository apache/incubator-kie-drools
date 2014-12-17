package org.drools.core.rule.constraint;

import org.drools.core.WorkingMemory;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.phreak.ReactiveObject;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectSink;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.drools.core.util.ClassUtils.getAccessor;

public class XpathConstraint extends MutableTypeConstraint {

    private final LinkedList<XpathChunk> chunks = new LinkedList<XpathChunk>();

    private Declaration declaration;

    public XpathConstraint() {
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
    public MutableTypeConstraint clone() {
        throw new UnsupportedOperationException("org.drools.core.rule.constraint.XpathConstraint.clone -> TODO");

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

    public From asFrom() {
        From from = new From( new XpathDataProvider(new XpathEvaluator(chunks)) );
        from.setResultClass(getResultClass());
        return from;
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

    private static class XpathEvaluator {
        private final LinkedList<XpathChunk> chunks;

        private XpathEvaluator(LinkedList<XpathChunk> chunks) {
            this.chunks = chunks;
        }

        private Iterable<?> evaluate(InternalWorkingMemory workingMemory, LeftTuple leftTuple, Object object) {
            Iterator<XpathChunk> xpathChunkIterator = chunks.iterator();
            List<Object> list = evaluateObject(workingMemory, leftTuple, xpathChunkIterator.next(), new ArrayList<Object>(), object);
            while (xpathChunkIterator.hasNext()) {
                list = evaluate(workingMemory, leftTuple, xpathChunkIterator.next(), list);
            }
            return list;
        }

        private List<Object> evaluate(InternalWorkingMemory workingMemory, LeftTuple leftTuple, XpathChunk chunk, Iterable<?> objects) {
            List<Object> list = new ArrayList<Object>();
            for (Object object : objects) {
                evaluateObject(workingMemory, leftTuple, chunk, list, object);
            }
            return list;
        }

        private List<Object> evaluateObject(InternalWorkingMemory workingMemory, LeftTuple leftTuple, XpathChunk chunk, List<Object> list, Object object) {
            Object result = chunk.evaluate(object);
            if (chunk.iterate && result instanceof Iterable) {
                for (Object value : (Iterable<?>) result) {
                    if (value instanceof ReactiveObject) {
                        ((ReactiveObject) value).addParent(object);
                    }
                    if (value != null && (chunk.constraints == null || match(workingMemory, leftTuple, chunk.constraints, value))) {
                        list.add(value);
                    }
                }
            } else {
                if (result instanceof ReactiveObject) {
                    ((ReactiveObject) result).addParent(object);
                }
                if (result != null && (chunk.constraints == null || match(workingMemory, leftTuple, chunk.constraints, result))) {
                    list.add(result);
                }
            }
            return list;
        }

        private boolean match(InternalWorkingMemory workingMemory, LeftTuple leftTuple, List<Constraint> constraints, Object object) {
            InternalFactHandle handle = new DefaultFactHandle(-1, object);
            for (Constraint constraint : constraints) {
                BetaNodeFieldConstraint betaConstraint = (BetaNodeFieldConstraint) constraint;
                ContextEntry context = betaConstraint.createContextEntry();
                if (context == null) {
                    if (!((AlphaNodeFieldConstraint)constraint).isAllowed(handle, workingMemory, null)) {
                        return false;
                    }
                } else {
                    context.updateFromFactHandle(workingMemory, handle);
                    if (!betaConstraint.isAllowedCachedRight(leftTuple, context)) {
                        return false;
                    }
                }
            }
            return true;
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
            constraints.add(constraint);
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

            if (obj instanceof ReactiveObject) {
                ((ReactiveObject) obj).setFactHandle(fh);
                ObjectSink sink = (ObjectSink)leftTuple.getSink().getLeftTupleSource();
                ((ReactiveObject) obj).addSink(sink);
            }

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
