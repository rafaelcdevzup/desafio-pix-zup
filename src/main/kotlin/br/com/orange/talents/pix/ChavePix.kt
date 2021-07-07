package br.com.orange.talents.pix

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(uniqueConstraints = [UniqueConstraint(
    name = "uk_chave_pix",
    columnNames = ["chave"]
)])
class ChavePix(
    @field:NotNull
    @Column(nullable = false)
    val clientId: UUID,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipo: TipoDeChave,

    @field:NotBlank
    @Column(nullable = false)
    var chave: String,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipoDeConta: TipoDeConta,

    @field:Valid
    @Embedded
    val conta: ContaAssociada
) {
    @Id
    @GeneratedValue
    val id: UUID? = null

    @Column(nullable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()

    override fun toString(): String {
        return "ChavePix(clientId=$clientId, tipo=$tipo, chave='$chave', tipoDeConta=$tipoDeConta, conta=$conta, id=$id, criadaEm=$criadaEm)"
    }

    //verifica se esta chave pertence ao cliente
    fun pertenceAo(clientId: UUID) = this.clientId.equals(clientId)

    // verifica se esta chave é aleatoria
    fun isAleatoria(): Boolean{
        return tipo == TipoDeChave.ALEATORIA
    }

    // atualizar o valor da chave, quando a chave for do tipo aleatoria
    fun atualiza(chave: String): Boolean{
        if(isAleatoria()){
            this.chave = chave
            return true
        }
        return false
    }
}