package org.drools.template;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.template.parser.Column;
import org.drools.template.parser.DefaultTemplateContainer;
import org.drools.template.parser.TemplateContainer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DataProviderCompilerIntegrationTest {

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
    
    private class TestDataProvider implements DataProvider {
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

    private ArrayList<String[]> rows = new ArrayList<String[]>();
    
    @Before
    public void setUp(){
         rows.add( new String[]{ "1",
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
                 "dummy"} );
         rows.add( new String[]{ "15",
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
                 "dummy" } );
         rows.add( new String[]{ "12",
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
                 "dummy" } );
         rows.add( new String[]{ "62",
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
                 "dummy" } );
    }
    
    @Test
    public void testCompiler() throws Exception {
        TestDataProvider tdp = new TestDataProvider( rows );
        final DataProviderCompiler converter = new DataProviderCompiler();
        final String drl = converter.compile( tdp,
                                              "/templates/rule_template_1.drl" );
        System.out.println( drl );
        assertEquals( EXPECTED_RULES.toString(), drl );
    }

    @Test
    public void testCompilerMaps() throws Exception {
        Collection<Map<String,Object>> maps = new ArrayList<Map<String,Object>>();
        final ObjectDataCompiler converter = new ObjectDataCompiler();
        InputStream templateStream =
            this.getClass().getResourceAsStream( "/templates/rule_template_1.drl" );
        TemplateContainer tc = new DefaultTemplateContainer( templateStream );
        Column[] columns = tc.getColumns();
        
        for( String[] row: rows ){
            Map<String,Object> map = new HashMap<String,Object>();
            for( int icol = 0; icol < columns.length; icol++ ){
            	Object value = row[icol];
            	if( value != null ){
            		map.put( columns[icol].getName(), value );
            	}
            }
            maps.add( map );
        }
        templateStream =
            this.getClass().getResourceAsStream( "/templates/rule_template_1.drl" );
        final String drl = converter.compile( maps, templateStream );
        System.out.println( drl );
        assertEquals( EXPECTED_RULES.toString(), drl );
    }

    public static class OBJ {
        private final String FEE_SCHEDULE_ID;
        private final String FEE_SCHEDULE_TYPE;
        private final String FEE_MODE_TYPE;
        private final String ENTITY_BRANCH;
        private final String PRODUCT_TYPE;
        private final String ACTIVITY_TYPE;
        public final String FEE_TYPE;
        public final String OWNING_PARTY;
        public final String CCY;
        public final String LC_AMOUNT;
        public final String AMOUNT;
        OBJ( String[] vals ){
        	FEE_SCHEDULE_ID   = vals[0];
        	FEE_SCHEDULE_TYPE = vals[1];
        	FEE_MODE_TYPE     = vals[2];
        	ENTITY_BRANCH     = vals[3];
        	PRODUCT_TYPE      = vals[4];
        	ACTIVITY_TYPE     = vals[5];
        	FEE_TYPE          = vals[6];
        	OWNING_PARTY      = vals[7];
        	CCY               = vals[8];
        	LC_AMOUNT         = vals[9];
        	AMOUNT            = vals[10];
        }
        public String getFEE_SCHEDULE_ID() {
        	return FEE_SCHEDULE_ID;
        }
        public String getFEE_SCHEDULE_TYPE() {
        	return FEE_SCHEDULE_TYPE;
        }
        public String getFEE_MODE_TYPE() {
        	return FEE_MODE_TYPE;
        }
        public String ENTITY_BRANCH() {
        	return ENTITY_BRANCH;
        }
        public String PRODUCT_TYPE() {
        	return PRODUCT_TYPE;
        }
        public String ACTIVITY_TYPE() {
        	return ACTIVITY_TYPE;
        }
    }

    @Test
    public void testCompilerObjs() throws Exception {
        Collection<Object> objs = new ArrayList<Object>();
        final ObjectDataCompiler converter = new ObjectDataCompiler();
        final InputStream templateStream =
            this.getClass().getResourceAsStream( "/templates/rule_template_1.drl" );
        
        for( String[] row: rows ){
            OBJ obj = new OBJ( row );
            objs.add( obj );
        }
        final String drl = converter.compile( objs, templateStream );
        System.out.println( drl );
        assertEquals( EXPECTED_RULES.toString(), drl );
    }


}
