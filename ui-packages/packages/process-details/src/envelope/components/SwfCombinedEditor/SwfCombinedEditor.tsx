/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useCallback, useMemo, useState } from 'react';
import {
  EmbeddedEditor,
  EmbeddedEditorChannelApiImpl
} from '@kie-tools-core/editor/dist/embedded';
import {
  ChannelType,
  EditorEnvelopeLocator,
  EnvelopeMapping
} from '@kie-tools-core/editor/dist/api';
import { Title, Card, CardHeader, CardBody } from '@patternfly/react-core';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import {
  EmbeddedEditorFile,
  StateControl
} from '@kie-tools-core/editor/dist/channel';
import {
  SwfCombinedEditorChannelApiImpl,
  SwfFeatureToggleChannelApiImpl,
  SwfPreviewOptionsChannelApiImpl
} from '@kie-tools/serverless-workflow-combined-editor/dist/impl';

interface ISwfCombinedEditorProps {
  sourceString: string;
  isStunnerEnabled: boolean;
  width?: number;
  height?: number;
}

const SwfCombinedEditor: React.FC<ISwfCombinedEditorProps & OUIAProps> = ({
  sourceString,
  isStunnerEnabled,
  width,
  height,
  ouiaId,
  ouiaSafe
}) => {
  const [isReady, setReady] = useState<boolean>(false);
  const stateControl = new StateControl();

  const getFileContent = useCallback(() => {
    const arr = new Uint8Array(sourceString.length);
    for (let i = 0; i < sourceString.length; i++) {
      arr[i] = sourceString.charCodeAt(i);
    }
    const decoder = new TextDecoder('utf-8');
    return decoder.decode(arr);
  }, [sourceString]);

  const getFileType = useCallback(() => {
    const source = getFileContent();
    if (source.trim().charAt(0) === '{') {
      return 'json';
    } else {
      return 'yaml';
    }
  }, [sourceString]);

  const embeddedFile: EmbeddedEditorFile = useMemo(() => {
    return {
      getFileContents: async () => Promise.resolve(getFileContent()),
      isReadOnly: true,
      fileExtension: `sw.${getFileType()}`,
      fileName: `*.sw.${getFileType()}`
    };
  }, [sourceString]);

  const editorEnvelopeLocator = useMemo(
    () =>
      new EditorEnvelopeLocator(window.location.origin, [
        new EnvelopeMapping({
          type: 'swf',
          filePathGlob: '**/*.sw.+(json|yml|yaml)',
          // look for the resources in the same path as the swf-diagram html
          resourcesPathPrefix: '.',
          envelopePath:
            'resources/serverless-workflow-combined-editor-envelope.html'
        })
      ]),
    [sourceString]
  );

  const channelApiImpl = useMemo(
    () =>
      new EmbeddedEditorChannelApiImpl(stateControl, embeddedFile, 'en', {
        kogitoEditor_ready: () => {
          setReady(true);
        }
      }),
    [stateControl, embeddedFile]
  );

  const swfFeatureToggleChannelApiImpl = useMemo(
    () =>
      new SwfFeatureToggleChannelApiImpl({
        stunnerEnabled: isStunnerEnabled
      }),
    [isStunnerEnabled]
  );

  const swfPreviewOptionsChannelApiImpl = useMemo(
    () =>
      new SwfPreviewOptionsChannelApiImpl({
        diagramDefaultWidth: '100%'
      }),
    []
  );

  const apiImpl = useMemo(
    () =>
      new SwfCombinedEditorChannelApiImpl(
        channelApiImpl,
        swfFeatureToggleChannelApiImpl,
        null,
        null,
        swfPreviewOptionsChannelApiImpl
      ),
    [
      channelApiImpl,
      swfFeatureToggleChannelApiImpl,
      swfPreviewOptionsChannelApiImpl
    ]
  );

  return (
    <Card
      style={{ height: height, width: width }}
      {...componentOuiaProps(ouiaId, 'swf-diagram', ouiaSafe)}
    >
      <CardHeader>
        <Title headingLevel="h3" size="xl">
          Serverless Workflow Diagram
        </Title>
      </CardHeader>
      <CardBody>
        <EmbeddedEditor
          customChannelApiImpl={apiImpl}
          isReady={isReady}
          file={embeddedFile}
          channelType={ChannelType.ONLINE_MULTI_FILE}
          editorEnvelopeLocator={editorEnvelopeLocator}
          locale={'en'}
          stateControl={stateControl}
        />
      </CardBody>
    </Card>
  );
};

export default SwfCombinedEditor;
