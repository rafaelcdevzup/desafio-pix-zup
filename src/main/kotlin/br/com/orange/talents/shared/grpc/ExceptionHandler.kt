package br.com.orange.talents.shared.grpc

import io.grpc.Metadata
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto

interface ExceptionHandler<in E: Exception> {

    //Lida com exceção e mapeia para StatusWithDetails
    fun handle(e: E): StatusWithDetails

    //Verifica se esta instância pode lidar com a exceção especificada ou não
    fun supports(e: Exception): Boolean

    //Wrapper simples para status e metadados (trailers)
    data class StatusWithDetails(val status: Status, val metadata: Metadata = Metadata()) {
        constructor(se: StatusRuntimeException): this(se.status, se.trailers ?: Metadata())
        constructor(sp: com.google.rpc.Status): this(StatusProto.toStatusRuntimeException(sp))

        fun asRuntimeException(): StatusRuntimeException{
            return status.asRuntimeException(metadata)
        }
    }
}