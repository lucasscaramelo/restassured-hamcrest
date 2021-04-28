package br.ce.waquino.rest;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class AplicacaoTest extends BaseTest{

    private String token;

    @Before
    public void login() {
        Map<String, String> login = new HashMap<>();
        login.put("email", "lucas@teste.com.br");
        login.put("senha", "123");

         token = given()
                .body(login)
         .when()
                .post("/signin")
         .then()
                .statusCode(200)
                .extract().path("token");
    }

    @Test
    public void naoDeveAcessarAPISemToken() {
        given()
        .when()
                .get("/contas")
        .then()
                .statusCode(401);
    }

    @Test
    public void deveIncluirContaComSucesso() {
        given()
                .header("Authorization", "JWT " + token)
                .body("{ \"name\": \"conta\" }")
        .when()
                .post("/contas")
        .then()
                .statusCode(201);
    }

    @Test
    public void deveAlterarContaComSucesso() {
        given()
                .header("Authorization", "JWT " + token)
                .body("{ \"name\": \"conta alterada\" }")
        .when()
                .put("/contas/1785")
        .then()
                .statusCode(200)
                .body("name", is("conta alterada"));
    }

    @Test
    public void naoDeveContaComMesmoNome() {
        given()
                .header("Authorization", "JWT " + token)
                .body("{ \"name\": \"conta alterada\" }")
        .when()
                .post("/contas")
        .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"));
    }

    @Test
    public void deveInserirMovimentacaoComSucesso() {
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(17098);
        mov.setDescricao("Movimenta");
        mov.setEnvolvido("Envolvido");
        mov.setTipo("REC");
        mov.setData_transacao("01/01/2000");
        mov.setData_pagamento("10/05/2010");
        mov.setValor(100f);
        mov.setStatus(true);

        given()
                .header("Authorization", "JWT " + token)
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(201);
    }

}
