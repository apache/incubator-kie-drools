/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder;

import java.util.Collections;

import org.drools.model.Condition;
import org.drools.model.Consequence;
import org.drools.model.GroupByPattern;
import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.Rule;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.constraints.SingleConstraint1;
import org.drools.model.constraints.SingleConstraint2;
import org.drools.model.constraints.SingleConstraint3;
import org.drools.model.constraints.SingleConstraint4;
import org.drools.model.constraints.SingleConstraint5;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.functions.Predicate3;
import org.drools.model.functions.Predicate4;
import org.drools.model.functions.Predicate5;
import org.drools.model.functions.accumulate.GroupKey;
import org.drools.model.impl.DeclarationImpl;
import org.drools.model.patterns.PatternImpl;
import org.drools.model.view.BindViewItem1;
import org.drools.model.view.CombinedExprViewItem;
import org.drools.model.view.Expr1ViewItemImpl;
import org.drools.model.view.Expr2ViewItemImpl;
import org.drools.model.view.Expr3ViewItemImpl;
import org.drools.model.view.Expr4ViewItemImpl;
import org.drools.model.view.Expr5ViewItemImpl;
import org.drools.model.view.ViewItem;
import org.drools.modelcompiler.RuleContext;
import org.drools.modelcompiler.dsl.pattern.D;
import org.drools.modelcompiler.util.EvaluationUtil;

public class GroupByBuilder {

    private final RuleContext ctx;
    private final GroupByPattern groupByPattern;

    public GroupByBuilder( RuleContext ctx, GroupByPattern groupByPattern ) {
        this.ctx = ctx;
        this.groupByPattern = groupByPattern;
    }

    public PatternImpl build() {
        Variable<GroupKey> inputVariable = new DeclarationImpl<>( GroupKey.class, "$group_" + groupByPattern.getTopic() ).setMetadata( GroupKey.Metadata.INSTANCE );
        BindViewItem1 binding = new BindViewItem1( groupByPattern.getVarKey(), new Function1.Impl<>( GroupKey::getKey ), inputVariable, new String[] { "key" }, null );

        Expr1ViewItemImpl topicExpr = new Expr1ViewItemImpl<>( "TOPIC_" + groupByPattern.getTopic(), inputVariable,
                new Predicate1.Impl<>( ( GroupKey g) -> g.getTopic().equals( groupByPattern.getTopic() ) ))
                .indexedBy( String.class, Index.ConstraintType.EQUAL, GroupKey.Metadata.INSTANCE.getPropertyIndex( "topic" ), GroupKey::getTopic, groupByPattern.getTopic() );
        topicExpr.reactOn( "topic" );

        ctx.addSubRule( insertingGroupRule(groupByPattern) );
        ctx.addSubRule( deletingGroupRule(groupByPattern) );

        addGroupingConstraint( groupByPattern );

        return new PatternImpl(inputVariable, new SingleConstraint1( topicExpr ), Collections.singletonList( binding ));
    }

