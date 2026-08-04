public abstract class Vector {

    protected double[] componentes;

    protected Vector(double... componentes) {
        this.componentes = componentes;
    }

    public int getDimension() {
        return componentes.length;
    }

    public double[] getComponentes() {
        return componentes.clone();
    }

    // Magnitud
    public double magnitud() {
        double sumaCuadrados = 0;
        for (double c : componentes) {
            sumaCuadrados += c * c;
        }
        return Math.sqrt(sumaCuadrados);
    }


    // Verifica que los vectores tengan la misma dimensiónyyy 
    protected void validarMismaDimension(Vector otro) {
        if (this.getDimension() != otro.getDimension()) {
            throw new IllegalArgumentException(
                "No se pueden operar vectores de distinta dimensión ("
                + this.getDimension() + "D con " + otro.getDimension() + "D)");
        }
    }

    public abstract Vector suma(Vector otro);

    public abstract Vector resta(Vector otro);

    @Override
    public abstract String toString();
}