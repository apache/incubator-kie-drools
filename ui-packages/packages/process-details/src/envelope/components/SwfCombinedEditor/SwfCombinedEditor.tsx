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

import React, { useCallback, useEffect, useMemo, useState } from 'react';
import {
  EmbeddedEditor,
  EmbeddedEditorChannelApiImpl,
  EmbeddedEditorRef
} from '@kie-tools-core/editor/dist/embedded';
import {
  ChannelType,
  EditorEnvelopeLocator,
  EnvelopeContentType,
  EnvelopeMapping
} from '@kie-tools-core/editor/dist/api';
import {
  Card,
  CardHeader,
  CardBody
} from '@patternfly/react-core/dist/js/components/Card';
import { Title } from '@patternfly/react-core/dist/js/components/Title';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import {
  EmbeddedEditorFile,
  StateControl
} from '@kie-tools-core/editor/dist/channel';
import {
  SwfCombinedEditorChannelApiImpl,
  SwfFeatureToggleChannelApiImpl,
  SwfPreviewOptionsChannelApiImpl
} from '@kie-tools/serverless-workflow-combined-editor/dist/impl';
import { useController } from '../../../hooks/useController';
import { ProcessInstance } from '@kogito-apps/management-console-shared/dist/types';
import { MessageBusClientApi } from '@kie-tools-core/envelope-bus/dist/api';
import { ServerlessWorkflowCombinedEditorChannelApi } from '@kie-tools/serverless-workflow-combined-editor/dist/api';
import { ServerlessWorkflowCombinedEditorEnvelopeApi } from '@kie-tools/serverless-workflow-combined-editor/dist/api/ServerlessWorkflowCombinedEditorEnvelopeApi';
interface ISwfCombinedEditorProps {
  workflowInstance: Pick<ProcessInstance, 'source' | 'nodes' | 'error'>;
  isStunnerEnabled: boolean;
  width?: number;
  height?: number;
}

const SwfCombinedEditor: React.FC<ISwfCombinedEditorProps & OUIAProps> = ({
  workflowInstance,
  isStunnerEnabled,
  width,
  height,
  ouiaId,
  ouiaSafe
}) => {
  const { source, nodes, error } = workflowInstance;
  const [editor, editorRef] = useController<EmbeddedEditorRef>();
  const [isReady, setReady] = useState<boolean>(false);

  const getFileContent = useCallback(() => {
    const arr = new Uint8Array(source.length);
    for (let i = 0; i < source.length; i++) {
      arr[i] = source.charCodeAt(i);
    }
    const decoder = new TextDecoder('utf-8');
    return decoder.decode(arr);
  }, [source]);

  const getFileType = useCallback(() => {
    const source = getFileContent();
    if (source.trim().charAt(0) === '{') {
      return 'json';
    } else {
      return 'yaml';
    }
  }, [source]);

  const embeddedFile: EmbeddedEditorFile = useMemo(() => {
    return {
      getFileContents: async () => Promise.resolve(getFileContent()),
      isReadOnly: true,
      fileExtension: `sw.${getFileType()}`,
      fileName: `*.sw.${getFileType()}`
    };
  }, [source]);

  const stateControl = useMemo(
    () => new StateControl(),
    [embeddedFile?.getFileContents]
  );

  const editorEnvelopeLocator = useMemo(
    () =>
      new EditorEnvelopeLocator(window.location.origin, [
        new EnvelopeMapping({
          type: 'swf',
          filePathGlob: '**/*.sw.+(json|yml|yaml)',
          // look for the resources in the same path as the swf-diagram html
          resourcesPathPrefix: '.',
          envelopeContent: {
            type: EnvelopeContentType.PATH,
            path: 'resources/serverless-workflow-combined-editor-envelope.html'
          }
        })
      ]),
    [source]
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
        defaultWidth: '100%'
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
        swfPreviewOptionsChannelApiImpl,
        null
      ),
    [
      channelApiImpl,
      swfFeatureToggleChannelApiImpl,
      swfPreviewOptionsChannelApiImpl
    ]
  );

  useEffect(() => {
    const combinedEditorChannelApi = embeddedFile
      ? (editor?.getEnvelopeServer()
          .envelopeApi as unknown as MessageBusClientApi<ServerlessWorkflowCombinedEditorChannelApi>)
      : undefined;
    const combinedEditorEnvelopeApi = embeddedFile
      ? (editor?.getEnvelopeServer()
          .envelopeApi as unknown as MessageBusClientApi<ServerlessWorkflowCombinedEditorEnvelopeApi>)
      : undefined;

    const nodeNames = [];

    nodes.forEach((node) => {
      nodeNames.push(node.name);
    });

    const colorConnectedEnds = nodeNames.includes('End');
    const isStartNodeAvailable = nodeNames.includes('Start');

    if (!isStartNodeAvailable) {
      nodeNames.push('Start');
    }
    if (combinedEditorEnvelopeApi && combinedEditorChannelApi) {
      let errorNode = null;
      if (error) {
        errorNode = nodes.filter(
          (node) => node.nodeId === error.nodeDefinitionId
        )[0];
        combinedEditorChannelApi.notifications.kogitoSwfCombinedEditor_combinedEditorReady.subscribe(
          () => {
            combinedEditorEnvelopeApi.notifications.kogitoSwfCombinedEditor_colorNodes.send(
              {
                nodeNames: [errorNode.name],
                color: '#f4d5d5',
                colorConnectedEnds
              }
            );
          }
        );
      }
      const successNodes = errorNode
        ? nodeNames.filter((nodeName) => nodeName !== errorNode.name)
        : nodeNames;
      combinedEditorChannelApi.notifications.kogitoSwfCombinedEditor_combinedEditorReady.subscribe(
        () => {
          combinedEditorEnvelopeApi.notifications.kogitoSwfCombinedEditor_colorNodes.send(
            {
              nodeNames: successNodes,
              color: '#d5f4e6',
              colorConnectedEnds
            }
          );
        }
      );
    }
  }, [editor, nodes, embeddedFile]);

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
          ref={editorRef}
        />
      </CardBody>
    </Card>
  );
};

export default SwfCombinedEditor;
