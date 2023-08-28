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

import React from 'react';
import { render } from '@testing-library/react';
import FormDetailsContainer from '../FormDetailsContainer';
import * as FormDetailsContext from '../../../../channel/FormDetails/FormDetailsContext';
import { FormDetailsGatewayApiImpl } from '../../../../channel/FormDetails/FormDetailsGatewayApi';
import DevUIAppContextProvider from '../../../contexts/DevUIAppContextProvider';
import {
  DefaultUser,
  User
} from '@kogito-apps/consoles-common/dist/environment/auth';
import { EmbeddedFormDetails } from '@kogito-apps/form-details';
import { FormType } from '@kogito-apps/forms-list';

jest
  .spyOn(FormDetailsContext, 'useFormDetailsGatewayApi')
  .mockImplementation(() => new FormDetailsGatewayApiImpl());
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

describe('FormDetailsContainer tests', () => {
  it('Snapshot', () => {
    const { container } = render(
      <DevUIAppContextProvider users={[user]} {...appContextProps}>
        <FormDetailsContainer
          formData={{
            name: 'form1',
            type: FormType.HTML,
            lastModified: new Date('2021-08-23T13:26:02.13Z')
          }}
          onSuccess={jest.fn}
          onError={jest.fn}
        />
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    expect(container.querySelector('div')).toBeTruthy();
  });
});
