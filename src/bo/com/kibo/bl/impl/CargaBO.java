/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.intf.ICargaBO;
import bo.com.kibo.dal.intf.ICargaDAO;
import bo.com.kibo.entidades.Carga;

/**
 *
 * @author Olvinho
 */
public class CargaBO extends ObjetoNegocioGenerico<Carga, Integer, ICargaDAO> implements ICargaBO {

    @Override
    ICargaDAO getObjetoDAO() {
        return getDaoManager().getCargaDAO();
    }

    @Override
    protected int IdPermisoInsertar() {
        return 10601;
    }

    @Override
    protected int IdPermisoActualizar() {
        return 10602;
    }

    protected void validar(Carga entity) {
        boolean codigoValido = true;
        if (isNullOrEmpty(entity.getCodigo())) {
            appendException(new BusinessExceptionMessage("El codigo es un campo requerido", "codigo"));
            codigoValido = false;
        } else if (entity.getCodigo().length() > 2) {
            appendException(new BusinessExceptionMessage("El nombre no puede tener más de 2 carácteres", "codigo"));
            codigoValido = false;
        }

        if (codigoValido) {
            if (entity.getId() == null) {
                //Insertando y verificamos si el código existe
                if (getObjetoDAO().getIdPorCodigo(entity.getCodigo()) != null) {
                    appendException(new BusinessExceptionMessage("El código '" + entity.getCodigo() + "' ya existe", "codigo"));
                }
            } else {
                //Se quiere actualizar, verificamos que es válido y que el código si cambio, no existe
                if (!getObjetoDAO().checkId(entity.getId())) {
                    appendException(new BusinessExceptionMessage("La carga con Id  '" + entity.getId() + "' no existe", "id"));
                } else {
                    Carga actual = getObjetoDAO().obtenerPorId(entity.getId());
                    if (!actual.getCodigo().equals(entity.getCodigo())) {
                        //El codigo cambio verificamos si existe
                        if (getObjetoDAO().getIdPorCodigo(entity.getCodigo()) != null) {
                            appendException(new BusinessExceptionMessage("El código '" + entity.getCodigo() + "' ya existe", "codigo"));
                        }
                    }
                }
            }
        }

    }
}
