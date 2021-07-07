package br.com.orange.talents.pix.registra

import br.com.orange.talents.RegistraChavePixRequest
import br.com.orange.talents.TipoDeChave.*
import br.com.orange.talents.TipoDeConta.*
import br.com.orange.talents.pix.TipoDeChave
import br.com.orange.talents.pix.TipoDeConta
import br.com.orange.talents.pix.registra.NovaChavePix

fun RegistraChavePixRequest.toModel(): NovaChavePix {
    return NovaChavePix(
        clientId = clientId,
        tipo = when(tipoDeChave){
            UNKNOWN_TIPO_CHAVE -> null
            else -> TipoDeChave.valueOf(tipoDeChave.name)
        },
        chave = chave,
        tipoDeConta = when(tipoDeConta){
            UNKNOWN_TIPO_CONTA -> null
            else -> TipoDeConta.valueOf(tipoDeConta.name)
        }
    )
}