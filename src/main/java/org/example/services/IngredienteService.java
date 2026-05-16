package org.example.services;

import org.example.models.Ingrediente;
import org.example.repositories.IngredienteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class IngredienteService {
    private final IngredienteRepository ingredienteRepository;

    public IngredienteService(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    @Transactional
    public Ingrediente crearIngrediente(Ingrediente ingrediente) {
        validarIngrediente(ingrediente);

        try{
            ingrediente.setId(null);
            ingrediente.setActivo(true);

            return ingredienteRepository.save(ingrediente);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("No se pudo guardar el ingrediente por un problema de integridad de datos.");
        }
    }

    @Transactional
    public Ingrediente modificarIngrediente(Long id, Ingrediente datosNuevos) {
        validarIngrediente(datosNuevos);

        Ingrediente ingrediente = buscarPorId(id);

        ingrediente.setNombre(datosNuevos.getNombre());
        ingrediente.setMarca(datosNuevos.getMarca());
        ingrediente.setUnidadMedida(datosNuevos.getUnidadMedida());
        ingrediente.setCostoUnitario(datosNuevos.getCostoUnitario());

        try{
            return ingredienteRepository.save(ingrediente);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("No se pudo actualizar el ingrediente por un problema de integridad de datos.");
        }
    }

    @Transactional
    public void bajaLogicaIngrediente(Long id) {
        Ingrediente ingrediente = buscarPorId(id);

        if(!ingrediente.isActivo()){
            throw new IllegalArgumentException("El ingrediente ya se encuentra inactivo.");
        }

        ingrediente.marcarComoInactivo();
        ingredienteRepository.save(ingrediente);
    }

    @Transactional
    public void reactivarIngrediente(Long id) {
        Ingrediente ingrediente = buscarPorId(id);

        if(ingrediente.isActivo()){
            throw new IllegalArgumentException("El ingrediente ya se encuentra activo.");
        }

        ingrediente.setActivo(true);
        ingredienteRepository.save(ingrediente);
    }

    public List<Ingrediente> obtenerStockActual() {
        return ingredienteRepository.findByActivoTrue();
    }

    public List<Ingrediente> listarTodos() {
        return ingredienteRepository.findAll();
    }

    public Ingrediente buscarPorId(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        return ingredienteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ingrediente no encontrado."));
    }

    private void validarIngrediente(Ingrediente ingrediente) {
        if(ingrediente == null) {
            throw new IllegalArgumentException("El ingrediente no puede ser nulo.");
        }

        if(ingrediente.getNombre() == null || ingrediente.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if(ingrediente.getMarca() == null || ingrediente.getMarca().isBlank()) {
            throw new IllegalArgumentException("La marca es obligatorio.");
        }

        if(ingrediente.getUnidadMedida() == null || ingrediente.getUnidadMedida().isBlank()) {
            throw new IllegalArgumentException("La unidad medida es obligatorio.");
        }

        if(ingrediente.getCostoUnitario() == null) {
            throw new IllegalArgumentException("El costo unitario es obligatorio.");
        }

        if(ingrediente.getCostoUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La costo unitario debe ser mayor a cero.");
        }
    }
}
