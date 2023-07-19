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
import { EnvelopeServer } from '@kie-tools-core/envelope-bus/dist/channel';
import {
  EmbeddedEnvelopeProps,
  RefForwardingEmbeddedEnvelope
} from '@kie-tools-core/envelope/dist/embedded';
import {
  TaskInboxApi,
  TaskInboxChannelApi,
  TaskInboxEnvelopeApi,
  TaskInboxDriver,
  TaskInboxState
} from '../api';
import { TaskInboxChannelApiImpl } from './TaskInboxChannelApiImpl';
import { ContainerType } from '@kie-tools-core/envelope/dist/api';
import { init } from '../envelope';

export interface Props {
  targetOrigin: string;
  initialState?: TaskInboxState;
  driver: TaskInboxDriver;
  allTaskStates?: string[];
  activeTaskStates?: string[];
}

export const EmbeddedTaskInbox = React.forwardRef(
  (props: Props, forwardedRef: React.Ref<TaskInboxApi>) => {
    const refDelegate = useCallback(
      (
        envelopeServer: EnvelopeServer<
          TaskInboxChannelApi,
          TaskInboxEnvelopeApi
        >
      ): TaskInboxApi => ({
        taskInbox__notify: (userName) =>
          envelopeServer.envelopeApi.requests.taskInbox__notify(userName)
      }),
      []
    );
    const pollInit = useCallback(
      (
        envelopeServer: EnvelopeServer<
          TaskInboxChannelApi,
          TaskInboxEnvelopeApi
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
            postMessage(message, targetOrigin, transfer) {
              window.postMessage(message, targetOrigin, transfer);
            }
          }
        });
        return envelopeServer.envelopeApi.requests.taskInbox__init(
          {
            origin: envelopeServer.origin,
            envelopeServerId: envelopeServer.id
          },
          {
            initialState: props.initialState,
            allTaskStates: props.allTaskStates,
            activeTaskStates: props.activeTaskStates
          }
        );
      },
      [props.allTaskStates, props.activeTaskStates]
    );

    return (
      <EmbeddedTaskInboxEnvelope
        ref={forwardedRef}
        apiImpl={new TaskInboxChannelApiImpl(props.driver)}
        origin={props.targetOrigin}
        refDelegate={refDelegate}
        pollInit={pollInit}
        config={{ containerType: ContainerType.DIV }}
      />
    );
  }
);

const EmbeddedTaskInboxEnvelope = React.forwardRef<
  TaskInboxApi,
  EmbeddedEnvelopeProps<TaskInboxChannelApi, TaskInboxEnvelopeApi, TaskInboxApi>
>(RefForwardingEmbeddedEnvelope);
