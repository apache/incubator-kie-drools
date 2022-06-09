package org.optaplanner.benchmark.impl.aggregator.swingui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;

public class MixedCheckBox extends JCheckBox {

    private String detail;
    private Object benchmarkResult;

    public MixedCheckBox() {
        this(null);
    }

    public MixedCheckBox(String text) {
        this(text, null);
    }

    public MixedCheckBox(String text, String detail) {
        this(text, detail, null);
    }

    public MixedCheckBox(String text, String detail, Object benchmarkResult) {
        super(text);
        this.detail = detail;
        this.benchmarkResult = benchmarkResult;
        setModel(new MixedCheckBoxModel());
        setStatus(MixedCheckBoxStatus.UNCHECKED);
        addMouseListener(new CustomCheckboxMouseListener());
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Object getBenchmarkResult() {
        return benchmarkResult;
    }

    public void setBenchmarkResult(Object benchmarkResult) {
        this.benchmarkResult = benchmarkResult;
    }

    public MixedCheckBoxStatus getStatus() {
        return ((MixedCheckBoxModel) getModel()).getStatus();
    }

    public void setStatus(MixedCheckBoxStatus status) {
        ((MixedCheckBoxModel) getModel()).setStatus(status);
    }

    private class CustomCheckboxMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            ((MixedCheckBoxModel) getModel()).switchStatus();
        }
    }

    private static class MixedCheckBoxModel extends ToggleButtonModel {

        private MixedCheckBoxStatus getStatus() {
            return isSelected() ? (isArmed() ? MixedCheckBoxStatus.MIXED : MixedCheckBoxStatus.CHECKED)
                    : MixedCheckBoxStatus.UNCHECKED;
        }

        private void setStatus(MixedCheckBoxStatus status) {
            if (status == MixedCheckBoxStatus.CHECKED) {
                setSelected(true);
                setArmed(false);
                setPressed(false);
            } else if (status == MixedCheckBoxStatus.UNCHECKED) {
                setSelected(false);
                setArmed(false);
                setPressed(false);
            } else if (status == MixedCheckBoxStatus.MIXED) {
                setSelected(true);
                setArmed(true);
                setPressed(true);
            } else {
                throw new IllegalArgumentException("Invalid argument ("
                        + status + ") supplied.");
            }
        }

        private void switchStatus() {
            switch (getStatus()) {
                case CHECKED:
                    setStatus(MixedCheckBoxStatus.UNCHECKED);
                    break;
                case UNCHECKED:
                    setStatus(MixedCheckBoxStatus.CHECKED);
                    break;
                case MIXED:
                    setStatus(MixedCheckBoxStatus.CHECKED);
                    break;
                default:
                    throw new IllegalStateException("The status (" + getStatus() + ") is not implemented.");
            }
        }
    }

    public enum MixedCheckBoxStatus {
        CHECKED,
        UNCHECKED,
        MIXED
    }

}
