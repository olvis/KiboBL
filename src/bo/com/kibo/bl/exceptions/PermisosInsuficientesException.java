/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.exceptions;

/**
 *
 * @author Olvinho
 */
public class PermisosInsuficientesException extends BusinessException {

    public PermisosInsuficientesException(String message) {
        super(message);
    }

    public PermisosInsuficientesException(String message, Throwable cause) {
        super(message, cause);
    }

}
