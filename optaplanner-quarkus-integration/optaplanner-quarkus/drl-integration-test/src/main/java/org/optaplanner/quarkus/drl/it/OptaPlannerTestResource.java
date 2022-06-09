package org.optaplanner.quarkus.drl.it;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.quarkus.drl.it.domain.TestdataQuarkusEntity;
import org.optaplanner.quarkus.drl.it.domain.TestdataQuarkusSolution;

@Path("/optaplanner/test")
public class OptaPlannerTestResource {

    @Inject
    SolverManager<TestdataQuarkusSolution, Long> solverManager;

    @POST
    @Path("/solver-factory")
    @Produces(MediaType.TEXT_PLAIN)
    public String solveWithSolverFactory() throws InterruptedException {
        TestdataQuarkusSolution planningProblem = new TestdataQuarkusSolution();
        planningProblem.setEntityList(Arrays.asList(
                new TestdataQuarkusEntity(),
                new TestdataQuarkusEntity()));
        planningProblem.setLeftValueList(Arrays.asList("a", "b", "c"));
        planningProblem.setRightValueList(Arrays.asList("1", "2", "3"));
        SolverJob<TestdataQuarkusSolution, Long> solverJob = solverManager.solve(1L, planningProblem);
        try {
            TestdataQuarkusSolution sol = solverJob.getFinalBestSolution();
            StringBuilder out = new StringBuilder();
            out.append("score=").append(sol.getScore()).append('\n');
            for (int i = 0; i < sol.getEntityList().size(); i++) {
                out.append("entity." + i + ".fullValue=").append(sol.getEntityList().get(i).getFullValue()).append('\n');
            }
            return out.toString();
        } catch (ExecutionException e) {
            throw new IllegalStateException("Solving failed.", e);
        }
    }

}
