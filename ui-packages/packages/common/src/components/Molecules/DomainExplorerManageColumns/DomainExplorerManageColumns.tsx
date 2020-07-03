import React, { useState, useEffect } from 'react';
import {
  Button,
  DataList,
  DataListCheck,
  DataListItem,
  DataListItemRow,
  DataListCell,
  DataListItemCells,
  DataListToggle,
  DataListContent,
  Dropdown,
  DropdownItem,
  DropdownPosition,
  DropdownToggle,
  DropdownToggleCheckbox,
  Modal,
  Text,
  TextContent,
  TextVariants
} from '@patternfly/react-core';
import { SyncIcon } from '@patternfly/react-icons';
import './DomainExplorerManageColumns.css';
import _ from 'lodash';
import gql from 'graphql-tag';
import { query } from 'gql-query-builder';
import { useApolloClient } from 'react-apollo';
import { validateResponse, filterColumnSelection } from '../../../utils/Utils';

export interface IOwnProps {
  columnPickerType: any;
  setColumnFilters: any;
  setTableLoading: any;
  getQueryTypes: any;
  setDisplayTable: any;
  parameters: any;
  setParameters: any;
  selected: any;
  setSelected: any;
  data: any;
  getPicker: any;
  setError: any;
  setDisplayEmptyState: any;
  rememberedParams: any;
  enableCache: boolean;
  setEnableCache: any;
  offsetVal: number;
  pageSize: number;
  setOffsetVal: (offsetVal: number) => void;
  setPageSize: (pageSize: number) => void;
  setIsLoadingMore: (isLoadingMoreVal: boolean) => void;
  isLoadingMore: boolean;
  metaData: any;
  setIsModalOpen: any;
  isModalOpen: boolean;
}

