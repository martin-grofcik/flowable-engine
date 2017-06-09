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
package org.flowable.app.service.idm;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.google.common.base.Function;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.flowable.app.model.common.RemoteGroup;
import org.flowable.app.model.common.RemoteToken;
import org.flowable.app.model.common.RemoteUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import static org.flowable.app.rest.HttpRequestHelper.executeHttpGet;

@Service
public class RemoteIdmServiceImpl implements RemoteIdmService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteIdmService.class);

    private static final String PROPERTY_URL = "idm.app.url";
    private static final String PROPERTY_ADMIN_USER = "idm.admin.user";
    private static final String PROPERTY_ADMIN_PASSWORD = "idm.admin.password";

    @Autowired
    protected Environment environment;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String url;
    protected String adminUser;
    protected String adminPassword;

    @PostConstruct
    protected void init() {
        url = environment.getRequiredProperty(PROPERTY_URL);
        adminUser = environment.getRequiredProperty(PROPERTY_ADMIN_USER);
        adminPassword = environment.getRequiredProperty(PROPERTY_ADMIN_PASSWORD);
    }

    @Override
    public RemoteUser authenticateUser(String username, String password) {
        JsonNode json = callRemoteIdmService(url + "/api/idm/users/" + encode(username), username, password);
        if (json != null) {
            return parseUserInfo(json);
        }
        return null;
    }

    @Override
    public RemoteToken getToken(String tokenValue) {
        JsonNode json = callRemoteIdmService(url + "/api/idm/tokens/" + encode(tokenValue), adminUser, adminPassword);
        if (json != null) {
            RemoteToken token = new RemoteToken();
            token.setId(json.get("id").asText());
            token.setValue(json.get("value").asText());
            token.setUserId(json.get("userId").asText());
            return token;
        }
        return null;
    }

    @Override
    public RemoteUser getUser(String userId) {
        JsonNode json = callRemoteIdmService(url + "/api/idm/users/" + encode(userId), adminUser, adminPassword);
        if (json != null) {
            return parseUserInfo(json);
        }
        return null;
    }

    @Override
    public List<RemoteUser> findUsersByNameFilter(String filter) {
        JsonNode json = callRemoteIdmService(url + "/api/idm/users?filter=" + encode(filter), adminUser, adminPassword);
        if (json != null) {
            return parseUsersInfo(json);
        }
        return new ArrayList<RemoteUser>();
    }

    @Override
    public List<RemoteUser> findUsersByGroup(String groupId) {
        JsonNode json = callRemoteIdmService(url + "/api/idm/groups/" + encode(groupId) + "/users", adminUser, adminPassword);
        if (json != null) {
            return parseUsersInfo(json);
        }
        return new ArrayList<RemoteUser>();
    }

    @Override
    public List<RemoteGroup> findGroupsByNameFilter(String filter) {
        JsonNode json = callRemoteIdmService(url + "/api/idm/groups?filter=" + encode(filter), adminUser, adminPassword);
        if (json != null) {
            return parseGroupsInfo(json);
        }
        return new ArrayList<RemoteGroup>();
    }

    protected JsonNode callRemoteIdmService(String url, String username, String password) {
        return executeHttpGet(url, username, password, new Function<HttpResponse, JsonNode>() {
            @Override
            public JsonNode apply(HttpResponse httpResponse) {
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    try {
                        return objectMapper.readTree(httpResponse.getEntity().getContent());
                    } catch (IOException e) {
                        logger.warn("Exception while getting token", e);
                    }
                }
                return null;
            }
        });
    }

    protected List<RemoteUser> parseUsersInfo(JsonNode json) {
        List<RemoteUser> result = new ArrayList<RemoteUser>();
        if (json != null && json.isArray()) {
            ArrayNode array = (ArrayNode) json;
            for (JsonNode userJson : array) {
                result.add(parseUserInfo(userJson));
            }
        }
        return result;
    }

    protected RemoteUser parseUserInfo(JsonNode json) {
        RemoteUser user = new RemoteUser();
        user.setId(json.get("id").asText());
        user.setFirstName(json.get("firstName").asText());
        user.setLastName(json.get("lastName").asText());
        user.setEmail(json.get("email").asText());
        user.setFullName(json.get("fullName").asText());

        if (json.has("groups")) {
            for (JsonNode groupNode : ((ArrayNode) json.get("groups"))) {
                user.getGroups().add(new RemoteGroup(groupNode.get("id").asText(), groupNode.get("name").asText()));
            }
        }

        if (json.has("privileges")) {
            for (JsonNode privilegeNode : ((ArrayNode) json.get("privileges"))) {
                user.getPrivileges().add(privilegeNode.asText());
            }
        }

        return user;
    }

    protected List<RemoteGroup> parseGroupsInfo(JsonNode json) {
        List<RemoteGroup> result = new ArrayList<RemoteGroup>();
        if (json != null && json.isArray()) {
            ArrayNode array = (ArrayNode) json;
            for (JsonNode userJson : array) {
                result.add(parseGroupInfo(userJson));
            }
        }
        return result;
    }

    protected RemoteGroup parseGroupInfo(JsonNode json) {
        RemoteGroup group = new RemoteGroup();
        group.setId(json.get("id").asText());
        group.setName(json.get("name").asText());
        return group;
    }

    protected String encode(String s) {
        if (s == null) {
            return "";
        }

        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            logger.warn("Could not encode url param", e);
            return null;
        }
    }

}
