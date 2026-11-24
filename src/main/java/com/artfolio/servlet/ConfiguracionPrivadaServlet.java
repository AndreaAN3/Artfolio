
package com.artfolio.servlet;

import com.artfolio.ConfiguracionPerfil;
import com.artfolio.dao.ConfiguracionPerfilDAO;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/api/privado/configuracion")
public class ConfiguracionPrivadaServlet extends HttpServlet {

    private final ConfiguracionPerfilDAO configuracionDAO =
            new ConfiguracionPerfilDAO();

    // ==========================================
    // CONSULTAR PERSONALIZACION
    // ==========================================

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        prepararRespuesta(response);

        Integer artistaId = obtenerArtistaId(request);

        if (artistaId == null) {

            enviarError(response, 401,
                    "Debes iniciar sesión.");

            return;
        }

        try {

            ConfiguracionPerfil configuracion =
                    configuracionDAO.obtenerPorArtista(
                            artistaId
                    );

            response.getWriter().write(
                    convertirJson(configuracion).toString()
            );

        } catch (SQLException ex) {

            errorBaseDatos(response, ex);
        }
    }

    // ==========================================
    // GUARDAR PERSONALIZACION
    // ==========================================

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        prepararRespuesta(response);

        request.setCharacterEncoding("UTF-8");

        Integer artistaId = obtenerArtistaId(request);

        if (artistaId == null) {

            enviarError(response, 401,
                    "Debes iniciar sesión.");

            return;
        }

        try (JsonReader reader =
                Json.createReader(request.getReader())) {

            JsonObject datos = reader.readObject();

            ConfiguracionPerfil configuracion =
                    configuracionDAO.obtenerPorArtista(
                            artistaId
                    );

            // La identidad siempre proviene de la sesión.

            configuracion.setArtistaId(artistaId);

            // Avatar del artista.

            configuracion.setAvatar(
                    campo(
                        datos,
                        "avatar",
                        configuracion.getAvatar()
                    )
            );

            // Color principal del perfil.

            configuracion.setColorPrincipal(
                    campo(
                        datos,
                        "colorPrincipal",
                        configuracion.getColorPrincipal()
                    )
            );

            // Color de la página de enlaces.

            configuracion.setColorEnlaces(
                    campo(
                        datos,
                        "colorEnlaces",
                        configuracion.getColorEnlaces()
                    )
            );

            // Mensaje personalizado de bienvenida.

            configuracion.setMensajeBienvenida(
                    campo(
                        datos,
                        "mensajeBienvenida",
                        configuracion.getMensajeBienvenida()
                    )
            );

            // Validar colores.

            validarColor(
                    configuracion.getColorPrincipal()
            );

            validarColor(
                    configuracion.getColorEnlaces()
            );

            // Guardar en MySQL.

            configuracionDAO.guardar(configuracion);

            ConfiguracionPerfil guardada =
                    configuracionDAO.obtenerPorArtista(
                            artistaId
                    );

            JsonObject resultado =
                    Json.createObjectBuilder()

                    .add("success", true)

                    .add("mensaje",
                        "Personalización guardada. ♡")

                    .add("configuracion",
                        convertirJson(guardada))

                    .build();

            response.getWriter().write(
                    resultado.toString()
            );

        } catch (JsonException | ClassCastException ex) {

            enviarError(response, 400,
                    "Los datos enviados no son válidos.");

        } catch (IllegalArgumentException ex) {

            enviarError(response, 400,
                    ex.getMessage());

        } catch (SQLException ex) {

            errorBaseDatos(response, ex);
        }
    }

    // ==========================================
    // RESTABLECER PERSONALIZACION
    // ==========================================

    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        prepararRespuesta(response);

        Integer artistaId = obtenerArtistaId(request);

        if (artistaId == null) {

            enviarError(response, 401,
                    "Debes iniciar sesión.");

            return;
        }

        try {

            configuracionDAO.restablecer(artistaId);

            ConfiguracionPerfil configuracion =
                    configuracionDAO.obtenerPorArtista(
                            artistaId
                    );

            JsonObject resultado =
                    Json.createObjectBuilder()

                    .add("success", true)

                    .add("mensaje",
                        "Diseño original restaurado. ♡")

                    .add("configuracion",
                        convertirJson(configuracion))

                    .build();

            response.getWriter().write(
                    resultado.toString()
            );

        } catch (SQLException ex) {

            errorBaseDatos(response, ex);
        }
    }

    // ==========================================
    // CONVERTIR CONFIGURACION A JSON
    // ==========================================

    private JsonObject convertirJson(
            ConfiguracionPerfil configuracion) {

        return Json.createObjectBuilder()

                .add("artistaId",
                    configuracion.getArtistaId())

                .add("avatar",
                    texto(configuracion.getAvatar()))

                .add("colorPrincipal",
                    texto(configuracion.getColorPrincipal()))

                .add("colorEnlaces",
                    texto(configuracion.getColorEnlaces()))

                .add("mensajeBienvenida",
                    texto(configuracion.getMensajeBienvenida()))

                .add("fechaActualizacion",
                    configuracion.getFechaActualizacion() == null
                        ? ""
                        : configuracion.getFechaActualizacion()
                                .toString())

                .build();
    }

    // ==========================================
    // VALIDAR COLORES HEXADECIMALES
    // ==========================================

    private void validarColor(String color) {

        if (color == null
                || !color.matches("^#[0-9a-fA-F]{6}$")) {

            throw new IllegalArgumentException(
                    "Selecciona un color hexadecimal válido."
            );
        }
    }

    // ==========================================
    // IDENTIDAD DEL ARTISTA
    // ==========================================

    private Integer obtenerArtistaId(
            HttpServletRequest request) {

        HttpSession session =
                request.getSession(false);

        if (session == null) {

            return null;
        }

        Object valor =
                session.getAttribute("artistaId");

        if (valor instanceof Integer id && id > 0) {

            return id;
        }

        return null;
    }

    // ==========================================
    // RECUPERAR CAMPOS JSON
    // ==========================================

    private String campo(
            JsonObject datos,
            String clave,
            String anterior) {

        if (!datos.containsKey(clave)) {

            return texto(anterior);
        }

        if (datos.isNull(clave)) {

            return "";
        }

        return datos.getString(clave).trim();
    }

    private String texto(String valor) {

        return valor == null ? "" : valor;
    }

    // ==========================================
    // PREPARAR RESPUESTA
    // ==========================================

    private void prepararRespuesta(
            HttpServletResponse response) {

        response.setContentType(
                "application/json;charset=UTF-8"
        );

        response.setHeader(
                "Cache-Control", "no-store"
        );

        response.setHeader(
                "X-Content-Type-Options", "nosniff"
        );
    }

    // ==========================================
    // ENVIAR ERROR JSON
    // ==========================================

    private void enviarError(
            HttpServletResponse response,
            int estado,
            String mensaje)
            throws IOException {

        response.setStatus(estado);

        response.getWriter().write(
                Json.createObjectBuilder()

                    .add("success", false)

                    .add("error", texto(mensaje))

                    .build()

                    .toString()
        );
    }

    private void errorBaseDatos(
            HttpServletResponse response,
            SQLException ex)
            throws IOException {

        getServletContext().log(
                "Error en la personalización del perfil",
                ex
        );

        enviarError(response, 500,
                "No se pudo completar la personalización.");
    }
}
