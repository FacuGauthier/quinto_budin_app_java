package org.example.services;

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
    public Cliente crearCliente(Cliente cliente) {
        validarCliente(cliente);

        try{
            cliente.setId(null);
            cliente.setActivo(true);

            return clienteRepository.save(cliente);
        } catch (DataIntegrityViolationException e){
            throw new IllegalArgumentException("No se pudo guardar el cliente por un problema de integridad de datos.");
        }
    }

    @Transactional
    public Cliente modificarCliente(Long id, Cliente datosNuevos){
        validarCliente(datosNuevos);

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

    public Cliente buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        return clienteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));
    }

    public List<Cliente> obtenerClientesActivos(){
        return clienteRepository.findByActivoTrue();
    }

    public List<Cliente> listarTodos(){
        return clienteRepository.findAll();
    }


    private void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }

        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if (cliente.getApellido() == null || cliente.getApellido().isBlank()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }

        if (cliente.getTelefono() == null || cliente.getTelefono().isBlank()) {
            throw new IllegalArgumentException("El teléfono es obligatorio.");
        }
    }
}
