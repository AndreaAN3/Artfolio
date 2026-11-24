
package com.artfolio.servlet;

import com.artfolio.Obra;
import com.artfolio.dao.ObraDAO;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
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
import java.util.Set;

@WebServlet("/api/privado/obras")
public class ObrasPrivadasServlet extends HttpServlet {

    private final ObraDAO obraDAO = new ObraDAO();

    private static final Set<String> CATEGORIAS =
            Set.of(
                "Ilustraciones",
                "Fotografía",
                "Diseño digital",
                "Animación",
                "Diseño gráfico"
            );

    // ==========================================
    // CONSULTAR OBRAS
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

            String id = request.getParameter("id");

            if (id != null) {

                Obra obra = obraDAO.buscarPorIdYArtista(
                        validarId(id),
                        artistaId
                );

                if (obra == null) {
                    enviarError(response, 404,
                            "Obra no encontrada.");
                    return;
                }

                response.getWriter().write(
                        convertirJson(obra).toString()
                );

                return;
            }

            JsonArrayBuilder resultado =
                    Json.createArrayBuilder();

            for (Obra obra :
                    obraDAO.listarPorArtista(artistaId)) {

                resultado.add(convertirJson(obra));
            }

            response.getWriter().write(
                    resultado.build().toString()
            );

        } catch (IllegalArgumentException ex) {

            enviarError(response, 400, ex.getMessage());

        } catch (SQLException ex) {

            errorBaseDatos(response, ex);
        }
    }

    // ==========================================
    // CREAR OBRA
    // ==========================================

    @Override
    protected void doPost(
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

            Obra obra = new Obra();

            obra.setArtistaId(artistaId);

            asignarDatos(obra, datos);

            int id = obraDAO.guardar(obra);

            response.setStatus(
                    HttpServletResponse.SC_CREATED
            );

            response.getWriter().write(
                    Json.createObjectBuilder()
                        .add("success", true)
                        .add("id", id)
                        .add("mensaje",
                            "Obra guardada correctamente. ♡")
                        .build()
                        .toString()
            );

        } catch (JsonException | ClassCastException ex) {

            enviarError(response, 400,
                    "Los datos enviados no son válidos.");

        } catch (IllegalArgumentException ex) {

            enviarError(response, 400, ex.getMessage());

        } catch (SQLException ex) {

            errorBaseDatos(response, ex);
        }
    }

    // ==========================================
    // ACTUALIZAR OBRA
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

            int obraId = validarId(
                    request.getParameter("id")
            );

            Obra obra = obraDAO.buscarPorIdYArtista(
                    obraId,
                    artistaId
            );

            if (obra == null) {
                enviarError(response, 404,
                        "Obra no encontrada.");
                return;
            }

            JsonObject datos = reader.readObject();

            asignarDatos(obra, datos);

            if (!obraDAO.actualizar(obra)) {

                enviarError(response, 409,
                        "No se pudo actualizar la obra.");

                return;
            }

            enviarExito(response,
                    "Obra actualizada correctamente. ♡");

        } catch (JsonException | ClassCastException ex) {

            enviarError(response, 400,
                    "Los datos enviados no son válidos.");

        } catch (IllegalArgumentException ex) {

            enviarError(response, 400, ex.getMessage());

        } catch (SQLException ex) {

            errorBaseDatos(response, ex);
        }
    }

    // ==========================================
    // ELIMINAR OBRA
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

            int obraId = validarId(
                    request.getParameter("id")
            );

            boolean eliminada = obraDAO.eliminar(
                    obraId,
                    artistaId
            );

            if (!eliminada) {

                enviarError(response, 404,
                        "Obra no encontrada.");

                return;
            }

            enviarExito(response,
                    "Obra eliminada correctamente.");

        } catch (IllegalArgumentException ex) {

            enviarError(response, 400, ex.getMessage());

        } catch (SQLException ex) {

            errorBaseDatos(response, ex);
        }
    }

    // ==========================================
    // ASIGNAR Y VALIDAR DATOS
    // ==========================================

    private void asignarDatos(
            Obra obra,
            JsonObject datos) {

        String titulo = campo(
                datos, "titulo", obra.getTitulo()
        );

        String descripcion = campo(
                datos, "descripcion",
                obra.getDescripcion()
        );

        String categoria = campo(
                datos, "categoria",
                obra.getCategoria()
        );

        String archivoUrl = campo(
                datos, "archivoUrl",
                obra.getArchivoUrl()
        );

        String imagenPortada = campo(
                datos, "imagenPortada",
                obra.getImagenPortada()
        );

        boolean publicada =
                datos.getBoolean(
                    "publicada",
                    obra.isPublicada()
                );

        if (titulo.isBlank()
                || titulo.length() > 200) {

            throw new IllegalArgumentException(
                    "El título de la obra no es válido."
            );
        }

        if (descripcion.length() > 10000) {

            throw new IllegalArgumentException(
                    "La descripción es demasiado larga."
            );
        }

        if (!CATEGORIAS.contains(categoria)) {

            throw new IllegalArgumentException(
                    "Selecciona una categoría válida."
            );
        }

        validarUrl(archivoUrl);
        validarUrl(imagenPortada);

        if (publicada && archivoUrl.isBlank()) {

            throw new IllegalArgumentException(
                    "Agrega la URL de la obra antes de publicarla."
            );
        }

        obra.setTitulo(titulo);
        obra.setDescripcion(descripcion);
        obra.setCategoria(categoria);
        obra.setArchivoUrl(archivoUrl);
        obra.setImagenPortada(imagenPortada);
        obra.setPublicada(publicada);
    }

    // ==========================================
    // CONVERTIR OBRA A JSON
    // ==========================================

    private JsonObject convertirJson(Obra obra) {

        return Json.createObjectBuilder()

                .add("id", obra.getId())

                .add("artistaId", obra.getArtistaId())

                .add("titulo",
                        texto(obra.getTitulo()))

                .add("descripcion",
                        texto(obra.getDescripcion()))

                .add("categoria",
                        texto(obra.getCategoria()))

                .add("archivoUrl",
                        texto(obra.getArchivoUrl()))

                .add("imagenPortada",
                        texto(obra.getImagenPortada()))

                .add("fechaPublicacion",
                        obra.getFechaPublicacion() == null
                            ? ""
                            : obra.getFechaPublicacion()
                                  .toString())

                .add("publicada", obra.isPublicada())

                .build();
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
    // VALIDAR ID
    // ==========================================

    private int validarId(String valor) {

        try {

            int id = Integer.parseInt(valor);

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return id;

        } catch (NumberFormatException ex) {

            throw new IllegalArgumentException(
                    "El ID de la obra no es válido."
            );
        }
    }

    // ==========================================
    // VALIDAR URL
    // ==========================================

    private void validarUrl(String valor) {

        if (valor == null || valor.isBlank()) {
            return;
        }

        if (valor.length() > 500) {

            throw new IllegalArgumentException(
                    "La URL es demasiado larga."
            );
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
    // METODOS AUXILIARES
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

    private void enviarExito(
            HttpServletResponse response,
            String mensaje)
            throws IOException {

        response.getWriter().write(
                Json.createObjectBuilder()
                    .add("success", true)
                    .add("mensaje", mensaje)
                    .build()
                    .toString()
        );
    }

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
                "Error en la gestión de obras", ex
        );

        enviarError(response, 500,
                "No se pudo completar la operación.");
    }
}
