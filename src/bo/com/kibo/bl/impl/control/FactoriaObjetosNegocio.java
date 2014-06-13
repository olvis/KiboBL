/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl.control;

import bo.com.kibo.bl.impl.AreaBO;
import bo.com.kibo.bl.impl.CalidadBO;
import bo.com.kibo.bl.impl.CargaBO;
import bo.com.kibo.bl.impl.EspecieBO;
import bo.com.kibo.bl.impl.FajaBO;
import bo.com.kibo.bl.impl.FormularioCensoBO;
import bo.com.kibo.bl.impl.FormularioCortaBO;
import bo.com.kibo.bl.impl.ReporteBO;
import bo.com.kibo.bl.impl.RolBO;
import bo.com.kibo.bl.impl.TrozaBO;
import bo.com.kibo.bl.impl.UsuarioBO;
import bo.com.kibo.bl.intf.IAreaBO;
import bo.com.kibo.bl.intf.ICalidadBO;
import bo.com.kibo.bl.intf.ICargaBO;
import bo.com.kibo.bl.intf.IEspecieBO;
import bo.com.kibo.bl.intf.IFajaBO;
import bo.com.kibo.bl.intf.IFormularioCensoBO;
import bo.com.kibo.bl.intf.IFormularioCortaBO;
import bo.com.kibo.bl.intf.IReporteBO;
import bo.com.kibo.bl.intf.IRolBO;
import bo.com.kibo.bl.intf.ITrozaBO;
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

    public IUsuarioBO getIUsuarioBO() {
        if (usuarioBO == null) {
            usuarioBO = new UsuarioBO();
        }
        return usuarioBO;
    }

    private IFajaBO fajaBO;

    public IFajaBO getFajaBO() {
        if (fajaBO == null) {
            fajaBO = new FajaBO();
        }
        return fajaBO;
    }

    private IEspecieBO especieBO;

    public IEspecieBO getEspecieBO() {
        if (especieBO == null) {
            especieBO = new EspecieBO();
        }
        return especieBO;
    }

    private ICargaBO cargaBO;

    public ICargaBO getCargaBO() {
        if (cargaBO == null) {
            cargaBO = new CargaBO();
        }
        return cargaBO;
    }

    private ICalidadBO calidadBO;

    public ICalidadBO getCalidadBO() {
        if (calidadBO == null) {
            calidadBO = new CalidadBO();
        }
        return calidadBO;
    }

    private ITrozaBO trozaBO;

    public ITrozaBO getTrozaBO() {
        if (trozaBO == null) {
            trozaBO = new TrozaBO();
        }
        return trozaBO;
    }

    private IFormularioCensoBO formularioCensoBO;

    public IFormularioCensoBO getFormularioCensoBO() {
        if (formularioCensoBO == null) {
            formularioCensoBO = new FormularioCensoBO();
        }
        return formularioCensoBO;
    }

    private IFormularioCortaBO formularioCortaBO;

    public IFormularioCortaBO getFormularioCortaBO() {
        if (formularioCortaBO == null) {
            formularioCortaBO = new FormularioCortaBO();
        }
        return formularioCortaBO;
    }

    private IRolBO rolBO;

    public IRolBO getRolBO() {
        if (rolBO == null) {
            rolBO = new RolBO();
        }
        return rolBO;
    }
    
    private IReporteBO reporteBO;
    
    public IReporteBO getReporteBO(){
        if (reporteBO == null){
            reporteBO = new ReporteBO();
        }
        return reporteBO;
    }

}
