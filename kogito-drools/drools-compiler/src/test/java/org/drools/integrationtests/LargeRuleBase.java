package org.drools.integrationtests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;

import junit.framework.Assert;

/**
 * This generates a large number of rules (complex ones) and then times compiling, serializing etc.
 */
public class LargeRuleBase {

	public static void main(String[] args) throws Exception {
		bigBlobCompile();
		//smallBlobCompile();
	}

	private static void bigBlobCompile() throws DroolsParserException,
			IOException, Exception {
		StringBuffer buf = new StringBuffer();
		buf.append(getHeader());

		for (int i = 0; i < 5000; i++) {
			String name = "x" + i;
			int status = i;

			String r = getTemplate1(name, status);
			buf.append(r);
		}

		/* love you */long time = System.currentTimeMillis();

		DrlParser ps = new DrlParser();
		PackageDescr pkg = ps.parse(new StringReader(buf.toString()));

		System.err.println("Time taken for parsing: "
				+ (System.currentTimeMillis() - time));

		time = System.currentTimeMillis();
		PackageBuilder b = new PackageBuilder();
		b.addPackage(pkg);
		Assert.assertFalse(b.getErrors().toString(), b.hasErrors());

		System.err.println("Time taken for compiling: "
				+ (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();

		Package p = b.getPackage();
		RuleBase rb = RuleBaseFactory.newRuleBase();

		rb.addPackage(p);



		System.err.println("Time taken rete building: "
				+ (System.currentTimeMillis() - time));

		File f = new File("foo.rulebase");
		if (f.exists()) f.delete();


		time = System.currentTimeMillis();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
		out.writeObject(rb);
		out.flush(); out.close();
		System.err.println("Time taken serializing rulebase: "
				+ (System.currentTimeMillis() - time));

		time = System.currentTimeMillis();
		ObjectInputStream in = new ObjectInputStream(new FileInputStream( f ) );
		RuleBase rb_ = (RuleBase) in.readObject();
		System.err.println("Time taken de-serializing rulebase: "
				+ (System.currentTimeMillis() - time));



	}

	private static void smallBlobCompile() throws DroolsParserException,
			IOException, Exception {


		/* love you */long time = System.currentTimeMillis();
		PackageBuilder b = new PackageBuilder();
		b.addPackageFromDrl(new StringReader(getHeader()));
		for (int i = 0; i < 5000; i++) {
			String name = "x" + i;
			int status = i;

			String r = getTemplate1(name, status);
			b.addPackageFromDrl(new StringReader(r));
		}



		Assert.assertFalse(b.getErrors().toString(), b.hasErrors());

		System.err.println("Time taken for compiling: "
				+ (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();

		Package p = b.getPackage();
		RuleBase rb = RuleBaseFactory.newRuleBase();

		rb.addPackage(p);

		System.err.println("Time taken rete building: "
				+ (System.currentTimeMillis() - time));
	}

	private static String getHeader() {
		return "package org.drools.test; \n " + "import org.drools.Person; \n "
				+ "import org.drools.Cheese; \n "
				+ "import org.drools.Cheesery; \n "
				+ " import java.util.List \n "
				+ " global List list \n dialect 'mvel'\n  ";
	}

	private static String getTemplate1(String name, int status) {
		return "rule 'match Person "
				+ name
				+ "' \n"
				+ " agenda-group \'xxx\' \n"
				+ " salience ($age2 - $age1) \n "
				+ " dialect 'mvel' \n"
				+ "	when \n "
				+ " 		$person : Person(name=='"
				+ name
				+ "', $age1 : age ) \n "
				+ "	    cheesery : Cheesery( cheeses contains $person, status == "
				+ status + " ) \n "
				+ " 		cheeses : List() from cheesery.getCheeses() \n "
				+ "		Person( age < ( $age1 ) ) \n "
				+ "		Person( $age2 : age -> ( $age1 == $age2 ) ) \n "
				+ "		eval( $age1 == $age2 ) \n " + "   then \n "
				+ "		list.add( $person ); \n "
				+ "		$person.setStatus(\"match Person ok\"); \n " + " end \n";
	}

}
