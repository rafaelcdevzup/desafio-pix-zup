package br.com.orange.talents.shared.grpc.handlers

import br.com.orange.talents.pix.ChavePixNaoEncontradaException
import br.com.orange.talents.shared.grpc.ExceptionHandler
import br.com.orange.talents.shared.grpc.ExceptionHandler.*
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChavePixNaoEncrontradaExceptionHandler : ExceptionHandler<ChavePixNaoEncontradaException> {

    override fun handle(e: ChavePixNaoEncontradaException): StatusWithDetails {
        return StatusWithDetails(Status.NOT_FOUND
                                       .withDescription(e.message)
                                       .withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixNaoEncontradaException
    }
}