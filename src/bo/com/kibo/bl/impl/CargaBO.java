/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.intf.ICargaBO;
import bo.com.kibo.dal.intf.ICargaDAO;
import bo.com.kibo.entidades.Carga;

/**
 *
 * @author Olvinho
 */
public class CargaBO extends ObjetoNegocioGenerico<Carga, Integer, ICargaDAO> implements ICargaBO{

    @Override
    ICargaDAO getObjetoDAO() {
        return getDaoManager().getCargaDAO();
    }
    
}
