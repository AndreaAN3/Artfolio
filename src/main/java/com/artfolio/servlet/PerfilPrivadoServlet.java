
package com.artfolio.servlet;

import com.artfolio.Artista;
import com.artfolio.dao.ArtistaDAO;

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
import java.net.URI;
import java.sql.SQLException;

@WebServlet("/api/privado/perfil")
public class PerfilPrivadoServlet extends HttpServlet {

    private final ArtistaDAO artistaDAO =
            new ArtistaDAO();

    // ==========================================
    // CONSULTAR PERFIL
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

            Artista artista =
                    artistaDAO.buscarPorId(artistaId);

            if (artista == null) {

                enviarError(response, 404,
                        "Perfil no encontrado.");

                return;
            }

            HttpSession session =
                    request.getSession(false);

            String csrfToken = (String)
                    session.getAttribute("csrfToken");

            JsonObject resultado =
                    Json.createObjectBuilder()

                    .add("id", artista.getId())

                    .add("nombre",
                            texto(artista.getNombre()))

                    .add("nombreArtistico",
                            texto(artista.getNombreArtistico()))

                    .add("biografia",
                            texto(artista.getBiografia()))

                    .add("especialidad",
                            texto(artista.getEspecialidad()))

                    .add("fotoPerfil",
                            texto(artista.getFotoPerfil()))

                    .add("instagram",
                            texto(artista.getInstagram()))

                    .add("sitioWeb",
                            texto(artista.getSitioWeb()))

                    .add("csrfToken",
                            texto(csrfToken))

                    .build();

            response.getWriter().write(
                    resultado.toString()
            );

        } catch (SQLException ex) {

            getServletContext().log(
                    "Error al consultar el perfil", ex
            );

            enviarError(response, 500,
                    "No se pudo consultar el perfil.");
        }
    }

    // ==========================================
    // ACTUALIZAR PERFIL
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

            Artista artista =
                    artistaDAO.buscarPorId(artistaId);

            if (artista == null) {

                enviarError(response, 404,
                        "Perfil no encontrado.");

                return;
            }

            // El ID siempre proviene de la sesión.

            artista.setId(artistaId);

            artista.setNombre(
                    campo(datos, "nombre",
                            artista.getNombre())
            );

            artista.setNombreArtistico(
                    campo(datos, "nombreArtistico",
                            artista.getNombreArtistico())
            );

            artista.setBiografia(
                    campo(datos, "biografia",
                            artista.getBiografia())
            );

            artista.setEspecialidad(
                    campo(datos, "especialidad",
                            artista.getEspecialidad())
            );

            artista.setFotoPerfil(
                    campo(datos, "fotoPerfil",
                            artista.getFotoPerfil())
            );

            artista.setInstagram(
                    campo(datos, "instagram",
                            artista.getInstagram())
            );

            artista.setSitioWeb(
                    campo(datos, "sitioWeb",
                            artista.getSitioWeb())
            );

            validarUrl(artista.getFotoPerfil());
            validarUrl(artista.getSitioWeb());

            if (!artistaDAO.actualizar(artista)) {

                enviarError(response, 409,
                        "No se pudo actualizar el perfil.");

                return;
            }

            JsonObject resultado =
                    Json.createObjectBuilder()

                    .add("success", true)

                    .add("mensaje",
                            "Perfil actualizado correctamente. ♡")

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

            getServletContext().log(
                    "Error al actualizar el perfil", ex
            );

            enviarError(response, 500,
                    "No se pudo actualizar el perfil.");
        }
    }

    // ==========================================
    // OBTENER IDENTIDAD DE LA SESION
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
            return anterior;
        }

        if (datos.isNull(clave)) {
            return "";
        }

        return datos.getString(clave).trim();
    }

    // ==========================================
    // VALIDAR URL
    // ==========================================

    private void validarUrl(String valor) {

        if (valor == null || valor.isBlank()) {
            return;
        }

        try {

            URI uri = URI.create(valor);

            String esquema = uri.getScheme();

            if (esquema == null
                    || (!esquema.equalsIgnoreCase("https")
                    && !esquema.equalsIgnoreCase("http"))
                    || uri.getHost() == null
                    || uri.getUserInfo() != null) {

                throw new IllegalArgumentException();
            }

        } catch (IllegalArgumentException ex) {

            throw new IllegalArgumentException(
                    "Introduce una URL HTTP o HTTPS válida."
            );
        }
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

    private String texto(String valor) {

        return valor == null ? "" : valor;
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

        JsonObject error =
                Json.createObjectBuilder()

                .add("success", false)

                .add("error", texto(mensaje))

                .build();

        response.getWriter().write(
                error.toString()
        );
    }
}
