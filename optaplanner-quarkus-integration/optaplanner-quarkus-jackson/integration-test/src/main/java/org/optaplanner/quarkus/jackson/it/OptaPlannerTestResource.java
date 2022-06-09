package org.optaplanner.quarkus.jackson.it;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.quarkus.jackson.it.domain.ITestdataPlanningSolution;

@Path("/optaplanner/test")
public class OptaPlannerTestResource {

    @Inject
    SolverManager<ITestdataPlanningSolution, Long> solverManager;

    @POST
    @Path("/solver-factory")
    public ITestdataPlanningSolution solveWithSolverFactory(ITestdataPlanningSolution problem) {
        SolverJob<ITestdataPlanningSolution, Long> solverJob = solverManager.solve(1L, problem);
        try {
            return solverJob.getFinalBestSolution();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Solving failed.", e);
        }
    }

}
