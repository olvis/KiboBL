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
public interface ITrozaBO extends IGenericoBO<Troza, Integer> {

    List<Troza> getTrozasParaTala(Integer idArea);

    List<Troza> getTrozasParaExtraccion(Integer idArea);

    List<Troza> getTrozasParaMovimiento(Integer idArea);

    List<String> getCodigosTrozaParaTala(Integer idArea, String codigoParcial);
    
    List<String> getCodigosTrozaParaExtraccion(Integer idArea, String codigoParcial);
    
    List<String> getCodigosTrozaParaMovimiento(Integer idArea, String codigoParcial);

    String getCodigo(Integer numero);

    Troza obtenerPorCodigo(String codigo, Integer idArea);

}
