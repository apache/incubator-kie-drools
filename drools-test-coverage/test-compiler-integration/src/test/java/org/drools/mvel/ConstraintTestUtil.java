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
package org.drools.mvel;

import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.drools.mvel.accessors.ClassFieldReader;
import org.drools.mvel.field.FieldFactory;
import org.drools.mvel.field.LongFieldImpl;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.base.rule.accessor.FieldValue;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.mvel.model.Cheese;

public class ConstraintTestUtil {

    public static AlphaNodeFieldConstraint createCheeseTypeEqualsConstraint(ClassFieldAccessorStore store, String rightvalue, boolean useLambdaConstraint) {
        final ClassFieldReader extractor = store.getReader(Cheese.class, "type");
        if (useLambdaConstraint) {
            return LambdaConstraintTestUtil.createCheeseTypeEqualsConstraint(rightvalue, extractor.getIndex());
        } else {
            final FieldValue field = FieldFactory.getInstance().getFieldValue(rightvalue);
            return new MVELConstraintTestUtil("type == \"" + rightvalue + "\"", field, extractor);
        }
    }

    public static AlphaNodeFieldConstraint createCheeseTypeEqualsConstraint(ReadAccessor extractor, String rightvalue, boolean useLambdaConstraint) {
        if (useLambdaConstraint) {
            return LambdaConstraintTestUtil.createCheeseTypeEqualsConstraint(rightvalue, extractor.getIndex());
        } else {
            final FieldValue field = FieldFactory.getInstance().getFieldValue(rightvalue);
            return new MVELConstraintTestUtil("type == \"" + rightvalue + "\"", field, extractor);
        }
    }

    public static AlphaNodeFieldConstraint createCheeseCharTypeEqualsConstraint(ReadAccessor extractor, int rightvalue, boolean useLambdaConstraint) {
        if (useLambdaConstraint) {
            return LambdaConstraintTestUtil.createCheeseCharTypeEqualsConstraint((char) rightvalue, extractor.getIndex());
        } else {
            return new MVELConstraintTestUtil("charType == " + rightvalue, new LongFieldImpl(rightvalue), extractor);
        }
    }

    public static AlphaNodeFieldConstraint createCheeseCharObjectTypeEqualsConstraint(ReadAccessor extractor, int rightvalue, boolean useLambdaConstraint) {
        if (useLambdaConstraint) {
            return LambdaConstraintTestUtil.createCheeseCharObjectTypeEqualsConstraint((char) rightvalue, extractor.getIndex());
        } else {
            return new MVELConstraintTestUtil("charObjectType == " + rightvalue, new LongFieldImpl(rightvalue), extractor);
        }
    }

    public static AlphaNodeFieldConstraint createCheesePriceEqualsConstraint(ReadAccessor extractor, int rightvalue, boolean useLambdaConstraint) {
        if (useLambdaConstraint) {
            return LambdaConstraintTestUtil.createCheesePriceEqualsConstraint(rightvalue, extractor.getIndex());
        } else {
            final FieldValue field = FieldFactory.getInstance().getFieldValue(rightvalue);
            return new MVELConstraintTestUtil("price == " + rightvalue, field, extractor);
        }
    }

    public static AlphaNodeFieldConstraint createCheesePriceGreaterConstraint(ReadAccessor extractor, int rightvalue, boolean useLambdaConstraint) {
        if (useLambdaConstraint) {
            return LambdaConstraintTestUtil.createCheesePriceGreaterConstraint(rightvalue, extractor.getIndex());
        } else {
            final FieldValue field = FieldFactory.getInstance().getFieldValue(rightvalue);
            return new MVELConstraintTestUtil("price > " + rightvalue, field, extractor);
        }
    }
}
