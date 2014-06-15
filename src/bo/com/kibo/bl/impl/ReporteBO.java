/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.intf.IReporteBO;
import bo.com.kibo.dal.intf.IReporteDAO;
import bo.com.kibo.entidades.reportes.CensoGeneral;
import bo.com.kibo.entidades.reportes.TrozaGeneral;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author Olvinho
 */
public class ReporteBO
        extends ObjetoNegocioGenerico<CensoGeneral, Integer, IReporteDAO>
        implements IReporteBO {

    @Override
    IReporteDAO getObjetoDAO() {
        return getDaoManager().getReporteDOA();
    }

    @Override
    public List<CensoGeneral> obtenerCensoGeneral() {
        return ejecutarEnTransaccion(new Callable<List<CensoGeneral>>() {
            @Override
            public List<CensoGeneral> call() throws Exception {
                return getObjetoDAO().obtenerCensoGeneral();
            }
        });
    }

    @Override
    public List<TrozaGeneral> obtenerTrozasGeneral() {
        return ejecutarEnTransaccion(new Callable<List<TrozaGeneral>>() {
            @Override
            public List<TrozaGeneral> call() throws Exception {
                return getObjetoDAO().obtenerTrozasGeneral();
            }
        });
    }

    @Override
    protected void validar(CensoGeneral entity) {
        appendException(new BusinessExceptionMessage("En los reportes no se permite insercció, ni actualización"));
    }

    @Override
    public List<CensoGeneral> obtenerSaldoCenso() {
        return ejecutarEnTransaccion(new Callable<List<CensoGeneral>>() {
            @Override
            public List<CensoGeneral> call() throws Exception {
                return getObjetoDAO().obtenerSaldoCenso();
            }
        });
    }

    @Override
    public List<TrozaGeneral> obtenerTalaGeneral() {
        return ejecutarEnTransaccion(new Callable<List<TrozaGeneral>>() {
            @Override
            public List<TrozaGeneral> call() throws Exception {
                return getObjetoDAO().obtenerTalaGeneral();
            }
        });
    }

    @Override
    public List<TrozaGeneral> obtenerTalaSaldo() {
        return ejecutarEnTransaccion(new Callable<List<TrozaGeneral>>() {
            @Override
            public List<TrozaGeneral> call() throws Exception {
                return getObjetoDAO().obtenerTalaSaldo();
            }
        });
    }

    @Override
    public List<TrozaGeneral> obtenerExtraccionGeneral() {
        return ejecutarEnTransaccion(new Callable<List<TrozaGeneral>>() {
            @Override
            public List<TrozaGeneral> call() throws Exception {
                return getObjetoDAO().obtenerExtraccionGeneral();
            }
        });
    }

    @Override
    public List<TrozaGeneral> obtenerExtraccionSaldo() {
        return ejecutarEnTransaccion(new Callable<List<TrozaGeneral>>() {
            @Override
            public List<TrozaGeneral> call() throws Exception {
                return getObjetoDAO().obtenerExtraccionSaldo();
            }
        });
    }

    @Override
    public List<TrozaGeneral> obtenerDespachoGeneral() {
        return ejecutarEnTransaccion(new Callable<List<TrozaGeneral>>() {
            @Override
            public List<TrozaGeneral> call() throws Exception {
                return getObjetoDAO().obtenerDespachoGeneral();
            }
        });
    }

}
