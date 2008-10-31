package org.drools.rule;

import junit.framework.TestCase;

import org.drools.integrationtests.SerializationHelper;

/**
 * Created by IntelliJ IDEA. User: Ming Jin Date: Mar 19, 2008 Time: 11:11:45 AM To change this template use File |
 * Settings | File Templates.
 */
public class EnumSerialiationTest extends TestCase {
    private static final String TEST_NAME   = "test name";

    public void testTypeDeclaration() throws Exception {
        TypeDeclaration typeDec1 = new TypeDeclaration(TEST_NAME);

        TypeDeclaration typeDec2    = SerializationHelper.serializeObject(typeDec1);

        assertEquals(typeDec1, typeDec2);
    }
}
