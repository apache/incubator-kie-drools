package org.drools.mvel.rule;

import org.drools.base.rule.TypeDeclaration;
import org.junit.Test;
import org.drools.core.integrationtests.SerializationHelper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by IntelliJ IDEA. User: Ming Jin Date: Mar 19, 2008 Time: 11:11:45 AM To change this template use File |
 * Settings | File Templates.
 */
public class EnumSerialiationTest {
    private static final String TEST_NAME   = "test name";

    @Test
    public void testTypeDeclaration() throws Exception {
        TypeDeclaration typeDec1 = new TypeDeclaration(TEST_NAME);

        TypeDeclaration typeDec2    = SerializationHelper.serializeObject(typeDec1);

        assertThat(typeDec2).isEqualTo(typeDec1);
    }
}
