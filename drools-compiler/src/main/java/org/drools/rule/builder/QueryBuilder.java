package org.drools.rule.builder;

import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.extractors.ArrayElementReader;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.DescrBuildError;
import org.drools.core.util.AbstractHashTable;
import org.drools.lang.descr.QueryDescr;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.rule.IndexableConstraint;
import org.drools.rule.Pattern;
import org.drools.rule.Query;
import org.drools.spi.AcceptsReadAccessor;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.Constraint;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.ObjectType;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class QueryBuilder implements EngineElementBuilder {
    public Pattern build(final RuleBuildContext context,
                         final QueryDescr queryDescr) {
        ObjectType queryObjectType = ClassObjectType.DroolsQuery_ObjectType;
        final Pattern pattern = new Pattern( context.getNextPatternId(),
                                             0, // offset is 0 by default
                                             queryObjectType,
                                             null );
        
        final InternalReadAccessor extractor = PatternBuilder.getFieldReadAccessor(context, queryDescr, queryObjectType, "name", null, true);
        final QueryNameConstraint constraint = new QueryNameConstraint(extractor, queryDescr.getName());

        PatternBuilder.registerReadAccessor( context, queryObjectType, "name", constraint );

        // adds appropriate constraint to the pattern
        pattern.addConstraint( constraint );

        ObjectType argsObjectType = ClassObjectType.DroolsQuery_ObjectType;
        
        InternalReadAccessor arrayExtractor = PatternBuilder.getFieldReadAccessor( context, queryDescr, argsObjectType, "elements", null, true );

        String[] params = queryDescr.getParameters();
        String[] types = queryDescr.getParameterTypes();
        int i = 0;
        
        Declaration[] declarations = new Declaration[ params.length ];
        
        try {
            for ( i = 0; i < params.length; i++ ) {
                Declaration declr = pattern.addDeclaration( params[i] );
                
                // this bit is different, notice its the ArrayElementReader that we wire up to, not the declaration.
                ArrayElementReader reader = new ArrayElementReader( arrayExtractor,
                                                                    i,
                                                                    context.getDialect().getTypeResolver().resolveType( types[i] ) );
                PatternBuilder.registerReadAccessor( context, argsObjectType, "elements", reader );
                
                declr.setReadAccessor( reader );
                
                declarations[i] = declr;
             }
            
            ((Query)context.getRule()).setParameters( declarations );
            
        } catch ( ClassNotFoundException e ) {
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          queryDescr,
                                                          e,
                                                          "Unable to resolve type '" + types[i] + " for parameter" + params[i] ) );
        }
        return pattern;
    }

    public static class QueryNameConstraint implements
            AlphaNodeFieldConstraint,
            IndexableConstraint,
            AcceptsReadAccessor,
            Externalizable {

        private InternalReadAccessor readAccessor;
        private String queryName;

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

        public boolean isIndexable() {
            return true;
        }

        public FieldValue getField() {
            return null;
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
}
