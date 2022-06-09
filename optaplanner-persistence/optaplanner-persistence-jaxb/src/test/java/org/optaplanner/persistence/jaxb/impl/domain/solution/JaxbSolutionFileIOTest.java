package org.optaplanner.persistence.jaxb.impl.domain.solution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCode;

import java.io.File;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.persistence.jaxb.impl.testdata.domain.JaxbTestdataEntity;
import org.optaplanner.persistence.jaxb.impl.testdata.domain.JaxbTestdataSolution;
import org.optaplanner.persistence.jaxb.impl.testdata.domain.JaxbTestdataValue;

class JaxbSolutionFileIOTest {

    private static File solutionTestDir;

    @BeforeAll
    public static void setup() {
        solutionTestDir = new File("target/solutionTest/");
        solutionTestDir.mkdirs();
    }

    @Test
    void readAndWrite() {
        JaxbSolutionFileIO<JaxbTestdataSolution> solutionFileIO = new JaxbSolutionFileIO<>(JaxbTestdataSolution.class);
        File file = new File(solutionTestDir, "testdataSolution.xml");

        JaxbTestdataSolution original = new JaxbTestdataSolution("s1");
        JaxbTestdataValue originalV1 = new JaxbTestdataValue("v1");
        original.setValueList(Arrays.asList(originalV1, new JaxbTestdataValue("v2")));
        original.setEntityList(Arrays.asList(
                new JaxbTestdataEntity("e1"), new JaxbTestdataEntity("e2", originalV1), new JaxbTestdataEntity("e3")));
        original.setScore(SimpleScore.of(-123));
        solutionFileIO.write(original, file);
        JaxbTestdataSolution copy = solutionFileIO.read(file);

        assertThat(copy).isNotSameAs(original);
        assertCode("s1", copy);
        assertAllCodesOfIterator(copy.getValueList().iterator(), "v1", "v2");
        assertAllCodesOfIterator(copy.getEntityList().iterator(), "e1", "e2", "e3");
        JaxbTestdataValue copyV1 = copy.getValueList().get(0);
        JaxbTestdataEntity copyE2 = copy.getEntityList().get(1);
        assertCode("v1", copyE2.getValue());
        assertThat(copyE2.getValue()).isSameAs(copyV1);
        assertThat(copy.getScore()).isEqualTo(SimpleScore.of(-123));
    }

}
