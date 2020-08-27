/**
 * Default <code>PMMLModelExecutor</code> for <b>${modelName}</b>
 */
public class PMML${modelName}ModelEvaluator extends DroolsModelExecutor {

    @Override
    public PMML_MODEL getPMMLModelType(){
        return PMML_MODEL.${modelNameUppercase}_MODEL;
    }
}
