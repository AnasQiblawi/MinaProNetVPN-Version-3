package spkmods.build.Graph;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.components.XAxis.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.*;
import com.github.mikephil.charting.interfaces.datasets.*;
import java.util.*;
import vpn.minapronet.com.eg.R;
import spkmods.build.Graph.*;
import androidx.*;

public class GraphHelper {
    private static final int TIME_PERIOD_SECCONDS = 0;
    private static Handler mHandler;
    private static GraphHelper m_GraphHelper;
    private String TAG = "GraphHelper";
    private int mColor;
	String a = new String(new byte[]{35,102,102,102,102,97,98,48,48});
    private Context mContext;
    private LineChart mLineChart;
    private boolean mLogScale = false;
    public Runnable triggerRefresh = new Runnable() {

        @Override
        public void run() {
            refreshGraph();
            GraphHelper.mHandler.postDelayed(this, (long) 3000);
        }
    };

	/* public class ValueFormat extends ValueFormatter {
	 @Override
	 public String getFormattedValue(float f) {
	 if (mLogScale && f < 2.1f) {
	 //return "< 100\u2009bit/s";
	 }
	 if (mLogScale) {
	 f = ((float) Math.pow((double) 10, (double) f)) / ((float) 8);
	 }
	 return humanReadableByteCount((long) f, true, mContext.getResources());
	 }
	 }*/

    /*@Override
	 public void updateByteCount(long j, long j2, long j3, long j4) {
	 ((AppCompatActivity) this.mContext).runOnUiThread(new Runnable() {
	 private final GraphHelper this$0;

	 {
	 this.this$0 = r1;
	 }

	 static GraphHelper access$0(AnonymousClass100000000 anonymousClass100000000) {
	 return anonymousClass100000000.this$0;
	 }

	 @Override
	 public void run() {
	 GraphHelper.mHandler.removeCallbacks(this.this$0.triggerRefresh);
	 this.this$0.refreshGraph();
	 GraphHelper.mHandler.postDelayed(this.this$0.triggerRefresh, (long) 3000);
	 }
	 });
	 }*/

    public static synchronized GraphHelper getHelper() {
        GraphHelper graphHelper;
        synchronized (GraphHelper.class) {
            if (m_GraphHelper == null) {
                m_GraphHelper = new GraphHelper();
            }
            if (mHandler == null) {
                mHandler = new Handler();
            }
            graphHelper = m_GraphHelper;
        }
        return graphHelper;
    }

    public GraphHelper color(int i) {
        this.mColor = i;
        return m_GraphHelper;
    }

    public GraphHelper chart(LineChart lineChart) {
        this.mLineChart = lineChart;
        return m_GraphHelper;
    }

    public GraphHelper with(Context context) {
        this.mContext = context;
        return m_GraphHelper;
    }

    public void refreshGraph() {
        try {
            this.mLineChart.getDescription().setEnabled(false);
            this.mLineChart.setTouchEnabled(false);
            this.mLineChart.setDrawGridBackground(false);
            this.mLineChart.getLegend().setEnabled(false);
            XAxis xAxis = this.mLineChart.getXAxis();
            xAxis.setPosition(XAxisPosition.BOTTOM);

			xAxis.setTextColor(Color.GRAY);
			//xAxis.setValueFormatter(new ValueFormat());
			xAxis.enableGridDashedLine(5f, 5f, 5f);
            YAxis axisLeft = this.mLineChart.getAxisLeft();
			axisLeft.enableGridDashedLine(5f, 5f, 5f);
            axisLeft.setTextColor(Color.GRAY);
            //axisLeft.setValueFormatter(new ValueFormat());
			this.mLineChart.getAxisRight().setTextColor(Color.GRAY);
			//this.mLineChart.getAxisRight().getLabelCount();
			//    this.mLineChart.getAxisRight().setEnabled(true);
            LineData dataSet = getDataSet(10);
			//    float yMax = dataSet.getYMax();
			//  if (this.mLogScale) {
			// axisLeft.setAxisMinimum(1.0f);
			// axisLeft.setAxisMaximum((float) Math.ceil((double) yMax));
			// axisLeft.setLabelCount((int) Math.ceil((double) (yMax - 1.0f)));
			//   } else {
			axisLeft.setAxisMinimum(0.0f);
			axisLeft.resetAxisMaximum();
			// axisLeft.setLabelCount(8);
			axisLeft.setTextSize(7);
			xAxis.setLabelCount(11);
			xAxis.setTextSize(7);
			this.mLineChart.getAxisRight().setTextSize(7);
			//this.mLineChart.getAxisRight().setLabelCount(11);
			// }
            if (((ILineDataSet) dataSet.getDataSetByIndex(0)).getEntryCount() < 1) {
                this.mLineChart.setData((LineData) null);
            } else {
                this.mLineChart.setData(dataSet);
            }
            this.mLineChart.invalidate();
        } catch (Exception e) {
            Log.e(this.TAG, e.toString());
        }
    }

