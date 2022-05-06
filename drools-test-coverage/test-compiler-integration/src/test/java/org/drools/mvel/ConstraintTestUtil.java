package org.drools.mvel;

import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.drools.mvel.accessors.ClassFieldReader;
import org.drools.mvel.field.FieldFactory;
import org.drools.mvel.field.LongFieldImpl;
import org.drools.core.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.core.rule.accessor.FieldValue;
import org.drools.core.rule.accessor.ReadAccessor;
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
