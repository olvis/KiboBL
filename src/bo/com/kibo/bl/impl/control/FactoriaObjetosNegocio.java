/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl.control;

import bo.com.kibo.bl.impl.AreaBO;
import bo.com.kibo.bl.impl.UsuarioBO;
import bo.com.kibo.bl.intf.IAreaBO;
import bo.com.kibo.bl.intf.IUsuarioBO;

/**
 *
 * @author Olvinho
 */
public class FactoriaObjetosNegocio {

    private static final ThreadLocal<FactoriaObjetosNegocio> caja = new ThreadLocal<>();

    private FactoriaObjetosNegocio() {

    }

    public static FactoriaObjetosNegocio getInstance() {
        FactoriaObjetosNegocio businessObjectsFactory = caja.get();
        if (businessObjectsFactory == null) {
            businessObjectsFactory = new FactoriaObjetosNegocio();
            caja.set(businessObjectsFactory);
        }
        return businessObjectsFactory;
    }

    private IAreaBO areaBO;
    public IAreaBO getAreaBO() {
        if (areaBO == null) {
            areaBO = new AreaBO();
        }
        return areaBO;
    }
    
    private IUsuarioBO usuarioBO;
    public IUsuarioBO getIUsuarioBO(){
        if (usuarioBO == null){
            usuarioBO = new UsuarioBO();
        }
        return usuarioBO;
    }

}