    private void addGroupingConstraint(GroupByPattern groupByPattern) {
        org.drools.model.Pattern[] groupingPatterns = groupByPattern.getGroupingPatterns();
        if (groupingPatterns.length == 1) {
            Expr2ViewItemImpl groupingExpr = new Expr2ViewItemImpl( "KEY_" + groupByPattern.getTopic(), groupingPatterns[0].getPatternVariable(), groupByPattern.getVarKey(),
                    new Predicate2.Impl<>( ( Object obj, Object $key ) -> EvaluationUtil.areNullSafeEquals( groupByPattern.getKey( obj ), $key ) ) )
                    .indexedBy( java.lang.Object.class, Index.ConstraintType.EQUAL, 1, groupByPattern::getKey, key -> key );
            (( PatternImpl ) groupingPatterns[0]).addConstraint( new SingleConstraint2( groupingExpr ) );
        } else if (groupingPatterns.length == 2) {
            Expr3ViewItemImpl groupingExpr = new Expr3ViewItemImpl( "KEY_" + groupByPattern.getTopic(), groupingPatterns[0].getPatternVariable(), groupingPatterns[1].getPatternVariable(), groupByPattern.getVarKey(),
                    new Predicate3.Impl<>( ( Object obj1, Object obj2, Object $key ) -> EvaluationUtil.areNullSafeEquals( groupByPattern.getKey( obj1, obj2 ), $key ) ) );
            (( PatternImpl ) groupingPatterns[1]).addConstraint( new SingleConstraint3( groupingExpr ) );
        } else if (groupingPatterns.length == 3) {
            Expr4ViewItemImpl groupingExpr = new Expr4ViewItemImpl( "KEY_" + groupByPattern.getTopic(), groupingPatterns[0].getPatternVariable(), groupingPatterns[1].getPatternVariable(), groupingPatterns[2].getPatternVariable(), groupByPattern.getVarKey(),
                    new Predicate4.Impl<>( ( Object obj1, Object obj2, Object obj3, Object $key ) -> EvaluationUtil.areNullSafeEquals( groupByPattern.getKey( obj1, obj2, obj3 ), $key ) ) );
            (( PatternImpl ) groupingPatterns[2]).addConstraint( new SingleConstraint4( groupingExpr ) );
        } else if (groupingPatterns.length == 4) {
            Expr5ViewItemImpl groupingExpr = new Expr5ViewItemImpl( "KEY_" + groupByPattern.getTopic(), groupingPatterns[0].getPatternVariable(), groupingPatterns[1].getPatternVariable(), groupingPatterns[2].getPatternVariable(), groupingPatterns[3].getPatternVariable(), groupByPattern.getVarKey(),
                    new Predicate5.Impl<>( ( Object obj1, Object obj2, Object obj3, Object obj4, Object $key ) -> EvaluationUtil.areNullSafeEquals( groupByPattern.getKey( obj1, obj2, obj3, obj4 ), $key ) ) );
            (( PatternImpl ) groupingPatterns[3]).addConstraint( new SingleConstraint5( groupingExpr ) );
        } else {
            throw new UnsupportedOperationException("GroupBy is implemented with up to 4 patterns");
        }
    }

    private Rule insertingGroupRule( GroupByPattern groupByPattern ) {
        Variable<Object> var_$key = D.declarationOf(Object.class, "$key");
        Variable<GroupKey> var_$group = D.declarationOf(GroupKey.class, GroupKey.Metadata.INSTANCE, "var_$group");

        PatternDSL.PatternDef<GroupKey> group = D.pattern(var_$group)
                .expr("TOPIC_" + groupByPattern.getTopic(),
                        (GroupKey _this) -> EvaluationUtil.areNullSafeEquals(_this.getTopic(), groupByPattern.getTopic()),
                        D.alphaIndexedBy(String.class, Index.ConstraintType.EQUAL,
                                GroupKey.Metadata.INSTANCE.getPropertyIndex( "topic" ),
                                (GroupKey _this) -> _this.getTopic(),
                                groupByPattern.getTopic()),
                        D.reactOn("topic"));

        Rule rule = D.rule("CREATE_GROUP_" + groupByPattern.getTopic()).build(
                buildGroupingViewForInsert( groupByPattern, var_$key ),
                D.not( buildGroupExistenceCheck( groupByPattern, var_$key, group ) ),
                buildGroupCreationConsequence( groupByPattern, var_$key ) );

        addGroupedPatternConstraints( groupByPattern, rule );

        return rule;
    }

