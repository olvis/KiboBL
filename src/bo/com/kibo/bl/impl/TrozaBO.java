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
import bo.com.kibo.entidades.DetalleCorta;
import bo.com.kibo.entidades.EncabezadoFormulario;
import bo.com.kibo.entidades.FormularioCenso;
import bo.com.kibo.entidades.FormularioCorta;
import bo.com.kibo.entidades.Troza;
import java.util.List;
import java.util.concurrent.Callable;

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
        troza.setFaja(censo.getFaja());
        
        troza = getObjetoDAO().persistir(troza);
        return  troza;
    }

    @Override
    protected void validar(Troza entity) {
        appendException(new BusinessExceptionMessage("La insercción o actualización directa no está permitida en las trozas"));
    }

    @Override
    public List<Troza> getTrozasParaCorta(final Integer idArea) {
        return ejecutarEnTransaccion(new Callable<List<Troza>>() {
            @Override
            public List<Troza> call() throws Exception {
               return getDaoManager().getTrozaDAO().getTrozasParaCorta(idArea);
            }
        });
    }

    @Override
    public String getCodigo(final Integer numero) {
        return ejecutarEnTransaccion(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getObjetoDAO().getCodigo(numero);
            }
        });
    }
    
    public void crearSeccion(DetalleCorta linea, FormularioCorta formulario){
        Troza seccion = new Troza();
        seccion.setCodigo(linea.getTroza().getCodigo() + Troza.SEPARADOR_CODIGO + linea.getCarga().getCodigo());
        seccion.setArea(formulario.getArea());
        
        if (linea.getEspecie() != null){
            seccion.setEspecie(linea.getEspecie());
        }else{
            seccion.setEspecie(linea.getTroza().getEspecie());
        }
        
        if(linea.getCalidad() != null){
            seccion.setCalidad(linea.getCalidad());
        }else{
            seccion.setCalidad(linea.getTroza().getCalidad());
        }
        
        seccion.setCalidad(linea.getCalidad());
        seccion.setExiste(Troza.EXISTE_EXISTE);
        seccion.setFaja(linea.getTroza().getFaja());
        seccion.setPadre(linea.getTroza());
        
        byte estadoSeccion = Troza.ESTADO_CENSADA;
        switch(formulario.getTipo()){
            case EncabezadoFormulario.TIPO_FORMULARIO_CORTA:
                estadoSeccion = Troza.ESTADO_TALADA;
                seccion.setFormularioCorta(formulario);
                break;
        }
        seccion.setEstado(estadoSeccion);
        getObjetoDAO().persistir(seccion);
        linea.getTroza().setExiste(Troza.EXISTE_SECCIONADA);
    }
    
    public void corregirMedidas(DetalleCorta linea){
        if (linea.getEspecie() != null){
            linea.getTroza().setEspecie(linea.getEspecie());
        }
        
        if (linea.getCalidad() != null){
            linea.getTroza().setCalidad(linea.getCalidad());
            
        }
        
        if (linea.getDmayor() != null){
            linea.getTroza().setdMayor(linea.getDmayor());
        }
        
        if (linea.getDmenor() != null){
            linea.getTroza().setdMenor(linea.getDmenor());
            
        }
        
        if (linea.getLargo() != null){
            linea.getTroza().setLargo(linea.getLargo());
        }
    }

    
    
}
