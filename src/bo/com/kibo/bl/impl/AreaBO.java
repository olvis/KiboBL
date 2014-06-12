/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.intf.IAreaBO;
import bo.com.kibo.dal.intf.IAreaDAO;
import bo.com.kibo.entidades.Area;
import java.util.concurrent.Callable;

/**
 *
 * @author Olvinho
 */
public class AreaBO extends GeoLugarBO<Area, IAreaDAO> implements IAreaBO {

    public AreaBO() {

    }

    @Override
    protected int IdPermisoInsertar() {
        return 10101;
    }

    @Override
    protected int IdPermisoActualizar() {
        return 10102;
    }

    @Override
    protected void validar(Area entity) {

        //Validacion de codigo
        boolean codigoValido = true;
        if (isNullOrEmpty(entity.getCodigo())) {
            appendException(new BusinessExceptionMessage("El código es un campo requerido", "codigo"));
            codigoValido = false;
        } else if (entity.getCodigo().length() > 15) {
            appendException(new BusinessExceptionMessage("El código no puede tener más de 15 carácteres", "codigo"));
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
                    appendException(new BusinessExceptionMessage("El área con Id  '" + entity.getId() + "' no existe", "id"));
                } else {
                    Area actual = getObjetoDAO().obtenerPorId(entity.getId());
                    if (!actual.getCodigo().equals(entity.getCodigo())) {
                        //El codigo cambio verificamos si existe
                        if (getObjetoDAO().getIdPorCodigo(entity.getCodigo()) != null) {
                            appendException(new BusinessExceptionMessage("El código '" + entity.getCodigo() + "' ya existe", "codigo"));
                        }
                    }
                }
            }
        }

        //Año inicial
        if ((entity.getAnioInicial() != null) && (entity.getAnioInicial() < 0)) {
            appendException(new BusinessExceptionMessage("El año inicial debe ser mayor que cero", "anioInicial"));
        }

        //Año final
        if ((entity.getAnioFinal() != null) && (entity.getAnioFinal() < 0)) {
            appendException(new BusinessExceptionMessage("El año final debe ser mayor que cero", "anioFinal"));
        }

        //Zona UTM
        if ((entity.getZonaUTM() != null) && (!(entity.getZonaUTM() >= 1 && entity.getZonaUTM() <= 60))) {
            appendException(new BusinessExceptionMessage("La zona debe estar comprendida entre 1 y 60", "zonaUTM"));
        }

        //Banda UMT
        if (!isNullOrEmpty(entity.getBandaUTM())) {
            char x = entity.getBandaUTM().charAt(0);
            entity.setBandaUTM(String.valueOf(entity.getBandaUTM().charAt(0)));
            if (!(x >= 'C' && x <= 'N')) {
                appendException(new BusinessExceptionMessage("La banda debe ser un carácter entre C y N", "bandaUTM"));
            }
        }
    }

    @Override
    IAreaDAO getObjetoDAO() {
        return getDaoManager().getAreaDAO();
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

    @Override
    public Area recuperarPorCodigo(final String codigo) {
        return ejecutarEnTransaccion(new Callable<Area>() {
            @Override
            public Area call() throws Exception {
                 return getObjetoDAO().recuperarPorCodigo(codigo);
            }
        });
    }

}
