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
package org.drools.core.util.index;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Collections;

import org.drools.base.base.ValueType;
import org.drools.base.rule.IndexableConstraint;
import org.drools.base.rule.accessor.FieldValue;
import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.util.index.RangeIndex.IndexType;

/**
 * 
 * Alpha Node range indexing implementation backed by RangeIndex per fieldIndex
 *
 */
public class AlphaRangeIndex implements Externalizable {

    private RangeIndex<Comparable, AlphaNode> rangeIndex;

    private CompositeObjectSinkAdapter.FieldIndex fieldIndex;

    private int size;

    public AlphaRangeIndex() {
        // constructor for serialisation
    }

    public AlphaRangeIndex(CompositeObjectSinkAdapter.FieldIndex fieldIndex) {
        this.fieldIndex = fieldIndex;
        rangeIndex = new RangeIndex<>();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(rangeIndex);
        out.writeObject(fieldIndex);
        out.writeInt(size);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        rangeIndex = (RangeIndex) in.readObject();
        fieldIndex = (CompositeObjectSinkAdapter.FieldIndex) in.readObject();
        size = in.readInt();
    }

    public void add(AlphaNode alphaNode) {
        IndexableConstraint constraint = (IndexableConstraint) alphaNode.getConstraint();
        Comparable key = extractKey(constraint);
        IndexType indexType = extractIndexType(constraint);
        AlphaNode previous = rangeIndex.addIndex(indexType, key, alphaNode);
        if (previous != null) {
            throw new IllegalStateException("Index conflict with " + alphaNode + " and " + previous + ". Please report this to Drools team." +
                    " You can workaround this issue by setting system property 'drools.alphaNodeRangeIndexThreshold' to '0'");
        }
        size++;
    }

    public void remove(AlphaNode alphaNode) {
        IndexableConstraint constraint = (IndexableConstraint) alphaNode.getConstraint();
        Comparable key = extractKey(constraint);
        IndexType indexType = extractIndexType(constraint);
        rangeIndex.removeIndex(indexType, key);
        size--;
    }

    private Comparable extractKey(IndexableConstraint constraint) {
        FieldValue field = constraint.getField();
        ValueType valueType = fieldIndex.getFieldExtractor().getValueType();
        if (valueType == ValueType.PCHAR_TYPE || valueType == ValueType.CHAR_TYPE) {
            return field.getCharValue();
        } else if (valueType == ValueType.PBYTE_TYPE || valueType == ValueType.BYTE_TYPE) {
            return field.getByteValue();
        } else if (valueType == ValueType.PSHORT_TYPE || valueType == ValueType.SHORT_TYPE) {
            return field.getShortValue();
        } else if (valueType == ValueType.PINTEGER_TYPE || valueType == ValueType.INTEGER_TYPE) {
            return field.getIntValue();
        } else if (valueType == ValueType.PLONG_TYPE || valueType == ValueType.LONG_TYPE) {
            return field.getLongValue();
        } else if (valueType == ValueType.PFLOAT_TYPE || valueType == ValueType.FLOAT_TYPE) {
            return field.getFloatValue();
        } else if (valueType == ValueType.PDOUBLE_TYPE || valueType == ValueType.DOUBLE_TYPE) {
            return field.getDoubleValue();
        } else if (valueType == ValueType.PBOOLEAN_TYPE || valueType == ValueType.BOOLEAN_TYPE) {
            return field.getBooleanValue();
        } else if (valueType == ValueType.STRING_TYPE) {
            return (Comparable) field.getValue();
        } else if (valueType.isDate()) {
            return (Comparable) field.getValue();
        } else if (valueType == ValueType.ARRAY_TYPE) {
            return (Comparable) field.getValue();
        } else if (valueType == ValueType.OBJECT_TYPE) {
            return (Comparable) field.getValue();
        } else if (valueType == ValueType.TRAIT_TYPE) {
            return (Comparable) field.getValue();
        } else if (valueType == ValueType.BIG_DECIMAL_TYPE) {
            return field.getBigDecimalValue();
        } else if (valueType == ValueType.BIG_INTEGER_TYPE) {
            return field.getBigIntegerValue();
        } else if (valueType == ValueType.CLASS_TYPE) {
            return (Comparable) field.getValue();
        } else {
            return (Comparable) field.getValue();
        }
    }

    private IndexType extractIndexType(IndexableConstraint constraint) {
        ConstraintTypeOperator constraintType = constraint.getConstraintType();
        switch (constraintType) {
            case LESS_THAN:
                return IndexType.LT;
            case LESS_OR_EQUAL:
                return IndexType.LE;
            case GREATER_THAN:
                return IndexType.GT;
            case GREATER_OR_EQUAL:
                return IndexType.GE;
            default:
                break;
        }
        throw new RuntimeException("Non relational operator must not be added to range index : constraint = " + constraint + ", constraintType = " + constraintType);
    }

    public int size() {
        return size;
    }

    public Collection<AlphaNode> getMatchingAlphaNodes(Object object) {
        Object value = fieldIndex.getFieldExtractor().getValue(object);
        if (value == null) {
            return Collections.emptyList();
        }
        return rangeIndex.getValues((Comparable) value);
    }

    public Collection<AlphaNode> getAllValues() {
        return rangeIndex.getAllValues();
    }

    public void clear() {
        rangeIndex = new RangeIndex<>();
    }

    public CompositeObjectSinkAdapter.FieldIndex getFieldIndex() {
        return fieldIndex;
    }
}
