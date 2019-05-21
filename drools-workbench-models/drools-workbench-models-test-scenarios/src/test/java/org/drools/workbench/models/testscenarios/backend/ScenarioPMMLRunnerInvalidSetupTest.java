package org.drools.workbench.models.testscenarios.backend;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.drools.workbench.models.testscenarios.backend.util.ScenarioXMLPersistence;
import org.junit.Test;

public class ScenarioPMMLRunnerInvalidSetupTest
        extends RuleUnit {

    @Test(expected = IllegalArgumentException.class)
    public void testNoKieSession() throws Exception {

        ScenarioPMMLRunner runner4JUnit = new ScenarioPMMLRunner(getKieSession("MyCard.scgd").getKieBase());

        runner4JUnit.run(ScenarioXMLPersistence.getInstance().unmarshal(readStream("MyTest.scenario")));
    }

    public String readStream(final String name) {
        try {

            final InputStream is = ScenarioPMMLRunnerInvalidSetupTest.class.getResourceAsStream(name);
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            final byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

            return buffer.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}