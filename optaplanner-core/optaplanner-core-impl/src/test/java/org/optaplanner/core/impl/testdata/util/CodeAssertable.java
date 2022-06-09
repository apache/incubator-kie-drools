package org.optaplanner.core.impl.testdata.util;

import java.util.List;
import java.util.Objects;

import org.optaplanner.core.impl.heuristic.move.CompositeMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.list.ListAssignMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.list.ListChangeMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.list.ListSwapMove;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChain;

public interface CodeAssertable {

    String getCode();

    static CodeAssertable convert(Object o) {
        Objects.requireNonNull(o);
        if (o instanceof CodeAssertable) {
            return (CodeAssertable) o;
        } else if (o instanceof ChangeMove) {
            ChangeMove<?> changeMove = (ChangeMove) o;
            final String code = convert(changeMove.getEntity()).getCode()
                    + "->" + convert(changeMove.getToPlanningValue()).getCode();
            return () -> code;
        } else if (o instanceof SwapMove) {
            SwapMove<?> swapMove = (SwapMove) o;
            final String code = convert(swapMove.getLeftEntity()).getCode()
                    + "<->" + convert(swapMove.getRightEntity()).getCode();
            return () -> code;
        } else if (o instanceof CompositeMove) {
            CompositeMove<?> compositeMove = (CompositeMove) o;
            StringBuilder codeBuilder = new StringBuilder(compositeMove.getMoves().length * 80);
            for (Move<?> move : compositeMove.getMoves()) {
                codeBuilder.append("+").append(convert(move).getCode());
            }
            final String code = codeBuilder.substring(1);
            return () -> code;
        } else if (o instanceof ListAssignMove) {
            ListAssignMove<?> listAssignMove = (ListAssignMove<?>) o;
            return () -> convert(listAssignMove.getMovedValue())
                    + " {null->"
                    + convert(listAssignMove.getDestinationEntity())
                    + "[" + listAssignMove.getDestinationIndex() + "]}";
        } else if (o instanceof ListChangeMove) {
            ListChangeMove<?> listChangeMove = (ListChangeMove<?>) o;
            return () -> convert(listChangeMove.getMovedValue())
                    + " {" + convert(listChangeMove.getSourceEntity())
                    + "[" + listChangeMove.getSourceIndex() + "]->"
                    + convert(listChangeMove.getDestinationEntity())
                    + "[" + listChangeMove.getDestinationIndex() + "]}";
        } else if (o instanceof ListSwapMove) {
            ListSwapMove<?> listSwapMove = (ListSwapMove<?>) o;
            return () -> convert(listSwapMove.getLeftValue())
                    + " {" + convert(listSwapMove.getLeftEntity())
                    + "[" + listSwapMove.getLeftIndex() + "]} <-> "
                    + convert(listSwapMove.getRightValue())
                    + " {" + convert(listSwapMove.getRightEntity())
                    + "[" + listSwapMove.getRightIndex() + "]}";
        } else if (o instanceof List) {
            List<?> list = (List) o;
            StringBuilder codeBuilder = new StringBuilder("[");
            boolean firstElement = true;
            for (Object element : list) {
                if (firstElement) {
                    firstElement = false;
                } else {
                    codeBuilder.append(", ");
                }
                codeBuilder.append(convert(element).getCode());
            }
            codeBuilder.append("]");
            final String code = codeBuilder.toString();
            return () -> code;
        } else if (o instanceof SubChain) {
            SubChain subChain = (SubChain) o;
            final String code = convert(subChain.getEntityList()).getCode();
            return () -> code;
        }
        throw new AssertionError(("o's class (" + o.getClass() + ") cannot be converted to CodeAssertable."));
    }
}
