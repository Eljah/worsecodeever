import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileCreationGraph {
    //private static final Pattern p = Pattern.compile("[-rw]{10} \\d{1} django django \\d{1}.\\d{1}M (\\D{3}) (\\s?\\d{1,2}) (\\d{1,2}):(\\d{1,2}) .*");
 //[-rw]{10} \d{1} django django \d? (\D{3}) (\s?\d{1,2}) (\d{1,2}):(\d{1,2}) .*
 private static final Pattern p = Pattern.compile("[-rw]{10} \\d{1} django django \\d* (\\D{3}) (\\s?\\d{1,2}) (\\d{1,2}):(\\d{1,2}) .*");

    public static void main(String[] args) throws IOException {


        TimeSeries xySeries = new TimeSeries("prometheus files");

        //try (BufferedReader br = new BufferedReader(new FileReader("djmetr"))) {
        try (BufferedReader br = new BufferedReader(new FileReader("stage"))) {
            for (String line; (line = br.readLine()) != null; ) {
                // process the line.
                Matcher m = p.matcher(line);
                if (m.find()) {
                    DateFormat df = new SimpleDateFormat("YYYYMMM(dd)mmss", Locale.ENGLISH);
                    Date result = df.parse("2020" + m.group(1) + "(" + m.group(2) + ")" + m.group(3) + m.group(4));
                    int addition=1;
                    if (xySeries.getValue(new Millisecond(result))!=null)
                    {
                        addition=xySeries.getValue(new Millisecond(result)).intValue()+1;
                    }
                    xySeries.addOrUpdate(new Millisecond(result),addition);
                } else {
                    System.err.println(line);
                }
            }
            // line is not visible here.
        } catch (ParseException e) {
            e.printStackTrace();
        }


        TimeSeriesCollection dataset = new TimeSeriesCollection();;
        dataset.addSeries(xySeries);

        JFreeChart histogram = ChartFactory.createXYLineChart(
                "Average salary per age",
                "Age",
                "Salary (€)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        XYPlot plot = histogram.getXYPlot();
        plot.setDomainAxis(new DateAxis());
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setDateFormatOverride(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS"));
        ChartUtils.saveChartAsPNG(new File("histogram.png"), histogram, 4500, 400);
    }
}
