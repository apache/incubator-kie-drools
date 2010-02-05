package org.drools.guvnor.client.modeldriven.brl;

import static org.junit.Assert.*;

import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.junit.Test;

public class CopyExpressionVisitorTest {

	@Test
	public void testCopy() {
		ExpressionFormLine efl = new ExpressionFormLine();
		FactPattern fp = new FactPattern(SuggestionCompletionEngine.TYPE_OBJECT);
		fp.boundName = "$o";
		efl.appendPart(new ExpressionVariable(fp));
		ExpressionMethod em = new ExpressionMethod("aMethod", "aClass", "aType");
		
		ExpressionFormLine param = new ExpressionFormLine();
		param.appendPart(new ExpressionText("\"hello world\""));
		em.putParam("hi", param);
		
		efl.appendPart(em);
		
		
		ExpressionCollectionIndex index = new ExpressionCollectionIndex("get", "aRetType", "aGeneric");
		efl.appendPart(index);
		
		index = new ExpressionCollectionIndex("get2", "aRetType", "aGeneric");
		efl.appendPart(index);
		
		System.out.println(efl.getText());
		ExpressionFormLine copy = new ExpressionFormLine(efl);
		assertNotSame(efl, copy);
		assertEquals(efl.getText(), copy.getText());
		for (ExpressionPart e1 = efl.getRootExpression(), e2 = copy.getRootExpression(); e1 != null; e1 = e1.getNext(), e2 = e2.getNext()) {
			assertNotSame(e1, e2);
			assertEquals(e1.getClass(), e2.getClass());
		}
	}

}
