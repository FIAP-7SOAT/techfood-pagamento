package br.com.fiap.techfood.app.adapter.input.web.client

import br.com.fiap.techfood.app.adapter.input.web.client.dto.ClientRequest
import br.com.fiap.techfood.app.adapter.input.web.client.dto.ClientResponse
import br.com.fiap.techfood.app.adapter.input.web.client.mapper.toClientResponse
import br.com.fiap.techfood.core.common.exception.ClientNotFoundException
import br.com.fiap.techfood.core.common.exception.ClientAlreadyExistsException
import br.com.fiap.techfood.core.port.input.ClientInputPort
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/clients")
class ClientResource(
    private val clientInput: ClientInputPort
) {

    @PostMapping
    fun create(@RequestBody request: ClientRequest): ClientResponse {
        try {
            clientInput.getClientByCpf(request.cpf)
            throw ClientAlreadyExistsException() // Throw exception if client exists
        } catch (e: ClientNotFoundException) {
            // Proceed if client is not found
        }

        // Create and persist the client
        val client = clientInput.create(UUID.randomUUID(), request.cpf, request.name, request.email)
        return client.toClientResponse()
    }


    /**
     * Retrieve a client by CPF
     */
    @GetMapping("/cpf/{cpf}")
    fun findByCpf(@PathVariable cpf: String): ClientResponse {
        val client = clientInput.getClientByCpf(cpf) ?: throw ClientNotFoundException()
        return client.toClientResponse()
    }

    /**
     * Retrieve all clients
     */
    @GetMapping
    fun findAll(): List<ClientResponse> {
        val clients = clientInput.findAll()
        return clients.map { it.toClientResponse() }
    }

    /**
     * Update a client
     */
    @PutMapping("/{clientId}")
    fun updateClient(@PathVariable clientId: UUID, @RequestBody request: ClientRequest): ClientResponse {
        val client = clientInput.update(clientId, request.cpf, request.name, request.email)
        return client.toClientResponse()
    }

    /**
     * Delete a client by CPF
     */
    @DeleteMapping("/{cpf}")
    fun deleteClient(@PathVariable cpf: String) {
        val client = clientInput.getClientByCpf(cpf) ?: throw ClientNotFoundException()
        clientInput.delete(cpf)
    }
}
