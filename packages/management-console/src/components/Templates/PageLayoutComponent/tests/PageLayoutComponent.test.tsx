import React from 'react';
import { shallow } from 'enzyme';
import PageLayoutComponent from '../PageLayoutComponent';
import { MockedProvider } from '@apollo/react-testing';

const props: any = {
  location: {
    pathname: '/ProcessInstances'
  }
};

const mocks = [];

describe('PageLayoutComponent component tests', () => {
  it('snapshot testing', () => {
    const wrapper = shallow(
      <MockedProvider mocks={mocks}>
        <PageLayoutComponent {...props} />
      </MockedProvider>
    );
    expect(wrapper.find(PageLayoutComponent)).toMatchSnapshot();
  });
});
