import { ChannelType } from '@kogito-tooling/core-api';
import {
  EditorType,
  EmbeddedEditorRouter,
  EmbeddedViewer,
  File
} from '@kogito-tooling/embedded-editor';
import { GwtEditorRoutes } from '@kogito-tooling/kie-bc-editors';
import React, { useMemo } from 'react';
import { ModelData } from '../../../types';

const DMN1_2: string = 'http://www.omg.org/spec/DMN/20151101/dmn.xsd';

type ModelDiagramProps = {
  model: ModelData;
};

const ModelDiagram = (props: ModelDiagramProps) => {
  const { model } = props;
  const type: string = model.type;

  const router: EmbeddedEditorRouter = useMemo(
    () =>
      new EmbeddedEditorRouter(
        new GwtEditorRoutes({
          dmnPath: 'gwt-editors/dmn',
          bpmnPath: 'gwt-editors/bpmn',
          scesimPath: 'gwt-editors/scesim'
        })
      ),
    []
  );

  if (type === DMN1_2) {
    return makeDMNEditor(model, router);
  }

  return DEFAULT;
};

function makeUnknownModel(): JSX.Element {
  return <div>Unknown model type</div>;
}

function makeDMNEditor(
  model: ModelData,
  router: EmbeddedEditorRouter
): JSX.Element {
  const file: File = {
    fileName: model.name,
    editorType: EditorType.DMN,
    getFileContents: () => Promise.resolve(model.model),
    isReadOnly: true
  };

  return (
    <EmbeddedViewer
      file={file}
      router={router}
      channelType={ChannelType.EMBEDDED}
      envelopeUri={'/envelope/envelope.html'}
    />
  );
}

const DEFAULT: JSX.Element = makeUnknownModel();

export default ModelDiagram;
