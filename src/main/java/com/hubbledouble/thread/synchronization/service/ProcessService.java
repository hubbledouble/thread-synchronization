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

package com.hubbledouble.thread.synchronization.service;

import com.hubbledouble.thread.synchronization.domain.Process;

/**
 * Interface used to provide main functionality hiding the data layer
 *
 * @author Jorge Saldivar
 */
public interface ProcessService {

    Process findByProcessName(String processName);

    Process findByProcessNameAndProcessSourceId(String processName, String processSourceId);

    boolean insertLock(String processName, String processSourceId);

    boolean transferLock(Process process, String processSourceId);

    void performHeartbeat(Process process);

    void delete(Process process);

    void deleteOldCompletedProcesses();

}
