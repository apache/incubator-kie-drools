/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.models.drools.provider;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dmg.pmml.Field;
import org.dmg.pmml.Model;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.ast.factories.KiePMMLDataDictionaryASTFactory;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModelWithSources;
import org.kie.pmml.models.drools.dto.DroolsCompilationDTO;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.util.StringUtils.getPkgUUID;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.getBaseDescr;

/**
 * Abstract <code>ModelImplementationProvider</code> for <b>KiePMMLDroolsModel</b>s
 */
public abstract class DroolsModelProvider<T extends Model, E extends KiePMMLDroolsModel> implements ModelImplementationProvider<T, E> {

    private static final Logger logger = LoggerFactory.getLogger(DroolsModelProvider.class.getName());

    @Override
    public Map<String, String> getSourcesMap(final CompilationDTO<T> compilationDTO) {
        throw new KiePMMLException("DroolsModelProvider.getSourcesMap is not meant to be invoked");
    }

    @Override
    public KiePMMLDroolsModelWithSources getKiePMMLModelWithSources(final CompilationDTO<T> compilationDTO) {
        logger.trace("getKiePMMLModelWithSources {} {} {}", compilationDTO.getPackageName(),
                     compilationDTO.getFields(), compilationDTO.getModel());
        try {
            final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
            KiePMMLDroolsAST kiePMMLDroolsAST = getKiePMMLDroolsASTCommon(compilationDTO.getFields(),
                                                                          compilationDTO.getModel(), fieldTypeMap);
            final DroolsCompilationDTO<T> droolsCompilationDTO =
                    DroolsCompilationDTO.fromCompilationDTO(compilationDTO, fieldTypeMap);
            Map<String, String> sourcesMap = getKiePMMLDroolsModelSourcesMap(droolsCompilationDTO);
            PackageDescr packageDescr = getPackageDescr(kiePMMLDroolsAST, compilationDTO.getPackageName());
            String pkgUUID = getPkgUUID("gav", compilationDTO.getPackageName());
            packageDescr.setPreferredPkgUUID(pkgUUID);
            return new KiePMMLDroolsModelWithSources(compilationDTO.getFileName(),
                                                     compilationDTO.getModelName(),
                                                     compilationDTO.getPackageName(),
                                                     compilationDTO.getKieMiningFields(),
                                                     compilationDTO.getKieOutputFields(),
                                                     compilationDTO.getKieTargetFields(),
                                                     sourcesMap,
                                                     pkgUUID,
                                                     packageDescr);
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    public PackageDescr getPackageDescr(final KiePMMLDroolsAST kiePMMLDroolsAST, final String packageName) {
        return getBaseDescr(kiePMMLDroolsAST, packageName);
    }

    /**
     * @param fields Should contain all fields retrieved from model, i.e. DataFields from DataDictionary,
     * DerivedFields from Transformations/LocalTransformations, OutputFields
     * @param model
     * @param fieldTypeMap
     * @param types
     * @return
     */
    public abstract KiePMMLDroolsAST getKiePMMLDroolsAST(final List<Field<?>> fields,
                                                         final T model,
                                                         final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                         final List<KiePMMLDroolsType> types);

    /**
     * @param compilationDTO
     * @return
     * @throws IOException
     */
    public abstract Map<String, String> getKiePMMLDroolsModelSourcesMap(final DroolsCompilationDTO<T> compilationDTO) throws IOException;

    /**
     * @param fields       Should contain all fields retrieved from model, i.e. DataFields from DataDictionary,
     *                     DerivedFields from Transformations/LocalTransformations, OutputFields
     * @param model
     * @param fieldTypeMap
     * @return
     */
    protected KiePMMLDroolsAST getKiePMMLDroolsASTCommon(final List<Field<?>> fields,
                                                         final T model,
                                                         final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        List<KiePMMLDroolsType> types = fieldTypeMap.values()
                .stream().map(kiePMMLOriginalTypeGeneratedType -> {
                    String type =
                            DATA_TYPE.byName(kiePMMLOriginalTypeGeneratedType.getOriginalType()).getMappedClass().getSimpleName();
                    return new KiePMMLDroolsType(kiePMMLOriginalTypeGeneratedType.getGeneratedType(), type);
                })
                .collect(Collectors.toList());
        types.addAll(KiePMMLDataDictionaryASTFactory.factory(fieldTypeMap).declareTypes(fields));
        return getKiePMMLDroolsAST(fields, model, fieldTypeMap, types);
    }
}
