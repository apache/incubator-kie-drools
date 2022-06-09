package org.optaplanner.persistence.jpa.impl.score.buildin.simplelong;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.TypeDef;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

class SimpleLongScoreHibernateTypeTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(SimpleLongScore.ZERO),
                SimpleLongScore.of(-10L),
                SimpleLongScore.ofUninitialized(-7, -10L));
    }

    @Entity
    @TypeDef(defaultForType = SimpleLongScore.class, typeClass = SimpleLongScoreHibernateType.class)
    public static class TestJpaEntity extends AbstractTestJpaEntity<SimpleLongScore> {

        @Columns(columns = { @Column(name = "initScore"), @Column(name = "score") })
        protected SimpleLongScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(SimpleLongScore score) {
            this.score = score;
        }

        @Override
        public SimpleLongScore getScore() {
            return score;
        }

        @Override
        public void setScore(SimpleLongScore score) {
            this.score = score;
        }

    }

}
