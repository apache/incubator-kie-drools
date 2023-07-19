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

import React, { useState } from 'react';
import {
  DataList,
  DataListItem,
  DataListCell
} from '@patternfly/react-core/dist/js/components/DataList';
import { Spinner } from '@patternfly/react-core/dist/js/components/Spinner';
import {
  DropdownItem,
  Dropdown,
  DropdownToggle,
  DropdownToggleAction
} from '@patternfly/react-core/dist/js/components/Dropdown';
import { Split, SplitItem } from '@patternfly/react-core/dist/js/layouts/Split';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import '../styles.css';
import { CheckIcon } from '@patternfly/react-icons/dist/js/icons/check-icon';

interface IOwnProps {
  offset: number;
  setOffset: (offset: number) => void;
  getMoreItems: (initval: number, pageSize: number) => void;
  pageSize: number;
  isLoadingMore: boolean;
  setLoadMoreClicked?: (loadMoreClicked: boolean) => void;
}

const LoadMore: React.FC<IOwnProps & OUIAProps> = ({
  offset,
  setOffset,
  getMoreItems,
  pageSize,
  isLoadingMore,
  setLoadMoreClicked,
  ouiaId,
  ouiaSafe
}) => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [loadMoreValue, setLoadMoreValue] = useState<number>(10);

  const loadMore = (newPageSize: number): void => {
    setLoadMoreClicked && setLoadMoreClicked(true);
    const newOffset = offset + pageSize;
    setOffset(newOffset);
    getMoreItems(newOffset, newPageSize);
  };

  const onToggle = (isDropdownOpen: boolean): void => {
    setIsOpen(isDropdownOpen);
  };

  const onSelect = (event: React.SyntheticEvent<HTMLDivElement>): void => {
    const selectedValue: number = parseInt(event.currentTarget.id, 10);
    setLoadMoreValue(selectedValue);
  };

  const dropdownItem = (count: number): JSX.Element => {
    return (
      <DropdownItem
        key={'loadmore' + count}
        component="button"
        id={count.toString()}
      >
        <Split hasGutter>
          <SplitItem>Load {count} more</SplitItem>
          {loadMoreValue === count && (
            <SplitItem>
              <CheckIcon size="sm" color="var(--pf-global--info-color--100)" />
            </SplitItem>
          )}
        </Split>
      </DropdownItem>
    );
  };
  return (
    <DataList
      aria-label="Simple data list example"
      {...componentOuiaProps(
        ouiaId,
        'load-more',
        ouiaSafe ? ouiaSafe : !isLoadingMore
      )}
    >
      <DataListItem aria-labelledby="kie-datalist-item">
        <DataListCell className="kogito-components-common__load-more">
          <div className="pf-u-float-right pf-u-mr-md">
            <Dropdown
              onSelect={onSelect}
              direction="up"
              toggle={
                <DropdownToggle
                  id={`toggle-id`}
                  onToggle={onToggle}
                  splitButtonItems={[
                    <DropdownToggleAction
                      key={`toggle-id-${ouiaId}`}
                      onClick={() => {
                        loadMore(loadMoreValue);
                        setIsOpen(false);
                      }}
                    >
                      {isLoadingMore ? (
                        <>
                          Loading...
                          <Spinner
                            size="md"
                            className="kogito-components-common__load-more-spinner"
                          />{' '}
                        </>
                      ) : (
                        `Load ${loadMoreValue} more`
                      )}
                    </DropdownToggleAction>
                  ]}
                />
              }
              isOpen={isOpen}
              dropdownItems={[
                dropdownItem(10),
                dropdownItem(20),
                dropdownItem(50),
                dropdownItem(100)
              ]}
            />
          </div>
        </DataListCell>
      </DataListItem>
    </DataList>
  );
};

export default LoadMore;
