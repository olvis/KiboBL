/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.intf.IFajaBO;
import bo.com.kibo.dal.intf.IFajaDAO;
import bo.com.kibo.entidades.Faja;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author Olvinho
 */
public class FajaBO extends GeoLugarBO<Faja, IFajaDAO> implements IFajaBO {

    public FajaBO() {

    }

    @Override
    IFajaDAO getObjetoDAO() {
        return getDaoManager().getFajaDAO();
    }

    @Override
    protected int IdPermisoActualizar() {
        return 10702;
    }

    @Override
    protected int IdPermisoInsertar() {
        return 10701;
    }

    @Override
    protected void validar(Faja entity) {
        boolean codigoValido = true; //El codigo se compone entre el bloque, numero y area
        if (isNullOrEmpty(entity.getBloque())) {
            appendException(new BusinessExceptionMessage("El bloque es un campo requerido", "bloque"));
            codigoValido = false;
        } else if (entity.getBloque().length() > 20) {
            appendException(new BusinessExceptionMessage("El bloque no puede tener más de 20 carácteres", "bloque"));
            codigoValido = false;
        }

        if (!(entity.getNumero() > 0)) {
            appendException(new BusinessExceptionMessage("El número debe ser un número que cero", "numero"));
            codigoValido = false;
        }

        if (entity.getArea() == null) {
            appendException(new BusinessExceptionMessage("El área es un campo requerido", "area"));
            codigoValido = false;
        } else {
            if (entity.getArea().getId() != null) {
                if (!(getDaoManager().getAreaDAO().checkId(entity.getArea().getId()))) {
                    appendException(new BusinessExceptionMessage("El área '" + entity.getArea().getId() + "' no existe", "area"));
                    codigoValido = false;
                }
            } else {
                //Buscamos por Codigo
                if (isNullOrEmpty(entity.getArea().getCodigo())) {
                    appendException(new BusinessExceptionMessage("El área es un campo requerido", "area"));
                    codigoValido = false;
                } else {
                    entity.getArea().setId(getDaoManager().getAreaDAO().getIdPorCodigo(entity.getArea().getCodigo()));
                    if (entity.getArea().getId() == null) {
                        appendException(new BusinessExceptionMessage("El área '" + entity.getArea().getCodigo() + "' no existe", "area"));
                        codigoValido = false;
                    }
                }
            }
        }

        if (codigoValido) {
            if (entity.getId() == null) {
                //Es inserccion
                if (getObjetoDAO().existeFaja(entity)) {
                    appendException(new BusinessExceptionMessage("La faja con número  '" + entity.getNumero() + "' y bloque '" + entity.getBloque() + "' ya existe en área especificada", "area"));
                }
            } else {
                boolean cambioCodigo = false;
                Faja actual = getObjetoDAO().obtenerPorId(entity.getId());
                if (!entity.getArea().getId().equals(actual.getArea().getId())) {
                    cambioCodigo = true;
                }

                if (!entity.getBloque().equalsIgnoreCase(actual.getBloque())) {
                    cambioCodigo = true;
                }

                if (entity.getNumero() != actual.getNumero()) {
                    cambioCodigo = true;
                }

                if (cambioCodigo) {
                    if (getObjetoDAO().existeFaja(entity)) {
                        appendException(new BusinessExceptionMessage("La faja con número  '" + entity.getNumero() + "' y bloque '" + entity.getBloque() + "' ya existe en área especificada", "area"));
                    }
                }

            }
        }

    }

    @Override
    protected void despuesDeRecuperar(Faja entidad) {
        super.despuesDeRecuperar(entidad); //To change body of generated methods, choose Tools | Templates.
        //Obligamos a cargar el area
        if (entidad.getArea() != null){
            entidad.getArea().getCodigo();
        }
    }

    @Override
    public List<Faja> obtenerFajasSegunArea(final Integer idArea) {
        return ejecutarEnTransaccion(new Callable<List<Faja>>() {
            @Override
            public List<Faja> call() throws Exception {
                return getObjetoDAO().obtenerFajasSegunArea(idArea);
            }
        });
    }
    
}
