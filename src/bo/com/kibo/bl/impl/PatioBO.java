/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.intf.IPatioBO;
import bo.com.kibo.dal.intf.IPatioDAO;
import bo.com.kibo.entidades.Patio;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author Olvinho
 */
public class PatioBO
        extends GeoLugarBO<Patio, IPatioDAO>
        implements IPatioBO {

    @Override
    IPatioDAO getObjetoDAO() {
        return getDaoManager().getPatioDAO();
    }

    @Override
    protected int IdPermisoActualizar() {
        return 10802;
    }

    @Override
    protected int IdPermisoInsertar() {
        return 10801;
    }

    @Override
    protected void validar(Patio entity) {
        boolean nombreValido = true;
        if (isNullOrEmpty(entity.getNombre())) {
            appendException(new BusinessExceptionMessage("El nombre es un campo requerido", "nombre"));
            nombreValido = false;
        } else if (entity.getNombre().length() > 50) {
            appendException(new BusinessExceptionMessage("El nombre no puede tener mas de 50 carácteres", "nombre"));
            nombreValido = false;
        }

        if (entity.getArea() == null) {
            appendException(new BusinessExceptionMessage("El área es un campo requerido", "area"));
            nombreValido = false;
        } else {
            if (entity.getArea().getId() != null) {
                if (!(getDaoManager().getAreaDAO().checkId(entity.getArea().getId()))) {
                    appendException(new BusinessExceptionMessage("El área '" + entity.getArea().getId() + "' no existe", "area"));
                    nombreValido = false;
                }
            } else {
                //Buscamos por Codigo
                if (isNullOrEmpty(entity.getArea().getCodigo())) {
                    appendException(new BusinessExceptionMessage("El área es un campo requerido", "area"));
                    nombreValido = false;
                } else {
                    entity.getArea().setId(getDaoManager().getAreaDAO().getIdPorCodigo(entity.getArea().getCodigo()));
                    if (entity.getArea().getId() == null) {
                        appendException(new BusinessExceptionMessage("El área '" + entity.getArea().getCodigo() + "' no existe", "area"));
                        nombreValido = false;
                    }
                }
            }
        }

        if (nombreValido) {
            if (entity.getId() == null) {
                //Insertando y verificamos si el código existe
                if (getObjetoDAO().getIdPatioPorNombre(entity.getNombre(), entity.getArea().getId()) != null) {
                    appendException(new BusinessExceptionMessage("El patio '" + entity.getNombre() + "' ya existe", "nombre"));
                }
            } else {
                //Se quiere actualizar, verificamos que es válido y que el código si cambio, no existe
                if (!getObjetoDAO().checkId(entity.getId())) {
                    appendException(new BusinessExceptionMessage("El patio con Id  '" + entity.getId() + "' no existe", "id"));
                } else {
                    Patio actual = getObjetoDAO().obtenerPorId(entity.getId());
                    if (!actual.getArea().getId().equals(entity.getArea().getId())
                            || !actual.getNombre().equals(entity.getNombre())) {
                        if (getObjetoDAO().getIdPatioPorNombre(entity.getNombre(), entity.getArea().getId()) != null) {
                            appendException(new BusinessExceptionMessage("El patio '" + entity.getNombre() + "' ya existe", "nombre"));
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<Patio> obtenerPatiosSegunArea(final Integer idArea) {
        return ejecutarEnTransaccion(new Callable<List<Patio>>() {
            @Override
            public List<Patio> call() throws Exception {
                return getObjetoDAO().obtenerPatiosSegunArea(idArea);
            }
        });
    }

    @Override
    protected void despuesDeRecuperar(Patio entidad) {
        super.despuesDeRecuperar(entidad);
        if (entidad.getArea() != null) {
            entidad.getArea().getCodigo();
        }
    }

    @Override
    public String getNombre(final Integer id) {
        return ejecutarEnTransaccion(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getObjetoDAO().getNombre(id);
            }
        });
    }

}
