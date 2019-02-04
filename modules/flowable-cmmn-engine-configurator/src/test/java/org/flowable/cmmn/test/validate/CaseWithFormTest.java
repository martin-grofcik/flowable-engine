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
package org.flowable.cmmn.test.validate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.impl.CmmnTestRunner;
import org.flowable.common.engine.impl.interceptor.EngineConfigurationConstants;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.form.api.FormDefinition;
import org.flowable.form.api.FormRepositoryService;
import org.flowable.form.engine.FlowableFormValidationException;
import org.flowable.form.engine.FormEngines;
import org.flowable.task.api.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author martin.grofcik
 */
@RunWith(CmmnTestRunner.class)
public class CaseWithFormTest {
    protected static CmmnEngineConfiguration cmmnEngineConfiguration;
    protected static ProcessEngine processEngine;

    protected CmmnRuntimeService cmmnRuntimeService;
    protected CmmnTaskService cmmnTaskService;
    protected FormRepositoryService formRepositoryService;

    @BeforeClass
    public static void bootProcessEngine() {
        if (processEngine == null) {
            processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("flowable.process-cmmn-form.cfg.xml").buildProcessEngine();
            cmmnEngineConfiguration = (CmmnEngineConfiguration) processEngine.getProcessEngineConfiguration()
                .getEngineConfigurations().get(EngineConfigurationConstants.KEY_CMMN_ENGINE_CONFIG);
            CmmnTestRunner.setCmmnEngineConfiguration(cmmnEngineConfiguration);
        }
    }

    @Before
    public void initialize() {
        cmmnTaskService = cmmnEngineConfiguration.getCmmnTaskService();
        cmmnRuntimeService = cmmnEngineConfiguration.getCmmnRuntimeService();

        formRepositoryService = FormEngines.getDefaultFormEngine().getFormRepositoryService();
        formRepositoryService.createDeployment().
            addClasspathResource("org/flowable/cmmn/test/simple.form").
            deploy();
    }

    @After
    public void cleanDeployments() {
        formRepositoryService.createDeploymentQuery().list().forEach(
            formDeployment -> formRepositoryService.deleteDeployment(formDeployment.getId())
        );
    }

    @Test
    @CmmnDeployment(
        resources = {
            "org/flowable/cmmn/test/oneTasksCaseWithForm.cmmn"
        }
    )
    public void startCaseWithForm() {
        try {
            cmmnRuntimeService.createCaseInstanceBuilder().
                caseDefinitionKey("oneTaskCaseWithForm").
                startFormVariables(Collections.singletonMap("variable", "VariableValue")).
                startWithForm();
            fail("Validation exception expected");
        } catch (FlowableFormValidationException e) {
            assertThat("Validation failed by default", is(e.getMessage()));
        }
        assertThat(SideEffectTaskListener.getSideEffect(), is(0));
    }

    @Test
    @CmmnDeployment(
        resources = {
            "org/flowable/cmmn/test/oneTasksCaseWithForm.cmmn"
        }
    )
    public void startCaseWithFormWithoutVariables() {
        try {
            cmmnRuntimeService.createCaseInstanceBuilder().
                caseDefinitionKey("oneTaskCaseWithForm").
                startWithForm();
            fail("Validation exception expected");
        } catch (FlowableFormValidationException e) {
            assertThat("Validation failed by default", is(e.getMessage()));
        }
        assertThat(SideEffectTaskListener.getSideEffect(), is(0));
    }

    @Test
    @CmmnDeployment(
        resources = {
            "org/flowable/cmmn/test/oneTasksCaseWithForm.cmmn"
        }
    )
    public void completeCaseTaskWithForm() {
        CaseInstance caze = cmmnRuntimeService.createCaseInstanceBuilder().
            caseDefinitionKey("oneTaskCaseWithForm").
            start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caze.getId()).singleResult();
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery().formDefinitionKey("form1").singleResult();
        assertThat(SideEffectTaskListener.getSideEffect(), is(1));
        SideEffectTaskListener.reset();

        try {
            cmmnTaskService.completeTaskWithForm(task.getId(), formDefinition.getId(), "__COMPLETE", Collections.singletonMap("var", "value"));
            fail("Validation exception expected");
        } catch (FlowableFormValidationException e) {
            assertThat("Validation failed by default", is(e.getMessage()));
        }
        assertThat(SideEffectTaskListener.getSideEffect(), is(0));
    }

    @Test
    @CmmnDeployment(
        resources = {
            "org/flowable/cmmn/test/oneTasksCaseWithForm.cmmn"
        }
    )
    public void completeCaseTaskWithFormWithoutVariables() {
        CaseInstance caze = cmmnRuntimeService.createCaseInstanceBuilder().
            caseDefinitionKey("oneTaskCaseWithForm").
            start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caze.getId()).singleResult();
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery().formDefinitionKey("form1").singleResult();
        assertThat(SideEffectTaskListener.getSideEffect(), is(1));
        SideEffectTaskListener.reset();

        try {
            cmmnTaskService.completeTaskWithForm(task.getId(), formDefinition.getId(), "__COMPLETE", null);
            fail("Validation exception expected");
        } catch (FlowableFormValidationException e) {
            assertThat("Validation failed by default", is(e.getMessage()));
        }
        assertThat(SideEffectTaskListener.getSideEffect(), is(0));
    }

}
