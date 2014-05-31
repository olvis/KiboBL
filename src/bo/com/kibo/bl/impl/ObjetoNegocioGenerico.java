 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessException;
import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.exceptions.PermisosInsuficientesException;
import bo.com.kibo.bl.intf.IGenericoBO;
import bo.com.kibo.dal.impl.control.FactoriaDAOManager;
import bo.com.kibo.dal.intf.IDAOGenerico;
import bo.com.kibo.dal.intf.control.IDAOManager;
import bo.com.kibo.entidades.RolPermiso;
import bo.com.kibo.entidades.RolPermisoId;
import bo.com.kibo.entidades.Usuario;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Olvinho
 * @param <T> Clase entidad
 * @param <ID> Clase que que representa el Id
 * @param <U> Clase DAO
 */
public abstract class ObjetoNegocioGenerico<T, ID extends Serializable, U extends IDAOGenerico<T, ID>> implements IGenericoBO<T, ID> {

    private IDAOManager daoManager;
    protected Integer idUsuario;
    protected Usuario usuarioActual;
    private BusinessException mensajesError;
    
    public ObjetoNegocioGenerico() {
        
    }

    public IDAOManager getDaoManager() {
        if (daoManager == null) {
            this.daoManager = FactoriaDAOManager.getDAOManager();
        }
        return daoManager;
    }

    protected <V> V ejecutarEnTransaccion(Callable<V> ejecutor) {
        boolean comiteado = false;
        try {
            getDaoManager().iniciarTransaccion();
            V result = ejecutor.call();
            getDaoManager().confirmarTransaccion();
            comiteado = true;
            return result;
        }catch(BusinessException ex){
            throw ex;
        } catch (Exception ex) {
            Logger.getLogger(ObjetoNegocioGenerico.class.getName()).log(Level.SEVERE, null, ex);
            throw new BusinessException("Error de ejecución dentro de la transacción");
        }
        finally{
            if (!comiteado){
                try {
                    getDaoManager().cancelarTransaccion();
                } catch (Exception e) {
                    
                }
            }
        }
    }
    
    protected void appendException(BusinessExceptionMessage message){
        if (mensajesError == null){
            mensajesError = new BusinessException(message);
            return;
        }
        mensajesError.getMessages().add(message);
    }

    @Override
    public Integer getIdUsuario() {
        return this.idUsuario;
    }

    @Override
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public T obtenerPorId(ID id) {
        return getObjetoDAO().obtenerPorId(id);
    }

    @Override
    public List<T> obtenerTodos() {
        return ejecutarEnTransaccion(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return getObjetoDAO().obtenerTodos();
            }
        });
    }

    @Override
    public void insertar(T entity) {
        if (idUsuario == null) {
            throw new BusinessException("Debe definir el usuario que solicitando la acción para continuar");
        }
        final T x = entity;
        ejecutarEnTransaccion(new Callable<Void>() {
            @Override
            public Void call() {
                usuarioActual = getDaoManager().getUsuarioDAO().recuperarPorId(idUsuario);
                if (usuarioActual == null) {
                    throw new BusinessException("El usuario especificado no existe");
                }
                if (!tienePermisoInsertar()){
                    throw new PermisosInsuficientesException("No tiene los privilegios necesarios para continuar, contacte al administrador");
                }
                mensajesError = null;
                validar(x);
                if (mensajesError != null){
                    throw mensajesError;
                }
                getObjetoDAO().persistir(x);
                return null;
            }
        });
    }

    protected void validar(T entity) {

    }

    @Override
    public void actualizar(T entity) {
        if (idUsuario == null) {
            throw new BusinessException("Debe definir el usuario que solicitando la acción para continuar");
        }
        final T x = entity;
        ejecutarEnTransaccion(new Callable<Void>() {
            @Override
            public Void call() {
                usuarioActual = getDaoManager().getUsuarioDAO().recuperarPorId(idUsuario);
                if (usuarioActual == null) {
                    throw new BusinessException("El usuario especificado no existe");
                }
                if (!tienePermisoModificar()){
                    throw new PermisosInsuficientesException("No tiene los privilegios necesarios para continuar, contacte al administrador");
                }
                mensajesError = null;
                validar(x);
                if (mensajesError != null){
                    throw mensajesError;
                }
                getObjetoDAO().persistir(x);
                return null;
            }
        });
    }

    /**
     * *
     *
     * @return Verdadero si el usuario actual puede insertar.
     */
    private boolean tienePermisoInsertar() {
        if (IdPermisoInsertar() == 0) {
            return true;
        }
        RolPermiso rp = getDaoManager().getRolPermisoDAO().recuperarPorId(new RolPermisoId(IdPermisoInsertar(), usuarioActual.getRol().getId()));
        if (rp == null) {
            return false;
        }
        return rp.isValor();
    }

    /**
     * *
     *
     * @return Verdadero si el usuario actual puede modificar.
     */
    private boolean tienePermisoModificar() {
        if (IdPermisoActualizar() == 0) {
            return true;
        }
        RolPermiso rp = getDaoManager().getRolPermisoDAO().recuperarPorId(new RolPermisoId(IdPermisoActualizar(), usuarioActual.getRol().getId()));
        if (rp == null) {
            return false;
        }
        return rp.isValor();
    }

    protected int IdPermisoInsertar() {
        return 0;
    }

    protected int IdPermisoActualizar() {
        return 0;
    }

    abstract U getObjetoDAO();
}