    private LineData getDataSet(int io) {

		List<Long> dList = StoredData.downloadList;
		ArrayList<Entry> e1 = new ArrayList<Entry>();
		float t1;

		for (int i = 0; i < dList.size(); i++) {

			t1 = (float) dList.get(i) / 1024;  //convert o Kilobyte
			//t2 = (float) uList.get(i) / 1024;
			e1.add(new Entry(i, t1));
		}
        List arrayList = new ArrayList();
        LineDataSet lineDataSet = new LineDataSet(e1, this.mContext.getString(R.string.app_name));
        setLineDataAttributes(lineDataSet, this.mColor);
        arrayList.add(lineDataSet);
        return new LineData(arrayList);
    }

    private void setLineDataAttributes(LineDataSet lineDataSet, int i) {
        lineDataSet.setLineWidth(2.0f);
        lineDataSet.setCircleRadius(0f);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setCircleColor(Color.parseColor((a)));
        lineDataSet.setColor(Color.parseColor((a)));
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
		lineDataSet.setCubicIntensity(0.2f);
		lineDataSet.setDrawFilled(true);
		lineDataSet.setDrawValues(false);
		lineDataSet.setFillColor(Color.parseColor("#ffffff"));
		lineDataSet.setFillAlpha(100);
		lineDataSet.setDrawHorizontalHighlightIndicator(false);


    }

    public void start() {
        //VpnStatus.addByteCountListener(this);
		GraphHelper.mHandler.removeCallbacks(triggerRefresh);
		refreshGraph();
		GraphHelper.mHandler.postDelayed( triggerRefresh, (long) 3000);
    }

    public void stop() {
        mHandler.removeCallbacks(this.triggerRefresh);
        //VpnStatus.removeByteCountListener(this);
        this.mLineChart.clear();
        this.mLineChart.invalidate();
    }

	public static String humanReadableByteCount(long bytes, boolean speed, Resources res) {
        if (speed) bytes = bytes * 8;
        int unit = speed ? 1000 : 1024;
        int exp = Math.max(0, Math.min((int) (Math.log(bytes) / Math.log(unit)), 3));
        float bytesUnit = (float) (bytes / Math.pow(unit, exp));
        if (speed) switch (exp) {
				case 0:
					return res.getString(R.string.bits_per_second, bytesUnit);
				case 1:
					return res.getString(R.string.kbits_per_second, bytesUnit);
				case 2:
					return res.getString(R.string.mbits_per_second, bytesUnit);
				default:
					return res.getString(R.string.gbits_per_second, bytesUnit);
			}
        else switch (exp) {
				case 0:
					return res.getString(R.string.volume_byte, bytesUnit);
				case 1:
					return res.getString(R.string.volume_kbyte, bytesUnit);
				case 2:
					return res.getString(R.string.volume_mbyte, bytesUnit);
				default:
					return res.getString(R.string.volume_gbyte, bytesUnit);
			}
    }
}

