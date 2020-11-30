/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author ASUS
 */
public class ChartGenerator {

    private int jumlahSakit;
    private int jumlahIdzin;
    private int jumlahHadir;

    public JFreeChart createChart(CategoryDataset dataset) {

        JFreeChart barChart = ChartFactory.createBarChart(
                "Attendance Statistic",
                "",
                "Total Session",
                dataset, PlotOrientation.VERTICAL,
                true, true, false);

        return barChart;
    }

    public CategoryDataset createDataset() {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        /*dataset.setValue(12, "Total Session", "Hadir");
        dataset.setValue(5, "Total Session", "Idzin");
        dataset.setValue(3, "Total Session", "Sakit");*/
        dataset.setValue(getJumlahHadir(), "Total Session", "Hadir");
        dataset.setValue(getJumlahIdzin(), "Total Session", "Idzin");
        dataset.setValue(getJumlahSakit(), "Total Session", "Sakit");

        return dataset;
    }

    /**
     * @return the jumlahSakit
     */
    public int getJumlahSakit() {
        return jumlahSakit;
    }

    /**
     * @param jumlahSakit the jumlahSakit to set
     */
    public void setJumlahSakit(int jumlahSakit) {
        this.jumlahSakit = jumlahSakit;
    }

    /**
     * @return the jumlahIdzin
     */
    public int getJumlahIdzin() {
        return jumlahIdzin;
    }

    /**
     * @param jumlahIdzin the jumlahIdzin to set
     */
    public void setJumlahIdzin(int jumlahIdzin) {
        this.jumlahIdzin = jumlahIdzin;
    }

    /**
     * @return the jumlahHadir
     */
    public int getJumlahHadir() {
        return jumlahHadir;
    }

    /**
     * @param jumlahHadir the jumlahHadir to set
     */
    public void setJumlahHadir(int jumlahHadir) {
        this.jumlahHadir = jumlahHadir;
    }

}
