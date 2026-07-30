import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;


public class VentanaPrincipal extends JFrame {

    private static final Color COLOR_A = new Color(0, 102, 204);   
    private static final Color COLOR_B = new Color(204, 51, 0);    
    private static final Color COLOR_R = new Color(0, 153, 76);    

    // ---- Componentes de la pestaña 2D ----
    private JTextField ax2, ay2, bx2, by2, escalar2;
    private JTextArea resultado2;
    private GraficoPanel2D grafico2D;

    //Componentes de la pestaña 3D 
    private JTextField ax3, ay3, az3, bx3, by3, bz3, escalar3;
    private JTextArea resultado3;
    private GraficoPanel3D grafico3D;

    public VentanaPrincipal() {
        super("Calculadora de Vectores");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.addTab("Vectores 2D", construirPestaña2D());
        pestañas.addTab("Vectores 3D", construirPestaña3D());

        add(pestañas);
    }

    //  Pestaña 2d
    private JPanel construirPestaña2D() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel controles = new JPanel();
        controles.setLayout(new BoxLayout(controles, BoxLayout.Y_AXIS));
        controles.setPreferredSize(new Dimension(320, 0));

        JPanel entradaA = new JPanel(new GridLayout(2, 2, 5, 5));
        entradaA.setBorder(tituloBorde("Vector A", COLOR_A));
        ax2 = new JTextField("3");
        ay2 = new JTextField("2");
        entradaA.add(new JLabel("x:"));
        entradaA.add(ax2);
        entradaA.add(new JLabel("y:"));
        entradaA.add(ay2);

        JPanel entradaB = new JPanel(new GridLayout(2, 2, 5, 5));
        entradaB.setBorder(tituloBorde("Vector B", COLOR_B));
        bx2 = new JTextField("-2");
        by2 = new JTextField("4");
        entradaB.add(new JLabel("x:"));
        entradaB.add(bx2);
        entradaB.add(new JLabel("y:"));
        entradaB.add(by2);

        JPanel entradaEscalar = new JPanel(new GridLayout(1, 2, 5, 5));
        entradaEscalar.setBorder(BorderFactory.createTitledBorder("Escalar (k) para A"));
        escalar2 = new JTextField("2");
        entradaEscalar.add(new JLabel("k:"));
        entradaEscalar.add(escalar2);

        JPanel botones = new JPanel(new GridLayout(0, 1, 5, 5));
        botones.setBorder(BorderFactory.createTitledBorder("Operaciones"));
        agregarBoton(botones, "Graficar A y B", e -> graficar2D());
        agregarBoton(botones, "A + B (suma)", e -> operar2D("suma"));
        agregarBoton(botones, "A - B (resta)", e -> operar2D("resta"));
        agregarBoton(botones, "A · B (producto punto)", e -> operar2D("punto"));
        agregarBoton(botones, "|A| y |B| (magnitud)", e -> operar2D("magnitud"));
        agregarBoton(botones, "Limpiar gráfico", e -> { grafico2D.limpiar(); resultado2.setText(""); });

        controles.add(entradaA);
        controles.add(Box.createVerticalStrut(8));
        controles.add(entradaB);
        controles.add(Box.createVerticalStrut(8));
        controles.add(entradaEscalar);
        controles.add(Box.createVerticalStrut(8));
        controles.add(botones);

        resultado2 = new JTextArea(6, 20);
        resultado2.setEditable(false);
        resultado2.setLineWrap(true);
        resultado2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scrollResultado = new JScrollPane(resultado2);
        scrollResultado.setBorder(BorderFactory.createTitledBorder("Resultado"));

        JPanel izquierda = new JPanel(new BorderLayout(5, 5));
        izquierda.add(new JScrollPane(controles), BorderLayout.CENTER);
        izquierda.add(scrollResultado, BorderLayout.SOUTH);

        grafico2D = new GraficoPanel2D();
        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setBorder(BorderFactory.createTitledBorder("Representación gráfica (plano XY)"));
        derecha.add(grafico2D, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, izquierda, derecha);
        split.setDividerLocation(340);
        panel.add(split, BorderLayout.CENTER);

