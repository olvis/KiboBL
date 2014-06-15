/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.intf.IFormularioMovimientoBO;
import bo.com.kibo.dal.intf.IFormularioMovimientoDAO;
import bo.com.kibo.entidades.FormularioMovimiento;
import bo.com.kibo.entidades.Troza;
import bo.com.kibo.entidades.intf.IDetallePostCenso;

/**
 *
 * @author Olvinho
 */
public class FormularioMovimientoBO
        extends FormularioPostCensoBO<FormularioMovimiento, IFormularioMovimientoDAO>
        implements IFormularioMovimientoBO {

    @Override
    protected void validarEncabezado(FormularioMovimiento entity) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if (isNullOrEmpty(entity.getDestino())) {
            appendException(new BusinessExceptionMessage("El campo destino es requerido", "destino"));
        } else if (entity.getDestino().length() > 50) {
            appendException(new BusinessExceptionMessage("El destino no puede tener más de 50 carácteres", "destino"));
        }

        if (isNullOrEmpty(entity.getGuia())) {
            appendException(new BusinessExceptionMessage("El campo guía es requerido", "guia"));
        } else if (entity.getDestino().length() > 50) {
            appendException(new BusinessExceptionMessage("La guía no puede tener más de 50 carácteres", "guia"));
        }

    }

    @Override
    protected byte estadoRequerido() {
        return Troza.ESTADO_ENPATIO;
    }

    @Override
    protected void validarDetalle(IDetallePostCenso linea, int index, FormularioMovimiento cabecera) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void procesarLineaDetalle(IDetallePostCenso linea, int index, FormularioMovimiento cabecera) {
        linea.getTroza().setExiste(Troza.EXISTE_DESPACHADA);
        linea.getTroza().setFormularioDespacho(cabecera);
    }

    @Override
    IFormularioMovimientoDAO getObjetoDAO() {
        return getDaoManager().getFormularioMovimientoDAO();
    }

}
