import React from 'react';
import { mount } from 'enzyme';
import { KogitoEmptyState, KogitoEmptyStateType } from '../KogitoEmptyState';
import { Button } from '@patternfly/react-core';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-icons', () => ({
  ...jest.requireActual('@patternfly/react-icons'),
  InfoCircleIcon: () => {
    return <MockedComponent />;
  },
  SearchIcon: () => {
    return <MockedComponent />;
  },
  ExclamationTriangleIcon: () => {
    return <MockedComponent />;
  }
}));

const props = {
  title: 'No child process instances',
  body: 'This process has no related sub processes',
  ouiaId: 'empty-state-ouia-id'
};

describe('KogitoEmptyState component tests', () => {
  it('Search test', () => {
    const wrapper = mount(
      <KogitoEmptyState type={KogitoEmptyStateType.Search} {...props} />
    );
    expect(wrapper).toMatchSnapshot();
  });
  it('Reset test', () => {
    const click = jest.fn();
    const wrapper = mount(
      <KogitoEmptyState
        type={KogitoEmptyStateType.Reset}
        onClick={click}
        {...props}
      />
    );
    expect(wrapper).toMatchSnapshot();
    wrapper.find(Button).simulate('click');
    expect(click).toHaveBeenCalledTimes(1);
  });
  it('Info test', () => {
    const wrapper = mount(
      <KogitoEmptyState type={KogitoEmptyStateType.Info} {...props} />
    );
    expect(wrapper).toMatchSnapshot();
  });
  it('Refresh test', () => {
    const click = jest.fn();
    const wrapper = mount(
      <KogitoEmptyState
        type={KogitoEmptyStateType.Refresh}
        onClick={click}
        {...props}
      />
    );
    expect(wrapper).toMatchSnapshot();
    wrapper.find(Button).simulate('click');
    expect(click).toHaveBeenCalledTimes(1);
  });
});
