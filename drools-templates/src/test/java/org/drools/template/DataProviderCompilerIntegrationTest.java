package org.drools.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;

import junit.framework.TestCase;

public class DataProviderCompilerIntegrationTest extends TestCase {

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
                             null,
                             "USD",
                             null,
                             "750" ) );
        rows.add( createRow( "15",
                             "STANDARD",
                             "FLAT",
                             null,
                             "SBLC",
                             "ISS",
                             "Commission",
                             null,
                             "YEN",
                             null,
                             "1600" ) );
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
                             "40" ) );
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
                             "45" ) );
        TestDataProvider tdp = new TestDataProvider( rows );
        final DataProviderCompiler converter = new DataProviderCompiler();
        final String drl = converter.compile( tdp,
                                              "/templates/rule_template_1.drl" );
        System.out.println( drl );

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
                               String cell11) {
        String[] row = new String[11];
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
        return row;
    }

}
