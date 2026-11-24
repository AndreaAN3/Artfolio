
package com.artfolio.servlet;

import com.artfolio.Enlace;
import com.artfolio.dao.EnlaceDAO;

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
import java.sql.SQLException;

@WebServlet("/api/privado/enlaces")
public class EnlacesPrivadosServlet extends HttpServlet {

    private final EnlaceDAO enlaceDAO =
            new EnlaceDAO();

    // ==========================================
    // CONSULTAR ENLACES
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

                Enlace enlace =
                        enlaceDAO.buscarPorIdYArtista(
                            validarId(id),
                            artistaId
                        );

                if (enlace == null) {

                    enviarError(response, 404,
                            "Enlace no encontrado.");

                    return;
                }

                response.getWriter().write(
                    convertirJson(enlace).toString()
                );

                return;
            }

            JsonArrayBuilder resultado =
                    Json.createArrayBuilder();

            for (Enlace enlace :
                    enlaceDAO.listarPorArtista(artistaId)) {

                resultado.add(
                    convertirJson(enlace)
                );
            }

            response.getWriter().write(
                resultado.build().toString()
            );

        } catch (IllegalArgumentException ex) {

            enviarError(response, 400,
                    ex.getMessage());

        } catch (SQLException ex) {

            errorBaseDatos(response, ex);
        }
    }

    // ==========================================
    // CREAR ENLACE
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

            Enlace enlace = new Enlace();

            enlace.setArtistaId(artistaId);
            enlace.setIcono("🌷");

            asignarDatos(enlace, datos);

            int id = enlaceDAO.guardar(enlace);

            response.setStatus(
                HttpServletResponse.SC_CREATED
            );

            response.getWriter().write(
                Json.createObjectBuilder()
                    .add("success", true)
                    .add("id", id)
                    .add("mensaje",
                        "Enlace guardado correctamente. ♡")
                    .build()
                    .toString()
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
    // ACTUALIZAR ENLACE
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

            int enlaceId = validarId(
                request.getParameter("id")
            );

            Enlace enlace =
                    enlaceDAO.buscarPorIdYArtista(
                        enlaceId,
                        artistaId
                    );

            if (enlace == null) {

                enviarError(response, 404,
                        "Enlace no encontrado.");

                return;
            }

            JsonObject datos = reader.readObject();

            asignarDatos(enlace, datos);

            if (!enlaceDAO.actualizar(enlace)) {

                enviarError(response, 409,
                        "No se pudo actualizar el enlace.");

                return;
            }

            enviarExito(response,
                    "Enlace actualizado correctamente. ♡");

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
    // ELIMINAR ENLACE
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

            int enlaceId = validarId(
                request.getParameter("id")
            );

            boolean eliminado =
                    enlaceDAO.eliminar(
                        enlaceId,
                        artistaId
                    );

            if (!eliminado) {

                enviarError(response, 404,
                        "Enlace no encontrado.");

                return;
            }

            enviarExito(response,
                    "Enlace eliminado correctamente.");

        } catch (IllegalArgumentException ex) {

            enviarError(response, 400,
                    ex.getMessage());

        } catch (SQLException ex) {

            errorBaseDatos(response, ex);
        }
    }

    // ==========================================
    // ASIGNAR DATOS
    // ==========================================

    private void asignarDatos(
            Enlace enlace,
            JsonObject datos) {

        enlace.setTitulo(
            campo(datos, "titulo",
                    enlace.getTitulo())
        );

        enlace.setUrl(
            campo(datos, "url",
                    enlace.getUrl())
        );

        enlace.setIcono(
            campo(datos, "icono",
                    enlace.getIcono())
        );

        if (datos.containsKey("orden")) {

            if (datos.isNull("orden")) {

                throw new IllegalArgumentException(
                    "El orden no es válido."
                );
            }

            try {

                enlace.setOrden(
                    datos.getJsonNumber("orden")
                         .intValueExact()
                );

            } catch (ArithmeticException ex) {

                throw new IllegalArgumentException(
                    "El orden debe ser un número entero."
                );
            }
        }
    }

    // ==========================================
    // CONVERTIR ENLACE A JSON
    // ==========================================

    private JsonObject convertirJson(Enlace enlace) {

        return Json.createObjectBuilder()

                .add("id",
                        enlace.getId())

                .add("artistaId",
                        enlace.getArtistaId())

                .add("titulo",
                        texto(enlace.getTitulo()))

                .add("url",
                        texto(enlace.getUrl()))

                .add("icono",
                        texto(enlace.getIcono()))

                .add("orden",
                        enlace.getOrden())

                .add("fechaCreacion",
                        enlace.getFechaCreacion() == null
                            ? ""
                            : enlace.getFechaCreacion()
                                    .toString())

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
                "El ID del enlace no es válido."
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
            "Error en la gestión de enlaces", ex
        );

        enviarError(response, 500,
            "No se pudo completar la operación.");
    }
}
