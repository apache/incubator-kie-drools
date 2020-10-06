import React from 'react';
import { AuditToolbarBottom } from '../AuditToolbar';
import { mount, shallow } from 'enzyme';

describe('Audit bottom toolbar', () => {
  test('renders correctly', () => {
    const wrapper = renderAuditToolbarBottom('shallow');
    expect(wrapper).toMatchSnapshot();
  });

  test('handles pagination', () => {
    const setPage = jest.fn();
    const setPageSize = jest.fn();
    const page = 2;
    const pageSize = 50;
    const wrapper = renderAuditToolbarBottom('mount', {
      setPage,
      setPageSize
    });

    wrapper.props().setPage(page);
    wrapper.props().setPageSize(pageSize);

    expect(setPage).toBeCalledTimes(1);
    expect(setPage).toBeCalledWith(page);
    expect(setPageSize).toBeCalledTimes(1);
    expect(setPageSize).toBeCalledWith(pageSize);
  });
});

const renderAuditToolbarBottom = (
  method: 'shallow' | 'mount',
  props?: Record<string, unknown>
) => {
  const defaultProps = {
    total: 20,
    page: 1,
    pageSize: 10,
    setPage: jest.fn(),
    setPageSize: jest.fn(),
    ...props
  };
  if (method === 'shallow') {
    return shallow(<AuditToolbarBottom {...defaultProps} />);
  } else {
    return mount(<AuditToolbarBottom {...defaultProps} />);
  }
};
