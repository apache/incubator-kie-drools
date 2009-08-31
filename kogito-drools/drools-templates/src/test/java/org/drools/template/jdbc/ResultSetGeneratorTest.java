package org.drools.template.jdbc;

import junit.framework.TestCase;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.RuleBaseFactory;
import org.drools.compiler.PackageBuilder;

import java.sql.*;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;


/**
 * /**
 * <p>A simple example of using the ResultSetGenerator.
 * The template used is "Cheese.drt" the same used by SimpleRuleTemplateExample.
 * Rather than use the spreadsheet ExampleCheese.xls, this example reads the data
 * from an HSQL database (which is created in this example.)</p>
 * @author Michael Neale
 * @author Bill Tarr       
 */
public class ResultSetGeneratorTest extends TestCase {
  


    public void testResultSet() throws Exception {

        // setup the HSQL database with our rules.
        Class.forName("org.hsqldb.jdbcDriver");
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:RULES", "sa", "");

        try {
            update("CREATE TABLE cheese_rules ( id INTEGER IDENTITY, persons_age INTEGER, birth_date DATE, cheese_type VARCHAR(256), log VARCHAR(256) )", conn);

            update("INSERT INTO cheese_rules(persons_age,birth_date,cheese_type,log) VALUES(42, '1950-01-01', 'stilton', 'Old man stilton')", conn);
            update("INSERT INTO cheese_rules(persons_age,birth_date,cheese_type,log) VALUES(10, '2009-01-01', 'cheddar', 'Young man cheddar')", conn);
        } catch (SQLException e) {
            // catch exception for table already existing
        }

        // query the DB for the rule rows, convert them using the template.

        Statement sta = conn.createStatement();
        ResultSet rs = sta.executeQuery("SELECT persons_age, cheese_type, log " +
                " FROM cheese_rules");

        final ResultSetGenerator converter = new ResultSetGenerator();
        final String drl1 = converter.compile(rs, getRulesStream());

        System.out.println(drl1);

        sta.close();

        String drl = drl1;

        // test that our rules can execute.
        final RuleBase rb = buildRuleBase(drl);

        WorkingMemory wm = rb.newStatefulSession();

        //now create some test data
        wm.insert(new Cheese("stilton",
                42));
        wm.insert(new Person("michael",
                "stilton",
                42));
        final List<String> list = new ArrayList<String>();
        wm.setGlobal("list",
                list);

        wm.fireAllRules();

        assertEquals(1, list.size());

    }

    /**
     * Build the rule base from the generated DRL.
     * Same method from SimpleRuleTemplateExample.
     *
     * @param drls variable length of Drools rules as strings
     * @return RuleBase built from the rules
     * @throws Exception Add Exception handling to a real implementation.
     */
    private RuleBase buildRuleBase(String... drls) throws Exception {
        //now we build the rule package and rulebase, as if they are normal rules
        PackageBuilder builder = new PackageBuilder();
        for (String drl : drls) {
            builder.addPackageFromDrl(new StringReader(drl));
        }

        //add the package to a rulebase (deploy the rule package).
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(builder.getPackage());
        return ruleBase;
    }

    /**
     * simple getter method looks up our template as a Resource
     *
     * @return the template as an InputStream
     */
    private InputStream getRulesStream() {
        return this.getClass().getResourceAsStream("/templates/Cheese.drt");
    }

    /**
     * An HSQL update wrapper from http://hsqldb.org/doc/guide/apb.html
     *
     * @param expression SQL expression
     * @throws SQLException just rethrowing all the errors for the example
     */
    private void update(String expression, Connection conn) throws SQLException {
        Statement st;
        st = conn.createStatement();
        int i = st.executeUpdate(expression);
        if (i == -1) {
            System.out.println("db error : " + expression);
        }

        st.close();
    }

}
