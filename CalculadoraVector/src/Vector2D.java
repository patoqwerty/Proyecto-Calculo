public class Vector2D extends Vector {

    public Vector2D(double x, double y) {
        super(x, y); 
    }

    public double getX() {
        return componentes[0];
    }

    public double getY() {
        return componentes[1];
    }

    public double angulo() {
        double grados = Math.toDegrees(Math.atan2(getY(), getX()));
        return (grados < 0) ? grados + 360 : grados;
    }

    // Crea un Vector2D en forma polar
    public static Vector2D desdePolar(double magnitud, double anguloGrados) {
        double rad = Math.toRadians(anguloGrados);
        return new Vector2D(magnitud * Math.cos(rad), magnitud * Math.sin(rad));
    }

    @Override
    public Vector2D suma(Vector otro) {
        validarMismaDimension(otro);
        double[] o = otro.getComponentes();
        return new Vector2D(getX() + o[0], getY() + o[1]);
    }

    @Override
    public Vector2D resta(Vector otro) {
        validarMismaDimension(otro);
        double[] o = otro.getComponentes();
        return new Vector2D(getX() - o[0], getY() - o[1]);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", getX(), getY());
    }
}