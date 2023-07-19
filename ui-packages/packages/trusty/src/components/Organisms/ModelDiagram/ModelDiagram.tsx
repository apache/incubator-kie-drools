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
