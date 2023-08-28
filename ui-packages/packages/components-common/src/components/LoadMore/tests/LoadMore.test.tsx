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
import { fireEvent, render, screen } from '@testing-library/react';
import { LoadMore } from '../LoadMore';

describe('LoadMore component tests with isLoading false', () => {
  const props = {
    offset: 0,
    setOffset: jest.fn(),
    getMoreItems: jest.fn(),
    pageSize: 10,
    isLoadingMore: false,
    ouiaId: 'load-more-ouia-id',
    setLoadMoreClicked: jest.fn()
  };
  it('snapshot testing', () => {
    render(<LoadMore {...props} />);
    expect(screen.getByTestId('load-more-data-list')).toMatchSnapshot();
  });
  it('select dropdown options tests', async () => {
    const { container } = render(<LoadMore {...props} />);
    fireEvent.click(screen.getByTestId('toggle-id'));
    const items = await screen.findAllByTestId('dropdown-item');
    expect(items).toHaveLength(4);
    fireEvent.click(items[1]);
    const svg_item1 = container.querySelector('img');
    expect(svg_item1).toBeDefined();
    fireEvent.click(items[2]);
    const svg_item2 = container.querySelector('img');
    expect(svg_item2).toBeDefined();
    fireEvent.click(items[3]);
    const svg_item3 = container.querySelector('img');
    expect(svg_item3).toBeDefined();
  });

  it('click loadmore button', () => {
    render(<LoadMore {...props} />);
    fireEvent.click(screen.getByTestId('toggle-action'));
    expect(props.getMoreItems).toHaveBeenCalled();
    expect(props.setLoadMoreClicked).toHaveBeenCalled();
    expect(props.setOffset).toHaveBeenCalled();
  });

  it('simulate loading state in button', async () => {
    const { container } = render(
      <LoadMore {...{ ...props, isLoadingMore: true, ouiaSafe: true }} />
    );
    expect(screen.queryByTestId('toggle-action')?.textContent).toContain(
      'Loading...'
    );
    expect(container).toMatchSnapshot();
  });
});
