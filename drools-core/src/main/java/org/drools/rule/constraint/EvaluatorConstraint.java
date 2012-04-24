package org.drools.rule.constraint;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.rule.IntervalProviderConstraint;
import org.drools.rule.MutableTypeConstraint;
import org.drools.rule.VariableRestriction;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.time.Interval;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class EvaluatorConstraint extends MutableTypeConstraint implements IntervalProviderConstraint {

    protected Declaration[] declarations;
    protected Evaluator evaluator;
    protected InternalReadAccessor extractor;
    protected FieldValue field;

    public EvaluatorConstraint() { }

    public EvaluatorConstraint(FieldValue field, Evaluator evaluator, InternalReadAccessor extractor) {
        this.field = field;
        this.declarations = new Declaration[0];
        this.evaluator = evaluator;
        this.extractor = extractor;
    }

    public EvaluatorConstraint(Declaration[] declarations, Evaluator evaluator, InternalReadAccessor extractor) {
        this.declarations = declarations;
        this.evaluator = evaluator;
        this.extractor = extractor;
    }

    protected boolean isLiteral() {
        return declarations.length == 0;
    }

    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        if (isLiteral()) {
            return evaluator.evaluate(workingMemory, extractor, handle.getObject(), field);
        }

        return evaluator.evaluate( workingMemory,
                                   extractor,
                                   evaluator.prepareLeftObject( handle ),
                                   declarations[0].getExtractor(),
                                   evaluator.prepareRightObject( handle ) );
    }

    public boolean isAllowedCachedLeft(ContextEntry context, InternalFactHandle handle) {
        if (isLiteral()) {
            return evaluator.evaluate( ((LiteralContextEntry) context).workingMemory,
                                       ((LiteralContextEntry) context).getFieldExtractor(),
                                       handle.getObject(),
                                       field );
        }

        return evaluator.evaluateCachedLeft( ((VariableContextEntry) context).workingMemory,
                                             (VariableContextEntry) context,
                                             evaluator.prepareRightObject(handle));
    }

    public boolean isAllowedCachedRight(LeftTuple tuple, ContextEntry context) {
        if (isLiteral()) {
            return evaluator.evaluate( ((LiteralContextEntry) context).workingMemory,
                                       ((LiteralContextEntry) context).getFieldExtractor(),
                                       ((LiteralContextEntry) context).getObject(),
                                       field );
        }

        return evaluator.evaluateCachedRight( ((VariableContextEntry) context).workingMemory,
                                              (VariableContextEntry) context,
                                              evaluator.prepareLeftObject(tuple.get(declarations[0])));
    }

    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {
        if ( declarations[0].equals( oldDecl ) ) {
            declarations[0] = newDecl;
        }
    }

    public Declaration[] getRequiredDeclarations() {
        return declarations;
    }

    public boolean isTemporal() {
        return evaluator != null && evaluator.isTemporal();
    }

    public Interval getInterval() {
        return evaluator == null ? null : evaluator.getInterval();
    }

    protected Evaluator getEvaluator() {
        return evaluator;
    }

    protected FieldValue getField() {
        return field;
    }

    protected InternalReadAccessor getExtractor() {
        return extractor;
    }

    public EvaluatorConstraint clone() {
        if (isLiteral()) {
            return new EvaluatorConstraint(field, evaluator, extractor);
        }

        Declaration[] clonedDeclarations = new Declaration[declarations.length];
        System.arraycopy(declarations, 0, clonedDeclarations, 0, declarations.length);
        return new EvaluatorConstraint(clonedDeclarations, evaluator, extractor);
    }

    public ContextEntry createContextEntry() {
        return isLiteral() ? new LiteralContextEntry(extractor) : VariableRestriction.createContextEntry(extractor, declarations[0], evaluator);
    }

    // Externalizable

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(field);
        out.writeObject(declarations);
        out.writeObject(extractor);
        out.writeObject(evaluator);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        field = (FieldValue) in.readObject();
        declarations = (Declaration[]) in.readObject();
        extractor = (InternalReadAccessor) in.readObject();
        evaluator = (Evaluator) in.readObject();
    }

    protected static class LiteralContextEntry implements ContextEntry {

        private static final long   serialVersionUID = 510l;
        public InternalReadAccessor extractor;
        public Object               object;
        public ContextEntry         next;
        public InternalWorkingMemory workingMemory;

        public LiteralContextEntry() {
        }

        public LiteralContextEntry(final InternalReadAccessor extractor) {
            this.extractor = extractor;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            extractor = (InternalReadAccessor) in.readObject();
            object = in.readObject();
            next = (ContextEntry) in.readObject();
            workingMemory = ( InternalWorkingMemory ) in .readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( extractor );
            out.writeObject( object );
            out.writeObject( next );
            out.writeObject( workingMemory );
        }

        public InternalReadAccessor getFieldExtractor() {
            return this.extractor;
        }

        public Object getObject() {
            return this.object;
        }

        public ContextEntry getNext() {
            return this.next;
        }

        public void setNext(final ContextEntry entry) {
            this.next = entry;
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = handle.getObject();
            this.workingMemory = workingMemory;
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final LeftTuple tuple) {
            this.workingMemory = workingMemory;
        }

        public void resetTuple() {
        }

        public void resetFactHandle() {
            this.object = null;
        }

    }
}
