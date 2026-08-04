import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GraficoPanel3D extends JPanel {

    private static class VectorDibujable {
        Vector3D vector;
        Color color;
        String etiqueta;

        VectorDibujable(Vector3D vector, Color color, String etiqueta) {
            this.vector = vector;
            this.color = color;
            this.etiqueta = etiqueta;
        }
    }

    private final List<VectorDibujable> vectores = new ArrayList<>();
    
    // Variables para escala y zoom
    private double escalaBase = 30;
    private double factorZoom = 1.0;

    // Ángulos de rotación 3D (en radianes)
    private double anguloX = Math.toRadians(20); // Rotación vertical (Pitch)
    private double anguloY = Math.toRadians(-45); // Rotación horizontal (Yaw)

    // Variables para el arrastre (paneo) y ratón
    private Point ultimoPuntoRaton;
    private int offsetX = 0;
    private int offsetY = 0;

    public GraficoPanel3D() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(500, 450));
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Configuración de controles del ratón (Rotación, Paneo y Zoom)
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ultimoPuntoRaton = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (ultimoPuntoRaton != null) {
                    int dx = e.getX() - ultimoPuntoRaton.x;
                    int dy = e.getY() - ultimoPuntoRaton.y;

                    // Click derecho o rueda central para mover el plano (paneo)
                    if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                        offsetX += dx;
                        offsetY += dy;
                    } 
                    // Click izquierdo para rotar en 3D
                    else if (SwingUtilities.isLeftMouseButton(e)) {
                        anguloY += dx * 0.01;
                        anguloX += dy * 0.01;

                        // Limitar la rotación vertical
                        double limite = Math.toRadians(89);
                        anguloX = Math.max(-limite, Math.min(limite, anguloX));
                    }

                    ultimoPuntoRaton = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() < 0) {
                    factorZoom *= 1.1; // Zoom in
                } else {
                    factorZoom /= 1.1; // Zoom out
                }

                if (factorZoom < 1.0) {
                    factorZoom = 1.0;
                    offsetX = 0;
                    offsetY = 0;
                }
                
                repaint();
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);
    }

    public void limpiar() {
        vectores.clear();
        offsetX = 0;
        offsetY = 0;
        factorZoom = 1.0;
        repaint();
    }

    public void agregarVector(Vector3D v, Color color, String etiqueta) {
        vectores.add(new VectorDibujable(v, color, etiqueta));
        recalcularEscala();
        repaint();
    }

    private void recalcularEscala() {
        double maxComponente = 5;
        for (VectorDibujable vd : vectores) {
            maxComponente = Math.max(maxComponente, Math.abs(vd.vector.getX()));
            maxComponente = Math.max(maxComponente, Math.abs(vd.vector.getY()));
            maxComponente = Math.max(maxComponente, Math.abs(vd.vector.getZ()));
        }
        int radioPixeles = Math.min(getWidth(), getHeight() > 0 ? getHeight() : 450) / 2 - 40;
        if (radioPixeles < 20) radioPixeles = 160;
        
        escalaBase = radioPixeles / (maxComponente * 1.6);
        factorZoom = 1.0;
        offsetX = 0;
        offsetY = 0;
    }

    private double getEscalaActual() {
        return escalaBase * factorZoom;
    }

    /**
     * Aplica la matriz de rotación 3D e invierte el eje Y para la pantalla Swing.
     */
    private Point proyectar(double x, double y, double z, int cx, int cy) {
        // 1. Rotación alrededor del eje Y (Yaw)
        double x1 = x * Math.cos(anguloY) + z * Math.sin(anguloY);
        double y1 = y;
        double z1 = -x * Math.sin(anguloY) + z * Math.cos(anguloY);

        // 2. Rotación alrededor del eje X (Pitch)
        double x2 = x1;
        double y2 = y1 * Math.cos(anguloX) - z1 * Math.sin(anguloX);

        // 3. Mapear a coordenadas 2D de pantalla aplicando el offset del paneo
        double escalaActual = getEscalaActual();
        int px = cx + (int) Math.round(x2 * escalaActual) + offsetX;
        int py = cy - (int) Math.round(y2 * escalaActual) + offsetY;

        return new Point(px, py);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        
        double escalaActual = getEscalaActual();

        // Calcular el paso dinámico para los números
        int pasoUnidades = 1;
        if (escalaActual < 30) {
            int[] pasos = {2, 5, 10, 20, 50, 100, 200, 500};
            for (int p : pasos) {
                if (p * escalaActual >= 30) {
                    pasoUnidades = p;
                    break;
                }
            }
        }

        dibujarCuadricula3D(g2, cx, cy, pasoUnidades);
        dibujarEjesYNumeros(g2, cx, cy, pasoUnidades);

        for (VectorDibujable vd : vectores) {
            dibujarFlecha(g2, cx, cy, vd.vector, vd.color, vd.etiqueta);
        }
    }

    /**
     * Dibuja cuadrículas expansivas en los planos principales
     */
    private void dibujarCuadricula3D(Graphics2D g2, int cx, int cy, int pasoUnidades) {
        int limite = 30 * pasoUnidades; 
        
        // --- Plano XY (Pared) --- Z = 0
        g2.setColor(new Color(240, 240, 240)); 
        g2.setStroke(new BasicStroke(1.0f));
        // Líneas paralelas al eje Y
        for (int x = -limite; x <= limite; x += pasoUnidades) {
            Point p1 = proyectar(x, -limite, 0, cx, cy);
            Point p2 = proyectar(x, limite, 0, cx, cy);
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        // Líneas paralelas al eje X
        for (int y = -limite; y <= limite; y += pasoUnidades) {
            Point p1 = proyectar(-limite, y, 0, cx, cy);
            Point p2 = proyectar(limite, y, 0, cx, cy);
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        // --- Plano XZ (Piso) --- Y = 0
        g2.setColor(new Color(225, 225, 225)); // Gris un poco más oscuro para diferenciar el piso
        // Líneas paralelas al eje Z
        for (int x = -limite; x <= limite; x += pasoUnidades) {
            Point p1 = proyectar(x, 0, -limite, cx, cy);
            Point p2 = proyectar(x, 0, limite, cx, cy);
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        // Líneas paralelas al eje X
        for (int z = -limite; z <= limite; z += pasoUnidades) {
            Point p1 = proyectar(-limite, 0, z, cx, cy);
            Point p2 = proyectar(limite, 0, z, cx, cy);
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    private void dibujarEjesYNumeros(Graphics2D g2, int cx, int cy, int pasoUnidades) {
        int limiteEjes = 20 * pasoUnidades; 

        Point origen = proyectar(0, 0, 0, cx, cy);
        
        // Coordenadas extremas (positivas y negativas) para cruzar todo el plano
        Point ejeXPos = proyectar(limiteEjes, 0, 0, cx, cy);
        Point ejeXNeg = proyectar(-limiteEjes, 0, 0, cx, cy);
        
        Point ejeYPos = proyectar(0, limiteEjes, 0, cx, cy);
        Point ejeYNeg = proyectar(0, -limiteEjes, 0, cx, cy);
        
        Point ejeZPos = proyectar(0, 0, limiteEjes, cx, cy);
        Point ejeZNeg = proyectar(0, 0, -limiteEjes, cx, cy);

        // Eje X (Rojo)
        g2.setColor(new Color(220, 50, 50));
        g2.setStroke(new BasicStroke(1.8f));
        g2.drawLine(ejeXNeg.x, ejeXNeg.y, ejeXPos.x, ejeXPos.y);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString("x", ejeXPos.x + 8, ejeXPos.y);

        // Eje Y (Verde)
        g2.setColor(new Color(50, 180, 50));
        g2.drawLine(ejeYNeg.x, ejeYNeg.y, ejeYPos.x, ejeYPos.y);
        g2.drawString("y", ejeYPos.x + 8, ejeYPos.y);

        // Eje Z (Azul)
        g2.setColor(new Color(50, 50, 220));
        g2.drawLine(ejeZNeg.x, ejeZNeg.y, ejeZPos.x, ejeZPos.y);
        g2.drawString("z", ejeZPos.x + 8, ejeZPos.y);

        // ================= Números Dinámicos =================
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.setColor(new Color(120, 120, 120));

        // Números para X
        for (int i = -limiteEjes; i <= limiteEjes; i += pasoUnidades) {
            if (i == 0) continue;
            Point p = proyectar(i, 0, 0, cx, cy);
            g2.drawString(String.valueOf(i), p.x + 2, p.y + 12);
        }

        // Números para Y
        for (int i = -limiteEjes; i <= limiteEjes; i += pasoUnidades) {
            if (i == 0) continue;
            Point p = proyectar(0, i, 0, cx, cy);
            g2.drawString(String.valueOf(i), p.x + 6, p.y + 4);
        }

        // Números para Z
        for (int i = -limiteEjes; i <= limiteEjes; i += pasoUnidades) {
            if (i == 0) continue;
            Point p = proyectar(0, 0, i, cx, cy);
            g2.drawString(String.valueOf(i), p.x - 15, p.y + 4);
        }
        
        // Cero en el origen
        g2.drawString("0", origen.x - 10, origen.y + 10);
    }

    private void dibujarFlecha(Graphics2D g2, int cx, int cy, Vector3D v, Color color, String etiqueta) {
        Point origen = proyectar(0, 0, 0, cx, cy);
        Point punta = proyectar(v.getX(), v.getY(), v.getZ(), cx, cy);

        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawLine(origen.x, origen.y, punta.x, punta.y);

        // Dirección 2D en pantalla para construir el triángulo de la punta
        double angulo = Math.atan2(punta.y - origen.y, punta.x - origen.x);
        int largoPunta = 10;

        int[] px = new int[3];
        int[] py = new int[3];
        px[0] = punta.x;
        py[0] = punta.y;
        px[1] = punta.x - (int) (largoPunta * Math.cos(angulo - Math.PI / 6));
        py[1] = punta.y - (int) (largoPunta * Math.sin(angulo - Math.PI / 6));
        px[2] = punta.x - (int) (largoPunta * Math.cos(angulo + Math.PI / 6));
        py[2] = punta.y - (int) (largoPunta * Math.sin(angulo + Math.PI / 6));

        g2.fillPolygon(px, py, 3);
        
        // Etiqueta del vector
        g2.drawString(etiqueta + " " + v.toString(), punta.x + 8, punta.y - 8);
    }
}