/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.model.api.dmndi;

public interface DMNStyle extends Style {

    public Color getFillColor();

    public void setFillColor(Color value);

    public Color getStrokeColor();

    public void setStrokeColor(Color value);

    public Color getFontColor();

    public void setFontColor(Color value);

    public String getFontFamily();

    public void setFontFamily(String value);

    public Double getFontSize();

    public void setFontSize(Double value);

    public Boolean isFontItalic();

    public void setFontItalic(Boolean value);

    public Boolean isFontBold();

    public void setFontBold(Boolean value);

    public Boolean isFontUnderline();

    public void setFontUnderline(Boolean value);

    public Boolean isFontStrikeThrough();

    public void setFontStrikeThrough(Boolean value);

    public AlignmentKind getLabelHorizontalAlignement();

    public void setLabelHorizontalAlignement(AlignmentKind value);

    public AlignmentKind getLabelVerticalAlignment();

    public void setLabelVerticalAlignment(AlignmentKind value);

}
