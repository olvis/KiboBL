/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.intf.IFormularioCensoBO;
import bo.com.kibo.dal.intf.IFormularioCensoDAO;
import bo.com.kibo.entidades.DetalleCenso;
import bo.com.kibo.entidades.FormularioCenso;
import bo.com.kibo.entidades.Troza;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Olvinho
 */
public class FormularioCensoBO
        extends ObjetoNegocioGenerico<FormularioCenso, Integer, IFormularioCensoDAO>
        implements IFormularioCensoBO {

    @Override
    protected int IdPermisoInsertar() {
        return 10401;
    }

    @Override
    IFormularioCensoDAO getObjetoDAO() {
        return getDaoManager().getFormularioCensoDAO();
    }

    @Override
    protected void validar(FormularioCenso entity) {
        if (entity.getId() != null) {
            appendException(new BusinessExceptionMessage("La actualización no esta permitida en este formulario"));
            return;
        }

        if (entity.getFecha() == null) {
            appendException(new BusinessExceptionMessage("El campo fecha es requerido", "fecha"));
        }

        if (entity.getArea() == null) {
            appendException(new BusinessExceptionMessage("El campo área es requerido", "area"));
        } else {
            if (entity.getArea().getId() != null) {
                if (!(getDaoManager().getAreaDAO().checkId(entity.getArea().getId()))) {
                    appendException(new BusinessExceptionMessage("El área '" + entity.getArea().getId() + "' no existe", "area"));
                }
            } else {
                //Buscamos por Codigo
                if (isNullOrEmpty(entity.getArea().getCodigo())) {
                    appendException(new BusinessExceptionMessage("El área es un campo requerido", "area"));
                } else {
                    entity.getArea().setId(getDaoManager().getAreaDAO().getIdPorCodigo(entity.getArea().getCodigo()));
                    if (entity.getArea().getId() == null) {
                        appendException(new BusinessExceptionMessage("El área '" + entity.getArea().getCodigo() + "' no existe", "area"));
                    }
                }
            }
        }

        if ((entity.getHoras() != null) && (entity.getHoras() < 0)) {
            appendException(new BusinessExceptionMessage("Las horas trabajadas debe ser un número positivo", "horas"));
        }

        if (entity.getFaja() == null) {
            appendException(new BusinessExceptionMessage("El campo faja es requerido", "faja"));
        } else {
            if (entity.getFaja().getId() != null) {
                if (!(getDaoManager().getFajaDAO().checkId(entity.getFaja().getId()))) {
                    appendException(new BusinessExceptionMessage("La faja '" + entity.getFaja().getId() + "' no existe", "faja"));
                }
            } else {
                entity.getFaja().setId(getDaoManager().getFajaDAO().getIdPorBloqueYNumero(entity.getFaja().getBloque(), entity.getFaja().getNumero()));
                if (entity.getFaja().getId() == null) {
                    appendException(new BusinessExceptionMessage("La faja especificada no existe", "faja"));
                } else {
                    entity.setFaja(getDaoManager().getFajaDAO().obtenerPorId(entity.getFaja().getId()));
                    if ((entity.getArea() != null) && (entity.getArea().getId() != null) && (!entity.getArea().getId().equals(entity.getFaja().getArea().getId()))) {
                        appendException(new BusinessExceptionMessage("La faja especificada no pertenece al área seleccionada", "faja"));
                    }
                }
            }
        }

        if (entity.getDetalle().isEmpty()) {
            appendException(new BusinessExceptionMessage("Debe agregar agregar árboles al detalle", "detalle"));
        }

        Map<String, Integer> codigos = new HashMap<>();
        //Validamos el detalle buscando duplicados
        for (int i = 0; i < entity.getDetalle().size(); i++) {
            validarLineaDetalle(entity.getDetalle().get(i), i + 1, entity);
            Integer filaDuplicada = codigos.get(entity.getDetalle().get(i).getCodigo());
            if (filaDuplicada != null) {
                appendException(new BusinessExceptionMessage("Registro duplicado con fila " + filaDuplicada, "detalle", i + 1));
            }
            if (!isNullOrEmpty(entity.getDetalle().get(i).getCodigo())) {
                codigos.put(entity.getDetalle().get(i).getCodigo(), i + 1);
            }
        }
    }

    private void validarLineaDetalle(DetalleCenso linea, int index, FormularioCenso cabecera) {
        if (isNullOrEmpty(linea.getCodigo())) {
            appendException(new BusinessExceptionMessage("El código es requerido", "codigo", index));
        } else {
            if ((cabecera.getArea() != null) && (cabecera.getArea().getId() != null)) {
                if (getDaoManager().getTrozaDAO().getIdPorCodigoArea(linea.getCodigo(), cabecera.getArea().getId()) != null) {
                    appendException(new BusinessExceptionMessage("El árbol '" + linea.getCodigo() + "' ya existe en la base de datos", "codigo", index));
                }
            }
        }

        if (linea.getEspecie() == null) {
            appendException(new BusinessExceptionMessage("El campo especie es requerido", "especie", index));
        } else {
            if (linea.getEspecie().getId() != null) {
                if (!getDaoManager().getEspecieDAO().checkId(linea.getEspecie().getId())) {
                    appendException(new BusinessExceptionMessage("La especie '" + linea.getEspecie().getId() + "' no existe", "especie", index));
                }
            } else {
                if (isNullOrEmpty(linea.getEspecie().getNombre())) {
                    appendException(new BusinessExceptionMessage("El campo especie es requerido", "especie", index));
                } else {
                    linea.getEspecie().setId(getDaoManager().getEspecieDAO().getIdPorNombre(linea.getEspecie().getNombre()));
                    if (linea.getEspecie().getId() == null) {
                        appendException(new BusinessExceptionMessage("La especie '" + linea.getEspecie().getNombre() + "' no existe", "especie", index));
                    }
                }
            }
        }

        if (linea.getDap() == null) {
            appendException(new BusinessExceptionMessage("El campo DAP es requerido", "dap", index));
        } else if (!(linea.getDap() > 0)) {
            appendException(new BusinessExceptionMessage("El campo DAP debe ser mayor que cero", "dap", index));
        }

        if (linea.getAltura() == null) {
            appendException(new BusinessExceptionMessage("El campo Altura es requerido", "altura", index));
        } else if (!(linea.getAltura() > 0)) {
            appendException(new BusinessExceptionMessage("El campo Altura debe ser mayor que cero", "altura", index));
        }

        if (linea.getCalidad() == null) {
            appendException(new BusinessExceptionMessage("El campo calidad es requerido", "calidad", index));
        } else {
            if (linea.getCalidad().getId() != null) {
                if (!getDaoManager().getCalidadDAO().checkId(linea.getCalidad().getId())) {
                    appendException(new BusinessExceptionMessage("La calidad '" + linea.getEspecie().getId() + "' no existe", "calidad", index));
                }
            } else {
                if (isNullOrEmpty(linea.getCalidad().getCodigo())) {
                    appendException(new BusinessExceptionMessage("El campo calidad es requerido", "calidad", index));
                } else {
                    linea.getCalidad().setId(getDaoManager().getCalidadDAO().getIdPorCodigo(linea.getCalidad().getCodigo()));
                    if (linea.getCalidad().getId() == null) {
                        appendException(new BusinessExceptionMessage("La calidad '" + linea.getCalidad().getCodigo() + "' no existe", "calidad", index));
                    }
                }
            }
        }

        linea.setCondicion(linea.getCondicion().toLowerCase());
        if (isNullOrEmpty(linea.getCondicion())) {
            appendException(new BusinessExceptionMessage("El campo condición es requerido", "condicion", index));
        } else if (!condicionValida(linea.getCondicion())) {
            appendException(new BusinessExceptionMessage("La condición no tiene un valor válido, los valores válidos son: 'apr', 'sem' o 'avc'", "condicion", index));
        }
    }

    private boolean condicionValida(String condicion) {
        return (condicion.equals("apr") || condicion.equals("sem") || condicion.equals("avc"));
    }

    @Override
    protected void postInsertar(FormularioCenso entidad) {
        //Creamos las trozas que son aprovechables
        for (DetalleCenso detalle : entidad.getDetalle()) {
            if ("apr".equalsIgnoreCase(detalle.getCondicion())) {
                TrozaBO trozaBO = new TrozaBO();
                Troza troza = trozaBO.crearTroza(detalle, entidad);
                detalle.setTroza(troza);
            }
        }

    }

    @Override
    protected void despuesDeRecuperar(FormularioCenso entidad) {
        entidad.getDetalle().size();
    }

}
