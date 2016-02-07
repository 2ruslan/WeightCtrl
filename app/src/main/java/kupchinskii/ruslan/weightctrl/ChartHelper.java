package kupchinskii.ruslan.weightctrl;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
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

        sRenderer.setColor(color);
        return sRenderer;
    }

    private static XYMultipleSeriesRenderer getMultiRender(String chartName) {
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();

        multiRenderer.setChartTitle(chartName);
        multiRenderer.setChartTitleTextSize(20);

        multiRenderer.setLabelsTextSize(12);

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

        float deltaY = ((ymax - ymin) / 10) - ymax == ymin ? 1 : 0;
        multiRenderer.setYAxisMin(ymin - deltaY);
        multiRenderer.setYAxisMax(ymax + deltaY);

        multiRenderer.setXAxisMax(xmax);
        multiRenderer.setXAxisMin(xmax > 11 ? xmax - 10 : 0);

        multiRenderer.addSeriesRenderer(sRenderer);

        multiRenderer.setShowAxes(false);
        multiRenderer.setShowGridX(true);
        multiRenderer.setShowGridY(false);
        multiRenderer.clearXTextLabels();
        multiRenderer.setXLabels(0);

        return ChartFactory.getCubeLineChartView(context,dataset,multiRenderer, 0.2f);
    }
}
