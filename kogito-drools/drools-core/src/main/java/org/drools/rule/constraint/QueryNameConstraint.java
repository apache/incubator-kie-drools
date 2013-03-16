package org.drools.rule.constraint;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.base.DroolsQuery;
import org.drools.core.base.field.ObjectFieldImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.index.IndexUtil;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.rule.IndexableConstraint;
import org.drools.core.spi.AcceptsReadAccessor;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;

public class QueryNameConstraint implements
        AlphaNodeFieldConstraint,
        IndexableConstraint,
        AcceptsReadAccessor,
        Externalizable {

    private InternalReadAccessor readAccessor;
    private String queryName;
    private FieldValue fieldValue;

    public QueryNameConstraint() { }

    public QueryNameConstraint(InternalReadAccessor readAccessor, String queryName) {
        this.readAccessor = readAccessor;
        this.queryName = queryName;
    }

    public ContextEntry createContextEntry() {
        return null;
    }

    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        return ((DroolsQuery)handle.getObject()).getName().equals(queryName);
    }

    public boolean isUnification() {
        return false;
    }

    public boolean isIndexable(short nodeType) {
        return true;
    }

    public IndexUtil.ConstraintType getConstraintType() {
        return IndexUtil.ConstraintType.EQUAL;
    }

    public FieldValue getField() {
        if ( fieldValue  == null ) {
            fieldValue = new ObjectFieldImpl( queryName );
        }
        return fieldValue;
    }

    public AbstractHashTable.FieldIndex getFieldIndex() {
        return null;
    }

    public InternalReadAccessor getFieldExtractor() {
        return readAccessor;
    }

    public void setReadAccessor(InternalReadAccessor readAccessor) {
        this.readAccessor = readAccessor;
    }

    public Declaration[] getRequiredDeclarations() {
        return new Declaration[0];
    }

    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) { }

    public Constraint clone() {
        return new QueryNameConstraint( readAccessor, queryName );
    }
    
    public String getQueryName() {
        return this.queryName;
    }

    public ConstraintType getType() {
        return ConstraintType.ALPHA;
    }

    public boolean isTemporal() {
        return false;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        readAccessor = (InternalReadAccessor) in.readObject();
        queryName = (String) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( readAccessor );
        out.writeObject( queryName );
    }

    @Override
    public int hashCode() {
        return queryName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof QueryNameConstraint && queryName.equals(((QueryNameConstraint)obj).queryName);
    }

    @Override
    public String toString() {
        return "QueryNameConstraint for " + queryName;
    }
}
