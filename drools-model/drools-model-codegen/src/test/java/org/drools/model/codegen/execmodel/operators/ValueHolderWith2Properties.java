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
package org.drools.model.codegen.execmodel.operators;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ValueHolderWith2Properties {

    // the first property is null
    // the second property has a value

    private Integer firstIntegerValue = null;
    private Integer secondIntegerValue = 0;
    private Long firstLongValue = null;
    private Long secondLongValue = 0L;
    private Byte firstByteValue = null;
    private Byte secondByteValue = 0;
    private Character firstCharacterValue = null;
    private Character secondCharacterValue = 'a';
    private Short firstShortValue = null;
    private Short secondShortValue = 0;
    private Float firstFloatValue = null;
    private Float secondFloatValue = 0f;
    private Double firstDoubleValue = null;
    private Double secondDoubleValue = 0d;
    private BigInteger firstBigIntegerValue = null;
    private BigInteger secondBigIntegerValue = BigInteger.valueOf(0);
    private BigDecimal firstBigDecimalValue = null;
    private BigDecimal secondBigDecimalValue = BigDecimal.valueOf(0);

    public ValueHolderWith2Properties() {}

    public Integer getFirstIntegerValue() {
        return firstIntegerValue;
    }

    public void setFirstIntegerValue(Integer firstIntegerValue) {
        this.firstIntegerValue = firstIntegerValue;
    }

    public Integer getSecondIntegerValue() {
        return secondIntegerValue;
    }

    public void setSecondIntegerValue(Integer secondIntegerValue) {
        this.secondIntegerValue = secondIntegerValue;
    }

    public Long getFirstLongValue() {
        return firstLongValue;
    }

    public void setFirstLongValue(Long firstLongValue) {
        this.firstLongValue = firstLongValue;
    }

    public Long getSecondLongValue() {
        return secondLongValue;
    }

    public void setSecondLongValue(Long secondLongValue) {
        this.secondLongValue = secondLongValue;
    }

    public Byte getFirstByteValue() {
        return firstByteValue;
    }

    public void setFirstByteValue(Byte firstByteValue) {
        this.firstByteValue = firstByteValue;
    }

    public Byte getSecondByteValue() {
        return secondByteValue;
    }

    public void setSecondByteValue(Byte secondByteValue) {
        this.secondByteValue = secondByteValue;
    }

    public Character getFirstCharacterValue() {
        return firstCharacterValue;
    }

    public void setFirstCharacterValue(Character firstCharacterValue) {
        this.firstCharacterValue = firstCharacterValue;
    }

    public Character getSecondCharacterValue() {
        return secondCharacterValue;
    }

    public void setSecondCharacterValue(Character secondCharacterValue) {
        this.secondCharacterValue = secondCharacterValue;
    }

    public Short getFirstShortValue() {
        return firstShortValue;
    }

    public void setFirstShortValue(Short firstShortValue) {
        this.firstShortValue = firstShortValue;
    }

    public Short getSecondShortValue() {
        return secondShortValue;
    }

    public void setSecondShortValue(Short secondShortValue) {
        this.secondShortValue = secondShortValue;
    }

    public Float getFirstFloatValue() {
        return firstFloatValue;
    }

    public void setFirstFloatValue(Float firstFloatValue) {
        this.firstFloatValue = firstFloatValue;
    }

    public Float getSecondFloatValue() {
        return secondFloatValue;
    }

    public void setSecondFloatValue(Float secondFloatValue) {
        this.secondFloatValue = secondFloatValue;
    }

    public Double getFirstDoubleValue() {
        return firstDoubleValue;
    }

    public void setFirstDoubleValue(Double firstDoubleValue) {
        this.firstDoubleValue = firstDoubleValue;
    }

    public Double getSecondDoubleValue() {
        return secondDoubleValue;
    }

    public void setSecondDoubleValue(Double secondDoubleValue) {
        this.secondDoubleValue = secondDoubleValue;
    }

    public BigInteger getFirstBigIntegerValue() {
        return firstBigIntegerValue;
    }

    public void setFirstBigIntegerValue(BigInteger firstBigIntegerValue) {
        this.firstBigIntegerValue = firstBigIntegerValue;
    }

    public BigInteger getSecondBigIntegerValue() {
        return secondBigIntegerValue;
    }

    public void setSecondBigIntegerValue(BigInteger secondBigIntegerValue) {
        this.secondBigIntegerValue = secondBigIntegerValue;
    }

    public BigDecimal getFirstBigDecimalValue() {
        return firstBigDecimalValue;
    }

    public void setFirstBigDecimalValue(BigDecimal firstBigDecimalValue) {
        this.firstBigDecimalValue = firstBigDecimalValue;
    }

    public BigDecimal getSecondBigDecimalValue() {
        return secondBigDecimalValue;
    }

    public void setSecondBigDecimalValue(BigDecimal secondBigDecimalValue) {
        this.secondBigDecimalValue = secondBigDecimalValue;
    }

    public static Character constantCharacterValue() {
        return Character.valueOf('a');
    }
}
