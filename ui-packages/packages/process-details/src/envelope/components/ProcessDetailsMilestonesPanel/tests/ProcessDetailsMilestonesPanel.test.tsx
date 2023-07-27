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
import ProcessDetailsMilestonesPanel from '../ProcessDetailsMilestonesPanel';
import { mount } from 'enzyme';
import { MilestoneStatus } from '@kogito-apps/management-console-shared/dist/types';
// tslint:disable: no-string-literal

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-icons/dist/js/icons/info-circle-icon', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    InfoCircleIcon: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock('@patternfly/react-core/dist/js/components/Tooltip', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-core'), {
    Tooltip: (props) => {
      return <>{props.children} </>;
    }
  })
);
describe('Process details page milestones panel', () => {
  const props = {
    milestones: [
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75m36',
        name: 'Milestone 1: Order placed',
        status: MilestoneStatus['Active'],
        __typename: 'Milestones'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75m66',
        name: 'Milestone 2: Order shipped',
        status: MilestoneStatus['Available'],
        __typename: 'Milestones'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75i86',
        name: 'Manager decision',
        status: MilestoneStatus['Completed'],
        __typename: 'Milestones'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75m88',
        name: 'Milestone 3: Order delivered and closed with customer sign off',
        status: MilestoneStatus['Available'],
        __typename: 'Milestones'
      }
    ]
  };
  const props2 = {
    milestones: [
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75m36',
        name: 'Milestone 1: Order placed',
        status: MilestoneStatus['Active'],
        __typename: 'Milestones'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75m66',
        name: 'Milestone 2: Order shipped',
        status: MilestoneStatus['Available'],
        __typename: 'Milestones'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75m88',
        name: 'Milestone 3: Order delivered',
        status: MilestoneStatus['Available'],
        __typename: 'Milestones'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75i86',
        name: 'Milestone 3: Order delivered and closed with customer sign off',
        status: MilestoneStatus['Completed'],
        __typename: 'Milestones'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75m66',
        name: 'Milestone 2: Order shipped',
        status: MilestoneStatus['Available'],
        __typename: 'Milestones'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75m66',
        name: 'Milestone 2: Order shipped',
        status: MilestoneStatus['ERROR'],
        __typename: 'Milestones'
      }
    ]
  };
  it('Snapshot test with default props', () => {
    const wrapper = mount(<ProcessDetailsMilestonesPanel {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('test assertions', () => {
    const wrapper = mount(<ProcessDetailsMilestonesPanel {...props2} />);
    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('p').at(2).text()).toEqual(
      'Milestone 3: Order delivered Available'
    );
  });
});
