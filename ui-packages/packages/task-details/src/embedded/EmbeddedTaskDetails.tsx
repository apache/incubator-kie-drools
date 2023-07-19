/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import React, { useCallback } from 'react';
import {
  TaskDetailsApi,
  TaskDetailsChannelApi,
  TaskDetailsEnvelopeApi
} from '../api';
import { ContainerType } from '@kie-tools-core/envelope/dist/api';
import { EnvelopeServer } from '@kie-tools-core/envelope-bus/dist/channel';
import {
  EmbeddedEnvelopeProps,
  RefForwardingEmbeddedEnvelope
} from '@kie-tools-core/envelope/dist/embedded';
import { EnvelopeBusMessage } from '@kie-tools-core/envelope-bus/dist/api';
import { UserTaskInstance } from '@kogito-apps/task-console-shared';
import { init } from '../envelope';

export type Props = {
  targetOrigin: string;
  userTask: UserTaskInstance;
};

export const EmbeddedTaskDetails = React.forwardRef(
  (props: Props, forwardedRef: React.Ref<TaskDetailsApi>) => {
    const refDelegate = useCallback(
      (
        envelopeServer: EnvelopeServer<
          TaskDetailsChannelApi,
          TaskDetailsEnvelopeApi
        >
      ): TaskDetailsApi => ({}),
      []
    );
    const pollInit = useCallback(
      (
        // eslint-disable-next-line
        envelopeServer: EnvelopeServer<
          TaskDetailsChannelApi,
          TaskDetailsEnvelopeApi
        >,
        container: () => HTMLDivElement
      ) => {
        init({
          config: {
            containerType: ContainerType.DIV,
            envelopeId: envelopeServer.id
          },
          container: container(),
          bus: {
            postMessage<D, Type>(
              message: EnvelopeBusMessage<D, Type>,
              targetOrigin?: string,
              transfer?: any
            ) {
              window.parent.postMessage(message, targetOrigin, transfer);
            }
          }
        });
        return envelopeServer.envelopeApi.requests.taskDetails__init(
          {
            origin: envelopeServer.origin,
            envelopeServerId: envelopeServer.id
          },
          {
            task: props.userTask
          }
        );
      },
      [props.userTask]
    );

    return (
      <EmbeddedTaskDetailsEnvelope
        ref={forwardedRef}
        apiImpl={props}
        origin={props.targetOrigin}
        refDelegate={refDelegate}
        pollInit={pollInit}
        config={{ containerType: ContainerType.DIV }}
      />
    );
  }
);

const EmbeddedTaskDetailsEnvelope = React.forwardRef<
  TaskDetailsApi,
  EmbeddedEnvelopeProps<
    TaskDetailsChannelApi,
    TaskDetailsEnvelopeApi,
    TaskDetailsApi
  >
>(RefForwardingEmbeddedEnvelope);
