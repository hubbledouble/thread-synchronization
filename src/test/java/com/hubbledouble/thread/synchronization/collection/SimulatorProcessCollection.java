/*
 *    Copyright (c) 2020, HubbleDouble
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.hubbledouble.thread.synchronization.collection;

import com.hubbledouble.thread.synchronization.domain.Process;

import java.util.HashMap;
import java.util.Map;

public class SimulatorProcessCollection {

    private Map<String, Process> processCollection = new HashMap<>();

    public Map<String, Process> getProcessCollection() {
        return processCollection;
    }

}
