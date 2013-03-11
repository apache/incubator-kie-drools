package org.drools.scorecards;

import junit.framework.Assert;
import org.junit.Test;

import static org.drools.scorecards.ScorecardCompiler.DrlType.INTERNAL_DECLARED_TYPES;

public class ScorecardParseErrorsTest {

    @Test
    public void testErrorCount() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        boolean compileResult = scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_errors.xls"));
        Assert.assertFalse(compileResult);
        Assert.assertEquals(4, scorecardCompiler.getScorecardParseErrors().size());
        Assert.assertEquals("$C$4", scorecardCompiler.getScorecardParseErrors().get(0).getErrorLocation());
        Assert.assertEquals("Scorecard Package is missing", scorecardCompiler.getScorecardParseErrors().get(0).getErrorMessage());
//        for(ScorecardError error : scorecardCompiler.getScorecardParseErrors()){
//            System.out.println("testErrorCount :"+error.getErrorLocation()+"->"+error.getErrorMessage());
//        }
    }

    @Test
    public void testWrongData() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler();
        boolean compileResult = scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_errors.xls"), "scorecards_wrongData");
//        for(ScorecardError error : scorecardCompiler.getScorecardParseErrors()){
//            System.out.println("testWrongData :"+error.getErrorLocation()+"->"+error.getErrorMessage());
//        }
        Assert.assertFalse(compileResult);
        Assert.assertEquals(4, scorecardCompiler.getScorecardParseErrors().size());
        Assert.assertEquals("$D$10", scorecardCompiler.getScorecardParseErrors().get(0).getErrorLocation());
        Assert.assertEquals("$D$19", scorecardCompiler.getScorecardParseErrors().get(1).getErrorLocation());
        Assert.assertEquals("$C$8", scorecardCompiler.getScorecardParseErrors().get(2).getErrorLocation());
        Assert.assertEquals("$C$28", scorecardCompiler.getScorecardParseErrors().get(3).getErrorLocation());

    }

    @Test
    public void testMissingDataType() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(ScorecardCompiler.DrlType.INTERNAL_DECLARED_TYPES);
        boolean compileResult = scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_errors.xls"), "missingDataType");
//        for(ScorecardError error : scorecardCompiler.getScorecardParseErrors()){
//            System.out.println("testMissingDataType :"+error.getErrorLocation()+"->"+error.getErrorMessage());
//        }
        Assert.assertFalse(compileResult);
        Assert.assertEquals(2, scorecardCompiler.getScorecardParseErrors().size());
        Assert.assertEquals("$C$8", scorecardCompiler.getScorecardParseErrors().get(0).getErrorLocation());
        Assert.assertEquals("$C$16", scorecardCompiler.getScorecardParseErrors().get(1).getErrorLocation());
    }

    @Test
    public void testMissingAttributes() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(ScorecardCompiler.DrlType.INTERNAL_DECLARED_TYPES);
        boolean compileResult = scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_errors.xls"), "incomplete_noAttr");
        Assert.assertFalse(compileResult);
//        Assert.assertEquals(2, scorecardCompiler.getScorecardParseErrors().size());
//        Assert.assertEquals("$C$11", scorecardCompiler.getScorecardParseErrors().get(0).getErrorLocation());
//        Assert.assertEquals("$C$19", scorecardCompiler.getScorecardParseErrors().get(1).getErrorLocation());
//        for(ScorecardError error : scorecardCompiler.getScorecardParseErrors()){
//            System.out.println("testMissingAttributes :"+error.getErrorLocation()+"->"+error.getErrorMessage());
//        }
    }
//
}
