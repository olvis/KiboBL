/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo.com.kibo.bl.intf;

import bo.com.kibo.entidades.Troza;
import java.util.List;

/**
 *
 * @author Olvinho
 */
public interface ITrozaBO extends IGenericoBO<Troza, Integer>{

    List<Troza> getTrozasParaCorta(Integer id);
    
    String getCodigo(Integer numero);
    
}
