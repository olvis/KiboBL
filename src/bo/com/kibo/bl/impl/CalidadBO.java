/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.intf.ICalidadBO;
import bo.com.kibo.dal.intf.ICalidadDAO;
import bo.com.kibo.entidades.Calidad;
import java.util.concurrent.Callable;

/**
 *
 * @author Olvinho
 */
public class CalidadBO extends ObjetoNegocioGenerico<Calidad, Integer, ICalidadDAO> implements ICalidadBO {

    @Override
    ICalidadDAO getObjetoDAO() {
        return getDaoManager().getCalidadDAO();
    }

    @Override
    protected int IdPermisoActualizar() {
        return 10202;
    }

    @Override
    protected int IdPermisoInsertar() {
        return 10201;
    }

    @Override
    protected void validar(Calidad entity) {
        //Validacion de codigo
        boolean codigoValido = true;
        if (isNullOrEmpty(entity.getCodigo())) {
            appendException(new BusinessExceptionMessage("El código es un campo requerido", "codigo"));
            codigoValido = false;
        } else if (entity.getCodigo().length() > 5) {
            appendException(new BusinessExceptionMessage("El código no puede tener más de 5 carácteres", "codigo"));
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
                    appendException(new BusinessExceptionMessage("La calidad con Id  '" + entity.getId() + "' no existe", "id"));
                } else {
                    Calidad actual = getObjetoDAO().obtenerPorId(entity.getId());
                    if (!actual.getCodigo().equals(entity.getCodigo())) {
                        //El codigo cambio verificamos si existe
                        if (getObjetoDAO().getIdPorCodigo(entity.getCodigo()) != null) {
                            appendException(new BusinessExceptionMessage("El código '" + entity.getCodigo() + "' ya existe", "codigo"));
                        }
                    }
                }
            }
        }
        
        if (!isNullOrEmpty(entity.getDescripcion()) && (entity.getDescripcion().length() > 50)){
            appendException(new BusinessExceptionMessage("La descripción no puede tener más de 50 carácteres", "descripcion"));
        }

    }

    @Override
    public String getCodigo(final Integer id) {
        return ejecutarEnTransaccion(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getObjetoDAO().getCodigo(id);
            }
        });
    }
    
}
