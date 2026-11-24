
package com.artfolio.servlet;

import com.artfolio.dao.AutenticacionDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Locale;

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {

    private final AutenticacionDAO autenticacionDAO =
            new AutenticacionDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        response.setContentType(
                "text/plain;charset=UTF-8"
        );

        response.setHeader(
                "Cache-Control",
                "no-store"
        );

        // 1. Obtener datos del formulario

        String nombre =
                request.getParameter("nombre");

        String nombreArtistico =
                request.getParameter("nombreArtistico");

        String correo =
                request.getParameter("correo");

        String password =
                request.getParameter("password");

        String confirmarPassword =
                request.getParameter("confirmarPassword");

        // 2. Verificar campos obligatorios

        if (nombre == null
                || nombre.isBlank()
                || nombreArtistico == null
                || nombreArtistico.isBlank()
                || correo == null
                || correo.isBlank()
                || password == null
                || password.isEmpty()
                || confirmarPassword == null
                || confirmarPassword.isEmpty()) {

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Todos los campos son obligatorios."
            );

            return;
        }

        // 3. Normalizar los datos

        nombre = nombre.trim();

        nombreArtistico =
                nombreArtistico.trim();

        correo =
                correo.trim()
                        .toLowerCase(Locale.ROOT);

        // 4. Validar nombres

        if (nombre.length() < 2
                || nombre.length() > 100) {

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El nombre debe tener entre "
                    + "2 y 100 caracteres."
            );

            return;
        }

        if (nombreArtistico.length() < 2
                || nombreArtistico.length() > 100) {

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El nombre artístico debe tener "
                    + "entre 2 y 100 caracteres."
            );

            return;
        }

        // 5. Validar correo electrónico

        if (correo.length() > 255
                || !correo.matches(
                        "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
                )) {

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Ingresa un correo electrónico válido."
            );

            return;
        }

        // 6. Validar longitud de contraseña

        if (password.length() < 12
                || password.length() > 128) {

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "La contraseña debe tener entre "
                    + "12 y 128 caracteres."
            );

            return;
        }

        // 7. Confirmar contraseña

        if (!password.equals(confirmarPassword)) {

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Las contraseñas no coinciden."
            );

            return;
        }

        // 8. Registrar artista

        try {

            if (autenticacionDAO.correoExiste(correo)) {

                enviarRespuesta(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        "Este correo ya está registrado."
                );

                return;
            }

            int artistaId =
                    autenticacionDAO.registrarArtista(
                            nombre,
                            nombreArtistico,
                            correo,
                            password
                    );

            if (artistaId <= 0) {

                getServletContext().log(
                        "El registro no devolvió "
                        + "un ID de artista válido."
                );

                enviarRespuesta(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo completar el registro. "
                        + "Intenta nuevamente."
                );

                return;
            }

            // 9. Registro exitoso

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_CREATED,
                    "¡Bienvenido a Artfolio! ♡ "
                    + "Tu cuenta fue creada correctamente. "
                    + "Ya puedes iniciar sesión."
            );

        } catch (SQLIntegrityConstraintViolationException ex) {

            getServletContext().log(
                    "Conflicto al registrar artista",
                    ex
            );

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_CONFLICT,
                    "No se pudo completar el registro. "
                    + "Verifica que el correo no esté registrado."
            );

        } catch (IllegalArgumentException ex) {

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Los datos del registro no son válidos."
            );

        } catch (SQLException ex) {

            getServletContext().log(
                    "Error SQL al registrar artista",
                    ex
            );

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo completar el registro. "
                    + "Intenta nuevamente."
            );
        }
    }

    // Respuestas HTTP para el frontend

    private void enviarRespuesta(
            HttpServletResponse response,
            int estado,
            String mensaje)
            throws IOException {

        response.setStatus(estado);

        response.getWriter().write(mensaje);
    }

    // Redirigir al formulario de registro

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath()
                + "/registro.html"
        );
    }
}
