package com.admin.auth.json;


import com.admin.auth.user.PrincipalLite;
import com.admin.common.util.JsonUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.Set;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2023/9/8
 */
public class UserInfoDeserializer extends JsonDeserializer<PrincipalLite> {

    private static final TypeReference<Set<SimpleGrantedAuthority>> SIMPLE_GRANTED_AUTHORITY_SET = new TypeReference<Set<SimpleGrantedAuthority>>() {
    };

    /**
     * This method will create {@link User} object. It will ensure successful object
     * creation even if password key is null in serialized json, because credentials may
     * be removed from the {@link User} by invoking {@link User#eraseCredentials()}. In
     * that case there won't be any password key in serialized json.
     * @param jp the JsonParser
     * @param ctxt the DeserializationContext
     * @return the user
     * @throws IOException if a exception during IO occurs
     * @throws JsonProcessingException if an error during JSON processing occurs
     */
    @Override
    public PrincipalLite deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);
//        JsonNode passwordNode = readJsonNode(jsonNode, "password");
//        String username = readJsonNode(jsonNode, "userName").asText();
//        String language = readJsonNode(jsonNode, "language").asText();
//        String login = readJsonNode(jsonNode, "login").asText();
//        String status = readJsonNode(jsonNode, "status").asText();
//        Long tenantId = readJsonNode(jsonNode, "tenantId").asLong();
//        Long currentRoleId = readJsonNode(jsonNode, "currentRoleId").asLong();
//        Long currentTenantId = readJsonNode(jsonNode, "currentTenantId").asLong();
//        Long accountId = readJsonNode(jsonNode, "accountId").asLong();
//        Boolean activated = readJsonNode(jsonNode, "activated").asBoolean();
//        String tenantCode = readJsonNode(jsonNode, "tenantCode").asText();
//        String email = readJsonNode(jsonNode, "email").asText();
//        String mobile = readJsonNode(jsonNode, "mobile").asText();
//        String userCode = readJsonNode(jsonNode, "userCode").asText();
//        String clientId = readJsonNode(jsonNode, "clientId").asText();
//        String roleText = readJsonNode(jsonNode, "roles").asText();
        return JsonUtil.fromJson(jsonNode.toString(), PrincipalLite.class);
        // return mapper.convertValue(jsonNode.toString(), PrincipalLite.class);

    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }

}
