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

import com.hubbledouble.thread.synchronization.service.LockService;
import com.hubbledouble.thread.synchronization.domain.Process;
import com.hubbledouble.thread.synchronization.service.ProcessService;

import java.time.Duration;
import java.time.Instant;

/**
 * Main functionality for lock mechanism
 *
 * @author Jorge Saldivar
 */
public class LockServiceImpl implements LockService {

    private final ProcessService processService;

    public LockServiceImpl(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public boolean acquireLock(String processName, String processSourceId, Duration duration) {

        processService.deleteOldCompletedProcesses();
        Process process = processService.findByProcessName(processName);

        if (null == process) {
            return processService.insertLock(processName, processSourceId);
        }

        if (!isProcessRunning(process, duration)) {
            return processService.transferLock(process, processSourceId);
        }

        return false;

    }

    @Override
    public void performHeartbeat(String processName, String processSourceId) {

        Process process = processService.findByProcessNameAndProcessSourceId(processName, processSourceId);

        if (null != process) {
            processService.performHeartbeat(process);
        }

    }

    @Override
    public void releaseLock(String processName, String processSourceId) {

        try {

            Process process = processService.findByProcessNameAndProcessSourceId(processName, processSourceId);

            if (null != process) {
                processService.delete(process);
            }

        } catch (Exception e) {
            // Empty catch due to code will recover from this
        }

    }

    private boolean isProcessRunning(Process process, Duration duration) {

        Instant past = Instant.now().minus(duration.plusSeconds(duration.getSeconds()));

        return null != process &&
                null != process.getHeartbeat() &&
                past.isBefore(process.getHeartbeat());

    }

}
