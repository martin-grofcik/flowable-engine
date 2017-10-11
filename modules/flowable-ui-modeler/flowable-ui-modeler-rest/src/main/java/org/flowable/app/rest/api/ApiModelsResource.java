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
package org.flowable.app.rest.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;

import org.flowable.app.domain.editor.Model;
import org.flowable.app.model.common.ResultListDataRepresentation;
import org.flowable.app.model.editor.ModelKeyRepresentation;
import org.flowable.app.model.editor.ModelRepresentation;
import org.flowable.app.security.SecurityUtils;
import org.flowable.app.service.api.ModelService;
import org.flowable.app.service.editor.FlowableModelQueryService;
import org.flowable.app.service.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ApiModelsResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiModelsResource.class);

    @Autowired
    protected FlowableModelQueryService modelQueryService;

    @Autowired
    protected ModelService modelService;

    @Autowired
    protected ObjectMapper objectMapper;

    @RequestMapping(value = "/editor/models", method = RequestMethod.GET, produces = "application/json")
    public ResultListDataRepresentation getModels(@RequestParam(required = false) String filter, @RequestParam(required = false) String sort, @RequestParam(required = false) Integer modelType,
            HttpServletRequest request) {

        return modelQueryService.getModels(filter, sort, modelType, request);
    }

    @RequestMapping(value = "/editor/import-process-model", method = RequestMethod.POST, produces = "application/json")
    public ModelRepresentation importProcessModel(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        return modelQueryService.importProcessModel(request, file);
    }

    @RequestMapping(value = "/editor/import-simulation-model", method = RequestMethod.POST, produces = "application/json")
    public ModelRepresentation importTestModel(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        return modelQueryService.importSimulationModel(request, file);
    }

    @RequestMapping(value = "/editor/models", method = RequestMethod.POST, produces = "application/json")
    public ModelRepresentation createModel(@RequestBody ModelRepresentation modelRepresentation, @RequestParam(required = false) String skeleton) {
        modelRepresentation.setKey(modelRepresentation.getKey().replaceAll(" ", ""));

        ModelKeyRepresentation modelKeyInfo = modelService.validateModelKey(null, modelRepresentation.getModelType(), modelRepresentation.getKey());
        if (modelKeyInfo.isKeyAlreadyExists()) {
            throw new BadRequestException("Provided model key already exists: " + modelRepresentation.getKey());
        }

        Model newModel = modelService.createModel(modelRepresentation, skeleton, SecurityUtils.getCurrentUserObject());
        return new ModelRepresentation(newModel);
    }

}
