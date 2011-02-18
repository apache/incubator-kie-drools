/*
 * Copyright 2010 JBoss Inc
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

package org.drools;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface PersonInterface {

    public abstract String getStatus();

    public abstract void setStatus(String status);

    public abstract String getLikes();

    public abstract String getName();

    public abstract int getAge();

    public abstract boolean isAlive();

    public abstract void setAlive(boolean alive);

    public abstract char getSex();

    public abstract void setSex(char sex);

    public abstract BigDecimal getBigDecimal();

    public abstract void setBigDecimal(BigDecimal bigDecimal);

    public abstract BigInteger getBigInteger();

    public abstract void setBigInteger(BigInteger bigInteger);

}
