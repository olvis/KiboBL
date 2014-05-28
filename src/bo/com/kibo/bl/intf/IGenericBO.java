/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo.com.kibo.bl.intf;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Olvinho
 * @param <T>
 * @param <ID>
 */
public interface IGenericBO<T, ID extends Serializable>{
    T obtenerPorId(ID id);
    List<T> obtenerTodos();
    void insertar(T entity);
    void actualizar(T entity);
    void setIdUsuario(Integer idUsuario);
    Integer getIdUsuario();
}
