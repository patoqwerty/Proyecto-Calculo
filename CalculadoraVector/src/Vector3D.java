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
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", getX(), getY(), getZ());
    }
}