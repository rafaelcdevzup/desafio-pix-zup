package br.com.orange.talents.integration.itau

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client(value = "\${itau.contas.url}")
interface ContasDeClientesNoItauClient {

    @Get(value = "/api/v1/clientes/{clientId}/contas{?tipo}")
    fun buscaContasPorTipo(@PathVariable clientId: String, @QueryValue tipo: String): HttpResponse<DadosDaContaResponse>
}