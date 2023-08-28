/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import { render, screen } from '@testing-library/react';
import CloudEventFormContainer, {
  CloudEventFormContainerParams
} from '../CloudEventFormContainer';
import { CloudEventFormGatewayApiImpl } from '../../../../channel/CloudEventForm/CloudEventFormGatewayApi';
import * as CloudEventFormContext from '../../../../channel/CloudEventForm/CloudEventFormContext';
import { EmbeddedCloudEventForm } from '@kogito-apps/cloud-event-form/dist/embedded';
import * as DevUIAppContext from '../../../contexts/DevUIAppContext';
import DevUIAppContextProvider from '../../../contexts/DevUIAppContextProvider';
import {
  DefaultUser,
  User
} from '@kogito-apps/consoles-common/dist/environment/auth';

const routerParams: CloudEventFormContainerParams = {};

jest
  .spyOn(CloudEventFormContext, 'useCloudEventFormGatewayApi')
  .mockImplementation(
    () => new CloudEventFormGatewayApiImpl('http://localhost:9000')
  );

jest.mock('react-router', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    ...jest.requireActual('react-router'),
    useParams: () => routerParams
  })
);

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

const properties = {
  isTriggerNewInstance: false,
  onSuccess: jest.fn(),
  onError: jest.fn()
};
const user: User = new DefaultUser('jon', []);
const appContextProps = {
  devUIUrl: 'http://localhost:9000',
  openApiPath: '/mocked',
  isProcessEnabled: false,
  isTracingEnabled: false,
  omittedProcessTimelineEvents: [],
  isStunnerEnabled: false,
  availablePages: [],
  customLabels: {
    singularProcessLabel: 'test-singular',
    pluralProcessLabel: 'test-plural'
  },
  diagramPreviewSize: { width: 100, height: 100 }
};

describe('CloudEventFormContainer tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    delete routerParams.instanceId;
  });

  it('Snapshot', () => {
    const { container } = render(
      <DevUIAppContextProvider users={[user]} {...appContextProps}>
        <CloudEventFormContainer {...properties} />
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    const checkDiv = container.querySelector('div');
    expect(checkDiv).toBeTruthy();
  });

  it('Snapshot - with router param', () => {
    routerParams.instanceId = '1234';

    const { container } = render(
      <DevUIAppContextProvider users={[user]} {...appContextProps}>
        <CloudEventFormContainer {...properties} />
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    const checkDiv = container.querySelector('div');
    expect(checkDiv).toBeTruthy();
  });
});
