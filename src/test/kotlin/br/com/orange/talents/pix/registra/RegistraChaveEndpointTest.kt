package br.com.orange.talents.pix.registra

import br.com.orange.talents.KeymanagerRegistraGrpcServiceGrpc
import br.com.orange.talents.RegistraChavePixRequest
import br.com.orange.talents.TipoDeChave
import br.com.orange.talents.TipoDeConta
import br.com.orange.talents.integration.itau.ContasDeClientesNoItauClient
import br.com.orange.talents.integration.itau.DadosDaContaResponse
import br.com.orange.talents.integration.itau.InstituicaoResponse
import br.com.orange.talents.integration.itau.TitularResponse
import br.com.orange.talents.pix.ChavePix
import br.com.orange.talents.pix.ChavePixRepository
import br.com.orange.talents.pix.ContaAssociada
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*
import javax.inject.Inject

/* é necessario desabilitar o controle transacional, por conta do gRPC server rodar em thread separada.
 * Caso não desabilite pode gerar problemas na preparação do cenario
 */
@MicronautTest(transactional = false)
internal class RegistraChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeymanagerRegistraGrpcServiceGrpc.KeymanagerRegistraGrpcServiceBlockingStub
){
    @Inject
    lateinit var itauClient: ContasDeClientesNoItauClient;

    companion object{
        val CLIENT_ID = UUID.randomUUID()
    }

    @BeforeEach
    fun setup(){
        repository.deleteAll()
    }

    @Test
    fun `deve registrar nova chave pix` () {
        //cenário
        `when`(itauClient.buscaContasPorTipo(clientId = CLIENT_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponse()))
        //ação
        val response = grpcClient.registra(RegistraChavePixRequest.newBuilder()
                                                            .setClientId(CLIENT_ID.toString())
                                                            .setTipoDeChave(TipoDeChave.CPF)
                                                            .setChave("86259871503")
                                                            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                                                            .build())
        //validação
        with(response){
           assertEquals(CLIENT_ID.toString(), clientId)
           assertNotNull(pixId)
        }
    }

    @Test
    fun `nao deve registrar chave pix quando chave existente` (){
        //cenário
        repository.save(chave(
            tipo = br.com.orange.talents.pix.TipoDeChave.CPF,
            chave = "86259871503",
            clientId = CLIENT_ID
        ))
        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registra(RegistraChavePixRequest.newBuilder()
                .setClientId(CLIENT_ID.toString())
                .setTipoDeChave(TipoDeChave.CPF)
                .setChave("86259871503")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build())
        }
        // validação
        with(thrown){
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave Pix '86259871503' existente", status.description)
        }
    }




    private fun dadosDaContaResponse(): DadosDaContaResponse {
        return DadosDaContaResponse(
            tipo = "CONTA_CORRENTE",
            instituicao = InstituicaoResponse("UNIBANCO ITAU SA", ContaAssociada.ITAU_UNIBANCO_ISPB),
            agencia = "1234",
            numero = "291900",
            titular = TitularResponse("Rafael Ponte", "63657520325")
        )
    }


    @MockBean(ContasDeClientesNoItauClient::class)
    fun itauClients(): ContasDeClientesNoItauClient? {
        return Mockito.mock(ContasDeClientesNoItauClient::class.java)
    }

    @Factory
    class Clients{
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerRegistraGrpcServiceGrpc.KeymanagerRegistraGrpcServiceBlockingStub?{
            return KeymanagerRegistraGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun chave(
       tipo: br.com.orange.talents.pix.TipoDeChave,
       chave: String = UUID.randomUUID().toString(),
       clientId: UUID = UUID.randomUUID(),
    ): ChavePix {
        return ChavePix(
            clientId = clientId,
            tipo = tipo,
            chave = chave,
            tipoDeConta = br.com.orange.talents.pix.TipoDeConta.CONTA_CORRENTE,
            conta = ContaAssociada(
                instituicao = "UNIBANCO ITAU",
                nomeDoTitular = "Rafael Ponte",
                cpfDoTitular = "86259871503",
                agencia = "1218",
                numeroDaConta = "291900"
            )
        )
    }
}