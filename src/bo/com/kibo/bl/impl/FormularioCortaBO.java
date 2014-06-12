/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.intf.IFormularioCortaBO;
import bo.com.kibo.dal.intf.IFormularioCortaDAO;
import bo.com.kibo.entidades.DetalleCorta;
import bo.com.kibo.entidades.FormularioCorta;
import bo.com.kibo.entidades.Troza;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Olvinho
 */
public class FormularioCortaBO
        extends ObjetoNegocioGenerico<FormularioCorta, Integer, IFormularioCortaDAO>
        implements IFormularioCortaBO {

    @Override
    IFormularioCortaDAO getObjetoDAO() {
        return getDaoManager().getFormularioCortaDAO();
    }

    @Override
    protected int IdPermisoInsertar() {
        return 10501;
    }

    @Override
    protected void despuesDeRecuperar(FormularioCorta entidad) {
        entidad.getDetalle().size();
    }

    @Override
    protected void validar(FormularioCorta entity) {
        if (entity.getId() != null) {
            appendException(new BusinessExceptionMessage("La actualización no esta permitida en este formulario"));
            return;
        }

        if (entity.getFecha() == null) {
            appendException(new BusinessExceptionMessage("El campo fecha es requerido", "fecha"));
        }

        if (entity.getArea() == null) {

        } else {
            if (entity.getArea().getId() != null) {
                if (!(getDaoManager().getAreaDAO().checkId(entity.getArea().getId()))) {
                    if (entity.getArea().getId() == 0) {
                        appendException(new BusinessExceptionMessage("El campo área es requerido", "area"));
                    } else {
                        appendException(new BusinessExceptionMessage("El área '" + entity.getArea().getId() + "' no existe", "area"));
                    }
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

        if (entity.getDetalle().isEmpty()) {
            appendException(new BusinessExceptionMessage("Debe agregar ágregar árboles al detalle", "detalle"));
        }

        Map<String, Integer> codigos = new HashMap<>();
        Map<String, Integer> trozasSinCarga = new HashMap<>();
        Map<String, Integer> trozasConCarga = new HashMap<>();
        //Validamos el detalle buscando duplicados
        for (int i = 0; i < entity.getDetalle().size(); i++) {
            DetalleCorta detalle = entity.getDetalle().get(i);
            validarLineaDetalle(detalle, i + 1, entity);
            if ((detalle.getTroza() != null) && !(isNullOrEmpty(detalle.getTroza().getCodigo()))) {
                Integer filaDuplicada = codigos.get(getCodigo(detalle));
                if (filaDuplicada != null) {
                    appendException(new BusinessExceptionMessage("Registro duplicado con fila " + filaDuplicada, "detalle", i + 1));
                }
                codigos.put(getCodigo(detalle), i + 1);

                if (detalle.getCarga() != null) {
                    //Sin carga
                    Integer aux = trozasConCarga.get(detalle.getTroza().getCodigo());
                    if (aux != null) {
                        appendException(new BusinessExceptionMessage("La troza fue definida con carga anteriormente en la fila "
                                + aux + " elimine la carga, o agregue carga a todos los registros de esta troza",
                                "detalle", i + 1));
                    }
                    trozasSinCarga.put(detalle.getTroza().getCodigo(), i + 1);
                } else {
                    //Con carga
                    Integer aux = trozasSinCarga.get(detalle.getTroza().getCodigo());
                    if (aux != null) {
                        appendException(new BusinessExceptionMessage("La troza fue definida sin carga anteriormente en la fila "
                                + aux + " elimine la carga, o agregue carga a todos los registros de esta troza",
                                "detalle", i + 1));
                    }
                    trozasConCarga.put(detalle.getTroza().getCodigo(), i + 1);
                }
            }
        }
    }

    private String getCodigo(DetalleCorta linea) {
        String codigo;
        codigo = linea.getTroza().getCodigo();
        if (linea.getCarga() != null) {
            codigo += "." + linea.getCarga().getCodigo();
        }
        return codigo;
    }

    private void validarLineaDetalle(DetalleCorta linea, int index, FormularioCorta cabecera) {
        //Cargar Troza y Carga

        boolean trozaValida = true;
        //Troza
        if (linea.getTroza() == null) {
            appendException(new BusinessExceptionMessage("Debe especificar el árbol", "arbol", index));
            trozaValida = false;
        } else {
            if (linea.getTroza().getNumero() != null) {
                if (!getDaoManager().getTrozaDAO().checkNumero(linea.getTroza().getNumero())) {
                    if (linea.getTroza().getNumero() == 0) {
                        appendException(new BusinessExceptionMessage("Debe especificar el árbol", "arbol", index));
                    } else {
                        appendException(new BusinessExceptionMessage("El árbol '" + linea.getTroza().getNumero() + "' no existe", "arbol", index));
                    }
                    trozaValida = false;
                } else {
                    linea.setTroza(getDaoManager().getTrozaDAO().obtenerPorId(linea.getTroza().getNumero()));
                }
            } else {
                if (isNullOrEmpty(linea.getTroza().getCodigo())) {
                    appendException(new BusinessExceptionMessage("Debe especificar el árbol", "troza", index));
                    trozaValida = false;
                } else if ((cabecera.getArea() != null) && (cabecera.getArea().getId() != null)) {
                    Integer numero = getDaoManager().getTrozaDAO().getIdPorCodigoArea(linea.getTroza().getCodigo(), cabecera.getArea().getId());
                    if (numero == null) {
                        appendException(new BusinessExceptionMessage("El árbol '" + linea.getTroza().getCodigo() + "' no existe", "arbol", index));
                        trozaValida = false;
                    } else {
                        linea.setTroza(getDaoManager().getTrozaDAO().obtenerPorId(numero));
                    }
                } else {
                    appendException(new BusinessExceptionMessage("No se puede encontrar el árbol, debe definir área y el código o número del árbol", "arbol", index));
                    trozaValida = false;
                }
            }
        }

        boolean cargaValida = true;
        //Carga
        if (linea.getCarga() != null) {
            if (linea.getCarga().getId() != null) {
                if (linea.getCarga().getId() == 0) {
                    linea.setCarga(null);
                } else {
                    if (!getDaoManager().getCargaDAO().checkId(linea.getCarga().getId())) {
                        appendException(new BusinessExceptionMessage("La carga '" + linea.getCarga().getId() + "' no existe", "carga", index));
                        cargaValida = false;
                    } else {
                        linea.setCarga(getDaoManager().getCargaDAO().obtenerPorId(linea.getCarga().getId()));
                    }
                }

            } else {
                if (isNullOrEmpty(linea.getCarga().getCodigo())) {
                    linea.setCarga(null);
                } else {
                    Integer idCarga = getDaoManager().getCargaDAO().getIdPorCodigo(linea.getCarga().getCodigo());
                    if (idCarga == null) {
                        appendException(new BusinessExceptionMessage("La carga '" + linea.getCarga().getCodigo() + "' no existe", "carga", index));
                        cargaValida = false;
                    } else {
                        linea.setCarga(getDaoManager().getCargaDAO().recuperarPorId(idCarga));
                    }
                }
            }
        }

        //Validcaion de negorio
        if (trozaValida) {
            if (linea.getTroza().getExiste() != Troza.EXISTE_EXISTE) {
                appendException(new BusinessExceptionMessage("El árbol '" + linea.getTroza().getCodigo() + "' se encuentra de baja", "troza", index));
            } else {
                if (linea.getTroza().getEstado() != Troza.ESTADO_CENSADA) {
                    appendException(new BusinessExceptionMessage("El árbol '" + linea.getTroza().getCodigo() + "' se encuentra en un estado no válido", "troza", index));
                }
            }
            if ((cargaValida && linea.getCarga() != null) && (cabecera.getArea() != null) && (cabecera.getArea().getId() != null)) {
                if (linea.getTroza().getExiste() == Troza.EXISTE_EXISTE) {
                    String codigoSeccion = linea.getTroza().getCodigo() + Troza.SEPARADOR_CODIGO + linea.getCarga().getCodigo();
                    if (getDaoManager().getTrozaDAO().getIdPorCodigoArea(codigoSeccion, cabecera.getArea().getId()) != null) {
                        appendException(new BusinessExceptionMessage("El árbol '" + codigoSeccion + "' ya existe", "troza", index));
                    }
                }
            }
        }

        //Especie
        if (linea.getEspecie() != null) {
            if (linea.getEspecie().getId() != null) {
                if (linea.getEspecie().getId() == 0) {
                    linea.setEspecie(null);
                } else {
                    if (!getDaoManager().getEspecieDAO().checkId(linea.getEspecie().getId())) {
                        appendException(new BusinessExceptionMessage("La especie '" + linea.getEspecie().getId() + "' no existe", "especie", index));
                    }
                }

            } else {
                if (isNullOrEmpty(linea.getEspecie().getNombre())) {
                    linea.setEspecie(null);
                } else {
                    linea.getEspecie().setId(getDaoManager().getEspecieDAO().getIdPorNombre(linea.getEspecie().getNombre()));
                    if (linea.getEspecie().getId() == null) {
                        appendException(new BusinessExceptionMessage("La especie '" + linea.getEspecie().getNombre() + "' no existe", "especie", index));
                    }
                }
            }
        }

        //Dmayor
        if (linea.getDmayor() == null) {
            appendException(new BusinessExceptionMessage("El campo DMayor es requerido", "dMayor", index));
        } else if (linea.getDmayor() <= 0) {
            appendException(new BusinessExceptionMessage("El campo DMayor debe ser mayor que cero", "dMayor", index));
        }

        //DMenor
        if (linea.getDmenor() == null) {
            appendException(new BusinessExceptionMessage("El campo DMenor es requerido", "dMenor", index));
        } else if (linea.getDmenor() <= 0) {
            appendException(new BusinessExceptionMessage("El campo DMenor debe ser mayor que cero", "dMenor", index));
        }

        //Largo
        if (linea.getLargo() == null) {
            appendException(new BusinessExceptionMessage("El campo Largo es requerido", "largo", index));
        } else if (linea.getLargo() <= 0) {
            appendException(new BusinessExceptionMessage("El campo Largo debe ser mayor que cero", "largo", index));
        }

        //Calidad
        if (linea.getCalidad() != null) {
            if (linea.getCalidad().getId() != null) {
                if (linea.getCalidad().getId() == 0) {
                    linea.setCalidad(null);
                } else {
                    if (!getDaoManager().getCalidadDAO().checkId(linea.getCalidad().getId())) {
                        appendException(new BusinessExceptionMessage("La calidad '" + linea.getEspecie().getId() + "' no existe", "calidad", index));
                    }
                }

            } else {
                if (isNullOrEmpty(linea.getCalidad().getCodigo())) {
                    linea.setCalidad(null);
                } else {
                    linea.getCalidad().setId(getDaoManager().getCalidadDAO().getIdPorCodigo(linea.getCalidad().getCodigo()));
                    if (linea.getCalidad().getId() == null) {
                        appendException(new BusinessExceptionMessage("La calidad '" + linea.getCalidad().getCodigo() + "' no existe", "calidad", index));
                    }
                }
            }
        }
    }

    @Override
    protected void postInsertar(FormularioCorta entidad) {
        for (DetalleCorta detalle : entidad.getDetalle()) {
            TrozaBO trozaBO = new TrozaBO();
            if (detalle.getCarga() == null) {
                detalle.getTroza().setEstado(Troza.ESTADO_TALADA);
                detalle.getTroza().setFormularioCorta(entidad);
                trozaBO.corregirMedidas(detalle);
            } else {
                //Seccionamos
                trozaBO.crearSeccion(detalle, entidad);
            }
        }
    }
}
