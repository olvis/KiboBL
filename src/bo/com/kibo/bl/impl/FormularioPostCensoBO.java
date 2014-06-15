/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.dal.intf.IDAOGenerico;
import bo.com.kibo.entidades.Troza;
import bo.com.kibo.entidades.intf.IDetallePostCenso;
import bo.com.kibo.entidades.intf.IFormularioPostCenso;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Olvinho
 * @param <T>
 * @param <U>
 */
public abstract class FormularioPostCensoBO<T extends IFormularioPostCenso, U extends IDAOGenerico<T, Integer>>
        extends ObjetoNegocioGenerico<T, Integer, U> {

    @Override
    protected final void validar(T entity) {
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

        if (entity.getDetalle().isEmpty()) {
            appendException(new BusinessExceptionMessage("Debe agregar agregar árboles al detalle", "detalle"));
        }

        validarEncabezado(entity);
        //Validamos detalle
        Map<String, Integer> codigos = new HashMap<>();
        Map<String, Integer> trozasSinCarga = new HashMap<>();
        Map<String, Integer> trozasConCarga = new HashMap<>();
        //Validamos el detalle buscando duplicados
        for (int i = 0; i < entity.getDetalle().size(); i++) {
            IDetallePostCenso detalle = (IDetallePostCenso) entity.getDetalle().get(i);
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

    private String getCodigo(IDetallePostCenso linea) {
        String codigo;
        codigo = linea.getTroza().getCodigo();
        if (linea.getCarga() != null) {
            codigo = linea.getCodigoCarga();
        }
        return codigo;
    }

    private void validarLineaDetalle(IDetallePostCenso linea, int index, T cabecera) {
        //Cargar Troza y Carga
        boolean trozaValida = true;
        //Troza
        if (linea.getTroza() == null) {
            appendException(new BusinessExceptionMessage("Debe especificar una troza", "troza", index));
            trozaValida = false;
        } else {
            if (linea.getTroza().getNumero() != null) {
                if (!getDaoManager().getTrozaDAO().checkNumero(linea.getTroza().getNumero())) {
                    appendException(new BusinessExceptionMessage("La troza '" + linea.getTroza().getNumero() + "' no existe", "troza", index));
                    trozaValida = false;
                } else {
                    linea.setTroza(getDaoManager().getTrozaDAO().obtenerPorId(linea.getTroza().getNumero()));
                }
            } else {
                if (isNullOrEmpty(linea.getTroza().getCodigo())) {
                    appendException(new BusinessExceptionMessage("Debe especificar una troza", "troza", index));
                    trozaValida = false;
                } else if ((cabecera.getArea() != null) && (cabecera.getArea().getId() != null)) {
                    Integer numero = getDaoManager().getTrozaDAO().getIdPorCodigoArea(linea.getTroza().getCodigo(), cabecera.getArea().getId());
                    if (numero == null) {
                        appendException(new BusinessExceptionMessage("La troza '" + linea.getTroza().getCodigo() + "' no existe", "troza", index));
                        trozaValida = false;
                    } else {
                        linea.setTroza(getDaoManager().getTrozaDAO().obtenerPorId(numero));
                    }
                } else {
                    appendException(new BusinessExceptionMessage("No se puede encontrar la troza, debe definir área y el código o número de la troza", "troza", index));
                    trozaValida = false;
                }
            }
        }

        boolean cargaValida = true;
        //Carga
        if (linea.getCarga() != null) {
            if (linea.getCarga().getId() != null) {
                if (!getDaoManager().getCargaDAO().checkId(linea.getCarga().getId())) {
                    appendException(new BusinessExceptionMessage("La carga '" + linea.getCarga().getId() + "' no existe", "carga", index));
                    cargaValida = false;
                } else {
                    linea.setCarga(getDaoManager().getCargaDAO().obtenerPorId(linea.getCarga().getId()));
                    if (isNullOrEmpty(linea.getCodigoCarga()) && trozaValida) {
                        //Defininimos el codigo
                        linea.setCodigoCarga(linea.getTroza().getCodigo() + Troza.SEPARADOR_CODIGO + linea.getCarga().getCodigo());
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
                        if (isNullOrEmpty(linea.getCodigoCarga()) && trozaValida) {
                            //Defininimos el codigo
                            linea.setCodigoCarga(linea.getTroza().getCodigo() + Troza.SEPARADOR_CODIGO + linea.getCarga().getCodigo());
                        }
                    }
                }
            }
        }

        if (trozaValida) {
            if (linea.getTroza().getExiste() != Troza.EXISTE_EXISTE) {
                appendException(new BusinessExceptionMessage("La troza '" + linea.getTroza().getCodigo() + "' se encuentra de baja", "troza", index));
            } else if (linea.getTroza().getEstado() != estadoRequerido()) {
                appendException(new BusinessExceptionMessage("La troza '" + linea.getTroza().getCodigo() + "' se encuentra en un estado inválido", "troza", index));
            }
            if ((cargaValida && linea.getCarga() != null) && (cabecera.getArea() != null) && (cabecera.getArea().getId() != null)) {
                if (linea.getTroza().getExiste() == Troza.EXISTE_EXISTE) {
                    String codigoSeccion = linea.getCodigoCarga();
                    if (getDaoManager().getTrozaDAO().getIdPorCodigoArea(codigoSeccion, cabecera.getArea().getId()) != null) {
                        appendException(new BusinessExceptionMessage("La troza '" + codigoSeccion + "' ya existe", "troza", index));
                    }
                }
            }
        }

        //Especie
        if (linea.getEspecie() != null) {
            if (linea.getEspecie().getId() != null) {
                if (!getDaoManager().getEspecieDAO().checkId(linea.getEspecie().getId())) {
                    appendException(new BusinessExceptionMessage("La especie '" + linea.getEspecie().getId() + "' no existe", "especie", index));
                }
            } else {
                if (isNullOrEmpty(linea.getEspecie().getNombre()) && trozaValida) {
                    linea.setEspecie(linea.getTroza().getEspecie());
                } else {
                    linea.getEspecie().setId(getDaoManager().getEspecieDAO().getIdPorNombre(linea.getEspecie().getNombre()));
                    if (linea.getEspecie().getId() == null) {
                        appendException(new BusinessExceptionMessage("La especie '" + linea.getEspecie().getNombre() + "' no existe", "especie", index));
                    }
                }
            }
        } else if (trozaValida) {
            linea.setEspecie(linea.getTroza().getEspecie());
        }

        //Calidad
        if (linea.getCalidad() != null) {
            if (linea.getCalidad().getId() != null) {
                if (!getDaoManager().getCalidadDAO().checkId(linea.getCalidad().getId())) {
                    appendException(new BusinessExceptionMessage("La calidad '" + linea.getEspecie().getId() + "' no existe", "calidad", index));
                }
            } else {
                if (isNullOrEmpty(linea.getCalidad().getCodigo()) && trozaValida) {
                    linea.setCalidad(linea.getTroza().getCalidad());
                } else {
                    linea.getCalidad().setId(getDaoManager().getCalidadDAO().getIdPorCodigo(linea.getCalidad().getCodigo()));
                    if (linea.getCalidad().getId() == null) {
                        appendException(new BusinessExceptionMessage("La calidad '" + linea.getCalidad().getCodigo() + "' no existe", "calidad", index));
                    }
                }
            }
        } else if (trozaValida) {
            linea.setCalidad(linea.getTroza().getCalidad());
        }

        if (linea.getCarga() != null) {
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
        } else {
            //Sin carga
            //Dmayor
            if (linea.getDmayor() == null) {
                if (trozaValida) {
                    linea.setDmayor(linea.getTroza().getdMayor());
                }
            } else if (linea.getDmayor() <= 0) {
                appendException(new BusinessExceptionMessage("El campo DMayor debe ser mayor que cero", "dMayor", index));
            }
            //DMenor
            if (linea.getDmenor() == null) {
                if (trozaValida) {
                    linea.setDmenor(linea.getTroza().getdMenor());
                }
            } else if (linea.getDmenor() <= 0) {
                appendException(new BusinessExceptionMessage("El campo DMenor debe ser mayor que cero", "dMenor", index));
            }

            //Largo
            if (linea.getLargo() == null) {
                if (trozaValida) {
                    linea.setLargo(linea.getTroza().getLargo());
                }
            } else if (linea.getLargo() <= 0) {
                appendException(new BusinessExceptionMessage("El campo Largo debe ser mayor que cero", "largo", index));
            }
        }
        validarDetalle(linea, index, cabecera);
    }

    @Override
    protected void despuesDeRecuperar(T entidad) {
        entidad.getDetalle().size();
    }

    @Override
    protected final void postInsertar(T entidad) {
        for (int i = 0; i < entidad.getDetalle().size(); i++) {
            IDetallePostCenso linea = (IDetallePostCenso) entidad.getDetalle().get(i);
            TrozaBO trozaBO = new TrozaBO();
            if (linea.getCarga() == null) {
                procesarLineaDetalle(linea, i, entidad);
                trozaBO.corregirMedidas(linea, entidad);
            } else {
                trozaBO.crearSeccion(linea, entidad);
            }
        }
    }

    protected abstract void validarEncabezado(T entity);

    protected abstract byte estadoRequerido();

    protected abstract void validarDetalle(IDetallePostCenso linea, int index, T cabecera);

    protected abstract void procesarLineaDetalle(IDetallePostCenso linea, int index, T cabecera);

}
