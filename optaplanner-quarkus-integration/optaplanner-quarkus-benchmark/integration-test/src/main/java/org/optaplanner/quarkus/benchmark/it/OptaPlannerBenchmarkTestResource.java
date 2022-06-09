package org.optaplanner.quarkus.benchmark.it;

import java.util.Arrays;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.quarkus.benchmark.it.domain.TestdataStringLengthShadowEntity;
import org.optaplanner.quarkus.benchmark.it.domain.TestdataStringLengthShadowSolution;

@Path("/optaplanner/test")
public class OptaPlannerBenchmarkTestResource {

    @Inject
    PlannerBenchmarkFactory benchmarkFactory;

    @POST
    @Path("/benchmark")
    @Produces(MediaType.TEXT_PLAIN)
    public String benchmark() {
        TestdataStringLengthShadowSolution planningProblem = new TestdataStringLengthShadowSolution();
        planningProblem.setEntityList(Arrays.asList(
                new TestdataStringLengthShadowEntity(),
                new TestdataStringLengthShadowEntity()));
        planningProblem.setValueList(Arrays.asList("a", "bb", "ccc"));
        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark(planningProblem);
        return benchmark.benchmark().toPath().toAbsolutePath().toString();
    }
}
