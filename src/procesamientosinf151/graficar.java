/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package procesamientosinf151;

import java.awt.BasicStroke;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author WINDOWS 10
 */
public class graficar extends JFrame {

    public Queue<datosGrafica> colaGrafica = new LinkedList();
    Proceso[] pro;

    public graficar(String title, Queue<datosGrafica> c, Proceso[] pro,boolean sw) {
        super(title);
        colaGrafica.addAll(c);
        this.pro = pro;
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                title,
                "Tiempo",
                "Proceso",
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        final XYPlot plot = xylineChart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        for(int i=0;i<pro.length;i++){
            renderer.setSeriesStroke(i, new BasicStroke(4.0f));
            
            renderer.setBaseLinesVisible(!sw);
            
        }
        plot.setRenderer(renderer);
        setContentPane(chartPanel);
    }

    public Queue<datosGrafica> getDatoGrafica(String proceso) {
        Queue<datosGrafica> aux = new LinkedList();
        Queue<datosGrafica> nuev = new LinkedList();
        while (!colaGrafica.isEmpty()) {
            datosGrafica dg = colaGrafica.remove();
            if (proceso.equals(dg.getProceso())) {
                nuev.add(dg);
            } else {
                aux.add(dg);
            }
        }
        colaGrafica.addAll(aux);
        return nuev;
    }
    

    private XYDataset createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries[] ser = new XYSeries[pro.length];
        
        int con=65;
        for (int i = 0; i < ser.length; i++) {
            ser[i] = new XYSeries(((char)con)+"");
            Queue<datosGrafica> aa = getDatoGrafica(((char)con)+"");
            while (!aa.isEmpty()) {
                datosGrafica dg = aa.remove();
                ser[i].add(dg.getInicial(), i+1);
                ser[i].add(dg.getFin(), i+1);
            }
            dataset.addSeries(ser[i]);
            con++;
        }
        
        return dataset;
    }
}
