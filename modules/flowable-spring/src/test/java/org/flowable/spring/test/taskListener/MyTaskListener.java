/* Licensed under the Apache License, Version 2.0 (the "License");
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

package org.flowable.spring.test.taskListener;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

/**
 * @author Joram Barrez
 */
public class MyTaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        delegateTask.setVariable("calledThroughNotify", delegateTask.getName() + "-notify");
    }

    @SuppressWarnings("unused")
    public void calledInExpression(DelegateTask task, String eventName) {
        String descriptions = task.getVariable("descriptions", String.class);
        String taskDescription = task.getVariable("taskDescription", String.class);
        task.setVariable("descriptions", descriptions + " + " + taskDescription);
    }

}
