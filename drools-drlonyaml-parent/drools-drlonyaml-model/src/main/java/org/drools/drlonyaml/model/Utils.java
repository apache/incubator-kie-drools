package org.drools.drlonyaml.model;

import java.util.ArrayList;
import java.util.List;

import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.PatternDescr;

public class Utils {
    private Utils() {
        // only static methods.
    }
    
    public static Base from(BaseDescr o) {
        if (o instanceof PatternDescr) {
            return Pattern.from((PatternDescr) o);
        } else if (o instanceof NotDescr) {
            return Not.from((NotDescr) o);
        } else if (o instanceof AndDescr) {
            return All.from((AndDescr) o);
        } else if (o instanceof ExistsDescr) {
            return Exists.from((ExistsDescr) o);
        } else {
            throw new UnsupportedOperationException("TODO: "+o);
        }
    }

    public static List<Base> from(List<BaseDescr> descrs) {
        List<Base> results = new ArrayList<>();
        for (Object item : descrs) {
            results.add(from((BaseDescr) item));
        }
        return results;
    }
}
