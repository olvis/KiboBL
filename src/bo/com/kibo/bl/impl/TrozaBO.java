/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.intf.ITrozaBO;
import bo.com.kibo.dal.intf.ITrozaDAO;
import bo.com.kibo.entidades.DetalleCenso;
import bo.com.kibo.entidades.EncabezadoFormulario;
import bo.com.kibo.entidades.FormularioCenso;
import bo.com.kibo.entidades.FormularioCorta;
import bo.com.kibo.entidades.FormularioExtraccion;
import bo.com.kibo.entidades.FormularioMovimiento;
import bo.com.kibo.entidades.Troza;
import bo.com.kibo.entidades.intf.IDetallePostCenso;
import bo.com.kibo.entidades.intf.IFormularioPostCenso;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author Olvinho
 */
public class TrozaBO extends ObjetoNegocioGenerico<Troza, Integer, ITrozaDAO> implements ITrozaBO {

    @Override
    ITrozaDAO getObjetoDAO() {
        return getDaoManager().getTrozaDAO();
    }

    public Troza crearTroza(DetalleCenso trozaCenso, FormularioCenso censo) {
        Troza troza = new Troza();

        troza.setCodigo(trozaCenso.getCodigo());
        troza.setArea(censo.getArea());
        troza.setEspecie(trozaCenso.getEspecie());
        troza.setCalidad(trozaCenso.getCalidad());
        troza.setEstado(Troza.ESTADO_CENSADA);
        troza.setExiste(Troza.EXISTE_EXISTE);
        troza.setX(trozaCenso.getX());
        troza.setY(trozaCenso.getY());
        troza.setFormularioCenso(censo);
        troza.setFaja(censo.getFaja());

        troza = getObjetoDAO().persistir(troza);
        return troza;
    }

    @Override
    protected void validar(Troza entity) {
        appendException(new BusinessExceptionMessage("La insercción o actualización directa no está permitida en las trozas"));
    }

    @Override
    public List<Troza> getTrozasParaTala(final Integer idArea) {
        return ejecutarEnTransaccion(new Callable<List<Troza>>() {
            @Override
            public List<Troza> call() throws Exception {
                return getDaoManager().getTrozaDAO().getTrozasParaTala(idArea);
            }
        });
    }

    @Override
    public List<Troza> getTrozasParaExtraccion(final Integer idArea) {
        return ejecutarEnTransaccion(new Callable<List<Troza>>() {
            @Override
            public List<Troza> call() throws Exception {
                return getDaoManager().getTrozaDAO().getTrozasParaExtraccion(idArea);
            }
        });
    }

    @Override
    public List<Troza> getTrozasParaMovimiento(final Integer idArea) {
        return ejecutarEnTransaccion(new Callable<List<Troza>>() {
            @Override
            public List<Troza> call() throws Exception {
                return getDaoManager().getTrozaDAO().getTrozasParaMovimiento(idArea);
            }
        });
    }

