package org.optaplanner.examples.cloudbalancing.optional.solver.move;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;

public class CloudComputerChangeMoveFactory implements MoveListFactory<CloudBalance> {

    @Override
    public List<CloudComputerChangeMove> createMoveList(CloudBalance cloudBalance) {
        List<CloudComputerChangeMove> moveList = new ArrayList<>();
        List<CloudComputer> cloudComputerList = cloudBalance.getComputerList();
        for (CloudProcess cloudProcess : cloudBalance.getProcessList()) {
            for (CloudComputer cloudComputer : cloudComputerList) {
                moveList.add(new CloudComputerChangeMove(cloudProcess, cloudComputer));
            }
        }
        return moveList;
    }

}
