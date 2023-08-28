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
import { render } from '@testing-library/react';
import CloudEventFieldLabelIcon from '../CloudEventFieldLabelIcon';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-icons/dist/esm/icons/help-icon', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    HelpIcon: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock('@patternfly/react-core/dist/js/components/Popover', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    Popover: () => {
      return <MockedComponent />;
    }
  })
);

describe('CloudEventFieldLabelIcon tests', () => {
  it('default snapshot test', () => {
    const container = render(
      <CloudEventFieldLabelIcon
        fieldId="endpoint"
        helpMessage="Sets the endpoint and method where the CloudEvent should be triggered."
      />
    ).container;

    expect(container).toMatchSnapshot();
  });

  it('default snapshot test - with header', () => {
    const container = render(
      <CloudEventFieldLabelIcon
        fieldId="eventType"
        helpMessage="Sets the type of the cloud event."
        cloudEventHeader="type"
      />
    ).container;

    expect(container).toMatchSnapshot();
  });
});
