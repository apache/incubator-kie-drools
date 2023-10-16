/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { useEffect, useRef } from 'react';
import { InputGroup, TextInput } from '@patternfly/react-core';
import flatpickr from 'flatpickr';
import { Instance as flatpickrInstance } from 'flatpickr/dist/types/instance';
import leftArrow from '../../../static/images/leftArrow.svg';
import rightArrow from '../../../static/images/rightArrow.svg';
import './DatePicker.scss';

type DatePickerProps = {
  fromDate?: string;
  id: string;
  label?: string;
  minDate?: string;
  maxDate?: string;
  onDateUpdate: (date: string) => void;
  value?: string;
};

const DatePicker = (props: DatePickerProps) => {
  const { fromDate, minDate, maxDate, value, onDateUpdate, id, label } = props;
  const datePicker = useRef<HTMLInputElement>();

  useEffect(() => {
    let calendar: flatpickrInstance;
    const onChange = (selectedDates: Date[], dateStr: string) => {
      onDateUpdate(dateStr);
    };
    if (datePicker.current) {
      calendar = flatpickr(datePicker.current, {
        allowInput: true,
        altInput: true,
        altFormat: 'F j, Y',
        dateFormat: 'Y-m-d',
        defaultDate: value,
        minDate,
        maxDate,
        monthSelectorType: 'static',
        onChange,
        prevArrow: `<img alt="Prev" src="${leftArrow}">`,
        nextArrow: `<img alt="Next" src="${rightArrow}">`,
        static: true
      });
    }
    return () => {
      calendar.destroy();
    };
  }, [fromDate, maxDate, onDateUpdate, minDate, value, id]);

  return (
    <InputGroup>
      <TextInput
        name={id}
        id={id}
        type="date"
        aria-label={label}
        ref={datePicker}
      />
    </InputGroup>
  );
};

export default DatePicker;

export const datePickerSetup = () => {
  flatpickr.l10ns.en.weekdays.shorthand.forEach((day, index, daysArray) => {
    if (daysArray[index] === 'Thu' || daysArray[index] === 'Th') {
      daysArray[index] = 'Th';
    } else if (daysArray[index] === 'Sat' || daysArray[index] === 'Sa') {
      daysArray[index] = 'Sa';
    } else {
      daysArray[index] = daysArray[index].charAt(0);
    }
  });
};
