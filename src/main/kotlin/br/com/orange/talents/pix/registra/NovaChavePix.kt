package br.com.orange.talents.pix.registra

import br.com.orange.talents.pix.ChavePix
import br.com.orange.talents.pix.ContaAssociada
import br.com.orange.talents.pix.TipoDeChave
import br.com.orange.talents.pix.TipoDeConta
import br.com.orange.talents.shared.ValidUUID
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
data class NovaChavePix(
    @field:ValidUUID
    @field:NotBlank
    val clientId: String?,
    @field:NotNull
    val tipo: TipoDeChave?,
    @field:Size(max = 77)
    val chave: String?,
    @field:NotNull
    val tipoDeConta: TipoDeConta?
) {

    fun toModel(conta: ContaAssociada): ChavePix{
        return ChavePix(
            clientId = UUID.fromString(this.clientId),
            tipo = TipoDeChave.valueOf(this.tipo!!.name),
            chave = if (this.tipo == TipoDeChave.ALEATORIA) UUID.randomUUID().toString() else this.chave!!,
            tipoDeConta = TipoDeConta.valueOf(this.tipoDeConta!!.name),
            conta = conta
        )
    }
}