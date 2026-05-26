package org.example.services;

import org.example.dtos.ingrediente.IngredienteCreateRequest;
import org.example.dtos.ingrediente.IngredienteRequest;
import org.example.dtos.ingrediente.IngredienteResponse;
import org.example.dtos.ingrediente.IngredienteUpdateRequest;
import org.example.models.Ingrediente;
import org.example.repositories.IngredienteRepository;
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
    public IngredienteResponse crearIngrediente(IngredienteCreateRequest ingredienteCreateRequest) {
        validarIngrediente(ingredienteCreateRequest);

        Ingrediente ingrediente = new Ingrediente();

        ingrediente.setId(null);
        ingrediente.setActivo(true);

        ingrediente.setNombre(ingredienteCreateRequest.nombre());
        ingrediente.setMarca(ingredienteCreateRequest.marca());
        ingrediente.setStockActual(ingredienteCreateRequest.stockActual());
        ingrediente.setUnidadMedida(ingredienteCreateRequest.unidadMedida());
        ingrediente.setCostoUnitario(ingredienteCreateRequest.costoUnitario());

        Ingrediente ingredienteGuardado = ingredienteRepository.save(ingrediente);

        return toResponse(ingredienteGuardado);
    }

    @Transactional
    public IngredienteResponse modificarIngrediente(Long id, IngredienteUpdateRequest ingredienteUpdateRequest) {
        if(id == null){
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        validarIngrediente(ingredienteUpdateRequest);

        Ingrediente ingrediente = buscarPorId(id);

        ingrediente.setNombre(ingredienteUpdateRequest.nombre());
        ingrediente.setMarca(ingredienteUpdateRequest.marca());
        ingrediente.setUnidadMedida(ingredienteUpdateRequest.unidadMedida());
        ingrediente.setCostoUnitario(ingredienteUpdateRequest.costoUnitario());

        Ingrediente ingredienteUpdated = ingredienteRepository.save(ingrediente);

        return toResponse(ingredienteUpdated);
    }

    @Transactional
    public IngredienteResponse bajaLogicaIngrediente(Long id) {
        Ingrediente ingrediente = buscarPorId(id);

        if(!ingrediente.isActivo()){
            throw new IllegalArgumentException("El ingrediente ya se encuentra inactivo.");
        }

        ingrediente.marcarComoInactivo();
        Ingrediente ingredienteUpdated = ingredienteRepository.save(ingrediente);

        return toResponse(ingredienteUpdated);
    }

    @Transactional
    public IngredienteResponse reactivarIngrediente(Long id) {
        Ingrediente ingrediente = buscarPorId(id);

        if(ingrediente.isActivo()){
            throw new IllegalArgumentException("El ingrediente ya se encuentra activo.");
        }

        ingrediente.setActivo(true);

        Ingrediente ingredienteUpdated = ingredienteRepository.save(ingrediente);

        return toResponse(ingredienteUpdated);
    }

    @Transactional(readOnly = true)
    public Ingrediente buscarPorId(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        return ingredienteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ingrediente no encontrado."));
    }

    @Transactional(readOnly = true)
    public List<IngredienteResponse> obtenerStockActual() {
        return ingredienteRepository.findByActivoTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<IngredienteResponse> listarTodos() {
        return ingredienteRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }


    private void validarIngrediente(IngredienteRequest ingrediente) {
        if(ingrediente == null) {
            throw new IllegalArgumentException("El ingrediente no puede ser nulo.");
        }

        if(ingrediente.nombre() == null || ingrediente.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if(ingrediente.marca() == null || ingrediente.marca().isBlank()) {
            throw new IllegalArgumentException("La marca es obligatorio.");
        }

        if(ingrediente.unidadMedida() == null || ingrediente.unidadMedida().isBlank()) {
            throw new IllegalArgumentException("La unidad medida es obligatorio.");
        }

        if(ingrediente.costoUnitario() == null) {
            throw new IllegalArgumentException("El costo unitario es obligatorio.");
        }

        if(ingrediente.costoUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La costo unitario debe ser mayor a cero.");
        }
    }
    private IngredienteResponse toResponse(Ingrediente ingrediente) {
        return new IngredienteResponse(
                ingrediente.getId(), ingrediente.getNombre(), ingrediente.getMarca(), ingrediente.getStockActual(), ingrediente.getUnidadMedida(), ingrediente.getCostoUnitario(), ingrediente.isActivo()
        );
    }
}
