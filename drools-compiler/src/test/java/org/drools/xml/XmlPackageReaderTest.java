package org.drools.xml;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.VariableRestrictionDescr;

public class XmlPackageReaderTest extends TestCase {
    public void testParsePackageName() throws Exception {
        final XmlPackageReader xmlPackageReader = new XmlPackageReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParsePackageName.xml" ) ) );
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertNotNull( packageDescr );
        assertEquals( "com.sample",
                      packageDescr.getName() );
    }

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
                      imports.get( 0 ) );
        assertEquals( "org.drools.*",
                      imports.get( 1 ) );
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
                      imports.get( 0 ) );
        assertEquals( "org.drools.*",
                      imports.get( 1 ) );

        final Map globals = packageDescr.getGlobals();
        assertEquals( 2,
                      globals.size() );
        assertEquals( "com.sample.X",
                      globals.get( "x" ) );
        assertEquals( "com.sample.Yada",
                      globals.get( "yada" ) );
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
                      imports.get( 0 ) );
        assertEquals( "org.drools.*",
                      imports.get( 1 ) );

        final Map globals = packageDescr.getGlobals();
        assertEquals( 2,
                      globals.size() );
        assertEquals( "com.sample.X",
                      globals.get( "x" ) );
        assertEquals( "com.sample.Yada",
                      globals.get( "yada" ) );

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
                      imports.get( 0 ) );
        assertEquals( "org.drools.*",
                      imports.get( 1 ) );

        final Map globals = packageDescr.getGlobals();
        assertEquals( 2,
                      globals.size() );
        assertEquals( "com.sample.X",
                      globals.get( "x" ) );
        assertEquals( "com.sample.Yada",
                      globals.get( "yada" ) );

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

        assertEquals( 1,
                      ruleDescr.getAttributes().size() );
        final AttributeDescr attributeDescr = (AttributeDescr) ruleDescr.getAttributes().get( 0 );
        assertEquals( "salience",
                      attributeDescr.getName() );
        assertEquals( "10",
                      attributeDescr.getValue() );

        final AndDescr lhs = ruleDescr.getLhs();
        assertEquals( 1,
                      lhs.getDescrs().size() );
        final ColumnDescr columnDescr = (ColumnDescr) lhs.getDescrs().get( 0 );
        assertEquals( "Foo",
                      columnDescr.getObjectType() );

        final String consequence = ruleDescr.getConsequence();
        assertNotNull( consequence );
        assertEquals( "System.out.println( \"hello\" );",
                      consequence.trim() );

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
                      imports.get( 0 ) );
        assertEquals( "org.drools.*",
                      imports.get( 1 ) );

        final Map globals = packageDescr.getGlobals();
        assertEquals( 2,
                      globals.size() );
        assertEquals( "com.sample.X",
                      globals.get( "x" ) );
        assertEquals( "com.sample.Yada",
                      globals.get( "yada" ) );

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
        final ColumnDescr column1 = (ColumnDescr) lhsDescr.getDescrs().get( 2 );
        assertNull( column1.getIdentifier() );
        assertEquals( "Foo",
                      column1.getObjectType() );

        final ColumnDescr column2 = (ColumnDescr) lhsDescr.getDescrs().get( 3 );
        assertEquals( "Bar",
                      column2.getObjectType() );
        assertEquals( "bar",
                      column2.getIdentifier() );

        final ColumnDescr column3 = (ColumnDescr) lhsDescr.getDescrs().get( 4 );
        //final LiteralDescr literalDescr = (LiteralDescr) column3.getDescrs().get( 0 );
        FieldConstraintDescr fieldConstraintDescr = (FieldConstraintDescr) column3.getDescrs().get( 0 );
        final LiteralRestrictionDescr literalDescr = (  LiteralRestrictionDescr ) fieldConstraintDescr.getRestrictions().get( 0 );
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
                      returnValueDescr.getText() );
        
      final VariableRestrictionDescr variableDescr = (VariableRestrictionDescr) fieldConstraintDescr.getRestrictions().get( 2 ); 
      assertEquals( "==",
                    variableDescr.getEvaluator() );
      assertEquals( "var1",
                    variableDescr.getIdentifier() );        
        
        final PredicateDescr predicateDescr = (PredicateDescr) column3.getDescrs().get( 1 );
        assertEquals( "field1",
                      predicateDescr.getFieldName() );
        assertEquals( "var1",
                      predicateDescr.getDeclaration() );
        assertEquals( "1==1",
                      predicateDescr.getText() );        


        final FieldBindingDescr fieldBindingDescr = (FieldBindingDescr) column3.getDescrs().get( 2 );
        assertEquals( "field1",
                      fieldBindingDescr.getFieldName() );
        assertEquals( "var1",
                      fieldBindingDescr.getIdentifier() );

        final NotDescr notDescr = (NotDescr) lhsDescr.getDescrs().get( 5 );
        assertEquals( 1,
                      notDescr.getDescrs().size() );
        ColumnDescr columnDescr = (ColumnDescr) notDescr.getDescrs().get( 0 );
        assertEquals( "Bar",
                      columnDescr.getObjectType() );

        final ExistsDescr existsDescr = (ExistsDescr) lhsDescr.getDescrs().get( 6 );
        assertEquals( 1,
                      existsDescr.getDescrs().size() );
        columnDescr = (ColumnDescr) existsDescr.getDescrs().get( 0 );
        assertEquals( "Bar",
                      columnDescr.getObjectType() );

        andDescr = (AndDescr) lhsDescr.getDescrs().get( 7 );
        assertEquals( 2,
                      andDescr.getDescrs().size() );
        orDescr = (OrDescr) andDescr.getDescrs().get( 0 );
        columnDescr = (ColumnDescr) orDescr.getDescrs().get( 0 );
        assertEquals( "Bar",
                      columnDescr.getObjectType() );
        columnDescr = (ColumnDescr) andDescr.getDescrs().get( 1 );
        assertEquals( "Yada",
                      columnDescr.getObjectType() );

        orDescr = (OrDescr) lhsDescr.getDescrs().get( 8 );
        assertEquals( 2,
                      orDescr.getDescrs().size() );
        andDescr = (AndDescr) orDescr.getDescrs().get( 0 );
        columnDescr = (ColumnDescr) andDescr.getDescrs().get( 0 );
        assertEquals( "Foo",
                      columnDescr.getObjectType() );
        columnDescr = (ColumnDescr) orDescr.getDescrs().get( 1 );
        assertEquals( "Zaa",
                      columnDescr.getObjectType() );

        final EvalDescr evalDescr = (EvalDescr) lhsDescr.getDescrs().get( 9 );
        assertEquals( "1==1",
                      evalDescr.getText() );
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
                      imports.get( 0 ) );
        assertEquals( "org.drools.*",
                      imports.get( 1 ) );

        final Map globals = packageDescr.getGlobals();
        assertEquals( 2,
                      globals.size() );
        assertEquals( "com.sample.X",
                      globals.get( "x" ) );
        assertEquals( "com.sample.Yada",
                      globals.get( "yada" ) );

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

        final String consequence = ruleDescr.getConsequence();
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
                      imports.get( 0 ) );
        assertEquals( "org.drools.*",
                      imports.get( 1 ) );

        final Map globals = packageDescr.getGlobals();
        assertEquals( 2,
                      globals.size() );
        assertEquals( "com.sample.X",
                      globals.get( "x" ) );
        assertEquals( "com.sample.Yada",
                      globals.get( "yada" ) );

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
        final ColumnDescr columnDescr = (ColumnDescr) lhs.getDescrs().get( 0 );
        assertEquals( "Foo",
                      columnDescr.getObjectType() );

    }
}
