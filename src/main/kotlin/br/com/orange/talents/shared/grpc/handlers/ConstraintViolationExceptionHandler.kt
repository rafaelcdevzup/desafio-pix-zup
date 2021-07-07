package br.com.orange.talents.shared.grpc.handlers

import br.com.orange.talents.shared.grpc.ExceptionHandler
import br.com.orange.talents.shared.grpc.ExceptionHandler.*
import com.google.protobuf.Any
import com.google.rpc.BadRequest
import com.google.rpc.Code
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

//Lida com os erros de validação do Bean adicionando suas violações aos trailers de solicitação (metadados)

@Singleton
class ConstraintViolationExceptionHandler : ExceptionHandler<ConstraintViolationException> {

    override fun handle(e: ConstraintViolationException): ExceptionHandler.StatusWithDetails {
        val details = BadRequest.newBuilder()
            .addAllFieldViolations(e.constraintViolations.map {
                BadRequest.FieldViolation.newBuilder()
                    .setField(it.propertyPath.last().name ?: "?? key ??")
                    .setDescription(it.message)
                    .build()
            })
            .build()
        val statusProto = com.google.rpc.Status.newBuilder()
            .setCode(Code.INVALID_ARGUMENT_VALUE)
            .setMessage("Dados inválidos")
            .addDetails(Any.pack(details))
            .build()
        return StatusWithDetails(statusProto)
    }

    override fun supports(e: Exception): Boolean {
        return e is ConstraintViolationException
    }
}