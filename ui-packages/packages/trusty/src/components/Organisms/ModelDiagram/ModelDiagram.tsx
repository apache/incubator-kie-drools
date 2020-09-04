import { ChannelType } from '@kogito-tooling/channel-common-api';
import { EditorEnvelopeLocator } from '@kogito-tooling/editor/dist/api';
import { EmbeddedViewer, File } from '@kogito-tooling/editor/dist/embedded';
import React from 'react';
import { ModelData } from '../../../types';

const DMN1_2: string = 'http://www.omg.org/spec/DMN/20151101/dmn.xsd';

type ModelDiagramProps = {
  model: ModelData;
};

const ModelDiagram = (props: ModelDiagramProps) => {
  const { model } = props;
  const type: string = model.type;

  const editorEnvelopeLocator: EditorEnvelopeLocator = {
    targetOrigin: window.location.origin,
    mapping: new Map([
      [
        'dmn',
        {
          resourcesPathPrefix: '../gwt-editors/dmn',
          envelopePath: '/envelope/envelope.html'
        }
      ]
    ])
  };

  if (type === DMN1_2) {
    return makeDMNEditor(model, editorEnvelopeLocator);
  }

  return DEFAULT;
};

function makeUnknownModel(): JSX.Element {
  return <div>Unknown model type</div>;
}

function makeDMNEditor(
  model: ModelData,
  editorEnvelopeLocator: EditorEnvelopeLocator
): JSX.Element {
  const file: File = {
    fileName: model.name,
    fileExtension: 'dmn',
    getFileContents: () => Promise.resolve(model.model),
    isReadOnly: true
  };

  return (
    <EmbeddedViewer
      file={file}
      editorEnvelopeLocator={editorEnvelopeLocator}
      channelType={ChannelType.EMBEDDED}
      locale={window.navigator.language}
    />
  );
}

const DEFAULT: JSX.Element = makeUnknownModel();

export default ModelDiagram;
