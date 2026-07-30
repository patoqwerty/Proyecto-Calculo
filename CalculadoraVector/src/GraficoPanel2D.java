import javax.swing.*;
import java.awt.*;
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
    private double escala = 40; 

    public GraficoPanel2D() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(500, 450));
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
    }

    public void limpiar() {
        vectores.clear();
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
        escala = radioPixeles / (maxComponente * 1.2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        dibujarCuadricula(g2, cx, cy);
        dibujarEjes(g2, cx, cy);

        for (VectorDibujable vd : vectores) {
            dibujarFlecha(g2, cx, cy, vd.vector.getX(), vd.vector.getY(), vd.color, vd.etiqueta);
        }
    }

    private void dibujarCuadricula(Graphics2D g2, int cx, int cy) {
        g2.setColor(new Color(235, 235, 235));
        int paso = (int) escala;
        if (paso < 5) paso = 5;
        for (int x = cx % paso; x < getWidth(); x += paso) {
            g2.drawLine(x, 0, x, getHeight());
        }
        for (int y = cy % paso; y < getHeight(); y += paso) {
            g2.drawLine(0, y, getWidth(), y);
        }
    }

    private void dibujarEjes(Graphics2D g2, int cx, int cy) {
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(0, cy, getWidth(), cy); // eje X
        g2.drawLine(cx, 0, cx, getHeight()); // eje Y
        g2.drawString("x", getWidth() - 15, cy - 8);
        g2.drawString("y", cx + 8, 12);
        g2.drawString("0", cx + 4, cy + 14);
    }

    private void dibujarFlecha(Graphics2D g2, int cx, int cy, double x, double y, Color color, String etiqueta) {
        int x1 = cx;
        int y1 = cy;
        int x2 = cx + (int) Math.round(x * escala);
        int y2 = cy - (int) Math.round(y * escala); // se invierte y

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
