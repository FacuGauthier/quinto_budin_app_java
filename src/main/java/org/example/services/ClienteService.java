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

        return new ClienteResponse(
                clienteGuardado.getId(), clienteGuardado.getNombre(), clienteGuardado.getApellido(), clienteGuardado.getTelefono(), clienteGuardado.isActivo()
        );
    }

    @Transactional
    public Cliente modificarCliente(Long id, ClienteUpdateRequest clienteUpdateRequest) {
        if(id == null){
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        validarCliente(clienteUpdateRequest);

        Cliente cliente = buscarPorId(id);

        cliente.setNombre(datosNuevos.getNombre());
        cliente.setApellido(datosNuevos.getApellido());
        cliente.setTelefono(datosNuevos.getTelefono());
        cliente.setActivo(datosNuevos.isActivo());

        try{
            return clienteRepository.save(cliente);

        } catch (DataIntegrityViolationException e){
            throw new IllegalArgumentException("No se pudo actualizar el cliente por un problema de integridad de datos.");
        }
    }

    @Transactional
    public void bajaLogicaCliente(Long id){
        Cliente cliente = buscarPorId(id);

        if(!cliente.isActivo()){
            throw new IllegalArgumentException("El cliente ya se encuentra inactivo.");
        }

        cliente.marcarComoInactivo();
        clienteRepository.save(cliente);
    }

    @Transactional
    public void reactivarCliente(Long id){
        Cliente cliente = buscarPorId(id);

        if(cliente.isActivo()){
            throw new IllegalArgumentException("El cliente ya se encuentra activo.");
        }

        cliente.setActivo(true);
        clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        return clienteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));
    }

    @Transactional(readOnly = true)
    public List<Cliente> obtenerClientesActivos(){
        return clienteRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos(){
        return clienteRepository.findAll();
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
}
