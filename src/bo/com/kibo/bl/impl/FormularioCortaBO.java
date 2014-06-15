/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.intf.IFormularioCortaBO;
import bo.com.kibo.dal.intf.IFormularioCortaDAO;
import bo.com.kibo.entidades.FormularioCorta;
import bo.com.kibo.entidades.Troza;
import bo.com.kibo.entidades.intf.IDetallePostCenso;

/**
 *
 * @author Olvinho
 */
public class FormularioCortaBO
        extends FormularioPostCensoBO<FormularioCorta, IFormularioCortaDAO>
        implements IFormularioCortaBO {

    @Override
    IFormularioCortaDAO getObjetoDAO() {
        return getDaoManager().getFormularioCortaDAO();
    }

    @Override
    protected int IdPermisoInsertar() {
        return 10501;
    }

    @Override
    protected void validarEncabezado(FormularioCorta entity) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected byte estadoRequerido() {
        return Troza.ESTADO_CENSADA;
    }

    @Override
    protected void validarDetalle(IDetallePostCenso linea, int index, FormularioCorta cabecera) {
        if (linea.getCarga() == null) {
            //Requerimos medidas
            //Dmayor
            if (linea.getDmayor() == null) {
                appendException(new BusinessExceptionMessage("El campo DMayor es requerido", "dMayor", index));
            } else if (linea.getDmayor() <= 0) {
                appendException(new BusinessExceptionMessage("El campo DMayor debe ser mayor que cero", "dMayor", index));
            }
            //DMenor
            if (linea.getDmenor() == null) {
                appendException(new BusinessExceptionMessage("El campo DMenor es requerido", "dMenor", index));
            } else if (linea.getDmenor() <= 0) {
                appendException(new BusinessExceptionMessage("El campo DMenor debe ser mayor que cero", "dMenor", index));
            }
            //Largo
            if (linea.getLargo() == null) {
                appendException(new BusinessExceptionMessage("El campo Largo es requerido", "largo", index));
            } else if (linea.getLargo() <= 0) {
                appendException(new BusinessExceptionMessage("El campo Largo debe ser mayor que cero", "largo", index));
            }
        }
    }

    @Override
    protected void procesarLineaDetalle(IDetallePostCenso linea, int index, FormularioCorta cabecera) {
        linea.getTroza().setEstado(Troza.ESTADO_TALADA);
        linea.getTroza().setFormularioCorta(cabecera);
    }
}
