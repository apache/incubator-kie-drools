import React from 'react';
import DataTableContainer from '../DataTableContainer';
import { getWrapper } from '@kogito-apps/common';
import { MemoryRouter as Router } from 'react-router-dom';
import { MockedProvider } from '@apollo/react-testing';

jest.mock('../../../Organisms/DataTable/DataTable');

describe('DataTableContainer component tests', () => {
  it('Snapshot tests', () => {
    const wrapper = getWrapper(
      <Router>
        <MockedProvider>
          <DataTableContainer />
        </MockedProvider>
      </Router>,
      'DataTableContainer'
    );
    expect(wrapper).toMatchSnapshot();
  });
});
