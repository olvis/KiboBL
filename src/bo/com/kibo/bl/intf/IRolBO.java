/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.intf;

import bo.com.kibo.entidades.Rol;
import bo.com.kibo.entidades.RolPermiso;
import bo.com.kibo.entidades.Usuario;
import java.util.List;

/**
 *
 * @author Olvinho
 */
public interface IRolBO extends IGenericoBO<Rol, Integer> {

    boolean verificarPermiso(Integer idPermiso, Usuario usuario);
    
    List<RolPermiso> getPermisos(int idRol, Integer idPadre);
    
    void guardarPermisos(List<RolPermiso> permisos);

    String getDescripcion(Integer id);
    
}
