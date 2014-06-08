/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo.com.kibo.bl.intf;

import bo.com.kibo.entidades.Usuario;

/**
 *
 * @author Olvinho
 */
public interface IUsuarioBO extends IGenericoBO<Usuario, Integer>{
    
    Integer getIdUsuarioPorEmail(String email);
    Usuario loguear(String usuario, String pass);
}
