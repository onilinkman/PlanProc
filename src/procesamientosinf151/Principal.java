/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package procesamientosinf151;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.JFreeChart;

public class Principal extends javax.swing.JFrame {

    public Principal() {
        initComponents();
        this.setLocationRelativeTo(null);
    }

    private int num = 65; //aqui es el ASCII para nombrar los procesos

    Stack<Proceso> pila = new Stack();
    Stack<Proceso> aux = new Stack();
    Queue<Proceso> cola = new LinkedList();//PARA EL ROUND ROBIN
    private double tiempoTanscurrido = 0;
    double temporizador = 0;

    public void generarFila() {
        DefaultTableModel model = (DefaultTableModel) this.jTable1.getModel();
        model.addRow(new Object[]{""});
    }

    public void generarFila2() {
        DefaultTableModel model = (DefaultTableModel) this.jTable2.getModel();
        model.addRow(new Object[]{""});
    }

    public void reiniciarTabla2() {
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "EN MEMORIA"
                }
        ));
    }

    public void ReiniciarTabla1() {
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Proceso", "Hora Llegada", "Tiempo Corrida", "Prioridad", "Hora Salida", "Tiempo Servicio", "T. Espera", "Indice Servicios"
                }
        ));
    }

    public void reiniciarPila() {
        for (int i = 0; i < pila.size(); i++) {
            pila.get(i).setTc(Double.valueOf(this.jTable1.getValueAt(i, 2).toString()));
        }
    }

    public void llenarTabla1() {
        ReiniciarTabla1();
        for (int i = 0; i < pila.size(); i++) {
            generarFila();
            Proceso p = pila.get(i);
            this.jTable1.setValueAt(p.getProceso(), i, 0);
            this.jTable1.setValueAt(p.getHll(), i, 1);
            this.jTable1.setValueAt(p.getTc(), i, 2);
            this.jTable1.setValueAt(p.getPrioridad(), i, 3);
        }
    }

    //******************* RESOLVER TS-TE-IC*************************************
    public void llenarRestoTablas() {
        DecimalFormat df = new DecimalFormat("#.00");
        double pts = 0;
        double pte = 0;
        double pis = 0;
        double cont = 0;
        for (int i = 0; i < this.jTable1.getRowCount(); i++) {
            if (this.jTable1.getValueAt(i, 4) != null) {
                String cu = this.jTable1.getValueAt(i, 4).toString();
                if (!cu.equals("")) {
                    double ts = Double.valueOf(cu) - Double.valueOf(this.jTable1.getValueAt(i, 1).toString());
                    this.jTable1.setValueAt(convertirComa(df.format(ts)), i, 5);
                    double tc = Double.valueOf(this.jTable1.getValueAt(i, 2).toString());
                    double te = ts - tc;
                    this.jTable1.setValueAt(convertirComa(df.format(te)), i, 6);
                    double is = tc / ts;
                    this.jTable1.setValueAt(convertirComa(df.format(is)), i, 7);
                    pts = pts + ts;
                    pte = pte + te;
                    pis = pis + is;
                    cont++;
                }
            }
        }

        pts = pts / cont;
        pte = pte / cont;
        pis = pis / cont;
        this.pantTe.setText(convertirComa(df.format(pte)) + "");
        this.pantTs.setText(convertirComa(df.format(pts)) + "");
        this.pantIs.setText(convertirComa(df.format(pis)) + "");

    }

    public Queue<datosGrafica> colaGrafica = new LinkedList();

    public double convertirComa(String num) {
        String cad = "";
        for (int i = 0; i < num.length(); i++) {
            if (num.charAt(i) == ',') {
                cad = cad + ".";
            } else {
                cad = cad + num.charAt(i);
            }
        }
        return Double.valueOf(cad);
    }

    //METODO ROUND ROBIN    ****************************************************
    int quantum = 0;

    //aqui se llena con todos para luego ser eliminados a medida que van ingresando
    public void llenarCola() {
        for (int i = 0; i < pro.length; i++) {
            cola.add(pro[i]);
        }
    }

    public int agregarACola2() {
        Queue<Proceso> auxx = new LinkedList();
        while (!cola.isEmpty()) {
            Proceso p = cola.remove();
            if (p.getHll() <= tiempoTanscurrido) {
                cola2.add(p);
            } else {
                auxx.add(p);
            }
        }
        cola.addAll(auxx);
        return 0;
    }

    public void prioridadRR() {
        if (cola.isEmpty() && cola2.isEmpty()) {
            actualizar();
            llenarCola();
            agregarACola2();
            prioridadRR();
        } else {
            resolverCola2();
        }
    }

    Queue<Proceso> cola2 = new LinkedList();//se le llenara de procesos que estan en el tiempo

    //CONTINUAR BUSCAR EN LA COLA 2 EL DE MENOR PRIORIDAD
    public void resolverCola2() {
        if (this.jRadioButton1.isSelected()) {
            Queue<Proceso> aux = new LinkedList();
            int posicion = 0;
            aux.addAll(cola2);
            int pri = priCola2();
            while (!aux.isEmpty()) {
                Proceso p = aux.remove();
                if (p.getPrioridad() == pri) {
                    break;
                }
                posicion++;
            }
            aux.clear();
            int i = 0;
            Proceso trab = new Proceso();
            while (!cola2.isEmpty()) {
                Proceso p = cola2.remove();
                if (posicion == i) {
                    trab = p;
                } else {
                    aux.add(p);
                }
                i++;
            }
            cola2.addAll(aux);
            //***
            Queue<Proceso> auxx = new LinkedList();
            while (!cola.isEmpty()) {
                Proceso p = cola.remove();
                if (p.getHll() <= tiempoTanscurrido + quantum) {
                    cola2.add(p);
                } else {
                    auxx.add(p);
                }
            }
            cola.addAll(auxx);
            //***
            if (trab.getTc() <= quantum) {
                datosGrafica df = new datosGrafica();
                df.setInicial(tiempoTanscurrido);
                tiempoTanscurrido = tiempoTanscurrido + trab.getTc();
                df.setFin(tiempoTanscurrido);
                df.setProceso(trab.getProceso());
                colaGrafica.add(df);
                this.jTable1.setValueAt(tiempoTanscurrido, posicionProceso(trab.getProceso()), 4);
                System.out.println("tiem tc<=q : " + tiempoTanscurrido);
                trab.setTc(0);
                contar++;
            } else {
                datosGrafica df = new datosGrafica();
                df.setInicial(tiempoTanscurrido);
                tiempoTanscurrido = tiempoTanscurrido + quantum;
                df.setFin(tiempoTanscurrido);
                df.setProceso(trab.getProceso());
                colaGrafica.add(df);
                System.out.println("tiem tc > q : " + tiempoTanscurrido);
                trab.setTc(trab.getTc() - quantum);
                cola2.add(trab);
            }
            this.pantTimpo.setText(tiempoTanscurrido + "");

            llenarTabla2Colas();
        } else {
            Proceso trab = cola2.remove();
            //***
            Queue<Proceso> auxx = new LinkedList();
            while (!cola.isEmpty()) {
                Proceso p = cola.remove();
                if (p.getHll() <= tiempoTanscurrido + quantum) {
                    cola2.add(p);
                } else {
                    auxx.add(p);
                }
            }
            cola.addAll(auxx);
            //***
            if (trab.getTc() <= quantum) {
                datosGrafica df = new datosGrafica();
                df.setInicial(tiempoTanscurrido);
                tiempoTanscurrido = tiempoTanscurrido + trab.getTc();
                df.setFin(tiempoTanscurrido);
                df.setProceso(trab.getProceso());
                colaGrafica.add(df);
                this.jTable1.setValueAt(tiempoTanscurrido, posicionProceso(trab.getProceso()), 4);
                trab.setTc(0);
                contar++;
            } else {
                datosGrafica df = new datosGrafica();
                df.setInicial(tiempoTanscurrido);
                tiempoTanscurrido = tiempoTanscurrido + quantum;
                df.setFin(tiempoTanscurrido);
                df.setProceso(trab.getProceso());
                colaGrafica.add(df);
                trab.setTc(trab.getTc() - quantum);
                cola2.add(trab);
            }
            this.pantTimpo.setText(tiempoTanscurrido + "");

            llenarTabla2Colas();
        }

    }

    public void llenarTabla2Colas() {
        reiniciarTabla2();
        Queue<Proceso> aux = new LinkedList();
        aux.addAll(cola2);
        int fila = 0;
        while (!aux.isEmpty()) {
            generarFila2();
            Proceso p = aux.remove();
            this.jTable2.setValueAt(p.getProceso(), fila, 0);
            fila++;
        }
    }

    public int priCola2() {
        Queue<Proceso> aux = new LinkedList();
        aux.addAll(cola2);
        if (!aux.isEmpty()) {
            int pri = aux.remove().getPrioridad();
            while (!aux.isEmpty()) {
                Proceso p = aux.remove();
                if (p.getPrioridad() < pri) {
                    pri = p.getPrioridad();
                }
            }
            return pri;
        }
        return -1;
    }

    // int buscarMenorPriEnCola(){
    //}
    // FCFS  METODOS PARA LA LA RESOLUCION
    Proceso[] pro = null;

    /**
     * ORDENA POR ORDEN DE LLEGADA
     */
    public void actualizar() {
        if (!pila.isEmpty()) {
            Stack<Proceso> auxs = new Stack();
            int fi = 0;
            auxs.addAll(pila);
            pro = new Proceso[pila.size()];
            while (!auxs.isEmpty()) {
                pro[fi] = auxs.remove(0);
                fi++;
            }

            for (int i = 0; i < pila.size() - 1; i++) {
                for (int j = i + 1; j < pila.size(); j++) {

                    if (pro[j].getHll() < pro[i].getHll()) {
                        Proceso aux1 = pro[j];
                        pro[j] = pro[i];
                        pro[i] = aux1;
                    }
                }
            }
        }
    }

    public void llenarTabla2() {
        reiniciarTabla2();
        int fila = 0;
        for (int i = 0; i < pro.length; i++) {
            if (pro[i].getHll() <= tiempoTanscurrido && !pro[i].isSw()) {
                generarFila2();
                this.jTable2.setValueAt(pro[i].getProceso(), fila, 0);
                fila++;
            }
        }
    }

    /**
     * BUSCA EL MENOR VALOR DE PRIORIDAD POSIBLE Que seria la mayor prioridad
     *
     * @return
     */
    public int buscarMenorPrioridad() {
        int pri = -1;
        if (!pila.isEmpty()) {
            for (int i = 0; i < pila.size(); i++) {
                Proceso p = pila.get(i);
                if (!p.isSw()) {
                    pri = p.getPrioridad();
                    for (int j = i + 1; j < pila.size(); j++) {
                        Proceso p2 = pila.get(j);
                        if (p2.getPrioridad() < pri && !p2.isSw()) {
                            pri = p2.getPrioridad();
                        }
                    }
                    return pri;
                }

            }
        }
        return pri;
    }

    public int posicionProceso(String pro) {

        for (int i = 0; i < pila.size(); i++) {
            if (pro.equals(pila.get(i).getProceso())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * ESTO SERA SIN TEMPORIZADOR
     */
    public void hallarHs() {
        if (pro != null) {
            if (this.jRadioButton1.isSelected()) {
                int pri = buscarMenorPrioridad();
                for (int i = 0; i < pro.length; i++) {
                    if (pri == pro[i].getPrioridad() && !pro[i].isSw()) {
                        datosGrafica df = new datosGrafica();
                        df.setInicial(tiempoTanscurrido);
                        tiempoTanscurrido += pro[i].getTc();
                        df.setFin(tiempoTanscurrido);
                        df.setProceso(pro[i].getProceso());
                        colaGrafica.add(df);
                        this.jTable1.setValueAt(tiempoTanscurrido, posicionProceso(pro[i].getProceso()), 4);
                        pro[i].setSw(true);
                        llenarTabla2();
                        break;
                    }
                }
            } else {
                for (int i = 0; i < pro.length; i++) {
                    if (!pro[i].isSw()) {
                        datosGrafica df = new datosGrafica();
                        df.setInicial(tiempoTanscurrido);
                        tiempoTanscurrido += pro[i].getTc();
                        df.setFin(tiempoTanscurrido);
                        df.setProceso(pro[i].getProceso());
                        colaGrafica.add(df);
                        this.jTable1.setValueAt(tiempoTanscurrido, posicionProceso(pro[i].getProceso()), 4);
                        pro[i].setSw(true);
                        llenarTabla2();
                        break;
                    }
                }
            }
        } else {
            actualizar();
            hallarHs();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jTextField4 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        pantTimpo = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        pantIs = new javax.swing.JTextField();
        pantTs = new javax.swing.JTextField();
        pantTe = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("PRIORIDAD, ROUND ROBIN Y FCFS");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, 455, 43));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Proceso", "Hora Llegada", "Tiempo Corrida","Prioridad", "Hora Salida","Tiempo Servicio","T. Espera","Indice Servicios"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable1MousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 700, 240));

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setText("AGREGAR");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 100, 90, 30));

        jLabel2.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        jLabel2.setText("QUANTUM");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 150, -1, -1));

        jTextField1.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 130, 30));

        jTextField2.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 100, 120, 30));

        jTextField3.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        getContentPane().add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 100, 150, 30));

        jLabel3.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        jLabel3.setText("H.LL");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, -1, -1));

        jLabel4.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        jLabel4.setText("TC");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 80, -1, -1));

        jRadioButton1.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        jRadioButton1.setText("CON PRIORIDAD");
        getContentPane().add(jRadioButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 140, -1, -1));

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        jRadioButton2.setText("ROUND ROBIN");
        getContentPane().add(jRadioButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 140, -1, -1));

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        jRadioButton3.setText("FCFS");
        getContentPane().add(jRadioButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 140, -1, -1));

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton2.setText("ELIMINAR");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 510, 140, 40));

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton3.setText("EDITAR");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 460, 140, 40));

        jButton4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton4.setText("POR PASOS");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 170, 120, 30));

        jButton5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton5.setText("RESOLVERLO");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 170, 130, 30));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "EN MEMORIA"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 210, 140, 240));

        jButton7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton7.setText("REINICIAR");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 130, 60));
        getContentPane().add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 170, 110, 30));

        jLabel5.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        jLabel5.setText("Tiempo Transcurido:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 80, -1, -1));

        pantTimpo.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        getContentPane().add(pantTimpo, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 100, 160, 30));

        jLabel6.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        jLabel6.setText("Prioridad");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 80, -1, -1));

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102), 2));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        jLabel7.setText("Tiempo Espera:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 55, 120, 20));

        jLabel8.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("PROMEDIOS");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 250, 20));

        jLabel9.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        jLabel9.setText("Tiempo Servicio:");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 120, 20));

        jLabel10.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        jLabel10.setText("Indice Servicio:");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 80, 120, 20));

        jButton8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton8.setText("Sacar Promedios");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 140, 60));

        pantIs.setEditable(false);
        pantIs.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.add(pantIs, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 80, 120, -1));

        pantTs.setEditable(false);
        pantTs.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.add(pantTs, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 30, 120, -1));

        pantTe.setEditable(false);
        pantTe.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.add(pantTe, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 55, 120, -1));

        jButton9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton9.setText("GRAFICAR");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 20, 130, 80));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 460, 540, 110));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        Proceso p = new Proceso();
        try {
            p.setHll(Double.valueOf(this.jTextField1.getText()));
            p.setTc(Double.valueOf(this.jTextField2.getText()));
            p.setPrioridad(Integer.valueOf(this.jTextField3.getText()));
            p.setProceso((char) num + "");
            num++;
            generarFila();
            pila.add(p);
            this.jTable1.setValueAt(p.getProceso(), pila.size() - 1, 0);
            this.jTable1.setValueAt(p.getHll(), pila.size() - 1, 1);
            this.jTable1.setValueAt(p.getTc(), pila.size() - 1, 2);
            this.jTable1.setValueAt(p.getPrioridad(), pila.size() - 1, 3);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            JOptionPane.showMessageDialog(null, "Rellene todos los campos con numeros realies, \n El quantum y Prioridad deben tener numeros enteros");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        try {
            if (this.jTable1.getSelectedRow() != -1) {
                pAux.setHll(Double.valueOf(this.jTextField1.getText()));
                pAux.setTc(Double.valueOf(this.jTextField2.getText()));
                pAux.setPrioridad(Integer.valueOf(this.jTextField3.getText()));
                llenarTabla1();
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione un objeto a editar");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        if (this.jRadioButton3.isSelected()) {
            hallarHs();
        } else if (this.jRadioButton2.isSelected()) {
            if (contar != pila.size()) {
                try {
                    quantum = Integer.valueOf(this.jTextField4.getText());
                    prioridadRR();
                } catch (Exception e) {
                    System.out.println("Error: " + e);
                }
            }
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        if (this.jRadioButton3.isSelected()) {
            for (int i = 0; i < pila.size(); i++) {
                hallarHs();
            }
        } else if (this.jRadioButton2.isSelected()) {
            if (contar != pila.size()) {
                try {
                    quantum = Integer.valueOf(this.jTextField4.getText());
                    while (contar < this.jTable1.getRowCount()) {
                        prioridadRR();
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e);
                }
            }
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private int contar = 0;
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        for (int i = 0; i < pila.size(); i++) {
            pila.get(i).setSw(false);
            this.jTable1.setValueAt("", i, 4);
            this.jTable1.setValueAt("", i, 5);
            this.jTable1.setValueAt("", i, 6);
            this.jTable1.setValueAt("", i, 7);
        }
        tiempoTanscurrido = 0;
        aux.addAll(pila);
        pro = null;
        reiniciarPila();
        cola.clear();
        cola2.clear();
        this.pantTimpo.setText(tiempoTanscurrido + "");
        contar = 0;
        colaGrafica=new LinkedList();
    }//GEN-LAST:event_jButton7ActionPerformed

    private Proceso pAux = null;

    private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
        // TODO add your handling code here:
        pAux = pila.get(this.jTable1.getSelectedRow());
    }//GEN-LAST:event_jTable1MousePressed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        int fila = this.jTable1.getSelectedRow();
        if (fila != -1) {
            num = 65;
            pila.remove(fila);
            for (int i = 0; i < pila.size(); i++) {
                pila.get(i).setProceso((char) num + "");
                num++;
            }
            llenarTabla1();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        try {
            llenarRestoTablas();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        graficar g=new graficar("GRAFICA", colaGrafica, pro,jRadioButton2.isSelected());
        g.setSize(800, 400);
        g.setLocationRelativeTo(null);
        g.setVisible(true);
    }//GEN-LAST:event_jButton9ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField pantIs;
    private javax.swing.JTextField pantTe;
    private javax.swing.JLabel pantTimpo;
    private javax.swing.JTextField pantTs;
    // End of variables declaration//GEN-END:variables
}
