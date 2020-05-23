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

package com.hubbledouble.thread.synchronization.service.impl;

import com.hubbledouble.thread.synchronization.domain.Process;
import com.hubbledouble.thread.synchronization.service.ProcessService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class SimulatorProcessServiceImpl implements ProcessService {

    private Map<String, Process> processCollection;

    public SimulatorProcessServiceImpl(Map<String, Process> processCollection) {
        this.processCollection = processCollection;
    }

    @Override
    public Process findByProcessName(String processName) {

        synchronized (processCollection) {
            return processCollection.getOrDefault(processName, null);
        }

    }

    @Override
    public Process findByProcessNameAndProcessSourceId(String processName, String processSourceId) {

        synchronized (processCollection) {

            if (null != processSourceId) {
                Process process = processCollection.getOrDefault(processName, null);
                if (null != process && processSourceId.equals(process.getProcessSourceId())) {
                    return process;
                }

            }

            return null;
        }

    }

    @Override
    public boolean insertLock(String processName, String processSourceId) {

        synchronized (processCollection) {

            if (!processCollection.containsKey(processName)) {
                processCollection.put(processName, new Process(processName, processSourceId));
                return true;

            }

            return false;
        }


    }

    @Override
    public boolean transferLock(Process process, String processSourceId) {
        synchronized (processCollection) {

            if (processCollection.containsKey(process.getProcessName())) {

                process.setProcessSourceId(processSourceId);
                process.setHeartbeat();
                process.setVersion();

                processCollection.put(process.getProcessName(), process);

                return true;

            }

            return false;
        }
    }

    @Override
    public void performHeartbeat(Process process) {

        transferLock(process, process.getProcessSourceId());

    }

    @Override
    public void delete(Process process) {

        synchronized (processCollection) {

            if(processCollection.containsKey(process.getProcessName())) {
                processCollection.remove(process.getProcessName());
            }

        }

    }

    @Override
    public void deleteOldCompletedProcesses() {

        synchronized (processCollection) {

            processCollection.values().removeIf(i ->
                    null == i.getHeartbeat() ||
                            Instant.now()
                                    .minus(10, ChronoUnit.MINUTES)
                                    .isAfter(i.getHeartbeat()));

        }

    }
}