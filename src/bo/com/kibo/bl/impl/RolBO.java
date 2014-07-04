/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.intf.IRolBO;
import bo.com.kibo.dal.intf.IRolDAO;
import bo.com.kibo.entidades.Rol;
import bo.com.kibo.entidades.RolPermiso;
import bo.com.kibo.entidades.RolPermisoId;
import bo.com.kibo.entidades.Usuario;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author Olvinho
 */
public class RolBO
        extends ObjetoNegocioGenerico<Rol, Integer, IRolDAO>
        implements IRolBO {

    @Override
    IRolDAO getObjetoDAO() {
        return getDaoManager().getRolDAO();
    }
    
    @Override
    protected int IdPermisoInsertar() {
        return 20101;
    }

    @Override
    protected int IdPermisoActualizar() {
        return 20102; 
    }

    @Override
    public boolean verificarPermiso(final Integer idPermiso, final Usuario usuario) {
        if (usuario == null) {
            return false;
        }

        if (usuario.getRol() == null) {
            return false;
        }

        if (usuario.getRol().getId() == null) {
            return false;
        }

        return ejecutarEnTransaccion(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                RolPermiso rp = getDaoManager().getRolPermisoDAO().recuperarPorId(new RolPermisoId(idPermiso, usuario.getRol().getId()));
                if (rp == null) {
                    return false;
                }
                return rp.isValor();
            }
        });
    }

    @Override
    protected void validar(Rol entity) {
        boolean descripcionValida = true;
        if (isNullOrEmpty(entity.getDescripcion())) {
            appendException(new BusinessExceptionMessage("La descripción es un campo requerido", "descripcion"));
            descripcionValida = false;
        } else if (entity.getDescripcion().length() > 50) {
            appendException(new BusinessExceptionMessage("La descripción no puede tener más de 50 carácteres", "descripcion"));
            descripcionValida = false;
        }

        if (descripcionValida) {
            if (entity.getId() == null) {
                //Insertando y verificamos si el código existe
                if (getObjetoDAO().getIdPorDescripcion(entity.getDescripcion()) != null) {
                    appendException(new BusinessExceptionMessage("El rol '" + entity.getDescripcion() + "' ya existe", "descripcion"));
                }
            } else {
                //Se quiere actualizar, verificamos que es válido y que el código si cambio, no existe
                if (!getObjetoDAO().checkId(entity.getId())) {
                    appendException(new BusinessExceptionMessage("El rol con Id  '" + entity.getId() + "' no existe", "id"));
                } else {
                    Rol actual = getObjetoDAO().obtenerPorId(entity.getId());
                    if (!actual.getDescripcion().equals(entity.getDescripcion())) {
                        //El codigo cambio verificamos si existe
                        if (getObjetoDAO().getIdPorDescripcion(entity.getDescripcion()) != null) {
                            appendException(new BusinessExceptionMessage("El rol '" + entity.getDescripcion() + "' ya existe", "descripcion"));
                        }
                    }
                }
            }
        }
    }
    

    @Override
    protected void postInsertar(Rol entidad) {
        
    }

    @Override
    public List<RolPermiso> getPermisos(final int idRol, final Integer idPadre) {
        return ejecutarEnTransaccion(new Callable<List<RolPermiso>>() {
            @Override
            public List<RolPermiso> call() throws Exception {
                return getDaoManager().getRolPermisoDAO().getPermisos(idRol, idPadre);
            }
        });
    }

    @Override
    public void guardarPermisos(final List<RolPermiso> permisos) {
        ejecutarEnTransaccion(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for(RolPermiso rp: permisos){
                    getDaoManager().getRolPermisoDAO().persistir(rp);
                }
                return null;
            }
        });
    }

    @Override
    public String getDescripcion(final Integer id) {
       return ejecutarEnTransaccion(new Callable<String>() {
           @Override
           public String call() throws Exception {
               return getObjetoDAO().getDescripcion(id);
           }
       });
    }
         
}
