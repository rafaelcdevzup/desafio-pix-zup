package br.com.orange.talents.shared.grpc.handlers

import br.com.orange.talents.pix.ChavePixExistenteException
import br.com.orange.talents.shared.grpc.ExceptionHandler
import br.com.orange.talents.shared.grpc.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChavePixExistenteExceptionHandler : ExceptionHandler<ChavePixExistenteException> {

    override fun handle(e: ChavePixExistenteException): StatusWithDetails {
        return StatusWithDetails(
            Status.ALREADY_EXISTS
                .withDescription(e.message)
                .withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixExistenteException
    }
}