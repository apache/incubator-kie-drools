package org.drools.decisiontable.parser;

import java.io.InputStream;

import org.drools.template.model.Package;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * A special test for parsing a large workbook, to see how it scales.
 */
public class RuleWorksheetParseLargeTest {

    private long startTimer;

    private long endTimer;

    /**
     * Tests parsing a large spreadsheet into an in memory ruleset. This doesn't
     * really do anything much at present. Takes a shed-load of memory to dump
     * out this much XML as a string, so really should think of using a stream
     * in some cases... (tried StringWriter, but is still in memory, so doesn't
     * help).
     *
     * Stream to a temp file would work: return a stream from that file
     * (decorate FileInputStream such that when you close it, it deletes the
     * temp file).... must be other options.
     *
     * @throws Exception
     */
    @Test
    public void testLargeWorkSheetParseToRuleset() throws Exception {
        //  Test removed until have streaming sorted in future. No one using Uber Tables just yet !
        InputStream stream = RuleWorksheetParseLargeTest.class.getResourceAsStream("/data/VeryLargeWorkbook.drl.xls");

        startTimer();
        final InputStream stream1 = stream;
        RuleSheetListener listener = RulesheetUtil.getRuleSheetListener(stream1);
        stopTimer();

        System.out.println("Time to parse large table : " + getTime() + "ms");
        Package ruleset = listener.getRuleSet();
        assertThat(ruleset).isNotNull();

//        startTimer();
//        String xml = ...;   // toXml() not in Package any more.
//        System.out.println(xml);
//        stopTimer();
//        System.out.println("Time taken for rendering: " + getTime() + "ms");
    }

    private void startTimer() {
        this.startTimer = System.currentTimeMillis();
    }


    private void stopTimer() {
        this.endTimer = System.currentTimeMillis();
    }

    private long getTime() {
        return this.endTimer - this.startTimer;
    }

}