const DomainExplorerManageColumns: React.FC<IOwnProps> = ({
  columnPickerType,
  setColumnFilters,
  setTableLoading,
  getQueryTypes,
  setDisplayTable,
  parameters,
  setParameters,
  selected,
  setSelected,
  data,
  getPicker,
  setError,
  setDisplayEmptyState,
  rememberedParams,
  enableCache,
  setEnableCache,
  pageSize,
  offsetVal,
  setOffsetVal,
  setPageSize,
  setIsLoadingMore,
  isLoadingMore,
  metaData,
  setIsModalOpen,
  isModalOpen
}) => {
  // tslint:disable: forin
  // tslint:disable: no-floating-promises
  const [expanded, setExpanded] = useState([]);
  const [enableRefresh, setEnableRefresh] = useState(true);
  const [isDropDownOpen, setIsDropDownOpen] = useState(false);
  const allSelections = [];

  const nullTypes = [null, 'String', 'Boolean', 'Int', 'DateTime'];
  const client = useApolloClient();

  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };

  const handleChange = (checked, event) => {
    const target = event.target;
    const selection = target.name;
    const selectionArray = target.name
      .split('/')
      .map(item => item.charAt(0).toLowerCase() + item.slice(1));
    setEnableRefresh(false);
    if (selected.includes(selection)) {
      setSelected(prevState => prevState.filter(item => item !== selection));
      const objValue = selectionArray.pop();
      const rest = filterColumnSelection(selectionArray, objValue);
      setParameters(prevState =>
        prevState.filter(obj => {
          if (!_.isEqual(obj, rest)) {
            return obj;
          }
        })
      );
    } else {
      setSelected(prevState => [...prevState, selection]);
      const objValue = selectionArray.pop();
      const rest = filterColumnSelection(selectionArray, objValue);
      setParameters(prevState => [...prevState, rest]);
    }
  };

  const tempExpanded = [];
  const toggle = id => {
    const index = expanded.indexOf(id);
    const newExpanded =
      index >= 0
        ? [
            ...expanded.slice(0, index),
            ...expanded.slice(index + 1, expanded.length)
          ]
        : [...expanded, id];
    tempExpanded.push(newExpanded);
    setExpanded(newExpanded);
  };

  const fetchSchema = option => {
    return (
      !getQueryTypes.loading &&
      getQueryTypes.data.__schema &&
      getQueryTypes.data.__schema.queryType.find(item => {
        if (item.name === option.type.name) {
          return item;
        }
      })
    );
  };
  const rootElements = [];
  let finalResult: any = [];
  let childItems;

  const childSelectionItems = (_data, title, ...attr) => {
    let nestedTitles = '';
    childItems =
      !getQueryTypes.loading &&
      _data.map((group, index) => {
        const label = title + '/' + attr.join();
        const childEle = (
          <DataListItem
            aria-labelledby={'kie-datalist-item-' + label.replace(/\,/g, '')}
            isExpanded={expanded.includes(label.replace(/\,/g, ''))}
            key={'kie-datalist-item-' + label.replace(/\,/g, '')}
          >
            <DataListItemRow>
              <DataListToggle
                onClick={() => toggle(label.replace(/\,/g, ''))}
                isExpanded={expanded.includes(label.replace(/\,/g, ''))}
                id={'kie-datalist-toggle-' + label.replace(/\,/g, '')}
                aria-controls={
                  'kie-datalist-toggle-' + label.replace(/\,/g, '')
                }
              />
              <DataListItemCells
                dataListCells={[
                  <DataListCell
                    id={'kie-datalist-item-' + label.replace(/\,/g, '')}
                    key={index}
                  >
                    {(title + ' / ' + attr.join()).replace(/\,/g, '')}
                  </DataListCell>
                ]}
              />
            </DataListItemRow>
            {group.fields
              .filter((item, _index) => {
                if (!nullTypes.includes(item.type.name)) {
                  const tempData = [];
                  const n = fetchSchema(item);
                  tempData.push(n);
                  nestedTitles = nestedTitles + ' / ' + item.name;
                  childSelectionItems(tempData, title, attr, nestedTitles);
                } else {
                  return item;
                }
              })
              .map((item, _index) => {
                const itemName = title + '/' + group.name + '/' + item.name;
                allSelections.push(itemName);
                return (
                  <DataListContent
                    aria-label={itemName}
                    id={'kie-datalist-content-' + itemName}
                    isHidden={!expanded.includes(label.replace(/\,/g, ''))}
                    className="kogito-common--manage-columns__data-list-content"
                    key={Math.random()}
                  >
                    <DataListItemRow>
                      <DataListCheck
                        aria-labelledby={'kie-datalist-item-' + itemName}
                        name={itemName}
                        checked={selected.includes(itemName)}
                        onChange={handleChange}
                      />
                      <DataListItemCells
                        dataListCells={[
                          <DataListCell
                            id={'kie-datalist-item-' + itemName}
                            key={_index}
                          >
                            {item.name}
                          </DataListCell>
                        ]}
                      />
                    </DataListItemRow>
                  </DataListContent>
                );
              })}
          </DataListItem>
        );
        return childEle;
      });
    finalResult.push(childItems);
  };

  const selectionItems = _data => {
    !getPicker.loading &&
      _data
        .filter((group, index) => {
          if (group.type.kind !== 'SCALAR') {
            return group;
          } else {
            allSelections.push(group.name);
            const rootEle = (
              <DataListItem
                aria-labelledby=""
                key={'kie-datalist-item-' + group.name}
              >
                <DataListItemRow>
                  <DataListCheck
                    aria-labelledby={'kie-datalist-item-' + group.name}
                    name={group.name}
                    checked={selected.includes(group.name)}
                    onChange={handleChange}
                  />
                  <DataListItemCells
                    dataListCells={[
                      <DataListCell
                        id={'kie-datalist-item-' + group.name}
                        key={index}
                      >
                        {group.name}
                      </DataListCell>
                    ]}
                  />
                </DataListItemRow>
              </DataListItem>
            );
            rootElements.push(rootEle);
          }
        })
        .map((group, index) => {
          const nestedEle = (
            <DataListItem
              aria-labelledby={'kie-datalist-item-' + group.name}
              isExpanded={expanded.includes(group.name)}
              id={'kie-datalist-item-' + group.name}
              key={'kie-datalist-item-' + group.name}
            >
              <DataListItemRow>
                <DataListToggle
                  onClick={() => toggle(group.name)}
                  isExpanded={expanded.includes(group.name)}
                  id={'kie-datalist-toggle-' + group.name}
                  aria-controls={'kie-datalist-toggle-' + group.name}
                />
                <DataListItemCells
                  dataListCells={[
                    <DataListCell
                      id={'kie-datalist-item-cell-' + group.name}
                      key={index}
                    >
                      {group.name}
                    </DataListCell>
                  ]}
                />
              </DataListItemRow>
              {group.type.fields &&
                group.type.fields
                  .filter((item, _index) => {
                    if (!nullTypes.includes(item.type.name)) {
                      const tempData = [];
                      const _v = fetchSchema(item);
                      tempData.push(_v);
                      childSelectionItems(tempData, group.name, item.name);
                    } else {
                      /* istanbul ignore else */
                      if (item.type.kind !== 'LIST') {
                        return item;
                      }
                    }
                  })
                  .map((item, _index) => {
                    const itemName = group.name + '/' + item.name;
                    allSelections.push(itemName);
                    return (
                      <DataListContent
                        aria-label={itemName}
                        id={'kie-datalist-content-' + itemName}
                        isHidden={!expanded.includes(group.name)}
                        key={_index}
                        className="kogito-common--manage-columns__data-list-content"
                      >
                        <DataListItemRow>
                          <DataListCheck
                            aria-labelledby={'kie-datalist-item-' + itemName}
                            name={itemName}
                            checked={selected.includes(itemName)}
                            onChange={handleChange}
                          />
                          <DataListItemCells
                            dataListCells={[
                              <DataListCell
                                id={'kie-datalist-item-' + itemName}
                                key={_index}
                              >
                                {item.name}
                              </DataListCell>
                            ]}
                          />
                        </DataListItemRow>
                      </DataListContent>
                    );
                  })}
            </DataListItem>
          );
          !finalResult.includes(nestedEle) && finalResult.push(nestedEle);
        });
  };
  columnPickerType && selectionItems(data);

  finalResult = finalResult.flat();
  finalResult.unshift(rootElements);

  function getAllChilds(arr, comp) {
    const unique = arr
      .map(e => e[comp])
      .map((e, i, final) => final.indexOf(e) === i && i)
      .filter(e => arr[e])
      .map(e => arr[e]);

    return unique;
  }

  useEffect(() => {
    /* istanbul ignore else */
    if (isLoadingMore) {
      generateQuery(parameters);
    }
  }, [isLoadingMore]);

  useEffect(() => {
    /* istanbul ignore else */
    if (
      (rememberedParams.length === 0 && parameters.length !== 1) ||
      rememberedParams.length > 0
    ) {
      generateQuery(parameters);
    }
  }, [parameters.length > 1]);

  async function generateQuery(paramFields) {
    setTableLoading(true);
    setEnableRefresh(true);
    if (columnPickerType && paramFields.length > 1) {
      const Query = query({
        operation: columnPickerType,
        fields: paramFields,
        variables: {
          pagination: {
            value: { offset: offsetVal, limit: pageSize },
            type: 'Pagination'
          }
        }
      });
      try {
        const response = await client.query({
          query: gql`
            ${Query.query}
          `,
          variables: Query.variables,
          fetchPolicy: enableCache ? 'cache-first' : 'network-only'
        });
        const firstKey = Object.keys(response.data)[0];
        if (response.data[firstKey].length > 0) {
          const resp = response.data;
          const respKeys = Object.keys(resp)[0];
          const tableContent = resp[respKeys];
          const finalResp = [];
          tableContent.map(content => {
            const finalObject = validateResponse(content, paramFields);
            finalResp.push(finalObject);
          });
          setColumnFilters(finalResp);
          setTableLoading(false);
          setDisplayTable(true);
          setEnableCache(false);
          setIsLoadingMore(false);
        } else {
          setTableLoading(false);
          setDisplayEmptyState(true);
          setEnableCache(false);
        }
      } catch (error) {
        setError(error);
      }
    } else {
      setTableLoading(false);
      setDisplayEmptyState(false);
      setDisplayTable(false);
    }
  }

  const renderModal = () => {
    const numSelected = selected.length;
    const allSelected = numSelected === allSelections.length;
    const anySelected = numSelected > 0;
    const someChecked = anySelected ? null : false;
    const isChecked = allSelected ? true : someChecked;

    const onDropDownToggle = isOpen => {
      setIsDropDownOpen(isOpen);
    };

    const onDropDownSelect = event => {
      setIsDropDownOpen(!isDropDownOpen);
    };

    const handleSelectClickNone = () => {
      setSelected([]);
      setParameters([metaData]);
    };

    const handleSelectClickAll = () => {
      setSelected(allSelections);
      const selectionArray = allSelections.map(ele =>
        ele.split('/').map(item => item.charAt(0).toLowerCase() + item.slice(1))
      );
      const finalObj = [];
      selectionArray.forEach(arr => {
        const objValue = arr.pop();
        const rest = filterColumnSelection(arr, objValue);
        finalObj.push(rest);
      });
      finalObj.push(metaData);
      setParameters(finalObj);
    };

    const items = [
      <DropdownItem key={0} onClick={() => handleSelectClickNone()}>
        Select none
      </DropdownItem>,
      <DropdownItem key={1} onClick={() => handleSelectClickAll()}>
        Select all
      </DropdownItem>
    ];
    const bulkSelection = (
      <Dropdown
        onSelect={onDropDownSelect}
        position={DropdownPosition.left}
        id="selectAll-dropdown"
        toggle={
          <DropdownToggle
            splitButtonItems={[
              <DropdownToggleCheckbox
                id="selectAll-dropdown-checkbox"
                key={1}
                aria-label={anySelected ? 'Deselect all' : 'Select all'}
                isChecked={isChecked}
                onClick={() => {
                  anySelected
                    ? handleSelectClickNone()
                    : handleSelectClickAll();
                }}
              />
            ]}
            onToggle={onDropDownToggle}
          >
            {numSelected !== 0 && (
              <React.Fragment>{numSelected} selected</React.Fragment>
            )}
          </DropdownToggle>
        }
        isOpen={isDropDownOpen}
        dropdownItems={items}
      />
    );
    return (
      <Modal
        title="Manage columns"
        isOpen={isModalOpen}
        isSmall
        description={
          <TextContent>
            <Text component={TextVariants.p}>
              Selected categories will be displayed in the table.
            </Text>
          </TextContent>
        }
        onClose={handleModalToggle}
        className="kogito-common--manage-columns__modal"
        actions={[
          <Button
            key="save"
            variant="primary"
            onClick={() => {
              onResetQuery(parameters);
            }}
            id="save-columns"
          >
            Save
          </Button>,
          <Button key="cancel" variant="secondary" onClick={handleModalToggle}>
            Cancel
          </Button>
        ]}
      >
        {bulkSelection}
        <DataList
          aria-label="Table column management"
          id="table-column-management"
          key={1}
          className="kogito-common--manage-columns__data-list"
          isCompact
        >
          {getAllChilds(finalResult, 'props')}
        </DataList>
      </Modal>
    );
  };

  const onResetQuery = _parameters => {
    setOffsetVal(0);
    offsetVal = 0;
    setPageSize(10);
    pageSize = 10;
    generateQuery(_parameters);
    setIsLoadingMore(false);
    setIsModalOpen(!isModalOpen);
  };

  const onRefresh = _parameters => {
    /* istanbul ignore else */
    if (enableRefresh && parameters.length > 1) {
      setOffsetVal(0);
      offsetVal = 0;
      setPageSize(10);
      pageSize = 10;
      generateQuery(_parameters);
      setIsLoadingMore(false);
    }
  };

  return (
    <>
      <Button
        variant="link"
        onClick={handleModalToggle}
        id="manage-columns-button"
      >
        Manage columns
      </Button>
      {renderModal()}
      <Button
        variant="plain"
        onClick={() => {
          onRefresh(parameters);
        }}
        className="pf-u-m-md"
        id="refresh-button"
        aria-label={'Refresh list'}
      >
        <SyncIcon />
      </Button>
    </>
  );
};

export default DomainExplorerManageColumns;
