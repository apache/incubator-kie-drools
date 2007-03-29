package org.drools.analytics;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.CheckedDroolsException;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.compiler.RuleBaseLoader;

import junit.framework.TestCase;

public class BasicTest extends TestCase {

    public void testLessThan() throws Exception {
        Rule rule = new Rule();
        
        Constraint con = new Constraint();
        con.setField( "foo" );
        con.setObjectType( "Bar" );
        con.setOperator( "<" );
        con.setRuleName( "xxx" );
        
        WorkingMemory wm = getAnalysisEngine();
        
        AnalysisResult result = new AnalysisResult();
        wm.setGlobal( "result", result );        
        wm.assertObject( con );
        wm.assertObject( rule );
        
        wm.fireAllRules();
        
        assertEquals(1, result.getWarnings().size());
        assertEquals(0, result.getErrors().size());        
    }
    
    public void testLessThanMultiple() throws Exception {
        final Constraint con = new Constraint();
        con.setField( "foo" );
        con.setObjectType( "Bar" );
        con.setOperator( "<" );
        con.setRuleName( "xxx" );
        
        Constraint con2 = new Constraint();
        con2.setField( "foo2" );
        con2.setObjectType( "Bar" );
        con2.setOperator( "<" );
        con2.setRuleName( "zar" );
        
        
        WorkingMemory wm = getAnalysisEngine();
        
        AnalysisResult result = new AnalysisResult();
        wm.setGlobal( "result", result );        
        wm.assertObject( con );
        wm.assertObject( con2 );
        wm.assertObject( new Rule() );
        wm.fireAllRules();
        
        assertEquals(2, result.getWarnings().size());
        assertEquals(0, result.getErrors().size());        
    }    
    
    public void testLessThanMultiple2() throws Exception {
        final Constraint con = new Constraint();
        con.setField( "foo" );
        con.setObjectType( "Bar" );
        con.setOperator( "<" );
        con.setRuleName( "xxx" );
        
        Constraint con2 = new Constraint();
        con2.setField( "foo2" );
        con2.setObjectType( "Bar" );
        con2.setOperator( "<" );
        con2.setRuleName( "xxx" );
        
        
        WorkingMemory wm = getAnalysisEngine();
        
        AnalysisResult result = new AnalysisResult();
        wm.setGlobal( "result", result );        
        wm.assertObject( con );
        wm.assertObject( con2 );
        wm.assertObject( new Rule() );
        
        wm.fireAllRules();
        
        assertEquals(2, result.getWarnings().size());
        assertEquals(0, result.getErrors().size());        
    }       

    private WorkingMemory getAnalysisEngine() throws CheckedDroolsException,
                                             IOException {
        Reader r = new InputStreamReader(this.getClass().getResourceAsStream( "/StaticQualityAnalysis.drl" ));
        RuleBase rb = RuleBaseLoader.getInstance().loadFromReader( r );
        WorkingMemory wm = rb.newWorkingMemory();
        return wm;
    }
    
    public void testLessThanOK() throws Exception {
        Constraint con = new Constraint();
        con.setField( "foo" );
        con.setObjectType( "Bar" );
        con.setOperator( "<" );
        con.setRuleName( "xxx" );
        
        Constraint con2 = new Constraint();
        con2.setField( "foo" );
        con2.setObjectType( "Bar" );
        con2.setOperator( ">=" );
        con2.setRuleName( "xxx2" );
        
        
        WorkingMemory wm = getAnalysisEngine();
        
        AnalysisResult result = new AnalysisResult();
        wm.setGlobal( "result", result );        
        
        wm.assertObject( con );
        wm.assertObject( con2 );
        wm.assertObject( new Rule() );
        
        wm.fireAllRules();
        
        assertEquals(0, result.getWarnings().size());
        assertEquals(0, result.getErrors().size());          
    }
    
    public void testNoRHS() throws Exception {
        WorkingMemory wm = getAnalysisEngine();
        
        Rule r = new Rule();
        r.setRhs( "" );
        
        AnalysisResult result = new AnalysisResult();
        
        wm.setGlobal( "result", result );
        
        wm.assertObject( r );
        
        wm.fireAllRules();
        
        assertEquals(1, result.getWarnings().size());
    }
    
}
