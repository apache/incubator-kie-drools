import React, { useState } from 'react';
import {
  DataList,
  DataListItem,
  DataListCell,
  Spinner,
  DropdownItem,
  Dropdown,
  DropdownToggle,
  DropdownToggleAction,
  Split,
  SplitItem
} from '@patternfly/react-core';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import '../../styles.css';
import { CheckIcon } from '@patternfly/react-icons';

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
        <DataListCell className="kogito-common__load-more">
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
                            className="kogito-common__load-more-spinner"
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
