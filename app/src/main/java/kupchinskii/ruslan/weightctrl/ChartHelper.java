package kupchinskii.ruslan.weightctrl;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.NumberFormat;

public  class ChartHelper {
     public static GraphicalView buildChartWeight(Context context) {
         Cursor crr =  DBHelper.GetReusultsAll() ;
         return getChartBase(context, crr, Color.GREEN, DB.C_RES_WEIGHT );
     }
    public static GraphicalView buildChartHips(Context context) {
        Cursor crr =  DBHelper.GetReusultsAll() ;
        return getChartBase(context, crr, Color.BLUE, DB.C_RES_HIPS );
    }

    private static XYSeriesRenderer getDefRender(int color) {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);

        XYSeriesRenderer sRenderer = new XYSeriesRenderer();
        sRenderer.setDisplayChartValues(true);
        sRenderer.setChartValuesFormat(format);
        sRenderer.setLineWidth((float) .2);
        sRenderer.setChartValuesTextSize(16);
        sRenderer.setChartValuesTextAlign(Paint.Align.RIGHT);


        XYSeriesRenderer.FillOutsideLine fill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
        fill.setColor(Color.LTGRAY);
        sRenderer.addFillOutsideLine(fill);
        sRenderer.setColor(color);
        return sRenderer;
    }

    private static XYMultipleSeriesRenderer getMultiRender(String chartName) {
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();

        multiRenderer.setXLabels(0);
        multiRenderer.setYLabelsAngle(-90);

        multiRenderer.setBarSpacing(2);

        multiRenderer.setShowGrid(false);
        multiRenderer.setChartTitle(chartName);
        multiRenderer.setChartTitleTextSize(18);

        multiRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        multiRenderer.setLabelsTextSize(18);

        multiRenderer.setShowLegend(false);
        multiRenderer.setClickEnabled(false);
        multiRenderer.setInScroll(true);
        multiRenderer.setPanEnabled(true, false);

        return multiRenderer;
    }

    private static GraphicalView getChartBase(Context context, Cursor crr
            , int color, String colName) {
        crr.moveToFirst();

        XYSeries viewsSeries = new XYSeries("Views");
        XYSeriesRenderer sRenderer = getDefRender(color);
        XYMultipleSeriesRenderer multiRenderer = getMultiRender( "вес" /*context.getString(chartname)*/);


        long xmax = 0;
        float ymin = 0, ymax = 0;

        for (int i = 0; i < crr.getCount(); i++) {
            float yval = crr.getFloat(crr.getColumnIndex(colName));
            if(colName == DB.C_RES_WEIGHT)
                yval /= 10;

            int xval = i + 1;

            if (yval < ymin) ymin = yval;
            if (yval > ymax) ymax = yval;
            xmax++;

            viewsSeries.add(i, yval);
            multiRenderer.addXTextLabel(i, String.valueOf(xval));
            crr.moveToNext();
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(viewsSeries);

        multiRenderer.setYAxisMin(ymin);
        multiRenderer.setYAxisMax(ymax + (ymax * 0.1));

        multiRenderer.setXAxisMax(xmax);
        multiRenderer.setXAxisMin(xmax > 7 ? xmax - 6 : 0);

        multiRenderer.setBarWidth(46);

        multiRenderer.addSeriesRenderer(sRenderer);

        return ChartFactory.getBarChartView(context, dataset, multiRenderer, BarChart.Type.DEFAULT);
    }
}
