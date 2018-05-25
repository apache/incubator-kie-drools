package org.kie.pmml.pmml_4_2.model.scorecard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.dmg.pmml.pmml_4_2.descr.Apply;
import org.dmg.pmml.pmml_4_2.descr.Attribute;
import org.dmg.pmml.pmml_4_2.descr.Characteristic;
import org.dmg.pmml.pmml_4_2.descr.ComplexPartialScore;
import org.dmg.pmml.pmml_4_2.descr.FieldRef;
import org.kie.pmml.pmml_4_2.PMML4Helper;

public class ComplexScore {
    private static PMML4Helper helper = new PMML4Helper();
    private String complexScore;
    private String characteristicName;
    private int attributeIndex;
    private List<String> fldNames;

    public ComplexScore(Characteristic charac, Attribute attrib) {
        this.characteristicName = charac.getName();
        this.attributeIndex = charac.getAttributes().indexOf(attrib);
        this.complexScore = helper.createPartialScoreFormula(attrib);
        this.fldNames = new ArrayList<>();
        createListOfFieldNames(attrib);
    }

    private void createListOfFieldNames(Attribute attrib) {
        ComplexPartialScore cps = attrib.getComplexPartialScore();
        if (cps != null) {
            if (cps.getApply() != null) {
                iterateThroughComplexPartialScoreElements(cps.getApply());
            } else if (cps.getFieldRef() != null) {
                fldNames.add(cps.getFieldRef().getField());
            }
        }
    }

    private void iterateThroughComplexPartialScoreElements(Serializable sz) {
        if (sz instanceof Apply) {
            Apply inner = (Apply)sz;
            inner.getConstantsAndFieldRevesAndNormContinuouses().forEach(s -> {iterateThroughComplexPartialScoreElements(s);});
        } else if (sz instanceof FieldRef) {
            fldNames.add(((FieldRef)sz).getField());
        }
    }

    public String getComplexScore() {
        return complexScore;
    }

    public String getCharacteristicName() {
        return characteristicName;
    }

    public int getAttributeIndex() {
        return attributeIndex;
    }

    public List<String> getFldNames() {
        return new ArrayList<>(this.fldNames);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + attributeIndex;
        result = prime * result + ((characteristicName == null) ? 0 : characteristicName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ComplexScore other = (ComplexScore) obj;
        if (attributeIndex != other.attributeIndex)
            return false;
        if (characteristicName == null) {
            if (other.characteristicName != null)
                return false;
        } else if (!characteristicName.equals(other.characteristicName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ComplexScore [complexScore=" + complexScore + ", characteristicName=" + characteristicName
                + ", attributeIndex=" + attributeIndex + ", fldNames=" + fldNames + "]";
    }
}
