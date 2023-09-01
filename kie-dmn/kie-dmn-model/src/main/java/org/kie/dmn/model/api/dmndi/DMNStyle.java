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
