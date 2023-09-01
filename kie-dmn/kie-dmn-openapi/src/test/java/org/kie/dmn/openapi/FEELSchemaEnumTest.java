package org.kie.dmn.openapi;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.feel.runtime.impl.RangeImpl;
import org.kie.dmn.openapi.impl.FEELSchemaEnum;

import static org.assertj.core.api.Assertions.assertThat;

public class FEELSchemaEnumTest extends BaseDMNOASTest {

    @Test
    public void testBasic() {
        List<Range> list = new ArrayList<>();
        list.add(new RangeImpl(RangeBoundary.CLOSED, 0, null, RangeBoundary.CLOSED));
        list.add(new RangeImpl(RangeBoundary.CLOSED, null, 100, RangeBoundary.CLOSED));
        Range result = FEELSchemaEnum.consolidateRanges(list);
        assertThat(result).isNotNull().isEqualTo(new RangeImpl(RangeBoundary.CLOSED, 0, 100, RangeBoundary.CLOSED));
    }

    @Test
    public void testInvalidRepeatedLB() {
        List<Range> list = new ArrayList<>();
        list.add(new RangeImpl(RangeBoundary.CLOSED, 0, null, RangeBoundary.CLOSED));
        list.add(new RangeImpl(RangeBoundary.CLOSED, 0, 100, RangeBoundary.CLOSED));
        Range result = FEELSchemaEnum.consolidateRanges(list);
        assertThat(result).isNull();
    }

    @Test
    public void testInvalidRepeatedUB() {
        List<Range> list = new ArrayList<>();
        list.add(new RangeImpl(RangeBoundary.CLOSED, null, 50, RangeBoundary.CLOSED));
        list.add(new RangeImpl(RangeBoundary.CLOSED, null, 100, RangeBoundary.CLOSED));
        Range result = FEELSchemaEnum.consolidateRanges(list);
        assertThat(result).isNull();
    }
}
