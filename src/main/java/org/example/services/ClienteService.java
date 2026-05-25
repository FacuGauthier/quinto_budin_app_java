package org.example.services;

import org.example.dtos.cliente.ClienteCreateRequest;
import org.example.dtos.cliente.ClienteRequest;
import org.example.dtos.cliente.ClienteResponse;
import org.example.dtos.cliente.ClienteUpdateRequest;
import org.example.models.Cliente;
import org.example.repositories.ClienteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ClienteResponse crearCliente(ClienteCreateRequest clienteCreateRequest) {
        validarCliente(clienteCreateRequest);

        Cliente cliente = new Cliente();

        cliente.setId(null);
        cliente.setActivo(true);

        cliente.setNombre(clienteCreateRequest.nombre());
        cliente.setApellido(clienteCreateRequest.apellido());
        cliente.setTelefono(clienteCreateRequest.telefono());

        Cliente clienteGuardado = clienteRepository.save(cliente);

        return toResponse(clienteGuardado);
    }

    @Transactional
    public ClienteResponse modificarCliente(Long id, ClienteUpdateRequest clienteUpdateRequest) {
        if(id == null){
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        validarCliente(clienteUpdateRequest);

        Cliente cliente = buscarPorId(id);

        cliente.setId(id);
        cliente.setActivo(clienteUpdateRequest.activo());

        cliente.setNombre(clienteUpdateRequest.nombre());
        cliente.setApellido(clienteUpdateRequest.apellido());
        cliente.setTelefono(clienteUpdateRequest.telefono());

        Cliente clienteUpdated = clienteRepository.save(cliente);

        return toResponse(clienteUpdated);
    }

    @Transactional
    public ClienteResponse bajaLogicaCliente(Long id){
        Cliente cliente = buscarPorId(id);

        if(!cliente.isActivo()){
            throw new IllegalArgumentException("El cliente ya se encuentra inactivo.");
        }

        cliente.marcarComoInactivo();
        Cliente clienteUpdated = clienteRepository.save(cliente);

        return toResponse(clienteUpdated);
    }

    @Transactional
    public ClienteResponse reactivarCliente(Long id){
        Cliente cliente = buscarPorId(id);

        if(cliente.isActivo()){
            throw new IllegalArgumentException("El cliente ya se encuentra activo.");
        }

        cliente.setActivo(true);
        Cliente clienteUpdated = clienteRepository.save(cliente);

        return toResponse(clienteUpdated);
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        return clienteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> obtenerClientesActivos(){
        return clienteRepository.findByActivoTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listarTodos(){
        return clienteRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }


    private void validarCliente(ClienteRequest cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }

        if (cliente.nombre() == null || cliente.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if (cliente.apellido() == null || cliente.apellido().isBlank()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }

        if (cliente.telefono() == null || cliente.telefono().isBlank()) {
            throw new IllegalArgumentException("El teléfono es obligatorio.");
        }
    }
    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getTelefono(),
                cliente.isActivo()
        );
    }
}
