/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl;

import bo.com.kibo.bl.exceptions.BusinessExceptionMessage;
import bo.com.kibo.bl.intf.IUsuarioBO;
import bo.com.kibo.dal.intf.IUsuarioDAO;
import bo.com.kibo.entidades.Usuario;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Olvinho
 */
public class UsuarioBO extends ObjetoNegocioGenerico<Usuario, Integer, IUsuarioDAO> implements IUsuarioBO {

    //algoritmos
    public static final String MD2 = "MD2";
    public static final String MD5 = "MD5";
    public static final String SHA1 = "SHA-1";
    public static final String SHA256 = "SHA-256";
    public static final String SHA384 = "SHA-384";
    public static final String SHA512 = "SHA-512";

    //Validar Email
    private final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    protected int IdPermisoInsertar() {
        return 20001;
    }

    @Override
    protected int IdPermisoActualizar() {
        return 20002;
    }

    @Override
    IUsuarioDAO getObjetoDAO() {
        return getDaoManager().getUsuarioDAO();
    }

    @Override
    public Integer getIdUsuarioPorEmail(String email) {
        final String x = email;
        return ejecutarEnTransaccion(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return getObjetoDAO().getIdUsuarioPorEmail(x);
            }
        });
    }

    @Override
    public Usuario loguear(final String usuario, final String pass) {
        return ejecutarEnTransaccion(new Callable<Usuario>() {
            @Override
            public Usuario call() throws Exception {
                Usuario user = getObjetoDAO().logear(usuario, pass);
                //if (user.getRol() != null){
                //    user.getRol().getDescripcion();
                //}
                return user;
            }
        });
    }

    @Override
    protected void validar(Usuario entity) {

        //Nombre
        boolean nombreValido = true;
        if (isNullOrEmpty(entity.getNombre())) {
            appendException(new BusinessExceptionMessage("El nombre es un campo requerido", "nombre"));
            nombreValido = false;
        } else if (entity.getNombre().length() > 30) {
            appendException(new BusinessExceptionMessage("El nombre no puede tener más de 30 carácteres", "nombre"));
            nombreValido = false;
        }

        if (nombreValido) {
            if (entity.getId() == null) {
                //Inserccion
                Integer id = getObjetoDAO().getIdPorNombre(entity.getNombre());
                if (id != null) {
                    appendException(new BusinessExceptionMessage("El nombre '" + entity.getNombre() + "' ya existe", "nombre"));
                }
            } else {
                //Actualizacion
                if (!getObjetoDAO().checkId(entity.getId())) {
                    appendException(new BusinessExceptionMessage("El usuario con Id  '" + entity.getId() + "' no existe", "id"));
                } else {
                    Usuario actual = getObjetoDAO().obtenerPorId(entity.getId());
                    if (!actual.getNombre().equals(entity.getNombre())) {
                        //El codigo cambio verificamos si existe
                        if (getObjetoDAO().getIdPorNombre(entity.getNombre()) != null) {
                            appendException(new BusinessExceptionMessage("El nombre '" + entity.getNombre() + "' ya existe", "nombre"));
                        }
                    }
                    entity.setContrasena(actual.getContrasena());
                }
            }
        }

        //Email
        boolean emailValido = true;
        if (isNullOrEmpty(entity.getEmail())) {
            appendException(new BusinessExceptionMessage("El email es un campo requerido", "email"));
            emailValido = false;
        } else if (entity.getEmail().length() > 50) {
            appendException(new BusinessExceptionMessage("El email no puede tener más de 50 carácteres", "email"));
            emailValido = false;
        } else if (!validarEmail(entity.getEmail())) {
            appendException(new BusinessExceptionMessage("El email no es válido", "email"));
            emailValido = false;
        }

        if (emailValido) {
            if (entity.getId() == null) {
                if ((getObjetoDAO().getIdUsuarioPorEmail(entity.getEmail()) != null)) {
                    appendException(new BusinessExceptionMessage("El email " + entity.getEmail() + " ya esta registrado", "email"));
                }
            } else if (getObjetoDAO().checkId(entity.getId())) {
                Usuario actual = getObjetoDAO().obtenerPorId(entity.getId());
                if (!actual.getEmail().equalsIgnoreCase(entity.getEmail())) {
                    if ((getObjetoDAO().getIdUsuarioPorEmail(entity.getEmail()) != null)) {
                        appendException(new BusinessExceptionMessage("El email " + entity.getEmail() + " ya esta registrado", "email"));
                    }
                }
            }
        }

        //Rol
        if (entity.getRol() == null) {
            appendException(new BusinessExceptionMessage("El rol es un campo requerido", "rol"));
        } else {
            if (entity.getRol().getId() != null) {
                if (!(getDaoManager().getRolDAO().checkId(entity.getRol().getId()))) {
                    appendException(new BusinessExceptionMessage("El rol '" + entity.getRol().getId() + "' no existe", "rol"));
                }
            } else {
                //Buscamos por Descripcion
                if (isNullOrEmpty(entity.getRol().getDescripcion())) {
                    appendException(new BusinessExceptionMessage("El rol es un campo requerido", "rol"));
                } else {
                    entity.getRol().setId(getDaoManager().getRolDAO().getIdPorDescripcion(entity.getRol().getDescripcion()));
                    if (entity.getRol().getId() == null) {
                        appendException(new BusinessExceptionMessage("El rol '" + entity.getRol().getDescripcion() + "' no existe", "rol"));
                    }
                }
            }
        }

    }

    private String contrasena;

    @Override
    protected void preInsertar(Usuario entidad) {
        //Generamos la conseña
        entidad.setContrasenaDesencriptada(cadenaAleatoria(15));
        entidad.setContrasena(encriptar(entidad.getContrasenaDesencriptada()));
        contrasena = entidad.getContrasenaDesencriptada();
    }

    @Override
    protected void postInsertar(Usuario entidad) {
        entidad.setContrasenaDesencriptada(contrasena);
    }

    public boolean validarEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private String cadenaAleatoria(int longitud) {
        String cadenaAleatoria = "";
        long milis = new java.util.GregorianCalendar().getTimeInMillis();
        Random r = new Random(milis);
        int i = 0;
        while (i < longitud) {
            char c = (char) r.nextInt(255);
            if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z')) {
                cadenaAleatoria += c;
                i++;
            }
        }
        return cadenaAleatoria;
    }

    @Override
    public String encriptar(String texto) {
        return getStringMessageDigest(texto, SHA256);
    }

    /**
     * *
     * Convierte un arreglo de bytes a String usando valores hexadecimales
     *
     * @param digest arreglo de bytes a convertir
     * @return String creado a partir de <code>digest</code>
     */
    private static String toHexadecimal(byte[] digest) {
        String hash = "";
        for (byte aux : digest) {
            int b = aux & 0xff;
            if (Integer.toHexString(b).length() == 1) {
                hash += "0";
            }
            hash += Integer.toHexString(b);
        }
        return hash;
    }

    /**
     * *
     * Encripta un mensaje de texto mediante algoritmo de resumen de mensaje.
     *
     * @param message texto a encriptar
     * @param algorithm algoritmo de encriptacion, puede ser: MD2, MD5, SHA-1,
     * SHA-256, SHA-384, SHA-512
     * @return mensaje encriptado
     */
    public static String getStringMessageDigest(String message, String algorithm) {
        byte[] digest = null;
        byte[] buffer = message.getBytes();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.reset();
            messageDigest.update(buffer);
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Error creando Digest");
        }
        return toHexadecimal(digest);
    }

}
