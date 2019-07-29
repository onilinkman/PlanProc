
package procesamientosinf151;

/**
 *
 * @author Christian Marban
 */
public class Proceso {
    private String proceso;
    private double hll;
    private double tc;
    private boolean sw;
    private int prioridad;
    public Proceso(){
        hll=0;
        tc=0;
        proceso="";
        prioridad=0;
        sw=false;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public double getHll() {
        return hll;
    }

    public void setHll(double hll) {
        this.hll = hll;
    }

    public double getTc() {
        return tc;
    }

    public void setTc(double tc) {
        this.tc = tc;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public boolean isSw() {
        return sw;
    }

    public void setSw(boolean sw) {
        this.sw = sw;
    }
    
    
    
}
