/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.formbuilder.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.MimeType;
import com.google.gwt.resources.client.ImageResource;

/**
 * Images should be loaded from here
 */
public interface FormBuilderResources extends ClientBundle {

    FormBuilderResources INSTANCE = GWT.create( FormBuilderResources.class );
    
    @Source("images/completeButton.png")
    ImageResource completeButton();
    
    @Source("images/textField.png")
    ImageResource textField();
    
    @Source("images/passwordField.png")
    ImageResource passwordField();
    
    @Source("images/label.png")
    ImageResource label();

    @Source("images/comboBox.png")
    ImageResource comboBox();
    
    @Source("images/error_icon.png")
    ImageResource errorIcon();

    @Source("images/horizontal_layout_icon.png")
    ImageResource horizontalLayoutIcon();
    
    @Source("images/table_layout_icon.png")
    ImageResource tableLayoutIcon();

    @Source("images/border_layout_icon.png")
    ImageResource borderLayoutIcon();

    @Source("images/header.png")
    ImageResource header();

    @Source("images/textArea.png")
    ImageResource textArea();

    @Source("images/hidden.png")
    ImageResource hidden();

    @Source("images/checkBox.png")
    ImageResource checkBox();

    @Source("images/fileInput.png")
    ImageResource fileInput();

    @Source("images/image.png")
    ImageResource image();

    @Source("images/html.png")
    ImageResource html();

    @Source("images/default_image_en.jpg")
    ImageResource defaultImage();

    @Source("images/radioButton.png")
    ImageResource radioButton();

    @Source("images/question.png")
    ImageResource questionIcon();

    @Source("images/absolute_layout_icon.png")
    ImageResource absoluteLayoutIcon();

    @Source("images/css_layout_icon.png")
    ImageResource cssLayoutIcon();
    
    @Source("images/conditional_block.png")
    ImageResource conditionalBlock();
    
    @Source("images/loop_block.png")
    ImageResource loopBlock();
    
    @Source("images/transformation_block.png")
    ImageResource transformationBlock();

    @Source("images/save_button.png")
    ImageResource saveButton();

    @Source("images/refresh_button.png")
    ImageResource refreshButton();

    @Source("images/flow_layout_icon.png")
    ImageResource flowLayoutIcon();

    @Source("images/lineGraph.png")
    ImageResource lineGraph();

    @Source("images/treeFolder.png")
    ImageResource treeFolder();

    @Source("images/treeLeaf.png")
    ImageResource treeLeaf();

    @Source("images/tabbed_layout_icon.png")
    ImageResource tabbedLayoutIcon();

    @Source("images/remove_small_icon.png")
    ImageResource removeSmallIcon();

    @Source("images/undo_button.png")
    ImageResource undoButton();
    
    @Source("images/redo_button.png")
    ImageResource redoButton();
    
    @Source("images/arrow_up.png")
    ImageResource arrowUp();

    @Source("images/arrow_down.png")
    ImageResource arrowDown();
    
    @Source("images/arrow_left.png")
    ImageResource arrowLeft();
    
    @Source("images/arrow_right.png")
    ImageResource arrowRight();

    @Source("images/richTextEditor.png")
    ImageResource richTextEditor();

    @Source("images/hiddenFieldIcon.png")
    ImageResource hiddenFieldIcon();

    @Source("images/client_script.png")
    ImageResource clientScript();

    @Source("images/clientScriptIcon.png")
    ImageResource clientScriptIcon();

    @Source("images/calendar.png")
    ImageResource calendar();

    @Source("images/calendarSquare.png")
    ImageResource calendarSquare();
    
    @Source("images/image_rolodex.png")
    ImageResource imageRolodex();
    
    @Source("images/summary.png")
    ImageResource summary();
    
    @Source("images/fieldSet.png")
    ImageResource fieldSet();
    
    @Source("images/menu_layout_icon.png")
    ImageResource menuLayout();
    
    @Source("images/rangeField.png")
    ImageResource rangeField();

    @Source("images/numberField.png")
    ImageResource numberField();
    
    @Source("images/audio.png")
    ImageResource audio();

    @Source("images/video.png")
    ImageResource video();
    
    @Source("images/canvas.png")
    ImageResource canvas();

    @Source("images/file_input_with_progress_bar.png")
    ImageResource fileInputWithProgressBar();

    @Source("images/flexible_table_icon.png")
    ImageResource flexibleTable();
    
    @Source("images/canvas_not_supported.svg")
    @MimeType("image/svg+xml")
    DataResource canvasNotSupported();

}
