package org.optaplanner.core.impl.heuristic.selector.move.generic;

import org.junit.Test;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.entityproviding.TestdataEntityProvidingEntity;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ChangeMoveTest {

    @Test
    public void isMoveDoable() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");

        TestdataEntityProvidingEntity a = new TestdataEntityProvidingEntity("a", Arrays.asList(v1, v2, v3), null);

        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        EntityDescriptor entityDescriptor = TestdataEntityProvidingEntity.buildEntityDescriptor();

        ChangeMove aMove = new ChangeMove(a, entityDescriptor.getGenuineVariableDescriptor("value"), v2);
        a.setValue(v1);
        assertEquals(true, aMove.isMoveDoable(scoreDirector));

        a.setValue(v2);
        assertEquals(false, aMove.isMoveDoable(scoreDirector));

        a.setValue(v3);
        assertEquals(true, aMove.isMoveDoable(scoreDirector));
    }

    @Test
    public void doMove() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");

        TestdataEntityProvidingEntity a = new TestdataEntityProvidingEntity("a", Arrays.asList(v1, v2, v3), null);

        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        EntityDescriptor entityDescriptor = TestdataEntityProvidingEntity.buildEntityDescriptor();

        ChangeMove aMove = new ChangeMove(a, entityDescriptor.getGenuineVariableDescriptor("value"), v2);
        a.setValue(v1);
        aMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());

        a.setValue(v2);
        aMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());

        a.setValue(v3);
        aMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());
    }

}
