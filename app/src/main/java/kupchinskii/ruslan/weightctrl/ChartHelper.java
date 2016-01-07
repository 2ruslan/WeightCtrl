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
         return getChartBase(context, crr, context.getResources().getColor(R.color.colorCalendarWeight), DB.C_RES_WEIGHT, context.getString(R.string.title_weight));
     }
    public static GraphicalView buildChartHips(Context context) {
        Cursor crr =  DBHelper.GetReusultsAll() ;
        return getChartBase(context, crr, context.getResources().getColor(R.color.colorCalendarWeight), DB.C_RES_HIPS, context.getString( R.string.title_hips) );
    }

    private static XYSeriesRenderer getDefRender(int color) {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);

        XYSeriesRenderer sRenderer = new XYSeriesRenderer();
        sRenderer.setDisplayChartValues(true);
        sRenderer.setChartValuesFormat(format);
        sRenderer.setLineWidth((float) 2);
        sRenderer.setChartValuesTextSize(16);
        sRenderer.setChartValuesTextAlign(Paint.Align.RIGHT);


        //XYSeriesRenderer.FillOutsideLine fill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
       // fill.setColor(Color.LTGRAY);
        //sRenderer.addFillOutsideLine(fill);
        sRenderer.setColor(color);
        return sRenderer;
    }

    private static XYMultipleSeriesRenderer getMultiRender(String chartName) {
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();

       // multiRenderer.setXLabels(0);
        //multiRenderer.setYLabelsAngle(-90);

      //  multiRenderer.setBarSpacing(2);

        multiRenderer.setChartTitle(chartName);
        multiRenderer.setChartTitleTextSize(18);

        //multiRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        //multiRenderer.setLabelsTextSize(18);


        multiRenderer.setShowGrid(true);
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setBackgroundColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        multiRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));



        multiRenderer.setShowLegend(false);
        multiRenderer.setClickEnabled(false);
        multiRenderer.setInScroll(true);
        multiRenderer.setPanEnabled(true, false);

        return multiRenderer;
    }

    private static GraphicalView getChartBase(Context context, Cursor crr
            , int color, String colName, String chartName) {
        crr.moveToFirst();

        XYSeries viewsSeries = new XYSeries("Views");
        XYSeriesRenderer sRenderer = getDefRender(color);
        XYMultipleSeriesRenderer multiRenderer = getMultiRender( chartName );

        long xmax = 0;
        float ymin = 99999, ymax = 0;

        for (int i = 0; i < crr.getCount(); i++) {
            float yval = crr.getFloat(crr.getColumnIndex(colName));

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

        float deltay = (ymax - ymin) / 10;
        multiRenderer.setYAxisMin(ymin - deltay);
        multiRenderer.setYAxisMax(ymax + deltay);

        multiRenderer.setXAxisMax(xmax);
        multiRenderer.setXAxisMin(xmax > 20 ? xmax - 19 : 0);

        multiRenderer.addSeriesRenderer(sRenderer);

        multiRenderer.setShowAxes(false);
        multiRenderer.setShowGridX(true);
        multiRenderer.setShowGridY(false);
        multiRenderer.clearXTextLabels();
        multiRenderer.setXLabels(0);

        return ChartFactory.getLineChartView (context,dataset,multiRenderer);
    }
}
