package org.optaplanner.core.impl.exhaustivesearch.node.bounder;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class TrendBasedScoreBounder implements ScoreBounder {

    protected final ScoreDefinition scoreDefinition;
    protected final InitializingScoreTrend initializingScoreTrend;

    public TrendBasedScoreBounder(InnerScoreDirectorFactory scoreDirectorFactory) {
        scoreDefinition = scoreDirectorFactory.getScoreDefinition();
        initializingScoreTrend = scoreDirectorFactory.getInitializingScoreTrend();
    }

    @Override
    public Score calculateOptimisticBound(ScoreDirector scoreDirector, Score score) {
        return scoreDefinition.buildOptimisticBound(initializingScoreTrend, score);
    }

    @Override
    public Score calculatePessimisticBound(ScoreDirector scoreDirector, Score score) {
        return scoreDefinition.buildPessimisticBound(initializingScoreTrend, score);
    }

}
