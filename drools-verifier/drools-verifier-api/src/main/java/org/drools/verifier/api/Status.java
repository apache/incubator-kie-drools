package org.drools.verifier.api;

public class Status {

    private String webWorkerUUID;

    private int startCheckIndex;
    private int endCheckIndex;
    private int totalCheckCount;

    public Status() {
    }

    public Status(final String webWorkerUUID,
                  final int startCheckIndex,
                  final int endCheckIndex,
                  final int totalCheckCount) {
        this.webWorkerUUID = webWorkerUUID;
        this.startCheckIndex = startCheckIndex;
        this.endCheckIndex = endCheckIndex;
        this.totalCheckCount = totalCheckCount;
    }

    public void setWebWorkerUUID(final String webWorkerUUID) {
        this.webWorkerUUID = webWorkerUUID;
    }

    public void setStartCheckIndex(final int startCheckIndex) {
        this.startCheckIndex = startCheckIndex;
    }

    public void setEndCheckIndex(final int endCheckIndex) {
        this.endCheckIndex = endCheckIndex;
    }

    public void setTotalCheckCount(final int totalCheckCount) {
        this.totalCheckCount = totalCheckCount;
    }

    public String getWebWorkerUUID() {
        return webWorkerUUID;
    }

    public int getStart() {
        return startCheckIndex;
    }

    public int getEnd() {
        return endCheckIndex;
    }

    public int getTotalCheckCount() {
        return totalCheckCount;
    }
}
