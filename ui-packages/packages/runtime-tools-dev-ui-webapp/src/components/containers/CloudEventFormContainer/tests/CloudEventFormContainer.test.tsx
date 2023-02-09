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
import { mount } from 'enzyme';
import CloudEventFormContainer, {
  CloudEventFormContainerParams
} from '../CloudEventFormContainer';
import { CloudEventFormGatewayApiImpl } from '../../../../channel/CloudEventForm/CloudEventFormGatewayApi';
import * as CloudEventFormContext from '../../../../channel/CloudEventForm/CloudEventFormContext';
import { EmbeddedCloudEventForm } from '@kogito-apps/cloud-event-form/dist/embedded';

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

jest.mock('@kogito-apps/cloud-event-form/dist/embedded', () =>
  Object.assign(
    {},
    jest.requireActual('@kogito-apps/cloud-event-form/dist/embedded'),
    {
      EmbeddedCloudEventForm: () => {
        return <MockedComponent />;
      }
    }
  )
);

const properties = {
  isTriggerNewInstance: false,
  onSuccess: jest.fn(),
  onError: jest.fn()
};

describe('CloudEventFormContainer tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    delete routerParams.instanceId;
  });

  it('Snapshot', () => {
    const wrapper = mount(<CloudEventFormContainer {...properties} />).find(
      CloudEventFormContainer
    );

    expect(wrapper).toMatchSnapshot();

    const embeddedForm = wrapper.find(EmbeddedCloudEventForm);
    expect(embeddedForm.props().isNewInstanceEvent).toBeFalsy();
    expect(embeddedForm.props().driver).not.toBeUndefined();
    expect(
      embeddedForm.props().defaultValues.cloudEventSource
    ).not.toBeUndefined();
    expect(embeddedForm.props().defaultValues.instanceId).toBeUndefined();
  });

  it('Snapshot - with router param', () => {
    routerParams.instanceId = '1234';

    const wrapper = mount(<CloudEventFormContainer {...properties} />).find(
      CloudEventFormContainer
    );

    expect(wrapper).toMatchSnapshot();

    const embeddedForm = wrapper.find(EmbeddedCloudEventForm);
    expect(embeddedForm.props().isNewInstanceEvent).toBeFalsy();
    expect(embeddedForm.props().driver).not.toBeUndefined();
    expect(
      embeddedForm.props().defaultValues.cloudEventSource
    ).not.toBeUndefined();
    expect(embeddedForm.props().defaultValues.instanceId).toBe('1234');
  });
});
