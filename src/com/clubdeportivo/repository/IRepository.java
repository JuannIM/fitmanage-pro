package com.clubdeportivo.repository;

import java.util.List;
import java.util.Optional;

/**
 * Contrato genérico de persistencia y acceso a datos (Patrón DAO / Repository).
 *
 * @param <T>  Tipo de la entidad de dominio
 * @param <ID> Tipo del identificador unívoco
 * @author Camila Díaz (Integrante 6 - Persistencia, QA & Auditoría)
 * @version 1.0.0
 */
public interface IRepository<T, ID> {

    T guardar(T entidad);

    Optional<T> buscarPorId(ID id);

    List<T> listarTodos();

    boolean eliminarPorId(ID id);

    int contar();

    void cargarDesdeDisco();

    void guardarEnDisco();
}
