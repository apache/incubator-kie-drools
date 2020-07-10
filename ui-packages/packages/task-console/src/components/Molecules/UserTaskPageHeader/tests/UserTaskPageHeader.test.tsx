import React from 'react';
import UserTaskPageHeader from '../UserTaskPageHeader';
import { getWrapper } from '@kogito-apps/common';
import { BrowserRouter } from 'react-router-dom';

describe('UserTaskPageHeader component tests', () => {
  it('Should render UserTaskPageHeader correctly', () => {
    const wrapper = getWrapper(
      <BrowserRouter>
        <UserTaskPageHeader />
      </BrowserRouter>,
      'UserTaskPageHeader'
    );
    expect(wrapper).toMatchSnapshot();
  });
});
