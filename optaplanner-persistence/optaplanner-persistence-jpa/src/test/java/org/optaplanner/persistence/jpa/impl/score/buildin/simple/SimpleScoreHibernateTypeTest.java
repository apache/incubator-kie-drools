package org.optaplanner.persistence.jpa.impl.score.buildin.simple;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.TypeDef;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

class SimpleScoreHibernateTypeTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(SimpleScore.ZERO),
                SimpleScore.of(-10),
                SimpleScore.ofUninitialized(-7, -10));
    }

    @Entity
    @TypeDef(defaultForType = SimpleScore.class, typeClass = SimpleScoreHibernateType.class)
    public static class TestJpaEntity extends AbstractTestJpaEntity<SimpleScore> {

        @Columns(columns = { @Column(name = "initScore"), @Column(name = "score") })
        protected SimpleScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(SimpleScore score) {
            this.score = score;
        }

        @Override
        public SimpleScore getScore() {
            return score;
        }

        @Override
        public void setScore(SimpleScore score) {
            this.score = score;
        }

    }

}
