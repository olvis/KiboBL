/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.intf;

import bo.com.kibo.entidades.reportes.CensoGeneral;
import bo.com.kibo.entidades.reportes.TrozaGeneral;
import java.util.List;

/**
 *
 * @author Olvinho
 */
public interface IReporteBO {

    List<CensoGeneral> obtenerCensoGeneral();

    List<CensoGeneral> obtenerSaldoCenso();

    List<TrozaGeneral> obtenerTrozasGeneral();

    List<TrozaGeneral> obtenerTalaGeneral();

    List<TrozaGeneral> obtenerTalaSaldo();

    List<TrozaGeneral> obtenerExtraccionGeneral();

    List<TrozaGeneral> obtenerExtraccionSaldo();

    List<TrozaGeneral> obtenerDespachoGeneral();
}
