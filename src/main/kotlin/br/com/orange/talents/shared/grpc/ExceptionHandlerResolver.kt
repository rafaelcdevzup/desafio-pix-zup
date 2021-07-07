package br.com.orange.talents.shared.grpc

import br.com.orange.talents.shared.grpc.handlers.DefaultExceptionHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExceptionHandlerResolver(@Inject private val handlers: List<ExceptionHandler<*>>,
) {

    private var defaultHandler: ExceptionHandler<Exception> = DefaultExceptionHandler()

    //Podemos substituir o manipulador de exceção padrão por meio deste construtor
    constructor(handlers: List<ExceptionHandler<Exception>>, defaultHandler: ExceptionHandler<Exception>) :this(handlers){
        this.defaultHandler = defaultHandler
    }

    fun resolver(e: Exception): ExceptionHandler<*>{
        val foundHandlers = handlers.filter { it.supports(e) }

        if (foundHandlers.size > 1)
            throw IllegalStateException("muito manipuladores suportando a mesma exceção `${e.javaClass.name}`: $foundHandlers")

        return foundHandlers.firstOrNull() ?: defaultHandler
    }
}