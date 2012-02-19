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
package org.jbpm.formbuilder.client.form.items;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.common.panels.CalendarPanel;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.CalendarRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.client.resources.FormBuilderResources;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class CalendarFormItem extends FBFormItem {

    private String defaultValue;
    private String iconUrl;
    private String calendarCss;
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
    private final DatePicker calendar = new DatePicker();
    private final Image icon = new Image();
    private final PopupPanel panel = new PopupPanel();
    private final TextBox text = new TextBox();
    
    public CalendarFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public CalendarFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        this.iconUrl = FormBuilderResources.INSTANCE.calendarSquare().getURL();
        icon.setUrl(this.iconUrl);
        icon.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        icon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                panel.setSize("183px", "183px");
                panel.setPopupPosition(event.getClientX(), event.getClientY());
                panel.setWidget(calendar);
                panel.show();
                
            }
        });
        calendar.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                panel.hide();
                text.setValue(format.format(event.getValue()));
            }
        });
        calendar.setSize("183px", "183px");
        CalendarPanel cPanel = new CalendarPanel(text, icon);
        cPanel.add(text);
        cPanel.add(icon);
        text.setSize("175px", "21px");
        cPanel.setSize("200px", "21px");
        setSize("200px", "21px");
        add(cPanel);
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("width", getWidth());
        map.put("height", getHeight());
        map.put("iconUrl", this.iconUrl);
        map.put("calendarCss", this.calendarCss);
        map.put("defaultValue", this.defaultValue);
        return map;
    }

    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        setWidth(extractString(asPropertiesMap.get("width")));
        setHeight(extractString(asPropertiesMap.get("height")));
        
        this.iconUrl = extractString(asPropertiesMap.get("iconUrl"));
        this.calendarCss = extractString(asPropertiesMap.get("calendarCss"));
        
        this.defaultValue = extractString(asPropertiesMap.get("defaultValue"));
        populate(this.calendar, this.text, this.icon);
    }
    
    private void populate(DatePicker calendar, TextBox text, Image icon) {
        if (getHeight() != null && !"".equals(getHeight())) {
            calendar.setHeight(getHeight());
        }
        if (getWidth() != null && !"".equals(getWidth())) {
            calendar.setWidth(getWidth());
        }
        if (this.defaultValue != null) {
            if (!"".equals(this.defaultValue)) {
                calendar.setValue(format.parse(this.defaultValue));
            } else {
                calendar.setValue(null);
            }
        }
        if (this.calendarCss != null && !"".equals(this.calendarCss)) {
            calendar.setStyleName(this.calendarCss);
        }
        if (this.iconUrl != null && !"".equals(this.iconUrl)) {
            icon.setUrl(this.iconUrl);
        }
        String cursor = icon.getElement().getStyle().getCursor();
        if (!Style.Cursor.POINTER.getCssName().equals(cursor)) {
            icon.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        }
    }

    @Override
    public FormItemRepresentation getRepresentation() {
        CalendarRepresentation crep = super.getRepresentation(new CalendarRepresentation());
        crep.setIconUrl(this.iconUrl);
        crep.setCalendarCss(this.calendarCss);
        crep.setDefaultValue(this.defaultValue);
        return crep;
    }

    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof CalendarRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "CalendarRepresentation"));
        }
        super.populate(rep);
        CalendarRepresentation crep = (CalendarRepresentation) rep;
        this.calendarCss = crep.getCalendarCss();
        this.iconUrl = crep.getIconUrl();
        this.defaultValue = crep.getDefaultValue();
        populate(this.calendar, this.text, this.icon);
    }
    
    @Override
    public FBFormItem cloneItem() {
        CalendarFormItem clone = super.cloneItem(new CalendarFormItem());
        clone.calendarCss = this.calendarCss;
        clone.defaultValue = this.defaultValue;
        clone.iconUrl = this.iconUrl;
        populate(clone.calendar, clone.text, clone.icon);
        return clone;
    }
    
    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        DatePicker date = new DatePicker();
        TextBox textBox = new TextBox();
        final PopupPanel panel = new PopupPanel();
        DatePicker calendar = new DatePicker();
        if (this.calendarCss != null && !"".equals(this.calendarCss)) {
            calendar.setStyleName(this.calendarCss);
        }
        panel.setSize("183px", "183px");
        panel.setWidget(calendar);
        Image icon = new Image();
        icon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                panel.setPopupPosition(event.getClientX(), event.getClientY());
                panel.show();
            }
        });
        populate(date, textBox, icon);
        CalendarPanel display = new CalendarPanel(textBox, icon);
        Object input = getInputValue(data);
        if (input != null) {
            textBox.setValue(input.toString());
        }
        if (getOutput() != null && getOutput().getName() != null) {
            textBox.setName(getOutput().getName());
        }
        super.populateActions(textBox.getElement());
        return display;
    }

}
