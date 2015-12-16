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
 *
 */

package org.drools.core.base.evaluators;

import org.drools.core.base.BaseEvaluator;
import org.drools.core.base.ValueType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.VariableRestriction;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

public abstract class PointInTimeEvaluator extends BaseEvaluator {
    protected long              initRange;
    protected long              finalRange;
    protected String            paramText;
    protected boolean           unwrapLeft;
    protected boolean           unwrapRight;

    public PointInTimeEvaluator() {
    }

    public PointInTimeEvaluator(final ValueType type,
                                final Operator operator,
                                final long[] parameters,
                                final String paramText,
                                final boolean unwrapLeft,
                                final boolean unwrapRight) {
        super( type, operator );
        this.paramText = paramText;
        this.unwrapLeft = unwrapLeft;
        this.unwrapRight = unwrapRight;
        this.setParameters( parameters );
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        super.readExternal( in );
        initRange = in.readLong();
        finalRange = in.readLong();
        paramText = (String) in.readObject();
        unwrapLeft = in.readBoolean();
        unwrapRight = in.readBoolean();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeLong( initRange );
        out.writeLong( finalRange );
        out.writeObject( paramText );
        out.writeBoolean( unwrapLeft );
        out.writeBoolean( unwrapRight );
    }

    @Override
    public boolean isTemporal() {
        return true;
    }

    protected abstract boolean evaluate(long rightTS, long leftTS);

    protected abstract long getLeftTimestamp( InternalFactHandle handle );

    protected abstract long getRightTimestamp( InternalFactHandle handle );

    private long getTimestamp(FieldValue value) {
        Object obj = value.getValue();
        if (obj instanceof Long) {
            return (Long)obj;
        }
        if (obj instanceof Date ) {
            return ( (Date) obj ).getTime();
        }
        throw new RuntimeException("Cannot extract timestamp from " + obj);
    }

    @Override
    public boolean evaluate(InternalWorkingMemory workingMemory,
                            final InternalReadAccessor extractor,
                            final InternalFactHandle object1,
                            final FieldValue object2) {
        long rightTS = extractor.isSelfReference() ?
                       getRightTimestamp( object1 ) :
                       extractor.getLongValue( workingMemory, object1.getObject() );
        long leftTS = getTimestamp(object2);
        return evaluate(rightTS, leftTS);
    }

    @Override
    public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                      final VariableRestriction.VariableContextEntry context,
                                      final InternalFactHandle right) {
        if ( context.leftNull ||
             context.extractor.isNullValue( workingMemory, right.getObject() ) ) {
            return false;
        }

        long leftTS = ((VariableRestriction.TimestampedContextEntry)context).timestamp;
        long rightTS = context.getFieldExtractor().isSelfReference() ?
                       getRightTimestamp(right) :
                       context.getFieldExtractor().getLongValue( workingMemory, right.getObject() );

        return evaluate(rightTS, leftTS);
    }

    @Override
    public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                       final VariableRestriction.VariableContextEntry context,
                                       final InternalFactHandle left) {
        if ( context.rightNull ||
             context.declaration.getExtractor().isNullValue( workingMemory, left.getObject() )) {
            return false;
        }

        long rightTS = ((VariableRestriction.TimestampedContextEntry)context).timestamp;
        long leftTS = context.declaration.getExtractor().isSelfReference() ?
                      getLeftTimestamp( left ) :
                      context.declaration.getExtractor().getLongValue( workingMemory, left.getObject() );

        return evaluate(rightTS, leftTS);
    }

    @Override
    public boolean evaluate(InternalWorkingMemory workingMemory,
                            final InternalReadAccessor extractor1,
                            final InternalFactHandle handle1,
                            final InternalReadAccessor extractor2,
                            final InternalFactHandle handle2) {
        if ( extractor1.isNullValue( workingMemory, handle1.getObject() ) ||
             extractor2.isNullValue( workingMemory, handle2.getObject() ) ) {
            return false;
        }

        long rightTS = extractor1.isSelfReference() ?
                       getRightTimestamp( handle1 ) :
                       extractor1.getLongValue( workingMemory, handle1.getObject() );

        long leftTS = extractor2.isSelfReference() ?
                      getLeftTimestamp( handle2 ) :
                      extractor2.getLongValue( workingMemory, handle2.getObject() );

        return evaluate(rightTS, leftTS);
    }

    private void setParameters(long[] parameters) {
        if ( parameters == null || parameters.length == 0 ) {
            // open bounded range
            this.initRange = 1;
            this.finalRange = Long.MAX_VALUE;
        } else if ( parameters.length == 1 ) {
            this.initRange = parameters[0];
            this.finalRange = Long.MAX_VALUE;
        } else if ( parameters.length == 2 ) {
            if ( parameters[0] <= parameters[1] ) {
                this.initRange = parameters[0];
                this.finalRange = parameters[1];
            } else {
                this.initRange = parameters[1];
                this.finalRange = parameters[0];
            }
        } else {
            throw new RuntimeException( "[PointInTimeEvaluator Evaluator]: Not possible to have more than 2 parameters: '" + paramText + "'" );
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + (int) (finalRange ^ (finalRange >>> 32));
        result = PRIME * result + (int) (initRange ^ (initRange >>> 32));
        result = PRIME * result + ((paramText == null) ? 0 : paramText.hashCode());
        result = PRIME * result + (unwrapLeft ? 1231 : 1237);
        result = PRIME * result + (unwrapRight ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( !super.equals( obj ) ) return false;
        if ( getClass() != obj.getClass() ) return false;
        PointInTimeEvaluator other = (PointInTimeEvaluator) obj;
        if ( finalRange != other.finalRange ) return false;
        if ( initRange != other.initRange ) return false;
        if ( paramText == null ) {
            if ( other.paramText != null ) return false;
        } else if ( !paramText.equals( other.paramText ) ) return false;
        return unwrapLeft == other.unwrapLeft && unwrapRight == other.unwrapRight;
    }

    @Override
    public String toString() {
        return this.getOperator().toString() + "[" + paramText + "]";
    }
}
