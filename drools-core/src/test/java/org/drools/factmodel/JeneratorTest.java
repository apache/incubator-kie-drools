package org.drools.factmodel;

import java.io.ByteArrayInputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import junit.framework.TestCase;


/**
 * This mostly shows how to go to a jar and back, if needed.
 * Probably can be partly ignored eventually.
 *
 * @author Michael Neale
 */
public class JeneratorTest extends TestCase {


	public void testRoundTrip() throws Exception {
		Fact f = new Fact();
		f.name = "Foobar";
		Field f1 = new Field();
		f1.name = "name";
		f1.type = "java.lang.String";
		f.fields.add(f1);

		Field f2 = new Field();
		f2.name = "age";
		f2.type = "java.lang.Integer";
		f.fields.add(f2);

		Fact f_  = new Fact();
		f_.name = "Baz";

		Field f1_ = new Field();
		f1_.name = "name";
		f1_.type = "java.lang.String";
		f_.fields.add(f1_);


		Jenerator jen = new Jenerator();
		byte[] data = jen.createJar(new Fact[] {f, f_}, "whee.waa");
		JarInputStream jis = new JarInputStream(new ByteArrayInputStream(data));
		JarEntry je = jis.getNextJarEntry();

		assertNotNull(je);
		System.err.println(je.getName());
		assertEquals("factmodel.xml", je.getName());


		je = jis.getNextJarEntry();

		assertNotNull(je);
		System.err.println(je.getName());
		assertEquals("whee/waa/Foobar.class", je.getName());


		je = jis.getNextJarEntry();

		assertNotNull(je);
		System.err.println(je.getName());
		assertEquals("whee/waa/Baz.class", je.getName());



		Fact[] facts = jen.loadMetaModel(new JarInputStream(new ByteArrayInputStream(data)));
		assertEquals(2, facts.length);
		assertEquals("Foobar", facts[0].name);
		assertEquals("Baz", facts[1].name);


	}

}
