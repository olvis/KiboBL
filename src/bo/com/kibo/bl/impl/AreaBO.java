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

/**
 *
 * @author Olvinho
 */
public class AreaBO extends GenericBO<Area, Integer, IAreaDAO> implements IAreaBO {

    public AreaBO() {
        super.objectDAO = getDaoManager().getAreaDAO();
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
        if (entity.getCodigo().isEmpty()) {
            appendException(new BusinessExceptionMessage("El código es un campo requerido", "codigo"));
            codigoValido = false;
        } else if (entity.getCodigo().length() > 15) {
            appendException(new BusinessExceptionMessage("El código no puede tener más de 15 carácteres", "codigo"));
            codigoValido = false;
        }
        
        if (codigoValido){
            if (entity.getId() == null) {
                //Insertando y verificamos si el código existe
                if (objectDAO.getIdPorCodigo(entity.getCodigo()) != null) {
                    appendException(new BusinessExceptionMessage("El código '" + entity.getCodigo() + "' ya existe", "codigo"));
                }
            } else {
                //Se quiere actualizar, verificamos que es válido y que el código si cambio, no existe
                if (!objectDAO.checkId(entity.getId())){
                    appendException(new BusinessExceptionMessage("El área con Id  '" + entity.getId() + "' no existe", "id"));
                }
                else{
                    Area actual = objectDAO.obtenerPorId(entity.getId());
                    if (!actual.getCodigo().equals(entity.getCodigo())){
                        //El codigo cambio verificamos si existe
                        if (objectDAO.getIdPorCodigo(entity.getCodigo()) != null) {
                            appendException(new BusinessExceptionMessage("El código '" + entity.getCodigo() + "' ya existe", "codigo"));
                        }
                    }
                }
            }
        }
        
        //Año inicial
        if ((entity.getAnioInicial()!= null) && (entity.getAnioInicial() < 0)){
            appendException(new BusinessExceptionMessage("El año inicial debe ser mayor que cero", "anioInicial"));
        }
        
        //Año final
        if ((entity.getAnioFinal()!= null) && (entity.getAnioFinal() < 0)){
            appendException(new BusinessExceptionMessage("El año final debe ser mayor que cero", "anioFinal"));
        }
        
        //Zona UTM
        if ((entity.getZonaUTM() != null) && (!(entity.getZonaUTM() >= 1 && entity.getZonaUTM() <= 11))){
            appendException(new BusinessExceptionMessage("La zona debe estar comprendida entre 1 y 11", "zonaUTM"));
        }
      
        //Banda UMT
        if (!entity.getBandaUTM().isEmpty()){
            char x = entity.getBandaUTM().charAt(0);
            entity.setBandaUTM(String.valueOf(entity.getBandaUTM().charAt(0)));
            if (!(x >= 'C' && x <= 'N')){
                appendException(new BusinessExceptionMessage("La banda debe ser un carácter entre C y N", "bandaUTM"));
            }
        }
    }

}
