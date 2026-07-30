public class Vector3D extends Vector {

    public Vector3D(double x, double y, double z) {
        super(x, y, z); 
    }

    public double getX() {
        return componentes[0];
    }

    public double getY() {
        return componentes[1];
    }

    public double getZ() {
        return componentes[2];
    }

    @Override
    public Vector3D suma(Vector otro) {
        validarMismaDimension(otro);
        double[] o = otro.getComponentes();
        return new Vector3D(getX() + o[0], getY() + o[1], getZ() + o[2]);
    }

    @Override
    public Vector3D resta(Vector otro) {
        validarMismaDimension(otro);
        double[] o = otro.getComponentes();
        return new Vector3D(getX() - o[0], getY() - o[1], getZ() - o[2]);
    }

    @Override
    public Vector3D escalar(double k) {
        return new Vector3D(getX() * k, getY() * k, getZ() * k);
    }

    @Override
    public Vector3D normalizar() {
        double m = magnitud();
        if (m == 0) return new Vector3D(0, 0, 0);
        return new Vector3D(getX() / m, getY() / m, getZ() / m);
    }

    public Vector3D productoCruz(Vector3D otro) {
        double cx = getY() * otro.getZ() - getZ() * otro.getY();
        double cy = getZ() * otro.getX() - getX() * otro.getZ();
        double cz = getX() * otro.getY() - getY() * otro.getX();
        return new Vector3D(cx, cy, cz);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", getX(), getY(), getZ());
    }
}

