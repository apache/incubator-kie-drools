package org.kie.pmml.models.drools.executor;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Class used inside drools. Rules are fired based on the value of status
 */
public class KiePMMLStatusHolder {

    private String status;

    private AtomicReference<Double> accumulator = new AtomicReference<>(0.0);

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAccumulator() {
        return accumulator.get();
    }

    public void accumulate(double toAccumulate) {
        accumulator.accumulateAndGet(toAccumulate, Double::sum);
    }

    @Override
    public String toString() {
        return "KiePMMLStatusHolder{" +
                "status='" + status + '\'' +
                "accumulator='" + accumulator + '\'' +
                '}';
    }
}
