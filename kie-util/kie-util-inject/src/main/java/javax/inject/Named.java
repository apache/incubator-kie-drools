/*
 * Copyright (C) 2009 The JSR-330 Expert Group
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

package javax.inject;

import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * String-based {@linkplain Qualifier qualifier}.
 *
 * <p>Example usage:
 *
 * <pre>
 *   public class Car {
 *     &#064;Inject <b>@Named("driver")</b> Seat driverSeat;
 *     &#064;Inject <b>@Named("passenger")</b> Seat passengerSeat;
 *     ...
 *   }</pre>
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface Named {

    /** The name.
     * @return the name.
     */
    String value() default "";
}
