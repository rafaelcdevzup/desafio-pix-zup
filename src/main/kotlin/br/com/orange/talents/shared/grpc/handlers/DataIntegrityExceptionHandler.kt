package br.com.orange.talents.shared.grpc.handlers

import br.com.orange.talents.shared.grpc.ExceptionHandler
import br.com.orange.talents.shared.grpc.ExceptionHandler.*
import io.grpc.Status
import io.micronaut.context.MessageSource
import org.hibernate.exception.ConstraintViolationException
import javax.inject.Inject
import javax.inject.Singleton

/* A ideia deste manipulador é lidar com erros de restrições de banco de dados,
 *  como restrições únicas ou FK por exemplo
 */
@Singleton
class DataIntegrityExceptionHandler(@Inject var messageSource: MessageSource): ExceptionHandler<ConstraintViolationException> {

    override fun handle(e: ConstraintViolationException): StatusWithDetails {
        val constraintName = e.constraintName
        if (constraintName.isNullOrBlank()){
            return internalServerError(e)
        }
        val message = messageSource.getMessage("data.integrity.error.$constraintName", MessageSource.MessageContext.DEFAULT)
        return message
            .map { alreadyExistsError(it, e) }
            .orElse(internalServerError(e))
    }

    override fun supports(e: Exception): Boolean {
        return e is ConstraintViolationException
    }

    private fun internalServerError(e: ConstraintViolationException) =
        StatusWithDetails(Status.INTERNAL
            .withDescription("Unexpected internal server error")
            .withCause(e))

    private fun alreadyExistsError(message: String?, e: ConstraintViolationException) =
        StatusWithDetails(Status.ALREADY_EXISTS
                                .withDescription(message)
                                .withCause(e))
}