    private PatternDSL.PatternDef<GroupKey> buildGroupExistenceCheck( GroupByPattern groupByPattern, Variable<Object> var_$key, PatternDSL.PatternDef<GroupKey> group ) {
        org.drools.model.Pattern[] groupingPatterns = groupByPattern.getGroupingPatterns();
        if (groupingPatterns.length == 1) {
            return group
                    .expr( "KEY_1_" + groupByPattern.getTopic(),
                            var_$key,
                            ( GroupKey _this, Object $key ) -> EvaluationUtil.areNullSafeEquals( _this.getKey(), $key ),
                            D.betaIndexedBy( Object.class,
                                    Index.ConstraintType.EQUAL,
                                    GroupKey.Metadata.INSTANCE.getPropertyIndex( "key" ),
                                    ( GroupKey _this ) -> _this.getKey(),
                                    key -> key ),
                            D.reactOn( "key" ) );
        }

        if (groupingPatterns.length == 2) {
            return group
                    .expr( "KEY_1_" + groupByPattern.getTopic(),
                            groupingPatterns[0].getPatternVariable(),
                            groupingPatterns[1].getPatternVariable(),
                            ( GroupKey _this, Object obj1, Object obj2 ) -> EvaluationUtil.areNullSafeEquals( _this.getKey(), groupByPattern.getKey( obj1, obj2 ) ),
                            D.reactOn( "key" ) );
        }

        if (groupingPatterns.length == 3) {
            return group
                    .expr( "KEY_1_" + groupByPattern.getTopic(),
                            groupingPatterns[0].getPatternVariable(),
                            groupingPatterns[1].getPatternVariable(),
                            groupingPatterns[2].getPatternVariable(),
                            ( GroupKey _this, Object obj1, Object obj2, Object obj3 ) -> EvaluationUtil.areNullSafeEquals( _this.getKey(), groupByPattern.getKey( obj1, obj2, obj3 ) ),
                            D.reactOn( "key" ) );
        }

        if (groupingPatterns.length == 4) {
            return group
                    .expr( "KEY_1_" + groupByPattern.getTopic(),
                            groupingPatterns[0].getPatternVariable(),
                            groupingPatterns[1].getPatternVariable(),
                            groupingPatterns[2].getPatternVariable(),
                            groupingPatterns[3].getPatternVariable(),
                            ( GroupKey _this, Object obj1, Object obj2, Object obj3, Object obj4 ) -> EvaluationUtil.areNullSafeEquals( _this.getKey(), groupByPattern.getKey( obj1, obj2, obj3, obj4 ) ),
                            D.reactOn( "key" ) );
        }

        throw new UnsupportedOperationException("GroupBy is implemented with up to 4 patterns");
    }

    private RuleItemBuilder<Consequence> buildGroupCreationConsequence( GroupByPattern groupByPattern, Variable<Object> var_$key ) {
        org.drools.model.Pattern<Object>[] groupingPatterns = groupByPattern.getGroupingPatterns();
        if (groupingPatterns.length == 1) {
            return D.on( var_$key ).execute( ( org.drools.model.Drools drools, Object $key ) ->
                    drools.insert( new GroupKey( groupByPattern.getTopic(), $key ) )
            );
        }

        if (groupingPatterns.length == 2) {
            return D.on( groupingPatterns[0].getPatternVariable(), groupingPatterns[1].getPatternVariable() )
                    .execute( ( org.drools.model.Drools drools, Object obj1, Object obj2 ) ->
                            drools.insert( new GroupKey( groupByPattern.getTopic(), groupByPattern.getKey( obj1, obj2 ) ) )
                    );
        }

        if (groupingPatterns.length == 3) {
            return D.on( groupingPatterns[0].getPatternVariable(), groupingPatterns[1].getPatternVariable(), groupingPatterns[2].getPatternVariable() )
                    .execute( ( org.drools.model.Drools drools, Object obj1, Object obj2, Object obj3 ) ->
                            drools.insert( new GroupKey( groupByPattern.getTopic(), groupByPattern.getKey( obj1, obj2, obj3 ) ) )
                    );
        }

        if (groupingPatterns.length == 3) {
            return D.on( groupingPatterns[0].getPatternVariable(), groupingPatterns[1].getPatternVariable(), groupingPatterns[2].getPatternVariable(), groupingPatterns[3].getPatternVariable() )
                    .execute( ( org.drools.model.Drools drools, Object obj1, Object obj2, Object obj3, Object obj4 ) ->
                            drools.insert( new GroupKey( groupByPattern.getTopic(), groupByPattern.getKey( obj1, obj2, obj3, obj4 ) ) )
                    );
        }

        throw new UnsupportedOperationException("GroupBy is implemented with up to 4 patterns");
    }

    private void addGroupedPatternConstraints( GroupByPattern groupByPattern, Rule rule ) {
        org.drools.model.Pattern[] groupingPatterns = groupByPattern.getGroupingPatterns();
        if (groupingPatterns.length == 1) {
            PatternImpl firstPattern = ( PatternImpl ) rule.getView().getSubConditions().get( 0 );
            firstPattern.addConstraint( groupingPatterns[0].getConstraint() );
        }
    }

    private ViewItem buildGroupingViewForInsert( GroupByPattern groupByPattern, Variable<Object> var_$key ) {
        org.drools.model.Pattern[] groupingPatterns = groupByPattern.getGroupingPatterns();
        if (groupingPatterns.length == 1) {
            return D.pattern( groupingPatterns[0].getPatternVariable() ).bind( var_$key, groupByPattern::getKey );
        }
        return new CombinedExprViewItem( Condition.Type.AND, toPatternDefs( groupingPatterns ));
    }

