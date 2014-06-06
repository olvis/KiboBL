/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.intf.IEspecieBO;
import bo.com.kibo.dal.intf.IEspecieDAO;
import bo.com.kibo.entidades.Especie;

/**
 *
 * @author Olvinho
 */
public class EspecieBO extends ObjetoNegocioGenerico<Especie, Integer, IEspecieDAO> implements IEspecieBO{

    @Override
    IEspecieDAO getObjetoDAO() {
        return getDaoManager().getEspecieDAO();
    }

    @Override
    protected int IdPermisoInsertar() {
        return 10301;
    }

    @Override
    protected int IdPermisoActualizar() {
        return 10302;
    }

    @Override
    protected void validar(Especie entity) {
        boolean nombreValido = true;
        if (isNullOrEmpty(entity.getNombre())){
         appendException(new BusinessExceptionMessage("El nombre es un campo requerido", "nombre"));
            nombreValido = false;   
        }else if (entity.getNombre().length() > 50){
            appendException(new BusinessExceptionMessage("El nombre no puede tener más de 50 carácteres", "nombre"));
            nombreValido = false;
        }
        
        if (nombreValido){
            if (entity.getId() == null){
                //Inserccion
                if (getObjetoDAO().getIdPorNombre(entity.getNombre()) != null) {
                    appendException(new BusinessExceptionMessage("La especie '" + entity.getNombre() + "' ya existe", "nombre"));
                }
            }else{
                //Actualizacion
                if (!getObjetoDAO().checkId(entity.getId())) {
                    appendException(new BusinessExceptionMessage("La especie con Id  '" + entity.getId() + "' no existe", "id"));
                } else {
                    Especie actual = getObjetoDAO().obtenerPorId(entity.getId());
                    if (!actual.getNombre().equals(entity.getNombre())) {
                        //El codigo cambio verificamos si existe
                        if (getObjetoDAO().getIdPorNombre(entity.getNombre()) != null) {
                            appendException(new BusinessExceptionMessage("La especie '" + entity.getNombre() + "' ya existe", "nombre"));
                        }
                    }
                }
            }
        }
        
        if (!isNullOrEmpty(entity.getCientifico()) && entity.getCientifico().length() > 50){
            appendException(new BusinessExceptionMessage("El nombre científico no puede tener más de 50 carácteres", "cientifico"));
        }
        
        if (entity.getFactor() < 0){
            appendException(new BusinessExceptionMessage("El factor debe ser mayor que cero", "factor"));
        }
        
        if ((entity.getDmc() != null) && (entity.getDmc() < 0)){
            appendException(new BusinessExceptionMessage("El DMC debe ser mayor que cero", "dmc"));
        }
    }
    
}
