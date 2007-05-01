package org.drools.examples.sudoku;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;


/**
 * This example shows, how a Sudoku puzzle can be solved using JBoss Rules
 * 
 * @author <a href="mailto:michael.frandsen@syngenio.de">Michael Frandsen</a>
 */
public class SudokuExample {

	public static void testWithInput(int[][] field){
        StatefulSession session = null;
		try {
        	Collection fields = new ArrayList();
        	//create Fields for every element of the matrix
        	//Print Sudoku, which is to solve
        	for(int i = 0; i < field.length; i++){
        		String row = "{";
        		for(int j = 0; j < field[i].length; j++){
        			row += field[i][j] + ",";
        			fields.add(new Field(""+field[i][j], j+1, i+1, getZone(i,j)));
        		}
        		row+= "}";
        		System.out.println(row);
        	}
        	//load up the rulebase
            RuleBase ruleBase = readRule();
            session = ruleBase.newStatefulSession();
            
            //go !
            Iterator iter = fields.iterator();

        	Collection handles = new ArrayList();
            while(iter.hasNext()){
            	handles.add(session.assertObject( iter.next() ));
            }            
            session.fireAllRules();
            
            System.out.println("Size: " + iteratorToList(session.iterateObjects()).size());
            
            //Get Result
            iter = session.iterateObjects();
            //Copy the values of the fields into the matrix
            while(iter.hasNext()) {
            	Object next = iter.next();
            	if(next instanceof Field){
            		field[((Field)next).getRow()-1][((Field)next).getColumn()-1] = Integer.parseInt(((Field)next).getValue());
            	}
            }
            
            //Pr
            for(int i = 0; i < field.length; i++){
        		String row = "{";
        		for(int j = 0; j < field[i].length; j++){
        			row += field[i][j] + ",";
        			fields.add(new Field(""+field[i][j], j+1, i+1, getZone(i,j)));
        		}
        		row+= "}";
        		System.out.println(row);
        	}
            
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if ( session != null ) {
                session.dispose();
            }
        }
	}
    public static final void main(String[] args) {
//    	testWithInput(FieldGenerator.getInstance().getField());
//    	testWithInput(FieldGenerator.getInstance().getFieldMiddle());
//    	testWithInput(FieldGenerator.getInstance().getFieldHard());
//    	testWithInput(FieldGenerator.getInstance().getFieldHard2());
//    	testWithInput(FieldGenerator.getInstance().getFieldHard3());
    	testWithInput(FieldGenerator.getInstance().getFieldHard4());
    }

    private static int getZone(int row, int column) {
    	if(column < 3 && row < 3){
    		return 1;
    	}
    	else if( column < 6 && row < 3 ){
    		return 2;
    	}
    	else if( column < 9 && row < 3){
    		return 3;
    	}
    	else if( column < 3 && row < 6){
    		return 4;
    	}
    	else if( column < 6 && row < 6){
    		return 5;
    	}
    	else if( column < 9 && row < 6){
    		return 6;
    	}
    	else if( column < 3){
    		return 7;
    	}
    	else if( column < 6){
    		return 8;
    	}
    	else if(column < 9){
    		return 9;
    	}
		return 0;
	}

	/**
     * Please note that this is the "low level" rule assembly API.
     */
	private static RuleBase readRule() throws Exception {
		//read in the source
		Reader source = new InputStreamReader( SudokuExample.class.getResourceAsStream( "sudoku.drl" ) );
		
		//optionally read in the DSL (if you are using it).
		//Reader dsl = new InputStreamReader( DroolsTest.class.getResourceAsStream( "/mylang.dsl" ) );

		//Use package builder to build up a rule package.
		//An alternative lower level class called "DrlParser" can also be used...
		
		PackageBuilder builder = new PackageBuilder();

		//this wil parse and compile in one step
		//NOTE: There are 2 methods here, the one argument one is for normal DRL.
		builder.addPackageFromDrl( source );

		//Use the following instead of above if you are using a DSL:
		//builder.addPackageFromDrl( source, dsl );
		
		//get the compiled package (which is serializable)
		Package pkg = builder.getPackage();
		
		//add the package to a rulebase (deploy the rule package).
		RuleBaseConfiguration conf = new RuleBaseConfiguration();
		conf.setRemoveIdentities( true );
		RuleBase ruleBase = RuleBaseFactory.newRuleBase(RuleBase.RETEOO, conf);
		ruleBase.addPackage( pkg );
		return ruleBase;
	}
    
    private static List iteratorToList(Iterator it) {
        List list = new ArrayList();
        for (;it.hasNext();) {
            list.add( it.next() );
        }
        return list;
    }
}
