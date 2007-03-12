package org.drools.rule.builder.dialect.mvel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.Package;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.ReturnValueConstraint;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.PredicateConstraint.PredicateContextEntry;
import org.drools.rule.builder.BuildContext;
import org.drools.rule.builder.dialect.java.BuildUtils;
import org.drools.rule.builder.dialect.java.DeclarationTypeFixer;
import org.drools.rule.builder.dialect.java.JavaExprAnalyzer;
import org.drools.rule.builder.dialect.java.JavaPredicateBuilder;
import org.drools.rule.builder.dialect.java.KnowledgeHelperFixer;
import org.drools.rule.builder.dialect.mvel.MVELEvalBuilder;
import org.drools.spi.DeclarationScopeResolver;
import org.drools.spi.FieldExtractor;

public class MVELReturnValueBuilderTest extends TestCase {

    public void setUp() {
    }

    public void testSimpleExpression() {
        Package pkg = new Package( "pkg1" );
        RuleDescr ruleDescr = new RuleDescr( "rule 1" );

        InstrumentedBuildContent context = new InstrumentedBuildContent( pkg,
                                                                         ruleDescr );
        InstrumentedDeclarationScopeResolver declarationResolver = new InstrumentedDeclarationScopeResolver();
        final FieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                                  "price" );
        Column columnA = new Column( 0,
                                     new ClassObjectType( int.class ) );

        Column columnB = new Column( 1,
                                     new ClassObjectType( int.class ) );

        Declaration a = new Declaration( "a",
                                         extractor,
                                         columnA );
        Declaration b = new Declaration( "b",
                                         extractor,
                                         columnB );

        Map map = new HashMap();
        map.put( "a",
                 a );
        map.put( "b",
                 b );
        declarationResolver.setDeclarations( map );
        context.setDeclarationResolver( declarationResolver );

        ReturnValueRestrictionDescr returnValueDescr = new ReturnValueRestrictionDescr("=");
        returnValueDescr.setContent( "a + b" );

        MVELReturnValueBuilder builder = new MVELReturnValueBuilder();

        List[] usedIdentifiers = new ArrayList[2];
        List list = new ArrayList();
        usedIdentifiers[1] = list;

        Declaration[] previousDeclarations = new Declaration[]{a, b};
        Declaration[] localDeclarations = new Declaration[]{};
        
        final ReturnValueRestriction returnValue = new ReturnValueRestriction(extractor,
                                                                              previousDeclarations,
                                                                              localDeclarations,
                                                                              ValueType.PINTEGER_TYPE.getEvaluator( Operator.EQUAL ) );

        BuildUtils utils = new BuildUtils( new KnowledgeHelperFixer(),
                                           new DeclarationTypeFixer(),
                                           new JavaExprAnalyzer(),
                                           null,
                                           null,
                                           null );

        builder.build( context,
                       utils,
                       usedIdentifiers,
                       previousDeclarations,
                       localDeclarations,
                       returnValue,
                       returnValueDescr );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        InternalWorkingMemory wm = (InternalWorkingMemory) ruleBase.newWorkingMemory();

        Cheese stilton = new Cheese( "stilton",
                                     10 );

        Cheese cheddar = new Cheese( "cheddar",
                                     10 );
        InternalFactHandle f0 = (InternalFactHandle) wm.assertObject( cheddar );
        ReteTuple tuple = new ReteTuple( f0 );
        
        InternalFactHandle f1 = (InternalFactHandle) wm.assertObject( stilton );
        tuple = new ReteTuple( tuple, f1 );        

        PredicateContextEntry predicateContext = new PredicateContextEntry();
        predicateContext.leftTuple = tuple;

        Cheese brie = new Cheese( "brie",
                                     20 );
        assertTrue( returnValue.isAllowed( extractor, brie, tuple, wm ) );
        
        brie.setPrice( 18 );
        assertFalse( returnValue.isAllowed( extractor, brie, tuple, wm ) );
    }

    public static class InstrumentedDeclarationScopeResolver extends DeclarationScopeResolver {
        private Map declarations;

        public InstrumentedDeclarationScopeResolver() {
            super( null );
        }

        public void setDeclarations(Map map) {
            this.declarations = map;
        }

        public Map getDeclarations() {
            return this.declarations;
        }
    }

    public static class InstrumentedBuildContent extends BuildContext {
        private DeclarationScopeResolver declarationScopeResolver;

        public InstrumentedBuildContent(Package pkg,
                                        RuleDescr ruleDescr) {
            super( pkg,
                   ruleDescr );
        }

        public void setDeclarationResolver(DeclarationScopeResolver declarationScopeResolver) {
            this.declarationScopeResolver = declarationScopeResolver;
        }

        public DeclarationScopeResolver getDeclarationResolver() {
            return this.declarationScopeResolver;
        }

    }

}
