/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.intf;

import bo.com.kibo.entidades.Faja;
import java.util.List;

/**
 *
 * @author Olvinho
 */
public interface IFajaBO extends IGeoLugarBO<Faja> {

    List<Faja> obtenerFajasSegunArea(Integer idArea);

}
