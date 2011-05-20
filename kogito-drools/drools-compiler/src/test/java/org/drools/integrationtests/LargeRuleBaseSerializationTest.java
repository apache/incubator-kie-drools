package org.drools.integrationtests;

import java.io.Reader;
import java.io.StringReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;

/**
 * Created by IntelliJ IDEA. User: SG0521861 Date: Mar 18, 2008 Time: 1:22:50 PM To change this template use File |
 * Settings | File Templates.
 */
public class LargeRuleBaseSerializationTest {
    private static final int    RULE_COUNT = Integer.parseInt(System.getProperty("test.count", "525"));
    private static final int    ITERATIONS = Integer.parseInt(System.getProperty("test.iterations", "5"));

    private static RuleBase ruleBase;
    private static byte[]   bytes;
    private static byte[]   compressedBytes;

    @Before
    public void setUp() throws Exception {
        if (ruleBase == null)
            ruleBase    = createRuleBase();
        if (bytes == null) {
            bytes   = DroolsStreamUtils.streamOut(ruleBase);
        }
        if (compressedBytes == null) {
            compressedBytes = DroolsStreamUtils.streamOut(ruleBase, true);
        }
    }

    @Test @Ignore
    public void testUnmarshallingPerformance() throws Exception {
        DroolsStreamUtils.streamIn(bytes);
        long    time    = System.currentTimeMillis();

        for (int i = ITERATIONS; i-- > 0; ) {
            DroolsStreamUtils.streamIn(bytes);
        }
        System.out.println("Total time of unmarshalling "+ITERATIONS+" times is "+
                           format(System.currentTimeMillis()-time));
    }

    @Test @Ignore
    public void testMarshallingPerformance() throws Exception {
        long    time    = System.currentTimeMillis();
        for (int i = ITERATIONS; i-- > 0; ) {
            DroolsStreamUtils.streamOut(ruleBase);
        }
        System.out.println("Total time of marshalling "+ITERATIONS+" times is "+
                           format(System.currentTimeMillis()-time)+" with size of "+bytes.length+" bytes");
    }

    @Test @Ignore
    public void testUnmarshallWithCompressionPerformance() throws Exception {
        long    time    = System.currentTimeMillis();

        for (int i = ITERATIONS; i-- > 0; ) {
            DroolsStreamUtils.streamIn(compressedBytes, true);
        }
        System.out.println("Total time of unmarshalling with compression "+ITERATIONS+" times is "+
                           format(System.currentTimeMillis()-time));
    }

    @Test @Ignore
    public void testMarshallWithCompressionPerformance() throws Exception {
        long    time    = System.currentTimeMillis();
        for (int i = ITERATIONS; i-- > 0; ) {
            DroolsStreamUtils.streamOut(ruleBase, true);
        }
        System.out.println("Total time of marshalling with compression "+ITERATIONS+" times is "+
                           format(System.currentTimeMillis()-time)+" with size of "+compressedBytes.length+" bytes");
    }

    private static final int    MILLIS_IN_SECOND    = 1000;
    private static final int    MILLIS_IN_MINUTE    = 60*MILLIS_IN_SECOND;
    private static final int    MILLIS_IN_HOUR      = 60*MILLIS_IN_MINUTE;

    private static String format(long time) {
        StringBuilder   sb  = new StringBuilder();

        if (time/MILLIS_IN_HOUR > 0) {
            sb.append(time/MILLIS_IN_HOUR).append(':');
            time    -= time/MILLIS_IN_HOUR*MILLIS_IN_HOUR;
        }
        if (time/MILLIS_IN_MINUTE > 0) {
            sb.append(time/MILLIS_IN_MINUTE).append(':');
            time    -= time/MILLIS_IN_MINUTE*MILLIS_IN_MINUTE;
        }
        sb.append(time*1.0/MILLIS_IN_SECOND);

        return sb.toString();
    }

    private static RuleBase createRuleBase() throws DroolsParserException {
        System.out.println("Generating "+RULE_COUNT+" rules");
        StringBuilder   sb  = new StringBuilder(LargeRuleBase.getHeader());

        for (int i = 0; i < RULE_COUNT; i++) {
            sb.append(LargeRuleBase.getTemplate1("testRule"+i, i));
        }
        System.out.println("Parsing "+RULE_COUNT+" rules");
        PackageBuilder pkgBuilder = new PackageBuilder();
        DrlParser ps = new DrlParser();
        PackageDescr pkgDescr = ps.parse(new StringReader(sb.toString()));

        pkgBuilder.addPackage(pkgDescr);

        Package pkg = pkgBuilder.getPackage();
        ruleBase = RuleBaseFactory.newRuleBase();

        ruleBase.addPackage(pkg);
        return ruleBase;
    }
    
    @Test
    public void test1() throws Exception {
        StringBuilder buf = new StringBuilder(80000);

        buf.append("package mypackage\n");
        buf.append("import " + LargeRuleBaseSerializationTest.class.getCanonicalName() + ".Parent\n");
        buf.append("import " + LargeRuleBaseSerializationTest.class.getCanonicalName() + ".Child\n");
        buf.append("import " + LargeRuleBaseSerializationTest.class.getCanonicalName() + ".Item\n");

        for (int i = 0; i < 3000; i++)
        {
            buf.append("rule 'Rule " + i + "'\n");
            buf.append(" when\n");
            buf.append(" $g:Parent()\n");
            buf.append(" $c:Child(parent==$g,code=='" + i + "')\n");
            buf.append(" Item(parentObject==$c,name=='xxx1', value == '1')\n");
            buf.append(" Item(parentObject==$c,name=='xxx2',value == '1')\n");
            buf.append(" Item(parentObject==$g,name=='xxx3',value == '200')\n");
            buf.append(" then\n");
            buf.append(" System.out.println('2');\n");
            buf.append("end\n");
        }        
        Reader source = new StringReader(buf.toString());

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl(source);
        Package pkg = builder.getPackage();

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(pkg);

        SerializationHelper.serializeObject( ruleBase );
    }
    
    public static class Parent
    {}

    public static class Child
    {
        Parent parent;
        String code;

        public Parent getParent()
        {
            return parent;
        }

        public void setParent(Parent parent)
        {
            this.parent = parent;
        }

        public String getCode()
        {
            return code;
        }

        public void setCode(String code)
        {
            this.code = code;
        }

    }

    public static class Item
    {
        Object parentObject;
        String name;
        String value;

        public Object getParentObject()
        {
            return parentObject;
        }

        public void setParentObject(Object parentObject)
        {
            this.parentObject = parentObject;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getValue()
        {
            return value;
        }

        public void setValue(String value)
        {
            this.value = value;
        }
    }    

}
