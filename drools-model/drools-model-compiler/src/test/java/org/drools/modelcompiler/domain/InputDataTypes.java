/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.domain;

import java.util.ArrayList;
import java.util.List;

public class InputDataTypes{
    private int no1Count;
    private int no2Count;
    private int no3Count;
    private int no4Count;
    private int no5Count;
    private int no6Count;
    private int no7Count;
    private int no8Count;
    private int no9Count;
    private int no10Count;
    private int no11Count;
    private int no12Count;
    private List<String> firings = new ArrayList<String>();

    public InputDataTypes() {
        no1Count = 1;
        no2Count = 1;
        no3Count = 1;
        no4Count = 1;
        no5Count = 1;
        no6Count = 1;
        no7Count = 1;
        no8Count = 1;
        no9Count = 1;
        no10Count = 1;
        no11Count = 1;
        no12Count = 1;
    }

    public int getNo1Count() {
        return no1Count;
    }
    public void setNo1Count(int no1Count) {
        this.no1Count = no1Count;
    }
    public int getNo2Count() {
        return no2Count;
    }
    public void setNo2Count(int no2Count) {
        this.no2Count = no2Count;
    }
    public int getNo3Count() {
        return no3Count;
    }
    public void setNo3Count(int no3Count) {
        this.no3Count = no3Count;
    }
    public int getNo4Count() {
        return no4Count;
    }
    public void setNo4Count(int no4Count) {
        this.no4Count = no4Count;
    }
    public int getNo5Count() {
        return no5Count;
    }
    public void setNo5Count(int no5Count) {
        this.no5Count = no5Count;
    }
    public int getNo6Count() {
        return no6Count;
    }
    public void setNo6Count(int no6Count) {
        this.no6Count = no6Count;
    }
    public int getNo7Count() {
        return no7Count;
    }
    public void setNo7Count(int no7Count) {
        this.no7Count = no7Count;
    }
    public int getNo8Count() {
        return no8Count;
    }
    public void setNo8Count(int no8Count) {
        this.no8Count = no8Count;
    }
    public int getNo9Count() {
        return no9Count;
    }
    public void setNo9Count(int no9Count) {
        this.no9Count = no9Count;
    }
    public int getNo10Count() {
        return no10Count;
    }
    public void setNo10Count(int no10Count) {
        this.no10Count = no10Count;
    }
    public int getNo11Count() {
        return no11Count;
    }
    public void setNo11Count(int no11Count) {
        this.no11Count = no11Count;
    }
    public int getNo12Count() {
        return no12Count;
    }
    public void setNo12Count(int no12Count) {
        this.no12Count = no12Count;
    }
    public List<String> getFirings() {
        return firings;
    }
    public void setFirings(List<String> firings) {
        this.firings = firings;
    }
}
