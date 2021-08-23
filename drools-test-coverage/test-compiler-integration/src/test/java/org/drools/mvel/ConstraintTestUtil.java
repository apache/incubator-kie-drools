package org.drools.mvel;

import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassFieldReader;
import org.drools.core.base.FieldFactory;
import org.drools.core.base.field.LongFieldImpl;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
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

    public static AlphaNodeFieldConstraint createCheeseTypeEqualsConstraint(InternalReadAccessor extractor, String rightvalue, boolean useLambdaConstraint) {
        if (useLambdaConstraint) {
            return LambdaConstraintTestUtil.createCheeseTypeEqualsConstraint(rightvalue, extractor.getIndex());
        } else {
            final FieldValue field = FieldFactory.getInstance().getFieldValue(rightvalue);
            return new MVELConstraintTestUtil("type == \"" + rightvalue + "\"", field, extractor);
        }
    }

    public static AlphaNodeFieldConstraint createCheeseCharTypeEqualsConstraint(InternalReadAccessor extractor, int rightvalue, boolean useLambdaConstraint) {
        if (useLambdaConstraint) {
            return LambdaConstraintTestUtil.createCheeseCharTypeEqualsConstraint((char) rightvalue, extractor.getIndex());
        } else {
            return new MVELConstraintTestUtil("charType == " + rightvalue, new LongFieldImpl(rightvalue), extractor);
        }
    }

    public static AlphaNodeFieldConstraint createCheeseCharObjectTypeEqualsConstraint(InternalReadAccessor extractor, int rightvalue, boolean useLambdaConstraint) {
        if (useLambdaConstraint) {
            return LambdaConstraintTestUtil.createCheeseCharObjectTypeEqualsConstraint((char) rightvalue, extractor.getIndex());
        } else {
            return new MVELConstraintTestUtil("charObjectType == " + rightvalue, new LongFieldImpl(rightvalue), extractor);
        }
    }

    public static AlphaNodeFieldConstraint createCheesePriceEqualsConstraint(InternalReadAccessor extractor, int rightvalue, boolean useLambdaConstraint) {
        if (useLambdaConstraint) {
            return LambdaConstraintTestUtil.createCheesePriceEqualsConstraint(rightvalue, extractor.getIndex());
        } else {
            final FieldValue field = FieldFactory.getInstance().getFieldValue(rightvalue);
            return new MVELConstraintTestUtil("price == " + rightvalue, field, extractor);
        }
    }

    public static AlphaNodeFieldConstraint createCheesePriceGreaterConstraint(InternalReadAccessor extractor, int rightvalue, boolean useLambdaConstraint) {
        if (useLambdaConstraint) {
            return LambdaConstraintTestUtil.createCheesePriceGreaterConstraint(rightvalue, extractor.getIndex());
        } else {
            final FieldValue field = FieldFactory.getInstance().getFieldValue(rightvalue);
            return new MVELConstraintTestUtil("price > " + rightvalue, field, extractor);
        }
    }
}
