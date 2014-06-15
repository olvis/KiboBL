/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.intf;

import bo.com.kibo.entidades.Carga;

/**
 *
 * @author Olvinho
 */
public interface ICargaBO extends IGenericoBO<Carga, Integer> {

    String getCodigo(Integer id);
}
