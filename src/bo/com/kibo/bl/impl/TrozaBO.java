/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.intf.ITrozaBO;
import bo.com.kibo.dal.intf.ITrozaDAO;
import bo.com.kibo.entidades.DetalleCenso;
import bo.com.kibo.entidades.FormularioCenso;
import bo.com.kibo.entidades.Troza;

/**
 *
 * @author Olvinho
 */
public class TrozaBO extends ObjetoNegocioGenerico<Troza, Integer, ITrozaDAO> implements ITrozaBO {

    @Override
    ITrozaDAO getObjetoDAO() {
        return getDaoManager().getTrozaDAO();
    }
    
    public Troza crearTroza(DetalleCenso trozaCenso, FormularioCenso censo){
        Troza troza = new Troza();
        
        troza.setCodigo(trozaCenso.getCodigo());
        troza.setArea(censo.getArea());
        troza.setEspecie(trozaCenso.getEspecie());
        troza.setCalidad(trozaCenso.getCalidad());
        troza.setEstado(Troza.ESTADO_CENSADA);
        troza.setExiste(Troza.EXISTE_EXISTE);
        troza.setX(trozaCenso.getX());
        troza.setY(trozaCenso.getY());
        troza.setFormularioCenso(censo);
        
        troza = getObjetoDAO().persistir(troza);
        return  troza;
    }

    @Override
    protected void validar(Troza entity) {
        appendException(new BusinessExceptionMessage("La insercción o actualización directa no está permitida en las trozas"));
    }

    
}
