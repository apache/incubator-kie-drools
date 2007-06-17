package org.drools.xml;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.VariableRestrictionDescr;
import org.drools.rule.Package;

public class XmlPackageReaderTest extends TestCase {
    
    
    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    
    public void testParsePackageName() throws Exception {
        final XmlPackageReader xmlPackageReader = new XmlPackageReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParsePackageName.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        assertEquals( "com.sample",
                      packageDescr.getName() );
    }
        
    public void testParseExists() throws Exception {
        final XmlPackageReader xmlPackageReader = new XmlPackageReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseExists.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        
        RuleDescr obj = (RuleDescr) packageDescr.getRules().get( 0 );
        Object existdescr = obj.getLhs().getDescrs().get( 1 );
        assertTrue( existdescr instanceof ExistsDescr );
        
        Object patternDescriptor = ((ExistsDescr) existdescr).getDescrs().get( 0 );
        assertTrue( patternDescriptor instanceof PatternDescr );
    }
    
    public void testParseForall() throws Exception {
        final XmlPackageReader xmlPackageReader = new XmlPackageReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseForall.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        
        RuleDescr obj = (RuleDescr) packageDescr.getRules().get( 0 );
        assertEquals( obj.getLhs().getDescrs().size(), 3); 
    }

    /*
    public void testParseFrom() throws Exception {
        final XmlPackageReader xmlPackageReader = new XmlPackageReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseFrom.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        assertEquals( "com.sample", packageDescr.getName() );
        fail();
    }
    
    
    public void testParseCollect() throws Exception {
        final XmlPackageReader xmlPackageReader = new XmlPackageReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseCollect.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        assertEquals( "com.sample", packageDescr.getName() );
        fail();
    }
    */
    

    public void testParseImport() throws Exception {
        final XmlPackageReader xmlPackageReader = new XmlPackageReader();
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
    }

    public void testParseGlobal() throws Exception {
        final XmlPackageReader xmlPackageReader = new XmlPackageReader();
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

    public void testParseFunction() throws Exception {
        final XmlPackageReader xmlPackageReader = new XmlPackageReader();
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

    public void testParseRule() throws Exception {
        final XmlPackageReader xmlPackageReader = new XmlPackageReader();
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
        final AttributeDescr attributeDescr = (AttributeDescr) ruleDescr.getAttributes().get( 0 );
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

    public void testParseLhs() throws Exception {
        final XmlPackageReader xmlPackageReader = new XmlPackageReader();
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
        orDescr = (OrDescr) andDescr.getDescrs().get( 0 );
        patternDescr = (PatternDescr) orDescr.getDescrs().get( 0 );
        assertEquals( "Bar",
                      patternDescr.getObjectType() );
        patternDescr = (PatternDescr) andDescr.getDescrs().get( 1 );
        assertEquals( "Yada",
                      patternDescr.getObjectType() );

        orDescr = (OrDescr) lhsDescr.getDescrs().get( 8 );
        assertEquals( 2,
                      orDescr.getDescrs().size() );
        andDescr = (AndDescr) orDescr.getDescrs().get( 0 );
        patternDescr = (PatternDescr) andDescr.getDescrs().get( 0 );
        assertEquals( "Foo",
                      patternDescr.getObjectType() );
        patternDescr = (PatternDescr) orDescr.getDescrs().get( 1 );
        assertEquals( "Zaa",
                      patternDescr.getObjectType() );

        final EvalDescr evalDescr = (EvalDescr) lhsDescr.getDescrs().get( 9 );
        assertEquals( "1==1",
                      evalDescr.getContent() );
    }

    public void testParseRhs() throws Exception {
        final XmlPackageReader xmlPackageReader = new XmlPackageReader();
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

    public void testParseQuery() throws Exception {
        final XmlPackageReader xmlPackageReader = new XmlPackageReader();
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
}
