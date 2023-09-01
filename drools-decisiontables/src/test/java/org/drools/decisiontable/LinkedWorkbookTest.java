package org.drools.decisiontable;

import java.io.InputStream;

import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class LinkedWorkbookTest {

    @Test
    public void testBrokenExternalLinkButValueIsCached() throws Exception {
        KieHelper kieHelper = new KieHelper();
        // do not modify this XLS file using OpenOffice or LibreOffice or the external link gets corrupted and the test fails!
        InputStream dtableIs = this.getClass().getResourceAsStream("BZ967609-brokenExtLinkButValueCached.drl.xls");
        kieHelper.addResource(ResourceFactory.newInputStreamResource(dtableIs),
                ResourceType.DTABLE);
        
        KieBase kbase = kieHelper.build();
        
        assertThat(kbase).isNotNull();
    }

}
