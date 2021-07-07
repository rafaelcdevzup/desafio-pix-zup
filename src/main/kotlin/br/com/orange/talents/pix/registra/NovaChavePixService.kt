package br.com.orange.talents.pix.registra

import br.com.orange.talents.integration.itau.ContasDeClientesNoItauClient
import br.com.orange.talents.pix.ChavePix
import br.com.orange.talents.pix.ChavePixRepository
import br.com.orange.talents.pix.ChavePixExistenteException
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
class NovaChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val itauClient: ContasDeClientesNoItauClient,) {


    @Transactional
    fun registra(@Valid novaChave: NovaChavePix): ChavePix{

        //1. Verificar se a chave já existe no banco de dados
       if (repository.existsByChave(novaChave.chave))
          throw ChavePixExistenteException("Chave Pix `${novaChave.chave}` existente")

        //2. buscar dados da conta no ERP do Itau
       val response = itauClient.buscaContasPorTipo(novaChave.clientId!!, novaChave.tipoDeConta!!.name)
       val conta = response.body()?.toModel() ?: throw IllegalStateException("Cliente não encontrado no Itau")

       //3. gravar no banco de dados
       val chave = novaChave.toModel(conta)
       repository.save(chave)
       return chave
    }
}