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
    protected InternalReadAccessor rightReadAccessor;
    protected FieldValue field;

    public EvaluatorConstraint() { }

    public EvaluatorConstraint(FieldValue field, Evaluator evaluator, InternalReadAccessor extractor) {
        this.field = field;
        this.declarations = new Declaration[0];
        this.evaluator = evaluator;
        this.rightReadAccessor = extractor;
    }

    public EvaluatorConstraint(Declaration[] declarations, Evaluator evaluator, InternalReadAccessor extractor) {
        this.declarations = declarations;
        this.evaluator = evaluator;
        this.rightReadAccessor = extractor;
    }

    protected boolean isLiteral() {
        return declarations.length == 0;
    }

    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        if (isLiteral()) {
            return evaluator.evaluate(workingMemory, rightReadAccessor, handle, field);
        }

        return evaluator.evaluate( workingMemory,
                                   rightReadAccessor,
                                   handle,
                                   declarations[0].getExtractor(),
                                   handle );
    }

    public boolean isAllowedCachedLeft(ContextEntry context, InternalFactHandle handle) {
        if (isLiteral()) {
            return evaluator.evaluate( ((LiteralContextEntry) context).workingMemory,
                                       ((LiteralContextEntry) context).getFieldExtractor(),
                                       handle,
                                       field );
        }

        return evaluator.evaluateCachedLeft( ((VariableContextEntry) context).workingMemory,
                                             (VariableContextEntry) context,
                                             handle );
    }

    public boolean isAllowedCachedRight(LeftTuple tuple, ContextEntry context) {
        if (isLiteral()) {
            return evaluator.evaluate( ((LiteralContextEntry) context).workingMemory,
                                       ((LiteralContextEntry) context).getFieldExtractor(),
                                       ((LiteralContextEntry) context).getFactHandle(),
                                       field );
        }

        return evaluator.evaluateCachedRight( ((VariableContextEntry) context).workingMemory,
                                              (VariableContextEntry) context,
                                              tuple.get(declarations[0]));
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

    public Evaluator getEvaluator() {
        return evaluator;
    }

    public boolean isSelf() {
        return rightReadAccessor.isSelfReference();
    }

    public FieldValue getField() {
        return field;
    }

    protected InternalReadAccessor getRightReadAccessor() {
        return rightReadAccessor;
    }

    public EvaluatorConstraint clone() {
        if (isLiteral()) {
            return new EvaluatorConstraint(field, evaluator, rightReadAccessor);
        }

        Declaration[] clonedDeclarations = new Declaration[declarations.length];
        System.arraycopy(declarations, 0, clonedDeclarations, 0, declarations.length);
        return new EvaluatorConstraint(clonedDeclarations, evaluator, rightReadAccessor);
    }

    public ContextEntry createContextEntry() {
        return isLiteral() ? new LiteralContextEntry(rightReadAccessor) : VariableRestriction.createContextEntry(rightReadAccessor, declarations[0], evaluator);
    }

    // Externalizable

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(field);
        out.writeObject(declarations);
        out.writeObject(rightReadAccessor);
        out.writeObject(evaluator);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        field = (FieldValue) in.readObject();
        declarations = (Declaration[]) in.readObject();
        rightReadAccessor = (InternalReadAccessor) in.readObject();
        evaluator = (Evaluator) in.readObject();
    }

    protected static class LiteralContextEntry implements ContextEntry {

        private static final long   serialVersionUID = 510l;
        public InternalReadAccessor extractor;
        public InternalFactHandle   factHandle;
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
            factHandle = ( InternalFactHandle ) in.readObject();
            next = (ContextEntry) in.readObject();
            workingMemory = ( InternalWorkingMemory ) in .readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( extractor );
            out.writeObject( factHandle );
            out.writeObject( next );
            out.writeObject( workingMemory );
        }

        public InternalReadAccessor getFieldExtractor() {
            return this.extractor;
        }

        public InternalFactHandle getFactHandle() {
            return this.factHandle;
        }

        public ContextEntry getNext() {
            return this.next;
        }

        public void setNext(final ContextEntry entry) {
            this.next = entry;
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.factHandle = handle;
            this.workingMemory = workingMemory;
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final LeftTuple tuple) {
            this.workingMemory = workingMemory;
        }

        public void resetTuple() {
        }

        public void resetFactHandle() {
            this.factHandle = null;
        }

    }
}
