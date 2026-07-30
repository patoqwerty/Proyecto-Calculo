//Clase padre
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

    //Magnitud

    public double magnitud() {
        double sumaCuadrados = 0;
        for (double c : componentes) {
            sumaCuadrados += c * c;
        }
        return Math.sqrt(sumaCuadrados);
    }

    //Verifica que los vectores tengan la misma dimensión
    protected void validarMismaDimension(Vector otro) {
        if (this.getDimension() != otro.getDimension()) {
            throw new IllegalArgumentException(
                "No se pueden operar vectores de distinta dimensión ("
                + this.getDimension() + "D con " + otro.getDimension() + "D)");
        }
    }

    public double productoPunto(Vector otro) {
        validarMismaDimension(otro);
        double resultado = 0;
        double[] otras = otro.getComponentes();
        for (int i = 0; i < componentes.length; i++) {
            resultado += componentes[i] * otras[i];
        }
        return resultado;
    }

    public double anguloEntre(Vector otro) {
        double cos = productoPunto(otro) / (this.magnitud() * otro.magnitud());
        // Evitar errores de redondeo que saquen el valor de [-1, 1]
        cos = Math.max(-1.0, Math.min(1.0, cos));
        return Math.toDegrees(Math.acos(cos));
    }

    public abstract Vector suma(Vector otro);

    public abstract Vector resta(Vector otro);

    public abstract Vector escalar(double k);

    public abstract Vector normalizar();

    @Override
    public abstract String toString();
}
