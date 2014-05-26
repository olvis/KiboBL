/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.intf.IAreaBO;
import bo.com.kibo.dal.intf.IAreaDAO;
import bo.com.kibo.entidades.Area;

/**
 *
 * @author Olvinho
 */
public class AreaBO extends GenericBO<Area, Integer, IAreaDAO> implements IAreaBO{

    public AreaBO() {
        super.objectDAO = getDaoManager().getAreaDAO();
    }
    
}
