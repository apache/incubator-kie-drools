import React from 'react';
import { shallow, mount } from 'enzyme';
import ProcessDetailsProcessVariables from '../ProcessDetailsProcessVariables';
import { GraphQL } from '@kogito-apps/common';
// tslint:disable: no-string-literal
// tslint:disable: no-unexpected-multiline

const MockedComponent = (): React.ReactElement => {
  return <></>;
};
jest.mock('react-json-view', () =>
  jest.fn((_props) => <MockedComponent {..._props} />)
);
jest.mock('@patternfly/react-icons', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    InfoCircleIcon: () => {
      return <MockedComponent />;
    }
  })
);
const props = {
  setUpdateJson: jest.fn(),
  displayLabel: false,
  updateJson: {
    trip: {
      begin: '2019-10-22T22:00:00Z[UTC]',
      city: 'Berlin',
      country: 'Germany',
      end: '2019-10-30T22:00:00Z[UTC]',
      visaRequired: false
    }
  },
  setDisplayLabel: jest.fn(),
  displaySuccess: false,
  processInstance: {
    state: GraphQL.ProcessInstanceState.Completed
  }
};

const props2 = {
  setUpdateJson: jest.fn(),
  displayLabel: true,
  updateJson: {
    trip: {
      begin: '2019-10-22T22:00:00Z[UTC]',
      city: 'Berlin',
      country: 'Germany',
      end: '2019-10-30T22:00:00Z[UTC]',
      visaRequired: false
    }
  },
  setDisplayLabel: jest.fn(),
  displaySuccess: true,
  processInstance: {
    state: GraphQL.ProcessInstanceState.Active
  }
};
describe('ProcessVariables component tests', () => {
  it('snapshot testing without variables', () => {
    const wrapper = shallow(<ProcessDetailsProcessVariables {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing with variables', () => {
    const wrapper = mount(<ProcessDetailsProcessVariables {...props2} />);
    expect(wrapper.find(ProcessDetailsProcessVariables)).toMatchSnapshot();
    const onEdit = () => {
      return null;
    };
    const obj = {
      name: false,
      onEdit,
      src: {
        trip: {
          begin: '2019-10-22T22:00:00Z[UTC]',
          city: 'Berlin',
          country: 'Germany',
          end: '2019-10-30T22:00:00Z[UTC]',
          visaRequired: false
        }
      }
    };
    wrapper.find('mockConstructor').first().props()['onEdit'](obj);
    expect(props2.setUpdateJson).toHaveBeenCalled();
    expect(props2.setDisplayLabel).toHaveBeenCalled();
  });
});
