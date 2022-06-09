package org.optaplanner.core.impl.constructionheuristic.placer.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCode;

import java.util.Iterator;

import org.optaplanner.core.impl.constructionheuristic.placer.Placement;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;

public abstract class AbstractEntityPlacerTest {

    public static <Solution_> void assertEntityPlacement(Placement<Solution_> placement, String entityCode,
            String... valueCodes) {
        Iterator<Move<Solution_>> iterator = placement.iterator();
        assertThat(iterator).isNotNull();
        for (String valueCode : valueCodes) {
            assertThat(iterator.hasNext()).isTrue();
            ChangeMove<Solution_> move = (ChangeMove<Solution_>) iterator.next();
            assertCode(entityCode, move.getEntity());
            assertCode(valueCode, move.getToPlanningValue());
        }
        assertThat(iterator.hasNext()).isFalse();
    }

    public static <Solution_> void assertValuePlacement(Placement<Solution_> placement, String valueCode,
            String... entityCodes) {
        Iterator<Move<Solution_>> iterator = placement.iterator();
        assertThat(iterator).isNotNull();
        for (String entityCode : entityCodes) {
            assertThat(iterator.hasNext()).isTrue();
            ChangeMove<Solution_> move = (ChangeMove<Solution_>) iterator.next();
            assertCode(entityCode, move.getEntity());
            assertCode(valueCode, move.getToPlanningValue());
        }
        assertThat(iterator.hasNext()).isFalse();
    }

}
