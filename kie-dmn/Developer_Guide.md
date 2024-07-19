# kie-dmn - Developer Guide

This module (and all its submodules) contains the code that make up the DMN engine.

The engine is responsible, in very general terms, of
1. compile the dmn model
2. validate it
3. evaluate it, against a given input

## DTOs

## Code usage

### Compilation
The simplest way to compile a model is to instantiate a [DMNRuntime](kie-dmn-api%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Fapi%2Fcore%2FDMNRuntime.java) from its file, e.g.
 ```java
      DMNRuntime runtime = DMNRuntimeUtil.createRuntime("simple-item-def.dmn", this.getClass() );
 ``` 
[DMNRuntimeUtil](kie-dmn-core%2Fsrc%2Ftest%2Fjava%2Forg%2Fkie%2Fdmn%2Fcore%2Futil%2FDMNRuntimeUtil.java) has different overrides of the `createRuntime` method

### Validation
To validate a dmn model, it is necessary to:
1. get an instance of [DMNValidator](kie-dmn-validation%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Fvalidation%2FDMNValidator.java), built with some [DMNProfile](kie-dmn-core%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Fcore%2Fcompiler%2FDMNProfile.java); e.g.
    ```java
      List<DMNProfile> defaultDMNProfiles = DMNAssemblerService.getDefaultDMNProfiles(ChainedProperties.getChainedProperties(ClassLoaderUtil.findDefaultClassLoader()));
      DMNValidator validator = DMNValidatorFactory.newValidator(defaultDMNProfiles);
   ``` 
2. invoke one of the `DMNValidator` methods; e.g.
   ```java
      List<DMNMessage> validate = validator.validate(
                getFile("dmn14simple.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
   ```

## Execution
To execute a dmn model, it is necessary to:
1. get an instance of `DMNValidator`, built around the given model; e.g.
    ```java
      DMNRuntime runtime = DMNRuntimeUtil.createRuntime("simple-item-def.dmn", this.getClass() );
   ``` 
2. get the instance of [DMNModel](kie-dmn-api%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Fapi%2Fcore%2FDMNModel.java) out of the former; e.g.
    ```java
      DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn/itemdef", "simple-item-def" );
   ```
3. instantiate a [DMNContext](kie-dmn-api%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Fapi%2Fcore%2FDMNContext.java) with the required input; e.g.
    ```java
      DMNContext context = DMNFactory.newContext();
      context.set( "Monthly Salary", 1000 );
   ```
4. get the [DMNResult](kie-dmn-api%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Fapi%2Fcore%2FDMNResult.java)
   ```java
      DMNResult dmnResult = runtime.evaluateAll(dmnModel, context );
   ```
5. depending on the model, the actual result could be stored in the given context, or inside a [DMNDecisionResult](kie-dmn-api%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Fapi%2Fcore%2FDMNDecisionResult.java)

## Execution flows

### Compilation
The original xml is first parsed to [Definitions](kie-dmn-model%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Fmodel%2Fapi%2FDefinitions.java) by the [DMNMarshaller](kie-dmn-api%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Fapi%2Fmarshalling%2FDMNMarshaller.java), that is a simple xml-marshaller([DMNAssemblerService#addResourcesAfterRules](kie-dmn-core%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Fcore%2Fassembler%2FDMNAssemblerService.java)) .
Then, each `Definitions` is wrapped inside a [DMNResource](kie-dmn-core%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Fcore%2Fassembler%2FDMNResource.java) and _compiled_ by [DMNCompilerImpl](kie-dmn-core%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Fcore%2Fcompiler%2FDMNCompilerImpl.java).
Each element of the `Definitions` is further translated to [DMNNode](kie-dmn-api%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Fapi%2Fcore%2Fast%2FDMNNode.java).
Then, the text representing each `DMNNode` is further compiled by the [DecisionCompiler](kie-dmn-core%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Fcore%2Fcompiler%2FDecisionCompiler.java) and, in turn, by [FEELImpl](kie-dmn-feel%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Ffeel%2Flang%2Fimpl%2FFEELImpl.java), that would return a [ProcessedExpression](kie-dmn-feel%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Ffeel%2Fcodegen%2Ffeel11%2FProcessedExpression.java). 
Inside `ProcessedExpression`, the expression is parsed to `org.antlr.v4.runtime.tree.ParseTree` by `org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser` (auto-generated), and that `ParsedTree` is visited by [ASTBuilderVisitor](kie-dmn-feel%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Ffeel%2Fparser%2Ffeel11%2FASTBuilderVisitor.java) to get the [BaseNode](kie-dmn-feel%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Ffeel%2Flang%2Fast%2FBaseNode.java) **_ast_**, that recursively contains all the nested `BaseNode`s. Finally, the [CompiledFEELExpression](kie-dmn-feel%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Ffeel%2Fcodegen%2Ffeel11%2FCompiledFEELExpression.java) is returned (`ProcessedExpression#asCompiledFEELExpression`) , to be executed.

#### Interpreted vs Codegen
The retrieved `CompiledFEELExpression` could be a _statically-interpreted_ [InterpretedExecutableExpression](kie-dmn-feel%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Ffeel%2Flang%2Fimpl%2FInterpretedExecutableExpression.java) (that wraps the original `BaseNode` **_ast_**) or could be a `CompiledExecutableExpression`, in which case:
1. source code is generated out of the given  `BaseNode` **_ast_** (by `ASTCompilerVisitor`)
2. code is compiled in-memory to a [CompiledExecutableExpression](kie-dmn-feel%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Ffeel%2Flang%2Fimpl%2FCompiledExecutableExpression.java) (by [CompilerBytecodeLoader](kie-dmn-feel%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Ffeel%2Fcodegen%2Ffeel11%2FCompilerBytecodeLoader.java))
3. the above `CompiledFEELExpression` is wrapped and returned inside a `CompiledExecutableExpression`

### Validation
Depending on a series of flags ( `VALIDATE_SCHEMA`, `VALIDATE_MODEL`, `VALIDATE_COMPILATION`, `ANALYZE_DECISION_TABLE`), [DMNValidatorImpl](kie-dmn-validation%2Fsrc%2Fmain%2Fjava%2Forg%2Fkie%2Fdmn%2Fvalidation%2FDMNValidatorImpl.java) executes the validation of the given model. 

Behind the scenes, the validation also uses the _rule engine_ and the _rules_ defined in the different `kie-dmn/kie-dmn-validation/src/main/resources/org/kie/dmn/validation/DMNv1()/*.drl` files.
This validation is fired inside `private List<DMNMessage> validateModel(DMNResource mainModel, List<DMNResource> otherModels)`

### Execution


















