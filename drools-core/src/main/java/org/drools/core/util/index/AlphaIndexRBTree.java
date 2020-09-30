/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.util.index;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.base.SimpleValueType;
import org.drools.core.base.ValueType;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.spi.FieldValue;
import org.drools.core.util.AlphaNodeRBTree;
import org.drools.core.util.FastIterator;

/**
 * 
 * Alpha Node range indexing implementation backed by AlphaNodeRBTree
 *
 */
public class AlphaIndexRBTree implements Externalizable {

    private AlphaNodeRBTree tree;

    private CompositeObjectSinkAdapter.FieldIndex fieldIndex;

    private int size;

    public AlphaIndexRBTree() {
        // constructor for serialisation
    }

    public AlphaIndexRBTree(CompositeObjectSinkAdapter.FieldIndex fieldIndex) {
        this.fieldIndex = fieldIndex;
        tree = new AlphaNodeRBTree();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(tree);
        out.writeObject(fieldIndex);
        out.writeInt(size);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        tree = (AlphaNodeRBTree) in.readObject();
        fieldIndex = (CompositeObjectSinkAdapter.FieldIndex) in.readObject();
        size = in.readInt();
    }

    public void add(AlphaNode alphaNode) {
        Comparable key = extractKey(alphaNode);
        tree.insert(key, alphaNode);
        size++;
    }

    public void remove(AlphaNode alphaNode) {
        Comparable key = extractKey(alphaNode);
        tree.delete(key);
        size--;
    }

    private Comparable extractKey(AlphaNode alphaNode) {
        IndexableConstraint constraint = (IndexableConstraint) alphaNode.getConstraint();
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
        } else if (valueType.getSimpleType() == SimpleValueType.DATE) {
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
            return (Comparable) field.getValue(); // TODO: Confirm non-Comparable object can be used in comparable constraint?
        }
    }

    public int size() {
        return size;
    }

    public FastIterator ascMatchingAlphaNodeIterator(Object object) {
        Object value = fieldIndex.getFieldExtactor().getValue(object);

        // for ascTree, collect left
        return tree.collectLeft((Comparable) value);
    }

    public FastIterator descMatchingAlphaNodeIterator(Object object) {
        Object value = fieldIndex.getFieldExtactor().getValue(object);

        // for descTree, collect right
        return tree.collectRight((Comparable) value);
    }

    public FastIterator fastIterator() {
        return tree.fastIterator();
    }

    public void clear() {
        tree = new AlphaNodeRBTree();
    }
}
