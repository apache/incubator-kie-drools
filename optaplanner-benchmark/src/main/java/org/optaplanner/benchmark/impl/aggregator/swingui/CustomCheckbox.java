/*
 * Copyright 2014 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.benchmark.impl.aggregator.swingui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;


public class CustomCheckbox extends JCheckBox {
    
    public static final CheckboxStatus CHECKED = CheckboxStatus.CHECKED;
    public static final CheckboxStatus UNCHECKED = CheckboxStatus.UNCHECKED;
    public static final CheckboxStatus MIXED = CheckboxStatus.MIXED;
    
    public CustomCheckbox(String text) {
        super(text);
        setModel(new CustomCheckboxModel());
        setStatus(UNCHECKED);
        addMouseListener(new CustomCheckboxMouseListener());
    }
    
    public CheckboxStatus getStatus() {
        return ((CustomCheckboxModel) getModel()).getStatus();
    }
    
    public void setStatus(CheckboxStatus status) {
        ((CustomCheckboxModel) getModel()).setStatus(status);
    }
    
    private class CustomCheckboxMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            ((CustomCheckboxModel) getModel()).switchStatus();
        }
    }
    
    private static class CustomCheckboxModel extends ToggleButtonModel {
        
        private CheckboxStatus getStatus() {
            return isSelected() ? (isArmed() ? MIXED : CHECKED) : UNCHECKED; 
        }
        
        private void setStatus(CheckboxStatus status) {
            if (CHECKED.equals(status)) {
                setSelected(true);
                setArmed(false);
                setPressed(false);
            } else if (UNCHECKED.equals(status)) {
                setSelected(false);
                setArmed(false);
                setPressed(false);
            } else if (MIXED.equals(status)) {
                setSelected(true);
                setArmed(true);
                setPressed(true);
            } else {
                throw new IllegalArgumentException("Invalid argument '" 
                        + status + "' supplied.");
            }
        }
        
        private void switchStatus() {
            switch (getStatus()) {
                case CHECKED: {
                    setStatus(UNCHECKED);
                    break;
                }
                case UNCHECKED: {
                    setStatus(CHECKED);
                    break;
                }
                case MIXED: {
                    setStatus(CHECKED);
                }
            }
        }
    }
    
    public static enum CheckboxStatus {
        CHECKED, UNCHECKED, MIXED
    }
}
