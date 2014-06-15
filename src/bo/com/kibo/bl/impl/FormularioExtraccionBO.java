/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.intf.IFormularioExtraccionBO;
import bo.com.kibo.dal.intf.IFormularioExtraccionDAO;
import bo.com.kibo.entidades.FormularioExtraccion;
import bo.com.kibo.entidades.Troza;
import bo.com.kibo.entidades.intf.IDetallePostCenso;

/**
 *
 * @author Olvinho
 */
public class FormularioExtraccionBO
        extends FormularioPostCensoBO<FormularioExtraccion, IFormularioExtraccionDAO>
        implements IFormularioExtraccionBO {

    @Override
    protected int IdPermisoInsertar() {
        return 10901;
    }

    @Override
    protected void validarEncabezado(FormularioExtraccion entity) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected byte estadoRequerido() {
        return Troza.ESTADO_TALADA;
    }

    @Override
    protected void validarDetalle(IDetallePostCenso linea, int index, FormularioExtraccion cabecera) {
        if (linea.getPatio() == null) {
            appendException(new BusinessExceptionMessage("El campo patio es requerido", "patio", index));
        } else {
            if (linea.getPatio().getId() != null) {
                if (!getDaoManager().getPatioDAO().checkId(linea.getPatio().getId())) {
                    appendException(new BusinessExceptionMessage("El patio '" + linea.getPatio().getId() + "' no existe", "patio", index));
                }
            } else {
                if (isNullOrEmpty(linea.getPatio().getNombre())) {
                    appendException(new BusinessExceptionMessage("El campo patio es requerido", "patio", index));
                } else {
                    linea.getPatio().setId(getDaoManager().getPatioDAO().getIdPorNombre(linea.getPatio().getNombre()));
                    if (linea.getPatio().getId() == null) {
                        appendException(new BusinessExceptionMessage("El patio '" + linea.getPatio().getNombre() + "' no existe", "patio", index));
                    }
                }
            }

        }
    }

    @Override
    IFormularioExtraccionDAO getObjetoDAO() {
        return getDaoManager().getFormularioExtraccionDAO();
    }

    @Override
    protected void procesarLineaDetalle(IDetallePostCenso linea, int index, FormularioExtraccion cabecera) {
        linea.getTroza().setEstado(Troza.ESTADO_ENPATIO);
        linea.getTroza().setPatio(linea.getPatio());
        linea.getTroza().setFormularioExtraccion(cabecera);
    }

}
