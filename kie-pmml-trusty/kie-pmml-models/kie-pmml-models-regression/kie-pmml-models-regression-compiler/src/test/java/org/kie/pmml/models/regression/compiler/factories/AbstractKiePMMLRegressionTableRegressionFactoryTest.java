package org.kie.pmml.models.regression.compiler.factories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.validator.ProblemReporter;
import com.github.javaparser.ast.validator.language_level_validations.Java8Validator;
import org.dmg.pmml.regression.CategoricalPredictor;
import org.dmg.pmml.regression.NumericPredictor;
import org.dmg.pmml.regression.PredictorTerm;
import org.dmg.pmml.regression.RegressionTable;
import org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils;

import static org.assertj.core.api.Assertions.fail;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getCategoricalPredictor;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getNumericPredictor;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getPredictorTerm;

public abstract class AbstractKiePMMLRegressionTableRegressionFactoryTest {

    protected RegressionTable regressionTable;
    protected List<CategoricalPredictor> categoricalPredictors;
    protected List<NumericPredictor> numericPredictors;
    protected List<PredictorTerm> predictorTerms;


    protected void commonValidateKiePMMLRegressionTable(String retrieved) {
        try {
            final CompilationUnit parsed = StaticJavaParser.parse(retrieved);
            final Java8Validator validator = new Java8Validator();
            final ProblemReporter problemReporter = new ProblemReporter(problem -> fail(problem.getMessage()));
            validator.accept(parsed.findRootNode(), problemReporter);
        } catch (Exception e) {
            fail("Failed to validate " + retrieved + " due to " + e.getMessage());
        }
    }

    protected RegressionTable getRegressionTable(double intercept, Object targetCategory) {
        categoricalPredictors = new ArrayList<>();
        numericPredictors = new ArrayList<>();
        predictorTerms = new ArrayList<>();
        numericPredictors.add(getNumericPredictor("NumPred-" + 3, 1, 32.55));
        IntStream.range(0, 3).forEach(i -> {
            IntStream.range(0, 2).forEach(j -> categoricalPredictors.add(getCategoricalPredictor("CatPred-" + i, 27.12, 3.46)));
            numericPredictors.add(getNumericPredictor("NumPred-" + i, 2, 13.11));
            predictorTerms.add(getPredictorTerm("PredTerm-" + i, 32.29,
                                                Arrays.asList(categoricalPredictors.get(0).getName().getValue(),
                                                              numericPredictors.get(0).getName().getValue())));
        });
        return PMMLModelTestUtils.getRegressionTable(categoricalPredictors, numericPredictors, predictorTerms, intercept, targetCategory);
    }
}