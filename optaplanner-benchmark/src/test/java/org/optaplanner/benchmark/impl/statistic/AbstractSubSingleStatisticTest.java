package org.optaplanner.benchmark.impl.statistic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

@Execution(ExecutionMode.CONCURRENT)
public abstract class AbstractSubSingleStatisticTest<Point_ extends StatisticPoint, SubSingleStatistic_ extends SubSingleStatistic<TestdataSolution, Point_>> {

    @Test
    void hibernation(@TempDir Path tempDir) {
        SubSingleBenchmarkResult subSingleBenchmarkResult = createSubStatistic(tempDir.toFile());
        Function<SubSingleBenchmarkResult, SubSingleStatistic_> constructor = getSubSingleStatisticConstructor();
        SubSingleStatistic_ subSingleStatistic = constructor.apply(subSingleBenchmarkResult);

        // Persist the point list.
        subSingleStatistic.setPointList(getInputPoints());
        subSingleStatistic.hibernatePointList();

        // Re-read the point list.
        SubSingleStatistic_ subSingleStatisticUnhibernated = constructor.apply(subSingleBenchmarkResult);
        subSingleStatisticUnhibernated.unhibernatePointList();

        assertSoftly(softly -> runTest(softly, subSingleStatisticUnhibernated.getPointList()));
    }

    @Test
    void serialization(@TempDir Path tempDir) throws IOException, JAXBException {
        SubSingleBenchmarkResult subSingleBenchmarkResult = createSubStatistic(tempDir.toFile());
        Function<SubSingleBenchmarkResult, SubSingleStatistic_> constructor = getSubSingleStatisticConstructor();
        SubSingleStatistic_ subSingleStatistic = constructor.apply(subSingleBenchmarkResult);
        subSingleStatistic.setPointList(getInputPoints());

        // Work around the fact that the statistic has no @XmlRootElement annotation
        Class<SubSingleStatistic_> clz = (Class<SubSingleStatistic_>) subSingleStatistic.getClass();
        QName qName = new QName(clz.getPackageName(), clz.getSimpleName());
        JAXBElement<SubSingleStatistic_> root = new JAXBElement<>(qName, clz, subSingleStatistic);
        JAXBContext context = JAXBContext.newInstance(subSingleStatistic.getClass());

        // Serialize the statistic to XML.
        Path tempFile = Files.createTempFile("optaplanner-", ".xml");
        try (Writer writer = new FileWriter(tempFile.toFile())) {
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(root, writer);
        } catch (Exception e) {
            Assertions.fail("Failed serializing statistic.", e);
        }

        // Deserialize from XML.
        Unmarshaller unmarshaller = context.createUnmarshaller();
        try {
            root = unmarshaller.unmarshal(new StreamSource(tempFile.toFile()), clz);
        } catch (Exception e) {
            Assertions.fail("Failed deserializing statistic.", e);
        }

        SubSingleStatistic_ deserialized = root.getValue();
        assertThat(deserialized.getStatisticType())
                .isEqualTo(subSingleStatistic.getStatisticType());
    }

    protected abstract Function<SubSingleBenchmarkResult, SubSingleStatistic_> getSubSingleStatisticConstructor();

    protected abstract List<Point_> getInputPoints();

    protected abstract void runTest(SoftAssertions assertions, List<Point_> outputPoints);

    private SubSingleBenchmarkResult createSubStatistic(File tempDirectory) {
        PlannerBenchmarkResult plannerBenchmarkResult = new PlannerBenchmarkResult();
        plannerBenchmarkResult.setBenchmarkReportDirectory(tempDirectory);
        ProblemBenchmarkResult<TestdataSolution> problemBenchmarkResult = new ProblemBenchmarkResult<>(plannerBenchmarkResult);
        problemBenchmarkResult.setName(UUID.randomUUID().toString());
        SolverBenchmarkResult solverBenchmarkResult = new SolverBenchmarkResult(plannerBenchmarkResult);
        solverBenchmarkResult.setScoreDefinition(TestdataSolution.buildSolutionDescriptor()
                .getScoreDefinition());
        solverBenchmarkResult.setName(UUID.randomUUID().toString());
        SingleBenchmarkResult singleBenchmarkResult = new SingleBenchmarkResult(solverBenchmarkResult, problemBenchmarkResult);
        SubSingleBenchmarkResult subSingleBenchmarkResult = new SubSingleBenchmarkResult(singleBenchmarkResult, 0);
        singleBenchmarkResult.setSubSingleBenchmarkResultList(Collections.singletonList(subSingleBenchmarkResult));
        problemBenchmarkResult.setSingleBenchmarkResultList(Collections.singletonList(singleBenchmarkResult));
        problemBenchmarkResult.makeDirs();
        return subSingleBenchmarkResult;
    }

}
