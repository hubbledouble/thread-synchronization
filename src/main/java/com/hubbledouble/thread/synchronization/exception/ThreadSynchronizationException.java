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

package com.hubbledouble.thread.synchronization.exception;

/**
 * All exceptions NOT thrown by {@link com.hubbledouble.thread.synchronization.RunnableCode}
 * will be wrapped into this type of exception. Such as issues connecting to DB to store the process status.
 *
 * @author Jorge Saldivar
 */
public class ThreadSynchronizationException extends RuntimeException {
    public ThreadSynchronizationException(Throwable cause) {
        super(cause);
    }
}
