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
import React, { useEffect } from 'react';
import { ModelData } from '../../../types';
import { StandaloneEditorApi } from '@kie-tools/kie-editors-standalone/dist/common/Editor';
import * as DmnEditor from '@kie-tools/kie-editors-standalone/dist/dmn';
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  Title
} from '@patternfly/react-core';
import { CubesIcon } from '@patternfly/react-icons';

const DMN1_2: string = 'http://www.omg.org/spec/DMN/20151101/dmn.xsd';
const DMN1_3: string = 'http://www.omg.org/spec/DMN/20180521/MODEL/';

type ModelDiagramProps = {
  model: ModelData;
};

const ModelDiagram = (props: ModelDiagramProps) => {
  const { model } = props;
  const dmnVersion: string = model.dmnVersion;

  useEffect(() => {
    let editor: StandaloneEditorApi | undefined = undefined;
    if (dmnVersion === DMN1_2 || dmnVersion === DMN1_3) {
      editor = DmnEditor.open({
        container: document.getElementById('dmn-editor-container'),
        initialContent: Promise.resolve(model.model),
        readOnly: true,
        origin: '*'
      });
      return () => {
        editor.close();
      };
    }
  }, [model]);

  return dmnVersion === DMN1_2 || dmnVersion === DMN1_3
    ? makeDMNEditor()
    : DEFAULT;
};

function makeUnknownModel(): JSX.Element {
  return (
    <EmptyState>
      <EmptyStateIcon icon={CubesIcon} />
      <Title headingLevel="h4" size="lg">
        Unsupported model type
      </Title>
      <EmptyStateBody>
        The type of model is unsupported and cannot be rendered.
      </EmptyStateBody>
    </EmptyState>
  );
}

function makeDMNEditor(): JSX.Element {
  return <div id="dmn-editor-container" style={{ height: '100%' }} />;
}

const DEFAULT: JSX.Element = makeUnknownModel();

export default ModelDiagram;
