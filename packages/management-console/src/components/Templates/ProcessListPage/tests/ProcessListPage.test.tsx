import React from 'react';
import ProcessListPage from '../ProcessListPage';
import { getWrapper } from '@kogito-apps/common';
import { MemoryRouter as Router } from 'react-router-dom';
import { MockedProvider } from '@apollo/react-testing';

jest.mock('../../../Molecules/ProcessListToolbar/ProcessListToolbar');
jest.mock('../../../Organisms/ProcessListTable/ProcessListTable');

describe('ProcessListPage component tests', () => {
  it('Snapshot tests', () => {
    const wrapper = getWrapper(
      <Router>
        <MockedProvider>
          <ProcessListPage />
        </MockedProvider>
      </Router>,
      'ProcessListPage'
    );
    expect(wrapper).toMatchSnapshot();
  });
});
