package org.drools.decisiontable.parser;

import org.drools.decisiontable.parser.LhsBuilder.FieldType;

import junit.framework.TestCase;

public class LhsBuilderTest extends TestCase {

	public void testBuildItUp() throws Exception {
		LhsBuilder builder = new LhsBuilder("Person");
		
		builder.addTemplate(1, "age");
		builder.addTemplate(2, "size != $param");
		builder.addTemplate(3, "date <");
		
		builder.addCellValue(1, "42");
		builder.addCellValue(2, "20");
		builder.addCellValue(3, "30");
		
		
		assertEquals("Person(age == \"42\", size != 20, date < \"30\")", builder.getResult());
        
        builder.clearValues();
        
        builder.addCellValue( 2, "42" );
        assertEquals("Person(size != 42)", builder.getResult());
	}
    
    public void testEmptyCells() {
        LhsBuilder builder = new LhsBuilder("Person");
        assertFalse(builder.hasValues());
    }
    
    public void testClassicMode() {
        LhsBuilder builder = new LhsBuilder("");
        builder.addTemplate( 1, "Person(age < $param)");
        builder.addCellValue( 1, "42" );
        
        assertEquals("Person(age < 42)", builder.getResult());
        
        builder = new LhsBuilder(null);
        builder.addTemplate( 3, "Foo(bar == $param)");
        builder.addTemplate( 4, "eval(true)");
        
        builder.addCellValue( 3, "42" );
        builder.addCellValue( 4, "Y" );
        
        assertEquals("Foo(bar == 42)\neval(true)", builder.getResult());
    }
 
	public void testForAllAndFucntion() {
		LhsBuilder builder = new LhsBuilder("");
		builder.addTemplate(1, "forall(&&){Foo(bar != $)}");
		builder.addCellValue(1, "42,43");
		assertEquals("Foo(bar != 42) && Foo(bar != 43)", builder.getResult());
	}
    
	public void testForAllOr() {
		LhsBuilder builder = new LhsBuilder("Person");
		builder.addTemplate(1, "forall(||){age < $}");
		builder.addCellValue(1, "42");
		assertEquals("Person(age < 42)", builder.getResult());
	}

	public void testForAllOrPrefix() {
		LhsBuilder builder = new LhsBuilder("Person");
		builder.addTemplate(1, "age < 10 && forall(||){age < $}");
		builder.addCellValue(1, "42");
		assertEquals("Person(age < 10 && age < 42)", builder.getResult());
	}
	
	public void testForAllOrCSV() {
		LhsBuilder builder = new LhsBuilder("Person");
		builder.addTemplate(1, "forall(||){age < $}");
		builder.addCellValue(1, "42, 43, 44");
		assertEquals("Person(age < 42 || age < 43 || age < 44)", builder
				.getResult());
	}

	public void testForAllAnd() {
		LhsBuilder builder = new LhsBuilder("Person");
		builder.addTemplate(1, "forall(&&){age < $}");
		builder.addCellValue(1, "42");
		assertEquals("Person(age < 42)", builder.getResult());
	}

	public void testForAllAndCSV() {
		LhsBuilder builder = new LhsBuilder("Person");
		builder.addTemplate(1, "forall(&&){age < $}");
		builder.addCellValue(1, "42, 43, 44");
		assertEquals("Person(age < 42 && age < 43 && age < 44)", builder
				.getResult());
	}

	public void testForAllAndForAllOrCSVMultiple() {
		LhsBuilder builder = new LhsBuilder("Person");
		builder.addTemplate(1, "forall(&&){age < $ || age == $}");
		builder.addCellValue(1, "42, 43, 44");
		assertEquals(
				"Person(age < 42 || age == 42 && age < 43 || age == 43 && age < 44 || age == 44)",
				builder.getResult());
	}

	public void testForAllsAndForAllOrCSVMultiple() {
		LhsBuilder builder = new LhsBuilder("Person");
		builder.addTemplate(1, "forall(&&){age < $ || age == $} && forall(&&){age < $ || age == $}");
		builder.addCellValue(1, "42, 43, 44");
		assertEquals(
				"Person(age < 42 || age == 42 && age < 43 || age == 43 && age < 44 || age == 44 && age < 42 || age == 42 && age < 43 || age == 43 && age < 44 || age == 44)",
				builder.getResult());
	}
	
    public void testIdentifyFieldTypes() {
        LhsBuilder builder = new LhsBuilder("");
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("age"));
        assertEquals(FieldType.OPERATOR_FIELD, builder.calcFieldType("age <"));
        assertEquals(FieldType.NORMAL_FIELD, builder.calcFieldType("age < $param"));
        assertEquals(FieldType.NORMAL_FIELD, builder.calcFieldType("forall(||){age < $}"));
        assertEquals(FieldType.NORMAL_FIELD, builder.calcFieldType("forall(&&){age < $}"));
        assertEquals(FieldType.NORMAL_FIELD, builder.calcFieldType("forall(,){age < $}"));
        assertEquals(FieldType.NORMAL_FIELD, builder.calcFieldType("forall(){age < $}"));
        assertEquals(FieldType.NORMAL_FIELD, builder.calcFieldType("forall(){age < $} && forall(){age == $}"));
        assertEquals(FieldType.NORMAL_FIELD, builder.calcFieldType("x && forall(){age < $} && forall(){age == $}"));
        assertEquals(FieldType.NORMAL_FIELD, builder.calcFieldType("x && forall(){age < $} && forall(){age == $} && y"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("age < $para"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("forall{||}{age < $}"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("forall(){}"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("forall(){age < $"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("forall(){,"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("forall({})"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("forall({}){test})"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("forall(&&){{}})"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("forall(&&){{})"));
    }
    
    public void testIdentifyColumnCorrectly() {
        LhsBuilder builder = new LhsBuilder(null);
        assertFalse(builder.isMultipleConstraints());
        
        //will be added to Foo
        builder = new LhsBuilder("Foo");
        assertTrue(builder.isMultipleConstraints());
        
        //will be added to eval
        builder = new LhsBuilder("f:Foo() eval  ");
        assertTrue(builder.isMultipleConstraints());
        
        //will just be verbatim
        builder = new LhsBuilder("f: Foo()");
        assertFalse(builder.isMultipleConstraints());
        
    }
	
}
