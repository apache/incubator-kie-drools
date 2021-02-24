package org.optaplanner.examples.batchscheduling.domain;

import java.util.List;

import org.optaplanner.examples.batchscheduling.app.BatchSchedulingApp;
import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("PipeRoutePath")
public class RoutePath extends AbstractPersistable {

    private Batch batch;
    private String path;
    private List<Segment> segmentList;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public List<Segment> getSegmentList() {
        return segmentList;
    }

    public void setSegmentList(List<Segment> segmentList) {
        this.segmentList = segmentList;
    }

    public static String[] getSegmentArray(String routePath) {
        String[] array1 = routePath.split(BatchSchedulingApp.ROUTE_PATH_SEPERATOR);

        String[] array2 = new String[array1.length - 1];

        for (int i = 0; i < array2.length; i++) {
            array2[i] = array1[i] + BatchSchedulingApp.ROUTE_PATH_SEPERATOR + array1[i + 1];
        }

        return array2;
    }

}
