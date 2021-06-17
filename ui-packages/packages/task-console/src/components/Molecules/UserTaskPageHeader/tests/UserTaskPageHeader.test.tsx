import React from 'react';
import UserTaskPageHeader from '../UserTaskPageHeader';
import { mount } from 'enzyme';
import { BrowserRouter } from 'react-router-dom';

describe('UserTaskPageHeader component tests', () => {
  it('Should render UserTaskPageHeader correctly', () => {
    const wrapper = mount(
      <BrowserRouter>
        <UserTaskPageHeader />
      </BrowserRouter>
    ).find('UserTaskPageHeader');
    expect(wrapper).toMatchSnapshot();
  });
});
