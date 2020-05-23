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
import com.hubbledouble.thread.synchronization.repository.ProcessRepository;
import com.hubbledouble.thread.synchronization.service.ProcessService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Main functionality for interacting with the {@link ProcessRepository}
 *
 * @author Jorge Saldivar
 */
public class ProcessServiceImpl implements ProcessService {

    private final ProcessRepository processRepository;

    public ProcessServiceImpl(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    @Override
    public Process findByProcessName(String processName) {
        return processRepository.findByProcessName(processName);
    }

    @Override
    public Process findByProcessNameAndProcessSourceId(String processName, String processSourceId) {
        return processRepository.findByProcessNameAndProcessSourceId(processName, processSourceId);
    }

    @Override
    public boolean insertLock(String processName, String processSourceId) {
        return processRepository.insertLock(processName, processSourceId);
    }

    @Override
    public boolean transferLock(Process process, String processSourceId) {
        return processRepository.transferLock(process, processSourceId);
    }

    @Override
    public void performHeartbeat(Process process) {
        processRepository.performHeartBeat(process);
    }

    @Override
    public void delete(Process process) {
        processRepository.delete(process);
    }

    @Override
    public void deleteOldCompletedProcesses() {
        Instant past = Instant.now().minus(10, ChronoUnit.MINUTES);
        processRepository.deleteProcessOlderThan(past);
    }

}