/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo.com.kibo.bl.intf;

import bo.com.kibo.entidades.Calidad;

/**
 *
 * @author Olvinho
 */
public interface ICalidadBO extends IGenericoBO<Calidad, Integer>{
    
    String getCodigo(Integer id);
    
}
