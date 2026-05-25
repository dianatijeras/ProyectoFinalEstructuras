package techpark.gui;

import techpark.model.usuarios.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana de login para el sistema Tech-Park UQ.
 */
public class LoginFrame extends JFrame {
    private final AppContext contexto = new AppContext();
    private final JTextField txtDocumento = new JTextField("3001");
    private final JPasswordField txtPassword = new JPasswordField("123");

    /**
     * Constructor de la clase LoginFrame. Configura la ventana y sus componentes para el inicio de sesión.
     */
    public LoginFrame() {
        setTitle("Tech-Park UQ - Login");
        setSize(360, 210);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        crearContenido();
    }

    /**
     * Crea el contenido de la ventana de login, incluyendo los campos de texto para el documento y la contraseña, así como el botón de ingreso.
     */
    private void crearContenido() {
        JPanel form = new JPanel(new GridLayout(4, 1, 8, 8));
        form.add(new JLabel("Documento:"));
        form.add(txtDocumento);
        form.add(new JLabel("Contraseña:"));
        form.add(txtPassword);

        JButton btnEntrar = new JButton("Ingresar");
        btnEntrar.addActionListener(e -> iniciarSesion());

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(new JLabel("Usuarios de prueba: admin 3001, operador 2001, visitante 1001. Clave: 123"), BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        panel.add(btnEntrar, BorderLayout.SOUTH);
        setContentPane(panel);
    }

    /**
     * Método para iniciar sesión. Intenta autenticar al usuario utilizando el servicio de autenticación del contexto.
     */
    private void iniciarSesion() {
        try {
            Usuario usuario = contexto.getServicioAutenticacion().iniciarSesion(
                    txtDocumento.getText(), new String(txtPassword.getPassword()));
            new VentanaPrincipal(contexto, usuario).setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

