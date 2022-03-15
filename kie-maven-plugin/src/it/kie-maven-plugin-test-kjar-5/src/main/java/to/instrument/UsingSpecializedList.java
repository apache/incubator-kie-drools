package to.instrument;

import java.util.List;

import org.magicwerk.brownies.collections.GapList;

public class UsingSpecializedList {

    private final String name;
    private List<String> gapList;

    public UsingSpecializedList(String name) {
        super();
        this.name = name;
        this.gapList = new GapList<String>();
    }

    public String getName() {
        return name;
    }

    public List<String> getGapList() {
        if (gapList == null) {
            gapList = new GapList<String>();
        }
        return gapList;
    }
}
