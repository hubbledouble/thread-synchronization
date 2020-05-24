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

package com.hubbledouble.thread.synchronization;

import com.hubbledouble.thread.synchronization.exception.RunnableCodeException;
import com.hubbledouble.thread.synchronization.exception.ThreadSynchronizationException;
import com.hubbledouble.thread.synchronization.processor.ThreadProcessor;
import com.hubbledouble.thread.synchronization.repository.impl.MongoProcessRepositoryImpl;
import com.hubbledouble.thread.synchronization.service.impl.LockServiceImpl;
import com.hubbledouble.thread.synchronization.service.impl.ProcessServiceImpl;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * Thread Synchronization.
 * Use this class to ensure two or more concurrent processes or threads
 * do not simultaneously execute some particular program segment known as critical section.
 *
 * @author Jorge Saldivar
 * @see <a href="https://en.wikipedia.org/wiki/Synchronization_(computer_science)#Thread_or_process_synchronization">
 * Thread Synchronization info
 * </a>
 */
public class ThreadSynchronization {

    private final ThreadProcessor threadProcessor;

    public ThreadSynchronization(MongoOperations mongoOperations) {
        this.threadProcessor =
                new ThreadProcessor(
                        new LockServiceImpl(
                                new ProcessServiceImpl(
                                        new MongoProcessRepositoryImpl(mongoOperations))));
    }

    /**
     * Call this method to perform thread synchronization
     * <p>
     * Usage:
     * <pre>
     *  <code>
     *       ThreadSynchronization threadSynchronization = new ThreadSynchronization(mongoOperations);
     *       threadSynchronization.execute("processName", () -> {
     *           String usage = "Usage";
     *           System.out.println(usage);
     *       });
     *  </code>
     *  </pre>
     *
     * @param processName
     * @param runnableCode
     * @return
     * @throws RunnableCodeException
     * @throws ThreadSynchronizationException
     */
    public boolean execute(String processName, RunnableCode runnableCode)
            throws RunnableCodeException, ThreadSynchronizationException {
        return threadProcessor.execute(processName, runnableCode);
    }

}