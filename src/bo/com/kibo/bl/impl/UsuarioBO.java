/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.intf.IUsuarioBO;
import bo.com.kibo.dal.intf.IUsuarioDAO;
import bo.com.kibo.entidades.Usuario;
import java.util.concurrent.Callable;

/**
 *
 * @author Olvinho
 */
public class UsuarioBO extends ObjetoNegocioGenerico<Usuario, Integer, IUsuarioDAO> implements IUsuarioBO{
    
    @Override
    IUsuarioDAO getObjetoDAO() {
        return getDaoManager().getUsuarioDAO();
    }

    @Override
    public Integer getIdUsuarioPorEmail(String email) {
        final String x = email;
        return ejecutarEnTransaccion(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return getObjetoDAO().getIdUsuarioPorEmail(x);
            }
        });
    }

    @Override
    public Usuario logear(final String usuario, final String pass) {        
        return ejecutarEnTransaccion(new Callable<Usuario>() {
            @Override
            public Usuario call() throws Exception {
                Usuario user = getObjetoDAO().logear(usuario, pass);
                user.setRol(getDaoManager().getRolDAO().recuperarPorId(user.getRol().getId()));
                return user;
            }
        });
    }
}
