package cl.duoc.jv0101.foodgo.usuarios;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CrudIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private String unique(String json) { return json.replace("TEST20261009", UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase()); }

    private long createParent() throws Exception {
        String result = mvc.perform(post("/api/usuarios").contentType("application/json")
                .content(unique("""
{"nombre":"Camila Soto","rol":"CLIENTE","email":"camila.soto.TEST20261009@example.com"}
"""))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(result).get("id").asLong();
    }

    private long createChild(String nested) throws Exception {
        String result = mvc.perform(post(nested).contentType("application/json")
                .content("""
{"alias":"Casa","calle":"Los Aromos 1450, departamento 302","comuna":"Ñuñoa"}
""")).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(result).get("id").asLong();
    }

    @Test
    void crudRelationsAndCascadeThroughHttp() throws Exception {
        long id = createParent();
        String nested = "/api/usuarios/" + id + "/direcciones";
        long childId = createChild(nested);
        mvc.perform(get("/api/usuarios/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.direcciones[0].id").value(childId));
        mvc.perform(get("/api/usuarios")).andExpect(status().isOk());
        mvc.perform(get(nested)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(childId));
        mvc.perform(get("/api/direcciones/" + childId)).andExpect(status().isOk());
        mvc.perform(put("/api/usuarios/" + id).contentType("application/json").content(unique("""
{"nombre":"Camila Soto Rojas","rol":"CLIENTE","email":"camila.soto.TEST20261009@example.com"}
"""))).andExpect(status().isOk());
        mvc.perform(put("/api/direcciones/" + childId).contentType("application/json").content("""
{"alias":"Trabajo","calle":"Providencia 2150, oficina 604","comuna":"Providencia"}
""")).andExpect(status().isOk());
        mvc.perform(delete("/api/direcciones/" + childId)).andExpect(status().isNoContent());
        mvc.perform(get("/api/direcciones/" + childId)).andExpect(status().isNotFound());
        long cascadeId = createChild(nested);
        mvc.perform(delete("/api/usuarios/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/usuarios/" + id)).andExpect(status().isNotFound());
        mvc.perform(get("/api/direcciones/" + cascadeId)).andExpect(status().isNotFound());
    }

    @Test
    void malformedJsonAndIdsReturnStructuredErrors() throws Exception {
        mvc.perform(post("/api/usuarios").contentType("application/json").content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        mvc.perform(get("/api/usuarios/no-es-numero"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void validationAndMissingResourcesReturnStructuredErrors() throws Exception {
        mvc.perform(post("/api/usuarios").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors").isNotEmpty());
        mvc.perform(get("/api/usuarios/9223372036854775807"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
        mvc.perform(post("/api/usuarios/9223372036854775807/direcciones").contentType("application/json")
                .content("""
{"alias":"Casa","calle":"Los Aromos 1450, departamento 302","comuna":"Ñuñoa"}
""")).andExpect(status().isNotFound());
        var longBody = (com.fasterxml.jackson.databind.node.ObjectNode) mapper.readTree("""
{"nombre":"Camila Soto","rol":"CLIENTE","email":"camila.soto.TEST20261009@example.com"}
""");
        longBody.put("nombre", "X".repeat(300));
        mvc.perform(post("/api/usuarios").contentType("application/json").content(longBody.toString())).andExpect(status().isBadRequest());
    }

    static Stream<Arguments> invalidInputs() {
        return Stream.of(
            Arguments.of("Correo inválido", "parent", """
{"nombre":"Camila Soto","rol":"CLIENTE","email":"camila-sin-correo"}
""", "email"),
            Arguments.of("Rol inválido", "parent", """
{"nombre":"Camila Soto","rol":"SUPERUSUARIO","email":"camila.soto.TEST20261009@example.com"}
""", "rol"),
            Arguments.of("Dirección vacía", "child", """
{"alias":"Casa","calle":"","comuna":"Ñuñoa"}
""", "calle")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidInputs")
    void businessValidationReturns400WithField(String name, String target, String body, String field) throws Exception {
        if ("parent".equals(target)) {
            mvc.perform(post("/api/usuarios").contentType("application/json").content(unique(body)))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors." + field).exists());
        } else {
            long id = createParent();
            mvc.perform(post("/api/usuarios/" + id + "/direcciones").contentType("application/json").content(body))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors." + field).exists());
            mvc.perform(delete("/api/usuarios/" + id)).andExpect(status().isNoContent());
        }
    }

    @Test
    void correoUnicoInclusoConDistintoUsoDeMayusculas() throws Exception {
        long id = createParent();
        String email = mapper.readTree(mvc.perform(get("/api/usuarios/" + id)).andReturn().getResponse().getContentAsString()).get("email").asText();
        var duplicado = mapper.readTree("""
{"nombre":"Camila Soto","rol":"CLIENTE","email":"camila.soto.TEST20261009@example.com"}
""");
        ((com.fasterxml.jackson.databind.node.ObjectNode) duplicado).put("email", email.toUpperCase(java.util.Locale.ROOT));
        mvc.perform(post("/api/usuarios").contentType("application/json").content(duplicado.toString()))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("Ya existe un usuario con ese correo"));
        long otro = createParent();
        mvc.perform(put("/api/usuarios/" + otro).contentType("application/json").content(duplicado.toString())).andExpect(status().isConflict());
        mvc.perform(delete("/api/usuarios/" + id)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/usuarios/" + otro)).andExpect(status().isNoContent());
    }

}
