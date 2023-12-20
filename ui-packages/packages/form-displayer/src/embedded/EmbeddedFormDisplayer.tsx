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
import React, { useCallback } from 'react';
import {
  FormDisplayerApi,
  FormDisplayerChannelApi,
  FormDisplayerEnvelopeApi
} from '../api';
import {
  Form,
  FormDisplayerInitArgs,
  FormOpened
} from '@kogito-apps/components-common/dist/types';
import { ContainerType } from '@kie-tools-core/envelope/dist/api';
import { EnvelopeServer } from '@kie-tools-core/envelope-bus/dist/channel';
import {
  EmbeddedEnvelopeProps,
  RefForwardingEmbeddedEnvelope
} from '@kie-tools-core/envelope/dist/embedded';

export type Props = {
  targetOrigin: string;
  formContent: Form;
  data?: any;
  context?: Record<string, any>;
  envelopePath: string;
  onOpenForm?: (opened: FormOpened) => void;
};

export const EmbeddedFormDisplayer = React.forwardRef(
  (props: Props, forwardedRef: React.Ref<FormDisplayerApi>) => {
    const refDelegate = useCallback(
      (
        envelopeServer: EnvelopeServer<
          FormDisplayerChannelApi,
          FormDisplayerEnvelopeApi
        >
      ): FormDisplayerApi => ({
        startSubmit: (context) => {
          return envelopeServer.envelopeApi.requests.formDisplayer__startSubmit(
            context
          );
        },
        notifySubmitResult: (response) => {
          envelopeServer.envelopeApi.notifications.formDisplayer__notifySubmitResponse.send(
            response
          );
        },
        init: (args: FormDisplayerInitArgs) => {
          envelopeServer.envelopeApi.notifications.formDisplayer__notifyInit.send(
            args
          );
        }
      }),
      []
    );

    const pollInit = useCallback(
      (
        // eslint-disable-next-line
        envelopeServer: EnvelopeServer<
          FormDisplayerChannelApi,
          FormDisplayerEnvelopeApi
        >
      ) => {
        return envelopeServer.envelopeApi.requests.formDisplayer__init(
          {
            origin: envelopeServer.origin,
            envelopeServerId: envelopeServer.id
          },
          {
            form: props.formContent,
            data: props.data ?? {},
            context: props.context ?? {}
          }
        );
      },
      []
    );

    return (
      <EmbeddedFormDisplayerEnvelope
        ref={forwardedRef}
        apiImpl={{
          notifyOnOpenForm: (opened) => {
            if (props.onOpenForm) {
              props.onOpenForm(opened);
            }
          }
        }}
        origin={props.targetOrigin}
        refDelegate={refDelegate}
        pollInit={pollInit}
        config={{
          containerType: ContainerType.IFRAME,
          envelopePath: props.envelopePath
        }}
      />
    );
  }
);

const EmbeddedFormDisplayerEnvelope = React.forwardRef<
  FormDisplayerApi,
  EmbeddedEnvelopeProps<
    FormDisplayerChannelApi,
    FormDisplayerEnvelopeApi,
    FormDisplayerApi
  >
>(RefForwardingEmbeddedEnvelope);
