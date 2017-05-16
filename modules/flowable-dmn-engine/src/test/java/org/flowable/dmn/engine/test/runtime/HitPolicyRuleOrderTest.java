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
package org.flowable.dmn.engine.test.runtime;

import org.flowable.dmn.api.DmnRuleService;
import org.flowable.dmn.engine.DmnEngine;
import org.flowable.dmn.engine.test.DmnDeploymentAnnotation;
import org.flowable.dmn.engine.test.FlowableDmnRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yvo Swillens
 */
public class HitPolicyRuleOrderTest {

    @Rule
    public FlowableDmnRule flowableDmnRule = new FlowableDmnRule();

    @Test
    @DmnDeploymentAnnotation
    public void ruleOrderHitPolicy() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();

        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();

        Map<String, Object> inputVariables = new HashMap<>();
        inputVariables.put("inputVariable1", 13);

        List<Map<String, Object>> result = dmnRuleService.executeDecisionByKey("decision1", inputVariables);

        Assert.assertEquals(2, result.size());
        Assert.assertEquals("result2", result.get(0).get("outputVariable1"));
        Assert.assertEquals("result4", result.get(1).get("outputVariable1"));
    }
}
