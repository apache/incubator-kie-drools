package org.optaplanner.quarkus.it.reflection;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.quarkus.it.reflection.domain.TestdataReflectionEntity;
import org.optaplanner.quarkus.it.reflection.domain.TestdataReflectionSolution;

@Path("/optaplanner/test")
public class OptaPlannerTestResource {

    @Inject
    SolverManager<TestdataReflectionSolution, Long> solverManager;

    @POST
    @Path("/solver-factory")
    @Produces(MediaType.TEXT_PLAIN)
    public String solveWithSolverFactory() {
        TestdataReflectionSolution planningProblem = new TestdataReflectionSolution();
        planningProblem.setEntityList(Arrays.asList(
                new TestdataReflectionEntity(),
                new TestdataReflectionEntity()));
        planningProblem.setFieldValueList(Arrays.asList("a", "bb", "ccc"));
        planningProblem.setMethodValueList(Arrays.asList("a", "bb", "ccc", "ddd"));
        SolverJob<TestdataReflectionSolution, Long> solverJob = solverManager.solve(1L, planningProblem);
        try {
            return solverJob.getFinalBestSolution().getScore().toString();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Solving was interrupted.", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Solving failed.", e);
        }
    }
}
