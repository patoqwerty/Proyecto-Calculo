import javax.swing.*;
import java.awt.*;
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
    private double escala = 30;

    // Ángulo de inclinación para el eje "profundidad" (z), en radianes.
    private static final double ANG_ISO = Math.toRadians(30);

    public GraficoPanel3D() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(500, 450));
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
    }

    public void limpiar() {
        vectores.clear();
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
        escala = radioPixeles / (maxComponente * 1.6);
    }

    private Point proyectar(double x, double y, double z, int cx, int cy) {
        double screenX = (x * Math.cos(ANG_ISO)) - (z * Math.cos(ANG_ISO));
        double screenY = (x * Math.sin(ANG_ISO)) + (z * Math.sin(ANG_ISO)) - y;
        int px = cx + (int) Math.round(screenX * escala);
        int py = cy + (int) Math.round(screenY * escala);
        return new Point(px, py);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        dibujarEjes(g2, cx, cy);

        for (VectorDibujable vd : vectores) {
            dibujarFlecha(g2, cx, cy, vd.vector, vd.color, vd.etiqueta);
        }
    }

    private void dibujarEjes(Graphics2D g2, int cx, int cy) {
        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(1.2f));

        double L = 6; // longitud de los ejes dibujados, en unidades

        Point origen = proyectar(0, 0, 0, cx, cy);
        Point ejeX = proyectar(L, 0, 0, cx, cy);
        Point ejeY = proyectar(0, L, 0, cx, cy);
        Point ejeZ = proyectar(0, 0, L, cx, cy);

        g2.drawLine(origen.x, origen.y, ejeX.x, ejeX.y);
        g2.drawLine(origen.x, origen.y, ejeY.x, ejeY.y);
        g2.drawLine(origen.x, origen.y, ejeZ.x, ejeZ.y);

        g2.setColor(Color.DARK_GRAY);
        g2.drawString("x", ejeX.x + 4, ejeX.y);
        g2.drawString("y", ejeY.x + 4, ejeY.y);
        g2.drawString("z", ejeZ.x + 4, ejeZ.y);
    }

    private void dibujarFlecha(Graphics2D g2, int cx, int cy, Vector3D v, Color color, String etiqueta) {
        Point origen = proyectar(0, 0, 0, cx, cy);
        Point punta = proyectar(v.getX(), v.getY(), v.getZ(), cx, cy);

        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawLine(origen.x, origen.y, punta.x, punta.y);

        double angulo = Math.atan2(origen.y - punta.y, punta.x - origen.x);
        int largoPunta = 9;
        int[] px = new int[3];
        int[] py = new int[3];
        px[0] = punta.x;
        py[0] = punta.y;
        px[1] = punta.x - (int) (largoPunta * Math.cos(angulo - Math.PI / 6));
        py[1] = punta.y + (int) (largoPunta * Math.sin(angulo - Math.PI / 6));
        px[2] = punta.x - (int) (largoPunta * Math.cos(angulo + Math.PI / 6));
        py[2] = punta.y + (int) (largoPunta * Math.sin(angulo + Math.PI / 6));
        g2.fillPolygon(px, py, 3);

        g2.drawString(etiqueta + " " + v.toString(), punta.x + 6, punta.y - 6);
    }
}

