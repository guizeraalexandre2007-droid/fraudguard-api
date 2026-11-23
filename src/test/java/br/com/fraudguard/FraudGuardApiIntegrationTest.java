package br.com.fraudguard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FraudGuardApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private Long criar(String url, String json) throws Exception {
        String body = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("id").asLong();
    }

    private String cliente() {
        return """
            {"nome":"Ana Silva","email":"ana@exemplo.com","documento":"12345678901"}
            """;
    }
    private String regra(String nome) {
        return """
            {"nome":"%s","descricao":"Monitorar transacoes de valor alto","limiteValor":1000.00,"ativa":true}
            """.formatted(nome);
    }
    private String pix(Long clienteId, String ids) {
        return """
            {"valor":2500.50,"status":"PENDENTE","clienteId":%d,"regraIds":%s,
             "chavePix":"ana@pix.com","bancoDestino":"Banco Exemplo"}
            """.formatted(clienteId, ids);
    }
    private String cartao(Long clienteId, String ids) {
        return """
            {"valor":190.90,"status":"APROVADA","clienteId":%d,"regraIds":%s,
             "ultimosQuatroDigitos":"1234","bandeira":"VISA"}
            """.formatted(clienteId, ids);
    }

    @Test
    void crudCompletoDeTodasAsEntidadesEConsultaPolimorfica() throws Exception {
        Long c = criar("/api/clientes", cliente());
        Long r = criar("/api/regras", regra("Valor Alto"));
        Long p = criar("/api/transacoes/pix", pix(c, "[" + r + "]"));
        Long k = criar("/api/transacoes/cartao", cartao(c, "[" + r + "]"));

        mvc.perform(get("/api/clientes")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/clientes/{id}", c)).andExpect(jsonPath("$.nome").value("Ana Silva"));
        mvc.perform(get("/api/regras")).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/regras/{id}", r)).andExpect(jsonPath("$.nome").value("Valor Alto"));
        mvc.perform(get("/api/transacoes")).andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/api/transacoes/pix")).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/transacoes/cartao")).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/transacoes/{id}", p)).andExpect(jsonPath("$.tipo").value("PIX"));
        mvc.perform(get("/api/transacoes/pix/{id}", p))
            .andExpect(jsonPath("$.chavePix").value("ana@pix.com"))
            .andExpect(jsonPath("$.regraIds[0]").value(r.intValue()));
        mvc.perform(get("/api/transacoes/cartao/{id}", k))
            .andExpect(jsonPath("$.tipo").value("CARTAO"))
            .andExpect(jsonPath("$.ultimosQuatroDigitos").value("1234"));

        mvc.perform(put("/api/clientes/{id}", c).contentType(MediaType.APPLICATION_JSON)
            .content(""" 
                {"nome":"Ana Souza","email":"ana@exemplo.com","documento":"12345678901"}
                """)).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Ana Souza"));
        mvc.perform(put("/api/regras/{id}", r).contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"nome":"Valor Muito Alto","descricao":"Nova regra","limiteValor":3000.00,"ativa":false}
                """)).andExpect(status().isOk()).andExpect(jsonPath("$.ativa").value(false));
        mvc.perform(put("/api/transacoes/pix/{id}", p).contentType(MediaType.APPLICATION_JSON)
            .content(pix(c, "[]"))).andExpect(status().isOk()).andExpect(jsonPath("$.regraIds", hasSize(0)));
        mvc.perform(put("/api/transacoes/cartao/{id}", k).contentType(MediaType.APPLICATION_JSON)
            .content(cartao(c, "[]"))).andExpect(status().isOk()).andExpect(jsonPath("$.regraIds", hasSize(0)));

        mvc.perform(delete("/api/transacoes/pix/{id}", p)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/transacoes/cartao/{id}", k)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/regras/{id}", r)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/clientes/{id}", c)).andExpect(status().isNoContent());
        mvc.perform(get("/api/transacoes")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void mesmaRegraPodeEstarEmMultiplasTransacoesEExclusaoDesassocia() throws Exception {
        Long c = criar("/api/clientes", cliente());
        Long r1 = criar("/api/regras", regra("Alta Frequencia"));
        Long r2 = criar("/api/regras", regra("Alto Valor"));
        Long p = criar("/api/transacoes/pix", pix(c, "[" + r1 + "," + r2 + "]"));
        Long k = criar("/api/transacoes/cartao", cartao(c, "[" + r1 + "]"));
        mvc.perform(get("/api/transacoes/pix/{id}", p))
            .andExpect(jsonPath("$.regraIds", hasSize(2)));
        mvc.perform(get("/api/transacoes/cartao/{id}", k))
            .andExpect(jsonPath("$.regraIds[0]").value(r1.intValue()));

        mvc.perform(delete("/api/regras/{id}", r1)).andExpect(status().isNoContent());
        mvc.perform(get("/api/transacoes/pix/{id}", p))
            .andExpect(jsonPath("$.regraIds[0]").value(r2.intValue()));
        mvc.perform(get("/api/transacoes/cartao/{id}", k))
            .andExpect(jsonPath("$.regraIds", hasSize(0)));
    }

    @Test
    void validaErrosDeNegocioEEntidadesNaoEncontradas() throws Exception {
        mvc.perform(get("/api/clientes/987654")).andExpect(status().isNotFound());
        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"nome":"","email":"invalido","documento":"x"}
                """))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.campos.email").exists());
        mvc.perform(post("/api/transacoes/pix").contentType(MediaType.APPLICATION_JSON)
            .content(pix(999999L, "[]"))).andExpect(status().isNotFound());

        Long c = criar("/api/clientes", cliente());
        Long p = criar("/api/transacoes/pix", pix(c, "[]"));
        mvc.perform(delete("/api/clientes/{id}", c))
            .andExpect(status().isConflict());
        mvc.perform(get("/api/transacoes/cartao/{id}", p))
            .andExpect(status().isNotFound());
        mvc.perform(post("/api/transacoes/cartao").contentType(MediaType.APPLICATION_JSON)
            .content(cartao(c, "[123456789]"))).andExpect(status().isNotFound());
        mvc.perform(post("/api/transacoes/cartao").contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"valor":-10,"status":"PENDENTE","clienteId":1,"regraIds":[],"ultimosQuatroDigitos":"1","bandeira":"VISA"}
                """))
            .andExpect(status().isBadRequest());
    }
}
