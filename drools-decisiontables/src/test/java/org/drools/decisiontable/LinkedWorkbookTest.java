package org.drools.decisiontable;

import java.io.InputStream;

import org.drools.template.parser.DecisionTableParseException;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.*;

public class LinkedWorkbookTest {

    //This test currently throws a DecisionTableParseException as the RuleTable value in the linked external workbook is not cached in BZ967609_sample.xls
    @Test(expected = DecisionTableParseException.class)
    public void testLinkedWorkbook() {
        KieHelper kieHelper = new KieHelper();
        InputStream is = this.getClass().getResourceAsStream( "BZ967609_sample.xls" );
        kieHelper.addResource( ResourceFactory.newInputStreamResource( is ),
                               ResourceType.DTABLE );

        KieBase kbase = kieHelper.build();
        assertNotNull( kbase );
    }

}