        return panel;
    }

    private Vector2D leerVectorA2D() {
        return new Vector2D(leerDouble(ax2), leerDouble(ay2));
    }

    private Vector2D leerVectorB2D() {
        return new Vector2D(leerDouble(bx2), leerDouble(by2));
    }

    private void graficar2D() {
        try {
            Vector2D a = leerVectorA2D();
            Vector2D b = leerVectorB2D();
            grafico2D.limpiar();
            grafico2D.agregarVector(a, COLOR_A, "A");
            grafico2D.agregarVector(b, COLOR_B, "B");
            resultado2.setText("A = " + a + "\nB = " + b);
        } catch (NumberFormatException ex) {
            mostrarError();
        }
    }

    private void operar2D(String operacion) {
        try {
            Vector2D a = leerVectorA2D();
            Vector2D b = leerVectorB2D();
            StringBuilder sb = new StringBuilder();
            Vector2D resultado = null;

            switch (operacion) {
                case "suma":
                    resultado = a.suma(b);
                    sb.append("A + B = ").append(resultado);
                    break;
                case "resta":
                    resultado = a.resta(b);
                    sb.append("A - B = ").append(resultado);
                    break;
                case "punto":
                    sb.append("A · B = ").append(String.format("%.3f", a.productoPunto(b)));
                    break;
                case "cruz":
                    sb.append("A x B (escalar 2D) = ").append(String.format("%.3f", a.productoCruz(b)));
                    break;
                case "magnitud":
                    sb.append("|A| = ").append(String.format("%.3f", a.magnitud()))
                      .append("\n|B| = ").append(String.format("%.3f", b.magnitud()));
                    break;
                case "escalar":
                    double k = leerDouble(escalar2);
                    resultado = a.escalar(k);
                    sb.append(k).append(" · A = ").append(resultado);
                    break;
                case "normalizar":
                    resultado = a.normalizar();
                    sb.append("Vector unitario de A = ").append(resultado);
                    break;
                case "angulo":
                    sb.append("Ángulo entre A y B = ")
                      .append(String.format("%.2f°", a.anguloEntre(b)));
                    break;
            }

            resultado2.setText(sb.toString());

            grafico2D.limpiar();
            grafico2D.agregarVector(a, COLOR_A, "A");
            grafico2D.agregarVector(b, COLOR_B, "B");
            if (resultado != null) {
                grafico2D.agregarVector(resultado, COLOR_R, "R");
            }
        } catch (NumberFormatException ex) {
            mostrarError();
        }
    }

    // Pestaña 3d
        private JPanel construirPestaña3D() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel controles = new JPanel();
        controles.setLayout(new BoxLayout(controles, BoxLayout.Y_AXIS));
        controles.setPreferredSize(new Dimension(320, 0));

        JPanel entradaA = new JPanel(new GridLayout(3, 2, 5, 5));
        entradaA.setBorder(tituloBorde("Vector A", COLOR_A));
        ax3 = new JTextField("3");
        ay3 = new JTextField("2");
        az3 = new JTextField("4");
        entradaA.add(new JLabel("x:"));
        entradaA.add(ax3);
        entradaA.add(new JLabel("y:"));
        entradaA.add(ay3);
        entradaA.add(new JLabel("z:"));
        entradaA.add(az3);

        JPanel entradaB = new JPanel(new GridLayout(3, 2, 5, 5));
        entradaB.setBorder(tituloBorde("Vector B", COLOR_B));
        bx3 = new JTextField("-2");
        by3 = new JTextField("-1");
        bz3 = new JTextField("4");
        entradaB.add(new JLabel("x:"));
        entradaB.add(bx3);
        entradaB.add(new JLabel("y:"));
        entradaB.add(by3);
        entradaB.add(new JLabel("z:"));
        entradaB.add(bz3);

        JPanel entradaEscalar = new JPanel(new GridLayout(1, 2, 5, 5));
        entradaEscalar.setBorder(BorderFactory.createTitledBorder("Escalar (k) para A"));
        escalar3 = new JTextField("2");
        entradaEscalar.add(new JLabel("k:"));
        entradaEscalar.add(escalar3);

        JPanel botones = new JPanel(new GridLayout(0, 1, 5, 5));
        botones.setBorder(BorderFactory.createTitledBorder("Operaciones"));
        //agregarBoton(botones, "Graficar A y B", e -> graficar3D());
        agregarBoton(botones, "A + B (suma)", e -> operar3D("suma"));
        agregarBoton(botones, "A - B (resta)", e -> operar3D("resta"));
        agregarBoton(botones, "A · B (producto punto)", e -> operar3D("punto"));
        agregarBoton(botones, "A x B (producto cruz / normal)", e -> operar3D("cruz"));
        agregarBoton(botones, "|A| y |B| (magnitud)", e -> operar3D("magnitud"));
        agregarBoton(botones, "k · A (escalar)", e -> operar3D("escalar"));
        agregarBoton(botones, "Normalizar A", e -> operar3D("normalizar"));
        agregarBoton(botones, "Ángulo entre A y B", e -> operar3D("angulo"));
        agregarBoton(botones, "Limpiar gráfico", e -> { grafico3D.limpiar(); resultado3.setText(""); });

        controles.add(entradaA);
        controles.add(Box.createVerticalStrut(8));
        controles.add(entradaB);
        controles.add(Box.createVerticalStrut(8));
        controles.add(entradaEscalar);
        controles.add(Box.createVerticalStrut(8));
        controles.add(botones);

        resultado3 = new JTextArea(6, 20);
        resultado3.setEditable(false);
        resultado3.setLineWrap(true);
        resultado3.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scrollResultado = new JScrollPane(resultado3);
        scrollResultado.setBorder(BorderFactory.createTitledBorder("Resultado"));

        JPanel izquierda = new JPanel(new BorderLayout(5, 5));
        izquierda.add(new JScrollPane(controles), BorderLayout.CENTER);
        izquierda.add(scrollResultado, BorderLayout.SOUTH);

        grafico3D = new GraficoPanel3D();
        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setBorder(BorderFactory.createTitledBorder("Representación gráfica (proyección isométrica XYZ)"));
        derecha.add(grafico3D, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, izquierda, derecha);
        split.setDividerLocation(340);
        panel.add(split, BorderLayout.CENTER);

        return panel;
    }

    private Vector3D leerVectorA3D() {
        return new Vector3D(leerDouble(ax3), leerDouble(ay3), leerDouble(az3));
    }

    private Vector3D leerVectorB3D() {
        return new Vector3D(leerDouble(bx3), leerDouble(by3), leerDouble(bz3));
    }

    private void graficar3D() {
        try {
            Vector3D a = leerVectorA3D();
            Vector3D b = leerVectorB3D();
            grafico3D.limpiar();
            grafico3D.agregarVector(a, COLOR_A, "A");
            grafico3D.agregarVector(b, COLOR_B, "B");
            resultado3.setText("A = " + a + "\nB = " + b);
        } catch (NumberFormatException ex) {
            mostrarError();
        }
    }

    private void operar3D(String operacion) {
        try {
            Vector3D a = leerVectorA3D();
            Vector3D b = leerVectorB3D();
            StringBuilder sb = new StringBuilder();
            Vector3D resultado = null;

            switch (operacion) {
                case "suma":
                    resultado = a.suma(b);
                    sb.append("A + B = ").append(resultado);
                    break;
                case "resta":
                    resultado = a.resta(b);
                    sb.append("A - B = ").append(resultado);
                    break;
                case "punto":
                    sb.append("A · B = ").append(String.format("%.3f", a.productoPunto(b)));
                    break;
                case "cruz":
                    resultado = a.productoCruz(b);
                    sb.append("A x B (vector normal) = ").append(resultado);
                    break;
                case "magnitud":
                    sb.append("|A| = ").append(String.format("%.3f", a.magnitud()))
                      .append("\n|B| = ").append(String.format("%.3f", b.magnitud()));
                    break;
                case "escalar":
                    double k = leerDouble(escalar3);
                    resultado = a.escalar(k);
                    sb.append(k).append(" · A = ").append(resultado);
                    break;
                case "normalizar":
                    resultado = a.normalizar();
                    sb.append("Vector unitario de A = ").append(resultado);
                    break;
                case "angulo":
                    sb.append("Ángulo entre A y B = ")
                      .append(String.format("%.2f°", a.anguloEntre(b)));
                    break;
            }

            resultado3.setText(sb.toString());

            grafico3D.limpiar();
            grafico3D.agregarVector(a, COLOR_A, "A");
            grafico3D.agregarVector(b, COLOR_B, "B");
            if (resultado != null) {
                grafico3D.agregarVector(resultado, COLOR_R, "R");
            }
        } catch (NumberFormatException ex) {
            mostrarError();
        }
    }

    // ======================================================
    //                   UTILIDADES DE UI
    // ======================================================
    private TitledBorder tituloBorde(String texto, Color color) {
        TitledBorder borde = BorderFactory.createTitledBorder(texto);
        borde.setTitleColor(color);
        return borde;
    }

    private void agregarBoton(JPanel contenedor, String texto, java.util.function.Consumer<ActionEvent> accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(accion::accept);
        contenedor.add(boton);
    }

    private double leerDouble(JTextField campo) {
        return Double.parseDouble(campo.getText().trim().replace(",", "."));
    }

    private void mostrarError() {
        JOptionPane.showMessageDialog(this,
            "Por favor ingresa solo números válidos en los campos de los vectores.",
            "Entrada inválida", JOptionPane.ERROR_MESSAGE);
    }
}