    private PatternDSL.PatternDef[] toPatternDefs( org.drools.model.Pattern[] groupingPatterns ) {
        PatternDSL.PatternDef[] groupingViews = new PatternDSL.PatternDef[groupingPatterns.length];
        for (int i = 0; i < groupingPatterns.length; i++) {
            groupingViews[i] = D.pattern( groupingPatterns[i].getPatternVariable() );
        }
        return groupingViews;
    }

    private Rule deletingGroupRule( GroupByPattern groupByPattern) {
        Variable<Object> var_$key = D.declarationOf(Object.class, "$key");
        Variable<GroupKey> var_$group = D.declarationOf(GroupKey.class, GroupKey.Metadata.INSTANCE, "var_$group");

        return D.rule("DELETE_GROUP_" + groupByPattern.getTopic()).build(
                D.pattern(var_$group)
                        .expr("TOPIC_" + groupByPattern.getTopic(),
                                (GroupKey _this) -> EvaluationUtil.areNullSafeEquals(_this.getTopic(), groupByPattern.getTopic()),
                                D.alphaIndexedBy(String.class, Index.ConstraintType.EQUAL,
                                        GroupKey.Metadata.INSTANCE.getPropertyIndex( "topic" ),
                                        (GroupKey _this) -> _this.getTopic(),
                                        groupByPattern.getTopic()),
                                D.reactOn("topic"))
                        .bind(var_$key,
                                (GroupKey _this) -> _this.getKey(),
                                D.reactOn("key")),
                D.not(buildGroupingViewForDelete( groupByPattern, var_$key )),
                D.on(var_$group).execute((org.drools.model.Drools drools, GroupKey $group) -> {
                    {
                        drools.delete($group);
                    }
                }));
    }

    private ViewItem buildGroupingViewForDelete( GroupByPattern groupByPattern, Variable<Object> var_$key ) {
        org.drools.model.Pattern[] groupingPatterns = groupByPattern.getGroupingPatterns();

        if (groupingPatterns.length == 1) {
            return D.pattern( groupingPatterns[0].getPatternVariable() )
                    .expr( "KEY_2_" + groupByPattern.getTopic(),
                            var_$key,
                            ( Object obj, Object $key ) -> EvaluationUtil.areNullSafeEquals( groupByPattern.getKey( obj ), $key ),
                            D.betaIndexedBy( java.lang.Object.class,
                                    Index.ConstraintType.EQUAL,
                                    1,
                                    groupByPattern::getKey,
                                    ( GroupKey _this ) -> _this.getKey() ) );
        }

        PatternDSL.PatternDef[] groupingViews = toPatternDefs( groupingPatterns );

        if (groupingPatterns.length == 2) {
            groupingViews[1].expr( "KEY_2_" + groupByPattern.getTopic(),
                    groupingPatterns[0].getPatternVariable(),
                    var_$key,
                    ( Object _this, Object obj1, Object $key ) -> EvaluationUtil.areNullSafeEquals( groupByPattern.getKey( obj1, _this ), $key ) );
        } else if (groupingPatterns.length == 3) {
            groupingViews[2].expr( "KEY_2_" + groupByPattern.getTopic(),
                    groupingPatterns[0].getPatternVariable(),
                    groupingPatterns[1].getPatternVariable(),
                    var_$key,
                    ( Object _this, Object obj1, Object obj2, Object $key ) -> EvaluationUtil.areNullSafeEquals( groupByPattern.getKey( obj1, obj2, _this ), $key ) );
        } else if (groupingPatterns.length == 4) {
            groupingViews[3].expr( "KEY_2_" + groupByPattern.getTopic(),
                    groupingPatterns[0].getPatternVariable(),
                    groupingPatterns[1].getPatternVariable(),
                    groupingPatterns[2].getPatternVariable(),
                    var_$key,
                    ( Object _this, Object obj1, Object obj2, Object obj3, Object $key ) -> EvaluationUtil.areNullSafeEquals( groupByPattern.getKey( obj1, obj2, obj3, _this ), $key ) );
        }

        return new CombinedExprViewItem(Condition.Type.AND, groupingViews);
    }

}
