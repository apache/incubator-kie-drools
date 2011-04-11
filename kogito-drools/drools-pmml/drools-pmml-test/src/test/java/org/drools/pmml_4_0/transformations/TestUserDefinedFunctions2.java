package org.drools.pmml_4_0.transformations;


import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 11/12/10
 * Time: 10:11 PM
 *
 * PMML Test : Focus on the DataDictionary section
 */
public class TestUserDefinedFunctions2 extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = true;
    private static final String source = "test_user_functions2.xml";
    private static final String packageName = "org.drools.pmml_4_0.test";



    @Before
    public void init() throws Exception {

        setKSession(getModelSession(source, VERBOSE));
        setKbase(getKSession().getKnowledgeBase());
    }



    @Test
    @Ignore
    public void functions() throws Exception {
        getKSession().getWorkingMemoryEntryPoint("in_Age").insert(10);

        getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName, "UserAge"), true, false, null, 6270.0  );

    }





}
