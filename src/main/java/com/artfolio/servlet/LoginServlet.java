
package com.artfolio.servlet;

import com.artfolio.dao.AutenticacionDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AutenticacionDAO autenticacionDAO =
            new AutenticacionDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Configurar respuesta

        request.setCharacterEncoding("UTF-8");

        response.setContentType(
                "text/plain;charset=UTF-8"
        );

        response.setHeader(
                "Cache-Control",
                "no-store"
        );

        // 2. Obtener credenciales

        String correo =
                request.getParameter("correo");

        String password =
                request.getParameter("password");

        // 3. Validar campos obligatorios

        if (correo == null
                || correo.isBlank()
                || password == null
                || password.isEmpty()) {

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Ingresa tu correo y contraseña."
            );

            return;
        }

        // 4. Normalizar correo

        correo = correo.trim()
                .toLowerCase(Locale.ROOT);

        // 5. Validar longitud de datos

        if (correo.length() > 255
                || password.length() > 128
                || password.length() < 12) {

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Los datos ingresados no son válidos."
            );

            return;
        }

        // 6. Validar formato de correo

        if (!correo.matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
        )) {

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Ingresa un correo electrónico válido."
            );

            return;
        }

        // 7. Autenticar usuario

        try {

            Integer artistaId =
                    autenticacionDAO.iniciarSesion(
                            correo,
                            password
                    );

            // Credenciales incorrectas

            if (artistaId == null
                    || artistaId <= 0) {

                enviarRespuesta(
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Correo o contraseña incorrectos."
                );

                return;
            }

            // 8. Renovar la sesión autenticada

            HttpSession anterior =
                    request.getSession(false);

            if (anterior != null) {
                anterior.invalidate();
            }

            HttpSession session =
                    request.getSession(true);

            // 9. Guardar identidad del artista

            session.setAttribute(
                    "artistaId",
                    artistaId
            );

            // 10. Expiración por inactividad

            session.setMaxInactiveInterval(
                    30 * 60
            );

            // 11. Respuesta exitosa

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_OK,
                    "¡Bienvenido a Artfolio! ♡ "
                    + "Inicio de sesión exitoso."
            );

        } catch (SQLException ex) {

            getServletContext().log(
                    "Error SQL al iniciar sesión",
                    ex
            );

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo iniciar sesión. "
                    + "Intenta nuevamente."
            );
        }
    }

    // Método auxiliar para respuestas HTTP

    private void enviarRespuesta(
            HttpServletResponse response,
            int estado,
            String mensaje)
            throws IOException {

        response.setStatus(estado);

        response.getWriter().write(mensaje);
    }

    // Redirigir al formulario de login

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath()
                + "/login.html"
        );
    }
}
