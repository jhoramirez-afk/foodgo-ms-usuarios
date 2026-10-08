package cl.duoc.jv0101.foodgo.usuarios;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CrudIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired cl.duoc.jv0101.foodgo.usuarios.repository.DireccionRepository children;

    @Test
    void crudRelationsAndCascadeThroughHttp() throws Exception {
        String created = mvc.perform(post("/api/usuarios").contentType("application/json")
                .content("""
{"nombre": "Cliente EP02", "rol": "CLIENTE", "email": "cliente@foodgo.cl"}
"""))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long parentId = mapper.readTree(created).get("id").asLong();
        String nested = "/api/usuarios/%s/direcciones".formatted(parentId);
        String child = mvc.perform(post(nested).contentType("application/json")
                .content("""
{"alias": "Casa", "calle": "Av. Principal 123", "comuna": "Santiago"}
"""))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long childId = mapper.readTree(child).get("id").asLong();
        mvc.perform(get("/api/usuarios/" + parentId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.direcciones[0].id").value(childId));
        mvc.perform(get("/api/usuarios")).andExpect(status().isOk());
        mvc.perform(get(nested)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(childId));
        mvc.perform(get("/api/direcciones/" + childId)).andExpect(status().isOk());
        mvc.perform(put("/api/usuarios/" + parentId).contentType("application/json")
                .content("""
{"nombre": "Cliente EP02 actualizado", "rol": "CLIENTE", "email": "cliente@foodgo.cl"}
""")).andExpect(status().isOk());
        mvc.perform(put("/api/direcciones/" + childId).contentType("application/json")
                .content("""
{"alias": "Casa", "calle": "Av. Principal 123", "comuna": "Santiago"}
""")).andExpect(status().isOk());
        mvc.perform(delete("/api/direcciones/" + childId)).andExpect(status().isNoContent());
        mvc.perform(get("/api/direcciones/" + childId)).andExpect(status().isNotFound());
        String second = mvc.perform(post(nested).contentType("application/json")
                .content("""
{"alias": "Casa", "calle": "Av. Principal 123", "comuna": "Santiago"}
"""))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long cascadeId = mapper.readTree(second).get("id").asLong();
        mvc.perform(delete("/api/usuarios/" + parentId)).andExpect(status().isNoContent());
        assertThat(children.existsById(cascadeId)).isFalse();
        mvc.perform(get("/api/usuarios/" + parentId)).andExpect(status().isNotFound());
    }

    @Test
    void malformedJsonAndIdsReturnStructuredErrors() throws Exception {
        mvc.perform(post("/api/usuarios").contentType("application/json").content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        mvc.perform(get("/api/usuarios/no-es-numero"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void databaseConstraintReturnsConflict() throws Exception {
        mvc.perform(post("/api/usuarios").contentType("application/json")
                .content("""
{"nombre": "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX", "rol": "CLIENTE", "email": "cliente@foodgo.cl"}
"""))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void validationAndMissingResourcesReturnStructuredErrors() throws Exception {
        mvc.perform(post("/api/usuarios").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        mvc.perform(get("/api/usuarios/9223372036854775807"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
        mvc.perform(post("/api/usuarios/%s/direcciones".formatted(Long.MAX_VALUE)).contentType("application/json")
                .content("""
{"alias": "Casa", "calle": "Av. Principal 123", "comuna": "Santiago"}
""")).andExpect(status().isNotFound());
    }
}
