package org.drools.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;

import junit.framework.TestCase;

public class DataProviderCompilerIntegrationTest extends TestCase {

    private static final StringBuffer EXPECTED_RULES = new StringBuffer();
    
    static {
        String head = "package org.drools.decisiontable;\n#generated from Decision Table\nglobal FeeResult result;\n\n";
        String rule3_a = "rule \"Fee Schedule_3\"\n\tagenda-group \"STANDARD\"\n\twhen\n\t\tFeeEvent(productType == \"SBLC\",\n";
        String rule3_b = "\t\t\tactivityType == \"ISS\",\n\t\t\tfeeType == \"Telex\",\n\n\n\t\t\tamount < 30000,\n\t\t\tccy == \"YEN\"\n\t\t)\n";
        String rule3_then = "\tthen\n\t\tresult.setSchedule(new FeeSchedule(\"62\", \"STANDARD\", 45));\nend\n\n";
        
        String rule2_a = "rule \"Fee Schedule_2\"\n\tagenda-group \"STANDARD\"\n\twhen\n\t\tFeeEvent(productType == \"SBLC\",\n";
        String rule2_b = "\t\t\tactivityType == \"ISS\",\n\t\t\tfeeType == \"Postage\",\n\n\n\n\t\t\tccy == \"YEN\"\n\t\t)\n";
        String rule2_then = "\tthen\n\t\tresult.setSchedule(new FeeSchedule(\"12\", \"STANDARD\", 40));\nend\n\n";

        String rule1_a = "rule \"Fee Schedule_1\"\n\tagenda-group \"STANDARD\"\n\twhen\n\t\tFeeEvent(productType == \"SBLC\",\n";
        String rule1_b = "\t\t\tactivityType == \"ISS\",\n\t\t\tfeeType == \"Commission\",\n\n\t\t\tentityBranch == \"Entity Branch 1\",\n\n\t\t\tccy == \"YEN\"\n\t\t)\n";
        String rule1_then = "\tthen\n\t\tresult.setSchedule(new FeeSchedule(\"15\", \"STANDARD\", 1600));\nend\n\n";
        
        String rule0_a = "rule \"Fee Schedule_0\"\n\tagenda-group \"STANDARD\"\n\twhen\n\t\tFeeEvent(productType == \"SBLC\",\n";
        String rule0_b = "\t\t\tactivityType == \"ISS\",\n\t\t\tfeeType == \"Commission\",\n\t\t\ttxParty == \"Party 1\",\n\n\n\t\t\tccy == \"USD\"\n\t\t)\n";
        String rule0_then = "\tthen\n\t\tresult.setSchedule(new FeeSchedule(\"1\", \"STANDARD\", 750));\nend\n\n\n";
        
        EXPECTED_RULES.append( head );
        EXPECTED_RULES.append( rule3_a ).append( rule3_b ).append( rule3_then );
        EXPECTED_RULES.append( rule2_a ).append( rule2_b ).append( rule2_then );
        EXPECTED_RULES.append( rule1_a ).append( rule1_b ).append( rule1_then );
        EXPECTED_RULES.append( rule0_a ).append( rule0_b ).append( rule0_then );
    }
    
    private class TestDataProvider
        implements
        DataProvider {

        private Iterator<String[]> iterator;

        TestDataProvider(List<String[]> rows) {
            this.iterator = rows.iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public String[] next() {
            return iterator.next();
        }

    }

    public void testCompiler() throws Exception {
        ArrayList<String[]> rows = new ArrayList<String[]>();
        rows.add( createRow( "1",
                             "STANDARD",
                             "FLAT",
                             null,
                             "SBLC",
                             "ISS",
                             "Commission",
                             "Party 1",
                             "USD",
                             null,
                             "750",
                             "dummy") );
        rows.add( createRow( "15",
                             "STANDARD",
                             "FLAT",
                             "Entity Branch 1",
                             "SBLC",
                             "ISS",
                             "Commission",
                             null,
                             "YEN",
                             null,
                             "1600",
                             "dummy" ) );
        rows.add( createRow( "12",
                             "STANDARD",
                             "FLAT",
                             null,
                             "SBLC",
                             "ISS",
                             "Postage",
                             null,
                             "YEN",
                             null,
                             "40",
                             "dummy" ) );
        rows.add( createRow( "62",
                             "STANDARD",
                             "FLAT",
                             null,
                             "SBLC",
                             "ISS",
                             "Telex",
                             null,
                             "YEN",
                             "< 30000",
                             "45",
                             "dummy" ) );
        TestDataProvider tdp = new TestDataProvider( rows );
        final DataProviderCompiler converter = new DataProviderCompiler();
        final String drl = converter.compile( tdp,
                                              "/templates/rule_template_1.drl" );
        System.out.println( drl );
        assertEquals( EXPECTED_RULES.toString(), drl );

    }

    private String[] createRow(String cell1,
                               String cell2,
                               String cell3,
                               String cell4,
                               String cell5,
                               String cell6,
                               String cell7,
                               String cell8,
                               String cell9,
                               String cell10,
                               String cell11,
                               String cell12) {
        String[] row = new String[12];
        row[0] = cell1;
        row[1] = cell2;
        row[2] = cell3;
        row[3] = cell4;
        row[4] = cell5;
        row[5] = cell6;
        row[6] = cell7;
        row[7] = cell8;
        row[8] = cell9;
        row[9] = cell10;
        row[10] = cell11;
        row[11] = cell12;
        return row;
    }

}
