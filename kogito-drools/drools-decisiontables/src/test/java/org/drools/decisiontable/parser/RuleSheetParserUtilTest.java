/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.decisiontable.parser;

import java.util.ArrayList;
import java.util.List;

import org.drools.template.model.Global;
import org.drools.template.model.Import;
import org.drools.template.parser.DecisionTableParseException;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 * 
 * Nuff said...
 */
public class RuleSheetParserUtilTest {

	@Test
	public void testRuleName() {
		final String row = "  RuleTable       This is my rule name";
		final String result = RuleSheetParserUtil.getRuleName( row );
		assertEquals( "This is my rule name",
				result );
	}

	/**
	 * This is here as the old way was to do this.
	 */
	@Ignore
	@Test
	public void testInvalidRuleName() {
		final String row = "RuleTable       This is my rule name (type class)";
		try {
			final String result = RuleSheetParserUtil.getRuleName( row );
			fail( "should have failed, but get result: " + result );
		} catch ( final IllegalArgumentException e ) {
			assertNotNull( e.getMessage() );
		}
	}

	@Test
	public void testIsStringMeaningTrue() {
		assertTrue( RuleSheetParserUtil.isStringMeaningTrue( "true" ) );
		assertTrue( RuleSheetParserUtil.isStringMeaningTrue( "TRUE" ) );
		assertTrue( RuleSheetParserUtil.isStringMeaningTrue( "yes" ) );
		assertTrue( RuleSheetParserUtil.isStringMeaningTrue( "oN" ) );

		assertFalse( RuleSheetParserUtil.isStringMeaningTrue( "no" ) );
		assertFalse( RuleSheetParserUtil.isStringMeaningTrue( "false" ) );
		assertFalse( RuleSheetParserUtil.isStringMeaningTrue( null ) );
	}

	@Test
	public void testListImports() {
		List<String> cellVals = null;

		List<Import> list = RuleSheetParserUtil.getImportList( cellVals );
		assertNotNull( list );
		assertEquals( 0, list.size() );

		cellVals = new ArrayList<String>();
		cellVals.add( "" );
		assertEquals( 0, RuleSheetParserUtil.getImportList( cellVals ).size() );

		cellVals.add( 0, "com.something.Yeah, com.something.No,com.something.yeah.*" );
		list = RuleSheetParserUtil.getImportList( cellVals );
		assertEquals( 3, list.size() );
		assertEquals( "com.something.Yeah",   (list.get( 0 )).getClassName() );
		assertEquals( "com.something.No",     (list.get( 1 )).getClassName() );
		assertEquals( "com.something.yeah.*", (list.get( 2 )).getClassName() );
	}

	@Test
	public void testListVariables() {
		List<String> varCells = new ArrayList<String>();
		varCells.add( "Var1 var1, Var2 var2,Var3 var3" );
		final List<Global> varList = RuleSheetParserUtil.getVariableList( varCells );
		assertNotNull( varList );
		assertEquals( 3,
				varList.size() );
		Global var = varList.get( 0 );
		assertEquals( "Var1",
				var.getClassName() );
		var = varList.get( 2 );
		assertEquals( "Var3",
				var.getClassName() );
		assertEquals( "var3",
				var.getIdentifier() );
	}

	@Test
	public void testBadVariableFormat() {
		List<String> varCells = new ArrayList<String>();
		varCells.add( "class1, object2" );
		try {
			RuleSheetParserUtil.getVariableList( varCells );
			fail( "should not work" );
		} catch ( final DecisionTableParseException e ) {
			assertNotNull( e.getMessage() );
		}
	}

	@Test
	public void testRowColumnToCellNAme() {
		String cellName = RuleSheetParserUtil.rc2name( 0, 0 );
		assertEquals( "A1", cellName );

		cellName = RuleSheetParserUtil.rc2name( 0, 10 );
		assertEquals( "K1", cellName );

		cellName = RuleSheetParserUtil.rc2name( 0, 42 );
		assertEquals( "AQ1", cellName );

		cellName = RuleSheetParserUtil.rc2name( 9, 27 );
		assertEquals( "AB10", cellName );

		cellName = RuleSheetParserUtil.rc2name( 99, 53 );
		assertEquals( "BB100", cellName );
	}
}
