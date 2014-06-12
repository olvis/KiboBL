/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.intf.IRolBO;
import bo.com.kibo.dal.intf.IRolDAO;
import bo.com.kibo.entidades.Rol;
import bo.com.kibo.entidades.RolPermiso;
import bo.com.kibo.entidades.RolPermisoId;
import bo.com.kibo.entidades.Usuario;
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
    public boolean verificarPermiso(final Integer idPermiso, final Usuario usuario) {
        if (usuario == null) {
            return false;
        }

        if (usuario.getRol() == null) {
            return false;
        }
        
        if (usuario.getRol().getId() == null){
            return false;
        }

        return ejecutarEnTransaccion(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                RolPermiso rp = getDaoManager().getRolPermisoDAO().recuperarPorId(new RolPermisoId(idPermiso, usuario.getRol().getId()));
                if (rp == null){
                    return false;
                }
                return rp.isValor();
            }
        });
    }
}
