/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.intf.IRolBO;
import bo.com.kibo.dal.intf.IRolDAO;
import bo.com.kibo.entidades.Rol;

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

}
