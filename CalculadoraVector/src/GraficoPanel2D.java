import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class GraficoPanel2D extends JPanel {

    private static class VectorDibujable {
        Vector2D vector;
        Color color;
        String etiqueta;

        VectorDibujable(Vector2D vector, Color color, String etiqueta) {
            this.vector = vector;
            this.color = color;
            this.etiqueta = etiqueta;
        }
    }

    private final List<VectorDibujable> vectores = new ArrayList<>();
    
    // Variables para controlar la escala y el zoom
    private double escalaBase = 40; 
    private double factorZoom = 1.0;

    // Variables para el desplazamiento (arrastrar el plano)
    private int offsetX = 0;
    private int offsetY = 0;
    private Point ultimoPuntoRaton;

    public GraficoPanel2D() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(500, 450));
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Adaptador para el ratón (Zoom y Arrastre)
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
                    offsetX += dx;
                    offsetY += dy;
                    ultimoPuntoRaton = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() < 0) {
                    // Girar hacia arriba: Zoom In
                    factorZoom *= 1.1;
                } else {
                    // Girar hacia abajo: Zoom Out
                    factorZoom /= 1.1;
                }

                // RESTRICCIÓN: No permitir alejar más allá de la vista calculada original
                if (factorZoom < 1.0) {
                    factorZoom = 1.0;
                    // Centrar la vista automáticamente si regresamos al límite
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

    public void agregarVector(Vector2D v, Color color, String etiqueta) {
        vectores.add(new VectorDibujable(v, color, etiqueta));
        recalcularEscala();
        repaint();
    }

    private void recalcularEscala() {
        double maxComponente = 5;
        for (VectorDibujable vd : vectores) {
            maxComponente = Math.max(maxComponente, Math.abs(vd.vector.getX()));
            maxComponente = Math.max(maxComponente, Math.abs(vd.vector.getY()));
        }
        int radioPixeles = Math.min(getWidth(), getHeight() > 0 ? getHeight() : 450) / 2 - 40;
        if (radioPixeles < 20) radioPixeles = 180;
        
        // Guardar la escala óptima inicial
        escalaBase = radioPixeles / (maxComponente * 1.2);
        
        // Resetear la vista al agregar nuevos datos
        factorZoom = 1.0; 
        offsetX = 0;
        offsetY = 0;
    }

    private double getEscalaActual() {
        return escalaBase * factorZoom;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = (getWidth() / 2) + offsetX;
        int cy = (getHeight() / 2) + offsetY;
        
        double escalaActual = getEscalaActual();

        // Calcular de cuánto en cuánto van los números (paso dinámico) para no amontonar texto
        int pasoUnidades = 1;
        if (escalaActual < 40) {
            int[] pasos = {2, 5, 10, 20, 50, 100, 200, 500, 1000};
            for (int p : pasos) {
                if (p * escalaActual >= 40) {
                    pasoUnidades = p;
                    break;
                }
            }
        }

        dibujarCuadricula(g2, cx, cy, escalaActual, pasoUnidades);
        dibujarEjesYNumeros(g2, cx, cy, escalaActual, pasoUnidades);

        for (VectorDibujable vd : vectores) {
            dibujarFlecha(g2, cx, cy, vd.vector.getX(), vd.vector.getY(), vd.color, vd.etiqueta);
        }
    }

    private void dibujarCuadricula(Graphics2D g2, int cx, int cy, double escalaActual, int pasoUnidades) {
        g2.setColor(new Color(235, 235, 235));
        
        // Evitar bucles infinitos por seguridad
        if (pasoUnidades * escalaActual < 5) return; 

        // Líneas verticales (ancladas al origen matemático)
        int minX = (int) Math.floor(-cx / escalaActual);
        int maxX = (int) Math.ceil((getWidth() - cx) / escalaActual);
        for (int i = minX; i <= maxX; i++) {
            if (i % pasoUnidades == 0) {
                int px = cx + (int)(i * escalaActual);
                g2.drawLine(px, 0, px, getHeight());
            }
        }

        // Líneas horizontales
        int minY = (int) Math.floor((cy - getHeight()) / escalaActual);
        int maxY = (int) Math.ceil(cy / escalaActual);
        for (int i = minY; i <= maxY; i++) {
            if (i % pasoUnidades == 0) {
                int py = cy - (int)(i * escalaActual);
                g2.drawLine(0, py, getWidth(), py);
            }
        }
    }

    private void dibujarEjesYNumeros(Graphics2D g2, int cx, int cy, double escalaActual, int pasoUnidades) {
        // Ejes X y Y
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(0, cy, getWidth(), cy); 
        g2.drawLine(cx, 0, cx, getHeight()); 
        
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString("x", getWidth() - 15, cy - 12);
        g2.drawString("y", cx + 12, 15);
        
        // Estilo de los números
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.setColor(new Color(100, 100, 100)); 

        // Eje X (Números)
        int minX = (int) Math.floor(-cx / escalaActual);
        int maxX = (int) Math.ceil((getWidth() - cx) / escalaActual);
        for (int i = minX; i <= maxX; i++) {
            if (i == 0) continue; 
            if (i % pasoUnidades == 0) {
                int px = cx + (int)(i * escalaActual);
                g2.drawLine(px, cy - 3, px, cy + 3); // Pequeña marca (tick)
                String texto = String.valueOf(i);
                int anchoTexto = g2.getFontMetrics().stringWidth(texto);
                g2.drawString(texto, px - anchoTexto / 2, cy + 16);
            }
        }

        // Eje Y (Números)
        int minY = (int) Math.floor((cy - getHeight()) / escalaActual);
        int maxY = (int) Math.ceil(cy / escalaActual);
        for (int i = minY; i <= maxY; i++) {
            if (i == 0) continue;
            if (i % pasoUnidades == 0) {
                int py = cy - (int)(i * escalaActual);
                g2.drawLine(cx - 3, py, cx + 3, py); // Pequeña marca (tick)
                String texto = String.valueOf(i);
                int anchoTexto = g2.getFontMetrics().stringWidth(texto);
                g2.drawString(texto, cx - anchoTexto - 6, py + 4);
            }
        }
        
        // Cero
        g2.drawString("0", cx + 5, cy + 14);
    }

    private void dibujarFlecha(Graphics2D g2, int cx, int cy, double x, double y, Color color, String etiqueta) {
        double escalaActual = getEscalaActual();
        int x1 = cx;
        int y1 = cy;
        int x2 = cx + (int) Math.round(x * escalaActual);
        int y2 = cy - (int) Math.round(y * escalaActual); 

        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawLine(x1, y1, x2, y2);

        // Punta de flecha
        double angulo = Math.atan2(y1 - y2, x2 - x1);
        int largoPunta = 10;
        AffineTransform t = new AffineTransform();
        t.setToIdentity();
        int[] px = new int[3];
        int[] py = new int[3];
        px[0] = x2;
        py[0] = y2;
        px[1] = x2 - (int) (largoPunta * Math.cos(angulo - Math.PI / 6));
        py[1] = y2 + (int) (largoPunta * Math.sin(angulo - Math.PI / 6));
        px[2] = x2 - (int) (largoPunta * Math.cos(angulo + Math.PI / 6));
        py[2] = y2 + (int) (largoPunta * Math.sin(angulo + Math.PI / 6));
        g2.fillPolygon(px, py, 3);

        // Etiqueta
        g2.drawString(etiqueta + " " + String.format("(%.1f, %.1f)", x, y), x2 + 6, y2 - 6);
    }
}