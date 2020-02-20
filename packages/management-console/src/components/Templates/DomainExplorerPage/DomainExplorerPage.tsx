import React, { useState, useEffect } from 'react';
import {
  DataToolbar,
  DataToolbarContent,
  DataToolbarToggleGroup,
  DataToolbarGroup,
  DataToolbarFilter
} from '@patternfly/react-core/dist/esm/experimental';
import {
  Dropdown,
  DropdownToggle,
  DropdownItem,
  PageSection,
  Breadcrumb,
  BreadcrumbItem
} from '@patternfly/react-core';
import { FilterIcon } from '@patternfly/react-icons';
import { Link } from 'react-router-dom';
import './DomainExplorerPage.css';
import DomainExplorerColumnPicker from '../../Organisms/DomainExplorerColumnPicker/DomainExplorerColumnPicker';
import DomainExplorerTable from '../../Organisms/DomainExplorerTable/DomainExplorerTable';
import PageTitleComponent from '../../Molecules/PageTitleComponent/PageTitleComponent';

import {
  useGetQueryTypesQuery,
  useGetQueryFieldsQuery,
  useGetInputFieldsFromQueryQuery
} from '../../../graphql/types';

const DomainExplorerPage = () => {
  const [initData2, setInitData2] = useState<any>({
    __schema: { queryType: [] }
  });
  const [schemaDropdown, setSchemaDropDown] = useState(false);
  const [currentCategory, setCurrentCategory] = useState('Domains');
  const [currentSchema, setCurrentSchema] = useState([]);
  const [currentQuery, setCurrentQuery] = useState('');
  const [columnPickerType, setColumnPickerType] = useState('');
  const [isCategoryDropdownOpen, setIsCategoryDropdownOpen] = useState(false);
  const [columnFilters, setColumnFilters] = useState({});
  const [tableLoading, setTableLoading] = useState(true);
  const [displayTable, setDisplayTable] = useState(false);
  const [selected, setSelected] = useState([]);
  const [parameters, setParameters] = useState([]);

  const temp = [];

  const getQuery = useGetQueryFieldsQuery();

  const getQueryTypes = useGetQueryTypesQuery();

  useEffect(() => {
    setInitData2(getQueryTypes.data);
  }, [getQueryTypes.data]);

  useEffect(() => {
    setParameters([]);
    setSelected([]);
  }, [columnPickerType]);

  useEffect(() => {
    setDisplayTable(false);
  }, [columnPickerType]);

  const getSchema: any = useGetInputFieldsFromQueryQuery({
    variables: { currentQuery }
  });

  useEffect(() => {
    setCurrentSchema(temp);
  }, [getSchema.data]);

  const onCategoryToggle = _isOpen => {
    setIsCategoryDropdownOpen(_isOpen);
  };

  const onCategorySelect = event => {
    setCurrentCategory(event.target.innerText);
    const tempChip = [];
    tempChip.push(event.target.innerText);
    const _a =
      !getQuery.loading &&
      getQuery.data.__type.fields.find(item => {
        if (item.name === event.target.innerText) {
          return item;
        }
      });
    setCurrentQuery(_a.args[0].type.name);
    setColumnPickerType(_a.type.ofType.name);
    setIsCategoryDropdownOpen(!isCategoryDropdownOpen);
    setSchemaDropDown(true);
  };

  const buildCategoryDropdown = () => {
    const queryDropDown =
      !getQuery.loading && getQuery.data.__type.fields.slice(2);
    const dropdownItems = [];
    !getQuery.loading &&
      queryDropDown.map((item, index) =>
        dropdownItems.push(<DropdownItem key={index}>{item.name}</DropdownItem>)
      );
    return (
      <DataToolbarFilter categoryName="Category">
        <Dropdown
          onSelect={onCategorySelect}
          position="left"
          toggle={
            <DropdownToggle
              onToggle={onCategoryToggle}
              style={{ width: '100%' }}
            >
              <FilterIcon /> {currentCategory}
            </DropdownToggle>
          }
          isOpen={isCategoryDropdownOpen}
          dropdownItems={dropdownItems}
          style={{ width: '100%' }}
        />
      </DataToolbarFilter>
    );
  };

  const renderToolbar = () => {
    return (
      <DataToolbar
        id="data-toolbar-with-chip-groups"
        className="pf-m-toggle-group-container"
        collapseListedFiltersBreakpoint="xl"
      >
        <DataToolbarContent>
          <DataToolbarToggleGroup toggleIcon={<FilterIcon />} breakpoint="xl">
            <DataToolbarGroup variant="filter-group">
              {buildCategoryDropdown()}
            </DataToolbarGroup>
            <DataToolbarGroup>
              {!getSchema.loading && (
                <DomainExplorerColumnPicker
                  columnPickerType={columnPickerType}
                  setColumnFilters={setColumnFilters}
                  setTableLoading={setTableLoading}
                  getQueryTypes={getQueryTypes}
                  setDisplayTable={setDisplayTable}
                  parameters={parameters}
                  setParameters={setParameters}
                  selected={selected}
                  setSelected={setSelected}
                />
              )}
            </DataToolbarGroup>
          </DataToolbarToggleGroup>
        </DataToolbarContent>
      </DataToolbar>
    );
  };

  return (
    <>
      <PageSection variant="light">
        <PageTitleComponent title="Domain Explorer" />
        <Breadcrumb>
          <BreadcrumbItem>
            <Link to={'/'}>Home</Link>
          </BreadcrumbItem>
          <BreadcrumbItem isActive>Domain Explorer</BreadcrumbItem>
        </Breadcrumb>
      </PageSection>
      <PageSection>
        {renderToolbar()}

        <div className="kogito-management-console--domain-explorer__table-OverFlow">
          <DomainExplorerTable
            columnFilters={columnFilters}
            tableLoading={tableLoading}
            displayTable={displayTable}
          />
        </div>
      </PageSection>
    </>
  );
};

export default DomainExplorerPage;
