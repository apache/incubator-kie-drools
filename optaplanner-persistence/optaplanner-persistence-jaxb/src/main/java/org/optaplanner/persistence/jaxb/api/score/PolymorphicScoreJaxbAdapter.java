package org.optaplanner.persistence.jaxb.api.score;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;

public class PolymorphicScoreJaxbAdapter extends XmlAdapter<PolymorphicScoreJaxbAdapter.JaxbAdaptedScore, Score> {

    @Override
    public Score unmarshal(JaxbAdaptedScore jaxbAdaptedScore) {
        if (jaxbAdaptedScore == null) {
            return null;
        }
        String scoreClassName = jaxbAdaptedScore.scoreClassName;
        String scoreString = jaxbAdaptedScore.scoreString;
        // TODO Can this delegate to ScoreUtils.parseScore()?
        if (scoreClassName.equals(SimpleScore.class.getName())) {
            return SimpleScore.parseScore(scoreString);
        } else if (scoreClassName.equals(SimpleLongScore.class.getName())) {
            return SimpleLongScore.parseScore(scoreString);
        } else if (scoreClassName.equals(SimpleBigDecimalScore.class.getName())) {
            return SimpleBigDecimalScore.parseScore(scoreString);
        } else if (scoreClassName.equals(HardSoftScore.class.getName())) {
            return HardSoftScore.parseScore(scoreString);
        } else if (scoreClassName.equals(HardSoftLongScore.class.getName())) {
            return HardSoftLongScore.parseScore(scoreString);
        } else if (scoreClassName.equals(HardSoftBigDecimalScore.class.getName())) {
            return HardSoftBigDecimalScore.parseScore(scoreString);
        } else if (scoreClassName.equals(HardMediumSoftScore.class.getName())) {
            return HardMediumSoftScore.parseScore(scoreString);
        } else if (scoreClassName.equals(HardMediumSoftLongScore.class.getName())) {
            return HardMediumSoftLongScore.parseScore(scoreString);
        } else if (scoreClassName.equals(HardMediumSoftBigDecimalScore.class.getName())) {
            return HardMediumSoftBigDecimalScore.parseScore(scoreString);
        } else if (scoreClassName.equals(BendableScore.class.getName())) {
            return BendableScore.parseScore(scoreString);
        } else if (scoreClassName.equals(BendableLongScore.class.getName())) {
            return BendableLongScore.parseScore(scoreString);
        } else if (scoreClassName.equals(BendableBigDecimalScore.class.getName())) {
            return BendableBigDecimalScore.parseScore(scoreString);
        } else {
            throw new IllegalArgumentException("Unrecognized scoreClassName (" + scoreClassName
                    + ") for scoreString (" + scoreString + ").");
        }
    }

    @Override
    public JaxbAdaptedScore marshal(Score score) {
        if (score == null) {
            return null;
        }
        return new JaxbAdaptedScore(score);
    }

    static class JaxbAdaptedScore {

        @XmlAttribute(name = "class")
        private String scoreClassName;
        @XmlValue
        private String scoreString;

        private JaxbAdaptedScore() {
            // Required by JAXB
        }

        public JaxbAdaptedScore(Score score) {
            this.scoreClassName = score.getClass().getName();
            this.scoreString = score.toString();
        }

        String getScoreClassName() {
            return scoreClassName;
        }

        String getScoreString() {
            return scoreString;
        }
    }
}