    @Override
    public String getCodigo(final Integer numero) {
        return ejecutarEnTransaccion(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getObjetoDAO().getCodigo(numero);
            }
        });
    }

    public Troza crearSeccion(IDetallePostCenso linea, IFormularioPostCenso formulario) {
        Troza seccion = new Troza();
        seccion.setCodigo(linea.getCodigoCarga());
        seccion.setArea(formulario.getArea());
        if (linea.getEspecie() != null) {
            seccion.setEspecie(linea.getEspecie());
        } else {
            seccion.setEspecie(linea.getTroza().getEspecie());
        }

        if (linea.getCalidad() != null) {
            seccion.setCalidad(linea.getCalidad());
        } else {
            seccion.setCalidad(linea.getTroza().getCalidad());
        }

        if (linea.getPatio() == null) {
            seccion.setPatio(linea.getTroza().getPatio());
        } else {
            seccion.setPatio(linea.getPatio());
        }

        //seccion.setCalidad(linea.getCalidad());
        seccion.setFaja(linea.getTroza().getFaja());
        seccion.setPadre(linea.getTroza());

        byte estadoSeccion = Troza.ESTADO_CENSADA;
        byte existeSeccion = Troza.EXISTE_EXISTE;
        switch (formulario.getTipo()) {
            case EncabezadoFormulario.TIPO_FORMULARIO_CORTA:
                estadoSeccion = Troza.ESTADO_TALADA;
                seccion.setFormularioCorta((FormularioCorta) formulario);
                break;
            case EncabezadoFormulario.TIPO_FORMULARIO_EXTRACCION:
                estadoSeccion = Troza.ESTADO_ENPATIO;
                seccion.setFormularioExtraccion((FormularioExtraccion) formulario);
                break;
            case EncabezadoFormulario.TIPO_FORMULARIO_MOVIMIENTO:
                estadoSeccion = Troza.ESTADO_ENPATIO;
                existeSeccion = Troza.EXISTE_DESPACHADA;
                seccion.setFormularioDespacho((FormularioMovimiento) formulario);
                break;
        }
        seccion.setExiste(existeSeccion);
        seccion.setEstado(estadoSeccion);
        seccion.setdMayor(linea.getDmayor());
        seccion.setdMenor(linea.getDmenor());
        seccion.setLargo(linea.getLargo());
        seccion = getObjetoDAO().persistir(seccion);
        linea.getTroza().setExiste(Troza.EXISTE_SECCIONADA);
        return seccion;
    }

    public void corregirMedidas(IDetallePostCenso linea, IFormularioPostCenso cabecera) {
        if (linea.getEspecie() != null) {
            if (!linea.getEspecie().getId().equals(linea.getTroza().getEspecie().getId())) {
                linea.getTroza().setEspecie(linea.getEspecie());
            }

        }

        if (linea.getCalidad() != null) {
            if (!linea.getCalidad().getId().equals(linea.getTroza().getCalidad().getId())) {
                linea.getTroza().setCalidad(linea.getCalidad());
            }
        }

        if (linea.getDmayor() != null) {
            if (linea.getTroza().getdMayor() == null) {
                linea.getTroza().setdMayor(linea.getDmayor());
            } else if (!linea.getDmayor().equals(linea.getTroza().getdMayor())) {
                linea.getTroza().setdMayor(linea.getDmayor());
            }
        }

        if (linea.getDmenor() != null) {
            if (linea.getTroza().getdMenor() == null) {
                linea.getTroza().setdMenor(linea.getDmenor());
            } else if (!linea.getDmenor().equals(linea.getTroza().getdMenor())) {
                linea.getTroza().setdMenor(linea.getDmenor());
            }
        }

        if (linea.getLargo() != null) {
            if (linea.getTroza().getLargo() == null) {
                linea.getTroza().setLargo(linea.getLargo());
            } else if (!linea.getLargo().equals(linea.getTroza().getLargo())) {
                linea.getTroza().setLargo(linea.getLargo());
            }
        }
    }

    

    @Override
    public Troza obtenerPorCodigo(final String codigo, final Integer idArea) {
        return ejecutarEnTransaccion(new Callable<Troza>() {
            @Override
            public Troza call() throws Exception {
                Integer id = getObjetoDAO().getIdPorCodigoArea(codigo, idArea);
                if (id == null) {
                    return null;
                }
                return getObjetoDAO().recuperarPorId(id);
            }
        });
    }

    @Override
    public List<String> getCodigosTrozaParaTala(final Integer idArea, final String codigoParcial) {
        return ejecutarEnTransaccion(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return getObjetoDAO().getCodigosTrozaParaTala(idArea, codigoParcial);
            }
        });
    }
    
    @Override
    public List<String> getCodigosTrozaParaExtraccion(final Integer idArea,final  String codigoParcial) {
        return ejecutarEnTransaccion(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return getObjetoDAO().getCodigosTrozaParaExtraccion(idArea, codigoParcial);
            }
        });
    }

    @Override
    public List<String> getCodigosTrozaParaMovimiento(final Integer idArea,final String codigoParcial) {
        return ejecutarEnTransaccion(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return getObjetoDAO().getCodigosTrozaParaMovimiento(idArea, codigoParcial);
            }
        });
    }

}
