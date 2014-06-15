/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.intf;

import bo.com.kibo.entidades.Patio;
import java.util.List;

/**
 *
 * @author Olvinho
 */
public interface IPatioBO extends IGeoLugarBO<Patio> {

    List<Patio> obtenerPatiosSegunArea(Integer idArea);

    String getNombre(Integer id);

}
