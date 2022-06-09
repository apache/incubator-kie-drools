package org.optaplanner.quarkus.it.devui;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.quarkus.it.devui.domain.TestdataStringLengthShadowEntity;
import org.optaplanner.quarkus.it.devui.domain.TestdataStringLengthShadowSolution;

@Path("/optaplanner/test")
public class OptaPlannerTestResource {

    @Inject
    SolverManager<TestdataStringLengthShadowSolution, Long> solverManager;

    @POST
    @Path("/solver-factory")
    @Produces(MediaType.TEXT_PLAIN)
    public String solveWithSolverFactory() {
        TestdataStringLengthShadowSolution planningProblem = new TestdataStringLengthShadowSolution();
        planningProblem.setEntityList(Arrays.asList(
                new TestdataStringLengthShadowEntity(),
                new TestdataStringLengthShadowEntity()));
        planningProblem.setValueList(Arrays.asList("a", "bb", "ccc"));
        SolverJob<TestdataStringLengthShadowSolution, Long> solverJob = solverManager.solve(1L, planningProblem);
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
