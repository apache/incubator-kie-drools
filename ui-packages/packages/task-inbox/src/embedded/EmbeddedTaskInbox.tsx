/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import React, { useCallback, useMemo } from 'react';
import { EnvelopeServer } from '@kogito-tooling/envelope-bus/dist/channel';
import { EmbeddedEnvelopeFactory } from '@kogito-tooling/envelope/dist/embedded';
import {
  TaskInboxApi,
  TaskInboxChannelApi,
  TaskInboxEnvelopeApi,
  TaskInboxDriver,
  TaskInboxState
} from '../api';
import { TaskInboxChannelApiImpl } from './TaskInboxChannelApiImpl';

export interface Props {
  targetOrigin: string;
  envelopePath: string;
  initialState?: TaskInboxState;
  driver: TaskInboxDriver;
  allTaskStates?: string[];
  activeTaskStates?: string[];
}

export const EmbeddedTaskInbox = React.forwardRef<TaskInboxApi, Props>(
  (props, forwardedRef) => {
    const pollInit = useCallback(
      (
        envelopeServer: EnvelopeServer<
          TaskInboxChannelApi,
          TaskInboxEnvelopeApi
        >
      ) => {
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

    const refDelegate = useCallback(
      (
        envelopeServer: EnvelopeServer<
          TaskInboxChannelApi,
          TaskInboxEnvelopeApi
        >
      ): TaskInboxApi => ({}),
      []
    );

    const EmbeddedEnvelope = useMemo(() => {
      return EmbeddedEnvelopeFactory({
        api: new TaskInboxChannelApiImpl(props.driver),
        envelopePath: props.envelopePath,
        origin: props.targetOrigin,
        refDelegate,
        pollInit
      });
    }, []);

    return <EmbeddedEnvelope ref={forwardedRef} />;
  }
);
