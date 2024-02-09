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

import org.drools.base.base.ClassObjectType;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.accessor.PatternExtractor;
import org.drools.model.Index;
import org.drools.model.constraints.SingleConstraint1;
import org.drools.model.constraints.SingleConstraint2;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.impl.DeclarationImpl;
import org.drools.model.index.AlphaIndexImpl;
import org.drools.modelcompiler.constraints.ConstraintEvaluator;
import org.drools.modelcompiler.constraints.LambdaConstraint;
import org.drools.modelcompiler.util.EvaluationUtil;
import org.drools.mvel.model.Cheese;

public class LambdaConstraintTestUtil {

    private LambdaConstraintTestUtil() {
        // don't instantiate a util class
    }

    public static <T> LambdaConstraint createLambdaConstraint1(Class<T> patternClass, Predicate1<T> predicate) {
        return createLambdaConstraint1(patternClass, null, predicate, null);
    }

    public static <T> LambdaConstraint createLambdaConstraint1(Class<T> patternClass, Pattern pattern, Predicate1<T> predicate, Index<T, ?> index) {
        String patternName = "GENERATED_$pattern_" + patternClass.getSimpleName();
        DeclarationImpl<T> decl = new DeclarationImpl<T>(patternClass, patternName);
        SingleConstraint1<T> singleConstraint = new SingleConstraint1<T>(decl, predicate);
        singleConstraint.setIndex(index);
        Declaration coreDecl = new Declaration(patternName, null, pattern, false);
        ConstraintEvaluator constraintEvaluator = new ConstraintEvaluator(new Declaration[]{coreDecl}, singleConstraint);
        return new LambdaConstraint(constraintEvaluator);
    }

    public static <T, A, B> LambdaConstraint createLambdaConstraint2(Class<A> patternClass, Class<B> rightClass, Pattern pattern, Pattern varPattern, String varName, Predicate2<A, B> predicate, Index<T, ?> index) {
        String patternName = "GENERATED_$pattern_" + patternClass.getSimpleName();
        DeclarationImpl<A> declA = new DeclarationImpl<A>(patternClass, patternName);
        DeclarationImpl<B> declB = new DeclarationImpl<B>(rightClass, varName);
        SingleConstraint2<A, B> singleConstraint = new SingleConstraint2<A, B>(declA, declB, predicate);
        singleConstraint.setIndex(index);
        Declaration patternDecl = new Declaration(patternName, new PatternExtractor(new ClassObjectType(patternClass, false)), pattern, false);
        Declaration varDecl = new Declaration(varName, new PatternExtractor(new ClassObjectType(rightClass, false)), varPattern, false);
        ConstraintEvaluator constraintEvaluator = new ConstraintEvaluator(new Declaration[]{patternDecl, varDecl}, singleConstraint);
        return new LambdaConstraint(constraintEvaluator);
    }

    public static LambdaConstraint createCheeseTypeEqualsConstraint(final String rightValue, int indexId) {
        // Typical LambdaConstraint used in drools-test-coverage. (type == "xxx")
        Pattern pattern = new Pattern( 0, new ClassObjectType( Cheese.class )  );
        Predicate1<Cheese> predicate = new Predicate1.Impl<Cheese>(_this -> EvaluationUtil.areNullSafeEquals(_this.getType(), rightValue));
        AlphaIndexImpl<Cheese, String> index = new AlphaIndexImpl<Cheese, String>(String.class, org.drools.model.Index.ConstraintType.EQUAL, indexId, _this -> _this.getType(), rightValue);
        return createLambdaConstraint1(Cheese.class, pattern, predicate, index);
    }

    public static LambdaConstraint createCheesePriceEqualsConstraint(final int rightValue, int indexId) {
        // Typical LambdaConstraint used in drools-test-coverage. (price == xxx)
        Pattern pattern = new Pattern(0, new ClassObjectType(Cheese.class));
        Predicate1<Cheese> predicate = new Predicate1.Impl<Cheese>(_this -> EvaluationUtil.areNullSafeEquals(_this.getPrice(), rightValue));
        AlphaIndexImpl<Cheese, Integer> index = new AlphaIndexImpl<Cheese, Integer>(Integer.class, org.drools.model.Index.ConstraintType.EQUAL, indexId, _this -> _this.getPrice(), rightValue);
        return createLambdaConstraint1(Cheese.class, pattern, predicate, index);
    }

    public static LambdaConstraint createCheesePriceGreaterConstraint(final int rightValue, int indexId) {
        // Typical LambdaConstraint used in drools-test-coverage. (price > xxx)
        Pattern pattern = new Pattern(0, new ClassObjectType(Cheese.class));
        Predicate1<Cheese> predicate = new Predicate1.Impl<Cheese>(_this -> EvaluationUtil.greaterThan(_this.getPrice(), rightValue));
        AlphaIndexImpl<Cheese, Integer> index = new AlphaIndexImpl<Cheese, Integer>(Integer.class, org.drools.model.Index.ConstraintType.GREATER_THAN, indexId, _this -> _this.getPrice(), rightValue);
        return createLambdaConstraint1(Cheese.class, pattern, predicate, index);
    }

    public static LambdaConstraint createCheeseCharTypeEqualsConstraint(final char rightValue, int indexId) {
        // Typical LambdaConstraint used in drools-test-coverage. indexId is required when the test uses hashKey
        Pattern pattern = new Pattern(0, new ClassObjectType(Cheese.class));
        Predicate1<Cheese> predicate = new Predicate1.Impl<Cheese>(_this -> EvaluationUtil.areNullSafeEquals(_this.getCharType(), rightValue));
        AlphaIndexImpl<Cheese, Character> index = new AlphaIndexImpl<Cheese, Character>(Character.class, org.drools.model.Index.ConstraintType.EQUAL, indexId, _this -> _this.getCharType(), rightValue);
        return LambdaConstraintTestUtil.createLambdaConstraint1(Cheese.class, pattern, predicate, index);
    }

    public static LambdaConstraint createCheeseCharObjectTypeEqualsConstraint(final char rightValue, int indexId) {
        // Typical LambdaConstraint used in drools-test-coverage. indexId is required when the test uses hashKey
        Pattern pattern = new Pattern(0, new ClassObjectType(Cheese.class));
        Predicate1<Cheese> predicate = new Predicate1.Impl<Cheese>(_this -> EvaluationUtil.areNullSafeEquals(_this.getCharObjectType(), rightValue));
        AlphaIndexImpl<Cheese, Character> index = new AlphaIndexImpl<Cheese, Character>(Character.class, org.drools.model.Index.ConstraintType.EQUAL, indexId, _this -> _this.getCharObjectType(), rightValue);
        return LambdaConstraintTestUtil.createLambdaConstraint1(Cheese.class, pattern, predicate, index);
    }
}
