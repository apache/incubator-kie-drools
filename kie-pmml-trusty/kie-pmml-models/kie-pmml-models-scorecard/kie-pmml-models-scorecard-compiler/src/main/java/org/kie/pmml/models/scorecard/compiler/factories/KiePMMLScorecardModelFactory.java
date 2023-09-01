package org.kie.pmml.models.scorecard.compiler.factories;

import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.pmml.api.enums.REASONCODE_ALGORITHM;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.scorecard.compiler.ScorecardCompilationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.Constants.MISSING_CONSTRUCTOR_IN_BODY;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLCharacteristicsFactory.getKiePMMLCharacteristicsSourcesMap;

public class KiePMMLScorecardModelFactory {

    static final String KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA = "KiePMMLScorecardModelTemplate.tmpl";
    static final String KIE_PMML_SCORECARD_MODEL_TEMPLATE = "KiePMMLScorecardModelTemplate";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLScorecardModelFactory.class.getName());

    private KiePMMLScorecardModelFactory() {
        // Avoid instantiation
    }

    public static Map<String, String> getKiePMMLScorecardModelSourcesMap(final ScorecardCompilationDTO compilationDTO) {
        String className = compilationDTO.getSimpleClassName();
        String packageName = compilationDTO.getPackageName();
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName,
                                                                                 KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA, KIE_PMML_SCORECARD_MODEL_TEMPLATE);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        String characteristicsClassName = compilationDTO.getCharacteristicsClassName();
        String fullCharacteristicsClassName = String.format(PACKAGE_CLASS_TEMPLATE, packageName,
                                                            characteristicsClassName);
        Map<String, String> toReturn = getKiePMMLCharacteristicsSourcesMap(compilationDTO);
        setConstructor(compilationDTO,
                       modelTemplate,
                       fullCharacteristicsClassName);
        String fullClassName = packageName + "." + className;
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    static void setConstructor(final ScorecardCompilationDTO compilationDTO,
                               final ClassOrInterfaceDeclaration modelTemplate,
                               final String fullCharacteristicsClassName) {
        KiePMMLModelFactoryUtils.init(compilationDTO,
                                      modelTemplate);
        final ConstructorDeclaration constructorDeclaration =
                modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        final BlockStmt body = constructorDeclaration.getBody();
        final ExplicitConstructorInvocationStmt superStatement =
                CommonCodegenUtils.getExplicitConstructorInvocationStmt(body)
                        .orElseThrow(() -> new KiePMMLException(String.format(MISSING_CONSTRUCTOR_IN_BODY, body)));
        ClassOrInterfaceType characteristicsClass = parseClassOrInterfaceType(fullCharacteristicsClassName);
        ObjectCreationExpr characteristicsReference = new ObjectCreationExpr();
        characteristicsReference.setType(characteristicsClass);
        superStatement.setArgument(3, characteristicsReference);
        superStatement.setArgument(4, getExpressionForObject(compilationDTO.getInitialScore()));
        superStatement.setArgument(5, getExpressionForObject(compilationDTO.isUseReasonCodes()));
        REASONCODE_ALGORITHM reasoncodeAlgorithm = compilationDTO.getREASONCODE_ALGORITHM();
        NameExpr reasonCodeExpr = new NameExpr(REASONCODE_ALGORITHM.class.getName() + "." + reasoncodeAlgorithm.name());
        superStatement.setArgument(6, reasonCodeExpr);
        superStatement.setArgument(7, getExpressionForObject(compilationDTO.getBaselineScore()));
    }
}
