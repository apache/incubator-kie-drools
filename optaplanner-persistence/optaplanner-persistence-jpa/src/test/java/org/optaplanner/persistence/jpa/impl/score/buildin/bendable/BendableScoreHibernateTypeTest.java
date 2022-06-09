package org.optaplanner.persistence.jpa.impl.score.buildin.bendable;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

class BendableScoreHibernateTypeTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(BendableScore.zero(3, 2)),
                BendableScore.of(new int[] { 10000, 2000, 300 }, new int[] { 40, 5 }),
                BendableScore.ofUninitialized(-7, new int[] { 10000, 2000, 300 }, new int[] { 40, 5 }));
    }

    @Entity
    @TypeDef(defaultForType = BendableScore.class, typeClass = BendableScoreHibernateType.class, parameters = {
            @Parameter(name = "hardLevelsSize", value = "3"), @Parameter(name = "softLevelsSize", value = "2") })
    public static class TestJpaEntity extends AbstractTestJpaEntity<BendableScore> {

        @Columns(columns = { @Column(name = "initScore"),
                @Column(name = "hard0Score"), @Column(name = "hard1Score"), @Column(name = "hard2Score"),
                @Column(name = "soft0Score"), @Column(name = "soft1Score") })
        protected BendableScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(BendableScore score) {
            this.score = score;
        }

        @Override
        public BendableScore getScore() {
            return score;
        }

        @Override
        public void setScore(BendableScore score) {
            this.score = score;
        }
    }
}
