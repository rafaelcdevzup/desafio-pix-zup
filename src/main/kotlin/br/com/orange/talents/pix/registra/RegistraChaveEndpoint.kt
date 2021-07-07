package br.com.orange.talents.pix.registra

import br.com.orange.talents.KeymanagerRegistraGrpcServiceGrpc
import br.com.orange.talents.RegistraChavePixRequest
import br.com.orange.talents.RegistraChavePixResponse
import br.com.orange.talents.shared.grpc.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RegistraChaveEndpoint(@Inject private val service: NovaChavePixService,)
    : KeymanagerRegistraGrpcServiceGrpc.KeymanagerRegistraGrpcServiceImplBase() {

    override fun registra(
        request: RegistraChavePixRequest,
        responseObserver: StreamObserver<RegistraChavePixResponse>
    ) {

        val novaChave = request.toModel()
        val chaveCriada = service.registra(novaChave)

        responseObserver.onNext(RegistraChavePixResponse.newBuilder()
                                        .setClientId(chaveCriada.clientId.toString())
                                        .setPixId(chaveCriada.id.toString())
                                        .build())
        responseObserver.onCompleted()
    }
}