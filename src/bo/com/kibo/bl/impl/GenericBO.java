/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessException;
import bo.com.kibo.bl.intf.Ejecutor;
import bo.com.kibo.bl.intf.IGenericBO;
import bo.com.kibo.dal.impl.control.DAOManagerFactory;
import bo.com.kibo.dal.intf.IGenericDAO;
import bo.com.kibo.dal.intf.control.IDAOManager;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Olvinho
 * @param <T> Clase entidad
 * @param <ID> Clase que que representa el Id
 * @param <U> Clase DAO
 */
public abstract class GenericBO<T, ID extends Serializable, U extends IGenericDAO<T, ID>> implements IGenericBO<T, ID> {

    protected U objectDAO;
    private IDAOManager daoManager;

    public GenericBO() {
        this.daoManager = DAOManagerFactory.getDAOManager();
    }

    public IDAOManager getDaoManager() {
        if (daoManager == null) {
            this.daoManager = DAOManagerFactory.getDAOManager();
        }
        return daoManager;
    }

    protected <V> V ejecutarEnTransaccion(Ejecutor<V> ejecutor) {
        try {
            getDaoManager().iniciarTransaccion();
            V result = ejecutor.call();
            getDaoManager().confirmarTransaccion();
            return result;
        } catch (Exception ex) {
            getDaoManager().cancelarTransaccion();
            Logger.getLogger(GenericBO.class.getName()).log(Level.SEVERE, null, ex);
            throw new BusinessException("Error ejecutando");
        }
    }

    @Override
    public T obtenerPorId(ID id) {
        return objectDAO.obtenerPorId(id);
    }

    @Override
    public List<T> obtenerTodos() {
        return ejecutarEnTransaccion(new Ejecutor<List<T>>() {
            @Override
            public List<T> call() {
                return objectDAO.obtenerTodos();
            }
        });
    }

    @Override
    public void insertar(T entity) {
        final T x = entity;
        ejecutarEnTransaccion(new Ejecutor<Void>() {
            @Override
            public Void call() {
                validar(x);
                objectDAO.persistir(x);
                return null;
            }
        });
    }

    protected void validar(T entity) {

    }

    @Override
    public void actualizar(T entity) {
        final T x = entity;
        ejecutarEnTransaccion(new Ejecutor<Void>() {
            @Override
            public Void call() {
                validar(x);
                objectDAO.persistir(x);
                return null;
            }
        });
    }

}
