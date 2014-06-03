/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.intf.IGeoLugarBO;
import bo.com.kibo.dal.intf.IGeoLugarDAO;
import bo.com.kibo.entidades.GeoLugar;

/**
 *
 * @author Olvinho
 * @param <T> Clase entidad
 * @param <U> Clase DAO
 */
public abstract class GeoLugarBO<T extends GeoLugar, U extends IGeoLugarDAO<T>> extends ObjetoNegocioGenerico<T, Integer, U> implements IGeoLugarBO<T>{

    @Override
    protected void despuesDeRecuperar(T entidad) {
        entidad.getPoligono().size();
    }
    
}
