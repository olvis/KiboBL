/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.intf;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Olvinho
 * @param <T>
 * @param <ID>
 */
public interface IGenericoBO<T, ID extends Serializable> {

    T recuperarPorId(ID id);

    List<T> obtenerTodos();

    T insertar(T entity);

    T actualizar(T entity);

    void setIdUsuario(Integer idUsuario);

    List<T> obtenerNuevosObjetos(Date fecha);

    Integer getIdUsuario();
}
