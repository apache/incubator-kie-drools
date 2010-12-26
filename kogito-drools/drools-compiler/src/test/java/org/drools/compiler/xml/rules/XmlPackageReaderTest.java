package org.drools.compiler.xml.rules;

import java.io.InputStreamReader;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.xml.XmlPackageReader;
import org.drools.lang.descr.AccessorDescr;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.ForallDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.VariableRestrictionDescr;

public class XmlPackageReaderTest {

    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    @Test
    public void testParseFrom() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseFrom.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        RuleDescr obj = (RuleDescr) packageDescr.getRules().get( 0 );
        PatternDescr patterndescr = (PatternDescr) obj.getLhs().getDescrs().get( 0 );
        
        FromDescr from = (FromDescr) patterndescr.getSource();
        
        AccessorDescr accessordescriptor =  (AccessorDescr) from.getDataSource();
        assertEquals( accessordescriptor.toString().trim(), "cheesery.getCheeses(i+4)" );

        assertEquals( patterndescr.getObjectType(), "Cheese" );
        assertEquals( patterndescr.getIdentifier(), "cheese" );
        
    }

    @Test
    public void testAccumulate() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseAccumulate.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        RuleDescr obj = (RuleDescr) packageDescr.getRules().get( 0 );

        Object patternobj = obj.getLhs().getDescrs().get( 0 );
        assertTrue( patternobj instanceof PatternDescr );
        final PatternDescr patterncheese = (PatternDescr) patternobj;
        assertEquals( patterncheese.getIdentifier(), "cheese" );
        assertEquals( patterncheese.getObjectType(), "Cheese" );
        
        AccumulateDescr accumulatedescr = (AccumulateDescr) patterncheese.getSource();
        assertEquals( "total += $cheese.getPrice();",
                      accumulatedescr.getActionCode() );
        assertEquals( "int total = 0;",
                      accumulatedescr.getInitCode() );
        assertEquals( "new Integer( total ) );",
                      accumulatedescr.getResultCode() );

        patternobj = obj.getLhs().getDescrs().get( 1 );
        assertTrue( patternobj instanceof PatternDescr );
        
        final PatternDescr patternmax = (PatternDescr) patternobj;
        assertEquals( patternmax.getIdentifier(), "max" );
        assertEquals( patternmax.getObjectType(), "Number" );
        
        accumulatedescr = (AccumulateDescr) patternmax.getSource();
        
        assertTrue( accumulatedescr.isExternalFunction() );
        
        assertEquals( "max",
                      accumulatedescr.getFunctionIdentifier() );
        
        assertNull( accumulatedescr.getInitCode() );
        assertNull( accumulatedescr.getActionCode() );
        assertNull( accumulatedescr.getResultCode() );
        assertNull( accumulatedescr.getReverseCode());
        
    }
    
    
    @Test
    public void testAccumulateMultiPattern() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseAccumulate.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        RuleDescr obj = (RuleDescr) packageDescr.getRules().get( 1 );

        Object patternobj = obj.getLhs().getDescrs().get( 0 );
        assertTrue( patternobj instanceof PatternDescr );
        final PatternDescr patterncheese = (PatternDescr) patternobj;
        assertEquals( patterncheese.getIdentifier(), "cheese" );
        assertEquals( patterncheese.getObjectType(), "Cheese" );
        
        AccumulateDescr accumulatedescr = (AccumulateDescr) patterncheese.getSource();
        assertEquals( "total += $cheese.getPrice();",
                      accumulatedescr.getActionCode() );
        assertEquals( "int total = 0;",
                      accumulatedescr.getInitCode() );
        assertEquals( "new Integer( total ) );",
                      accumulatedescr.getResultCode() );
        
        AndDescr anddescr = (AndDescr) accumulatedescr.getInput();
        
        List descrlist = anddescr.getDescrs(); 
        
        PatternDescr[] listpattern = (PatternDescr[]) descrlist.toArray(new PatternDescr[descrlist.size()]);
        
        assertEquals(listpattern[0].getObjectType(), "Milk");
        assertEquals(listpattern[1].getObjectType(), "Cup");
    }
    
    @Test
    public void testParseForall() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseForall.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );

        RuleDescr obj = (RuleDescr) packageDescr.getRules().get( 0 );
        ForallDescr forall = (ForallDescr) obj.getLhs().getDescrs().get( 0 );
        List forallPaterns = forall.getDescrs();

        PatternDescr pattarnState = (PatternDescr) forallPaterns.get( 0 );
        PatternDescr personState = (PatternDescr) forallPaterns.get( 1 );
        PatternDescr cheeseState = (PatternDescr) forallPaterns.get( 2 );

        assertEquals( pattarnState.getObjectType(),
                      "State" );
        assertEquals( personState.getObjectType(),
                      "Person" );
        assertEquals( cheeseState.getObjectType(),
                      "Cheese" );
    }

    @Test
    public void testParseExists() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseExists.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );

        RuleDescr obj = (RuleDescr) packageDescr.getRules().get( 0 );
        Object existdescr = obj.getLhs().getDescrs().get( 0 );
        assertTrue( existdescr instanceof ExistsDescr );

        Object patternDescriptor = ((ExistsDescr) existdescr).getDescrs().get( 0 );
        assertTrue( patternDescriptor instanceof PatternDescr );
        assertEquals( ((PatternDescr) patternDescriptor).getObjectType(),
                      "Person" );

        Object notDescr = obj.getLhs().getDescrs().get( 1 );

        assertEquals( notDescr.getClass().getName(),
                      NotDescr.class.getName() );
        existdescr = ((NotDescr) notDescr).getDescrs().get( 0 );
        patternDescriptor = ((ExistsDescr) existdescr).getDescrs().get( 0 );
        assertTrue( patternDescriptor instanceof PatternDescr );
        assertEquals( ((PatternDescr) patternDescriptor).getObjectType(),
                      "Cheese" );
    }

    @Test
    public void testParseCollect() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseCollect.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();

        assertNotNull( packageDescr );

        RuleDescr obj = (RuleDescr) packageDescr.getRules().get( 0 );
        Object objectpattern = obj.getLhs().getDescrs().get( 0 );
        assertTrue( objectpattern instanceof PatternDescr );

        PatternDescr patterndescr = (PatternDescr) objectpattern;
        
        assertEquals( patterndescr.getObjectType(),
                                    "Cheese" );
        
        Object collectobj = patterndescr.getSource();
        
        assertTrue( collectobj instanceof CollectDescr );
        
        CollectDescr collectDescr = (CollectDescr) collectobj;
        
        PatternDescr inputpattern =  collectDescr.getInputPattern();
        
        assertEquals( inputpattern.getObjectType(),
                    "Person" );
        Object fieldContraintObject = inputpattern.getConstraint().getDescrs().get( 0 );
        assertTrue( fieldContraintObject instanceof FieldConstraintDescr );
        FieldConstraintDescr fieldconstraintdescr = (FieldConstraintDescr) fieldContraintObject;
        assertEquals( fieldconstraintdescr.getFieldName(),
                      "hair" );
        Object literal1 = fieldconstraintdescr.getRestrictions().get( 0 );
        assertTrue( literal1 instanceof LiteralRestrictionDescr );
        LiteralRestrictionDescr literalDesc = (LiteralRestrictionDescr) literal1;
        assertEquals( literalDesc.getEvaluator(),
                      "==" );
        assertEquals( literalDesc.getText(),
                      "pink" );

        fieldContraintObject = patterndescr.getConstraint().getDescrs().get( 0 );
        assertTrue( fieldContraintObject instanceof FieldConstraintDescr );
        fieldconstraintdescr = (FieldConstraintDescr) fieldContraintObject;
        assertEquals( fieldconstraintdescr.getFieldName(),
                      "type" );
        literal1 = fieldconstraintdescr.getRestrictions().get( 0 );
        assertTrue( literal1 instanceof LiteralRestrictionDescr );
        literalDesc = (LiteralRestrictionDescr) literal1;
        assertEquals( literalDesc.getEvaluator(),
                      "==" );
        assertEquals( literalDesc.getText(),
                      "1" );
        
    }

    @Test
    public void testParsePackageName() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParsePackageName.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        assertEquals( "com.sample",
                      packageDescr.getName() );
    }

    @Test
    public void testParseImport() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseImport.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        assertEquals( "com.sample",
                      packageDescr.getName() );

        final List imports = packageDescr.getImports();
        assertEquals( 2,
                      imports.size() );
        assertEquals( "java.util.HashMap",
                      ((ImportDescr) imports.get( 0 )).getTarget() );
        assertEquals( "org.drools.*",
                      ((ImportDescr) imports.get( 1 )).getTarget() );
        
        final List functionImport = packageDescr.getFunctionImports();
        
        assertEquals("org.drools.function", 
                     ((FunctionImportDescr) functionImport.get( 0 )).getTarget() );
    }

    @Test
    public void testParseGlobal() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseGlobal.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        assertEquals( "com.sample",
                      packageDescr.getName() );

        final List imports = packageDescr.getImports();
        assertEquals( 2,
                      imports.size() );
        assertEquals( "java.util.HashMap",
                      ((ImportDescr) imports.get( 0 )).getTarget() );
        assertEquals( "org.drools.*",
                      ((ImportDescr) imports.get( 1 )).getTarget() );

        final List globals = packageDescr.getGlobals();
        assertEquals( 2,
                      globals.size() );
        final GlobalDescr x = (GlobalDescr) globals.get( 0 );
        final GlobalDescr yada = (GlobalDescr) globals.get( 1 );
        assertEquals( "com.sample.X",
                      x.getType() );
        assertEquals( "x",
                      x.getIdentifier() );
        assertEquals( "com.sample.Yada",
                      yada.getType() );
        assertEquals( "yada",
                      yada.getIdentifier() );
    }

    @Test
    public void testParseFunction() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseFunction.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        assertEquals( "com.sample",
                      packageDescr.getName() );

        final List imports = packageDescr.getImports();
        assertEquals( 2,
                      imports.size() );
        assertEquals( "java.util.HashMap",
                      ((ImportDescr) imports.get( 0 )).getTarget() );
        assertEquals( "org.drools.*",
                      ((ImportDescr) imports.get( 1 )).getTarget() );

        final List globals = packageDescr.getGlobals();
        assertEquals( 2,
                      globals.size() );
        final GlobalDescr x = (GlobalDescr) globals.get( 0 );
        final GlobalDescr yada = (GlobalDescr) globals.get( 1 );
        assertEquals( "com.sample.X",
                      x.getType() );
        assertEquals( "x",
                      x.getIdentifier() );
        assertEquals( "com.sample.Yada",
                      yada.getType() );
        assertEquals( "yada",
                      yada.getIdentifier() );

        final FunctionDescr functionDescr = (FunctionDescr) packageDescr.getFunctions().get( 0 );
        final List names = functionDescr.getParameterNames();
        assertEquals( "foo",
                      names.get( 0 ) );
        assertEquals( "bada",
                      names.get( 1 ) );

        final List types = functionDescr.getParameterTypes();
        assertEquals( "Bar",
                      types.get( 0 ) );
        assertEquals( "Bing",
                      types.get( 1 ) );

        assertEquals( "System.out.println(\"hello world\");",
                      functionDescr.getText().trim() );
    }

    @Test
    public void testParseRule() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseRule.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        assertEquals( "com.sample",
                      packageDescr.getName() );

        final List imports = packageDescr.getImports();
        assertEquals( 2,
                      imports.size() );
        assertEquals( "java.util.HashMap",
                      ((ImportDescr) imports.get( 0 )).getTarget() );
        assertEquals( "org.drools.*",
                      ((ImportDescr) imports.get( 1 )).getTarget() );

        final List globals = packageDescr.getGlobals();
        assertEquals( 2,
                      globals.size() );
        final GlobalDescr x = (GlobalDescr) globals.get( 0 );
        final GlobalDescr yada = (GlobalDescr) globals.get( 1 );
        assertEquals( "com.sample.X",
                      x.getType() );
        assertEquals( "x",
                      x.getIdentifier() );
        assertEquals( "com.sample.Yada",
                      yada.getType() );
        assertEquals( "yada",
                      yada.getIdentifier() );

        final FunctionDescr functionDescr = (FunctionDescr) packageDescr.getFunctions().get( 0 );
        final List names = functionDescr.getParameterNames();
        assertEquals( "foo",
                      names.get( 0 ) );
        assertEquals( "bada",
                      names.get( 1 ) );

        final List types = functionDescr.getParameterTypes();
        assertEquals( "Bar",
                      types.get( 0 ) );
        assertEquals( "Bing",
                      types.get( 1 ) );

        assertEquals( "System.out.println(\"hello world\");",
                      functionDescr.getText().trim() );

        final RuleDescr ruleDescr = (RuleDescr) packageDescr.getRules().get( 0 );
        assertEquals( "simple_rule",
                      ruleDescr.getName() );

        assertEquals( 4,
                      ruleDescr.getAttributes().size() );
        final AttributeDescr attributeDescr = (AttributeDescr) ruleDescr.getAttributes().get( "salience" );
        assertEquals( "salience",
                      attributeDescr.getName() );
        assertEquals( "10",
                      attributeDescr.getValue() );

        final AndDescr lhs = ruleDescr.getLhs();
        assertEquals( 7,
                      lhs.getDescrs().size() );
        final PatternDescr patternDescr = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( "Bar",
                      patternDescr.getObjectType() );

        final String consequence = (String) ruleDescr.getConsequence();
        assertNotNull( consequence );
    }

    @Test
    public void testParseLhs() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseLhs.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        assertEquals( "com.sample",
                      packageDescr.getName() );

        final List imports = packageDescr.getImports();
        assertEquals( 2,
                      imports.size() );
        assertEquals( "java.util.HashMap",
                      ((ImportDescr) imports.get( 0 )).getTarget() );
        assertEquals( "org.drools.*",
                      ((ImportDescr) imports.get( 1 )).getTarget() );

        final List globals = packageDescr.getGlobals();
        assertEquals( 2,
                      globals.size() );
        final GlobalDescr x = (GlobalDescr) globals.get( 0 );
        final GlobalDescr yada = (GlobalDescr) globals.get( 1 );
        assertEquals( "com.sample.X",
                      x.getType() );
        assertEquals( "x",
                      x.getIdentifier() );
        assertEquals( "com.sample.Yada",
                      yada.getType() );
        assertEquals( "yada",
                      yada.getIdentifier() );

        final FunctionDescr functionDescr = (FunctionDescr) packageDescr.getFunctions().get( 0 );
        final List names = functionDescr.getParameterNames();
        assertEquals( "foo",
                      names.get( 0 ) );
        assertEquals( "bada",
                      names.get( 1 ) );

        final List types = functionDescr.getParameterTypes();
        assertEquals( "Bar",
                      types.get( 0 ) );
        assertEquals( "Bing",
                      types.get( 1 ) );

        assertEquals( "System.out.println(\"hello world\");",
                      functionDescr.getText().trim() );

        final RuleDescr ruleDescr = (RuleDescr) packageDescr.getRules().get( 0 );
        assertEquals( "my rule",
                      ruleDescr.getName() );

        final AndDescr lhsDescr = ruleDescr.getLhs();

        AndDescr andDescr = (AndDescr) lhsDescr.getDescrs().get( 0 );
        OrDescr orDescr = (OrDescr) lhsDescr.getDescrs().get( 1 );
        final PatternDescr pattern1 = (PatternDescr) lhsDescr.getDescrs().get( 2 );
        assertNull( pattern1.getIdentifier() );
        assertEquals( "Foo",
                      pattern1.getObjectType() );

        final PatternDescr pattern2 = (PatternDescr) lhsDescr.getDescrs().get( 3 );
        assertEquals( "Bar",
                      pattern2.getObjectType() );
        assertEquals( "bar",
                      pattern2.getIdentifier() );

        final PatternDescr pattern3 = (PatternDescr) lhsDescr.getDescrs().get( 4 );
        //final LiteralDescr literalDescr = (LiteralDescr) pattern3.getDescrs().get( 0 );
        final FieldConstraintDescr fieldConstraintDescr = (FieldConstraintDescr) pattern3.getDescrs().get( 0 );
        final LiteralRestrictionDescr literalDescr = (LiteralRestrictionDescr) fieldConstraintDescr.getRestrictions().get( 0 );
        assertEquals( "field1",
                      fieldConstraintDescr.getFieldName() );
        assertEquals( "==",
                      literalDescr.getEvaluator() );
        assertEquals( "value1",
                      literalDescr.getText() );

        final ReturnValueRestrictionDescr returnValueDescr = (ReturnValueRestrictionDescr) fieldConstraintDescr.getRestrictions().get( 1 );
        assertEquals( "==",
                      returnValueDescr.getEvaluator() );
        assertEquals( "1==1",
                      returnValueDescr.getContent() );

        final VariableRestrictionDescr variableDescr = (VariableRestrictionDescr) fieldConstraintDescr.getRestrictions().get( 2 );
        assertEquals( "==",
                      variableDescr.getEvaluator() );
        assertEquals( "var1",
                      variableDescr.getIdentifier() );

        final PredicateDescr predicateDescr = (PredicateDescr) pattern3.getDescrs().get( 1 );
        assertEquals( "1==1",
                      predicateDescr.getContent() );

        final FieldBindingDescr fieldBindingDescr = (FieldBindingDescr) pattern3.getDescrs().get( 2 );
        assertEquals( "field1",
                      fieldBindingDescr.getFieldName() );
        assertEquals( "var1",
                      fieldBindingDescr.getIdentifier() );

        final NotDescr notDescr = (NotDescr) lhsDescr.getDescrs().get( 5 );
        assertEquals( 1,
                      notDescr.getDescrs().size() );
        PatternDescr patternDescr = (PatternDescr) notDescr.getDescrs().get( 0 );
        assertEquals( "Bar",
                      patternDescr.getObjectType() );

        final ExistsDescr existsDescr = (ExistsDescr) lhsDescr.getDescrs().get( 6 );
        assertEquals( 1,
                      existsDescr.getDescrs().size() );
        patternDescr = (PatternDescr) existsDescr.getDescrs().get( 0 );
        assertEquals( "Bar",
                      patternDescr.getObjectType() );

        andDescr = (AndDescr) lhsDescr.getDescrs().get( 7 );
        assertEquals( 2,
                      andDescr.getDescrs().size() );
        orDescr = (OrDescr) andDescr.getDescrs().get( 1 );
        patternDescr = (PatternDescr) orDescr.getDescrs().get( 0 );
        assertEquals( "Bar",
                      patternDescr.getObjectType() );
        patternDescr = (PatternDescr) andDescr.getDescrs().get( 0 );
        assertEquals( "Yada",
                      patternDescr.getObjectType() );

        orDescr = (OrDescr) lhsDescr.getDescrs().get( 8 );
        assertEquals( 2,
                      orDescr.getDescrs().size() );
        andDescr = (AndDescr) orDescr.getDescrs().get( 1 );
        patternDescr = (PatternDescr) andDescr.getDescrs().get( 0 );
        assertEquals( "Foo",
                      patternDescr.getObjectType() );
        patternDescr = (PatternDescr) orDescr.getDescrs().get( 0 );
        assertEquals( "Zaa",
                      patternDescr.getObjectType() );

        final EvalDescr evalDescr = (EvalDescr) lhsDescr.getDescrs().get( 9 );
        assertEquals( "1==1",
                      evalDescr.getContent() );
    }

    @Test
    public void testParseRhs() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseRhs.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        assertEquals( "com.sample",
                      packageDescr.getName() );

        final List imports = packageDescr.getImports();
        assertEquals( 2,
                      imports.size() );
        assertEquals( "java.util.HashMap",
                      ((ImportDescr) imports.get( 0 )).getTarget() );
        assertEquals( "org.drools.*",
                      ((ImportDescr) imports.get( 1 )).getTarget() );

        final List globals = packageDescr.getGlobals();
        assertEquals( 2,
                      globals.size() );
        final GlobalDescr x = (GlobalDescr) globals.get( 0 );
        final GlobalDescr yada = (GlobalDescr) globals.get( 1 );
        assertEquals( "com.sample.X",
                      x.getType() );
        assertEquals( "x",
                      x.getIdentifier() );
        assertEquals( "com.sample.Yada",
                      yada.getType() );
        assertEquals( "yada",
                      yada.getIdentifier() );

        final FunctionDescr functionDescr = (FunctionDescr) packageDescr.getFunctions().get( 0 );
        final List names = functionDescr.getParameterNames();
        assertEquals( "foo",
                      names.get( 0 ) );
        assertEquals( "bada",
                      names.get( 1 ) );

        final List types = functionDescr.getParameterTypes();
        assertEquals( "Bar",
                      types.get( 0 ) );
        assertEquals( "Bing",
                      types.get( 1 ) );

        assertEquals( "System.out.println(\"hello world\");",
                      functionDescr.getText().trim() );

        final RuleDescr ruleDescr = (RuleDescr) packageDescr.getRules().get( 0 );
        assertEquals( "my rule",
                      ruleDescr.getName() );

        final String consequence = (String) ruleDescr.getConsequence();
        assertNotNull( consequence );
        assertEquals( "System.out.println( \"hello\" );",
                      consequence.trim() );
    }

    @Test
    public void testParseQuery() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseQuery.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        assertEquals( "com.sample",
                      packageDescr.getName() );

        final List imports = packageDescr.getImports();
        assertEquals( 2,
                      imports.size() );
        assertEquals( "java.util.HashMap",
                      ((ImportDescr) imports.get( 0 )).getTarget() );
        assertEquals( "org.drools.*",
                      ((ImportDescr) imports.get( 1 )).getTarget() );

        final List globals = packageDescr.getGlobals();
        assertEquals( 2,
                      globals.size() );
        final GlobalDescr x = (GlobalDescr) globals.get( 0 );
        final GlobalDescr yada = (GlobalDescr) globals.get( 1 );
        assertEquals( "com.sample.X",
                      x.getType() );
        assertEquals( "x",
                      x.getIdentifier() );
        assertEquals( "com.sample.Yada",
                      yada.getType() );
        assertEquals( "yada",
                      yada.getIdentifier() );

        final FunctionDescr functionDescr = (FunctionDescr) packageDescr.getFunctions().get( 0 );
        final List names = functionDescr.getParameterNames();
        assertEquals( "foo",
                      names.get( 0 ) );
        assertEquals( "bada",
                      names.get( 1 ) );

        final List types = functionDescr.getParameterTypes();
        assertEquals( "Bar",
                      types.get( 0 ) );
        assertEquals( "Bing",
                      types.get( 1 ) );

        assertEquals( "System.out.println(\"hello world\");",
                      functionDescr.getText().trim() );

        final QueryDescr queryDescr = (QueryDescr) packageDescr.getRules().get( 0 );
        assertEquals( "my query",
                      queryDescr.getName() );

        final AndDescr lhs = queryDescr.getLhs();
        assertEquals( 1,
                      lhs.getDescrs().size() );
        final PatternDescr patternDescr = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( "Foo",
                      patternDescr.getObjectType() );

    }
    
    private XmlPackageReader getXmReader() {
        PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
        
        return new XmlPackageReader( conf.getSemanticModules() );        
    }
}
