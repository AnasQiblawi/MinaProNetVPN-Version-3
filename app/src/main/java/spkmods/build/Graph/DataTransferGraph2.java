package spkmods.build.Graph;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.Bitmap.*;
import android.graphics.Paint.*;
import android.os.*;
import android.util.*;
import android.view.*;
import spkmods.build.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.http.conn.util.*;
import vpn.minapronet.com.eg.R;
import android.graphics.Bitmap.Config;
import android.net.*;
public class DataTransferGraph2 extends View
{
    public static long download;
    BandwidthSampleType internet_type;
    private long last_download;
    private long last_upload;
    Bitmap mBackground;
    private Paint paint;
    private Paint paintAntiAlias;
    private Paint paintDownloadVpnOff;
    private Paint paintDownloadVpnOn;
    private Paint paintHintingOff;
    private Paint paintUploadVpnOff;
    private Paint paintUploadVpnOn;
    private Path painterPath;
    private RectF painterRect;
    private Vector<BandwidthSample> samples;
    Timer updateTimer;
    public static long upload;
	private static String myip;
	private long mLastTime;
	private static int colorText = 0xffffffff;

	private static int colorDown = 0xff29B6F6;

	private static int colorUp = 0xff29B6F6;
	private String trafficdata;

	private Context mActivity;
	public static long access$1 = 0;


    public static class BandwidthSample
	{
        long download;
        BandwidthSampleType internet_type;
        long upload;

        BandwidthSample(long j, long j2, BandwidthSampleType bandwidthSampleType)
		{
            download = j;
            upload = j2;
            internet_type = bandwidthSampleType;
        }
    }

	public enum BandwidthSampleType
	{
        VPN_OFF,
        VPN_ON
		}

	public static class GraphData
	{

		private static long xdown = 0;

		private static long xup = 0;
		private static long m_totalBytesSent;

        private static long m_totalOverheadBytesSent;
        private static long m_totalBytesReceived;

        private static long m_totalOverheadBytesReceived;

		public GraphData()
		{}
		public GraphData(long j, long j2)
		{
			xdown = j;
			xup = j2;
		}

		public void setDown(int bytes, int overheadBytes)
		{
			this.m_totalBytesReceived += bytes;
			this.m_totalOverheadBytesReceived += overheadBytes;
		}

		public void setUp(int bytes, int overheadBytes)
		{
			this.m_totalBytesSent += bytes;
			this.m_totalOverheadBytesSent += overheadBytes;
		}

		public static long xdown()
		{
			return m_totalBytesReceived;
		}

		public static long xup()
		{
			return m_totalBytesSent;
		}

		public static synchronized long getTotalOverheadBytesSnd()
        {
            return m_totalOverheadBytesSent;
        }


		public static synchronized long getTotalOverheadBytesRcv()
        {
            return m_totalOverheadBytesReceived;
        }

	}
    public DataTransferGraph2(Context context)
	{
        super(context);
        init((AttributeSet) null, 0);
    }

    public DataTransferGraph2(Context context, AttributeSet attributeSet)
	{
        super(context, attributeSet);
		TypedArray attr = context.getTheme().obtainStyledAttributes(
			attributeSet,
			R.styleable.DataGraph,
			0, 0
        );
		try
		{
			colorDown = attr.getColor(R.styleable.DataGraph_dg_colorDown, 0xff29B6F6);
			colorUp = attr.getColor(R.styleable.DataGraph_dg_colorUp, 0xff29B6F6);
			colorText = attr.getColor(R.styleable.DataGraph_dg_colorText, 0xffffffff);
        }finally{
            // release the TypedArray so that it can be reused.
            attr.recycle();
        }
        init(attributeSet, 0);
    }

    public DataTransferGraph2(Context context, AttributeSet attributeSet, int i)
	{
        super(context, attributeSet, i);
		mActivity = context;
		TypedArray attr = context.getTheme().obtainStyledAttributes(
			attributeSet,
			R.styleable.DataGraph,
			0, 0
        );
		try
		{
			colorDown = attr.getColor(R.styleable.DataGraph_dg_colorDown, 0xff29B6F6);
			colorUp = attr.getColor(R.styleable.DataGraph_dg_colorUp, 0xff29B6F6);
			colorText = attr.getColor(R.styleable.DataGraph_dg_colorText, 0xffffffff);
        }finally{
            attr.recycle();
        }
		init(attributeSet, i);
    }

	public DataTransferGraph2 setColorDown(int color)
	{
		colorDown = color;
		return this;
	}

	public DataTransferGraph2 setColorUp(int color)
	{
		colorUp = color;
		return this;
	}

	public DataTransferGraph2 setColorText(int color)
	{
		colorText = color;
		return this;
	}

    public synchronized void setInternetType(BandwidthSampleType bandwidthSampleType)
	{
        synchronized (this)
		{
            internet_type = bandwidthSampleType;
        }
    }

    private void init(AttributeSet attrs, int defStyle)
	{
        samples = new Vector();
        last_download = -1;
        last_upload = -1;
        paint = new Paint(0);
        paintDownloadVpnOn = new Paint(0);
        paintUploadVpnOn = new Paint(0);
        paintDownloadVpnOff = new Paint(0);
        paintUploadVpnOff = new Paint(0);
        paintDownloadVpnOn.setColor(colorDown);
       	paintUploadVpnOn.setColor(colorUp);
        paintDownloadVpnOff.setColor(colorDown);
        paintUploadVpnOff.setColor(colorUp);
        paintAntiAlias = new Paint(1);
        paintHintingOff = new Paint(0);
        painterRect = new RectF();
        painterPath = new Path();
        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new TimerTask() {

				public void run()
				{
					synchronized (this)
					{
						long totalRxBytes = GraphData.xdown() + GraphData.getTotalOverheadBytesRcv() + GraphData.xup + GraphData.getTotalOverheadBytesSnd();
						access$1 = totalRxBytes - mLastTime;
						trafficdata = Count(access$1, true);
						mLastTime = totalRxBytes;
						long current_download = GraphData.xdown() + GraphData.getTotalOverheadBytesRcv();
						long current_upload = GraphData.xup + GraphData.getTotalOverheadBytesSnd();
						if (current_download < 0){
							current_download = 0;
						}
						if (current_upload < 0){
							current_upload = 0;
						}
						if (last_download < 0){
							last_download = current_download;
						}
						if (last_upload < 0){
							last_upload = current_upload;
						}
						download = current_download - last_download;
						upload = current_upload - last_upload;
						if (download < 0){
							download = 0;
						}
						if (upload < 0){
							upload = 0;
						}
						samples.add(0, new BandwidthSample(download, upload, internet_type));
						while (samples.size() > 110)
						{
							samples.remove(samples.size() - 1);
						}
						last_download = current_download;
						last_upload = current_upload;
					}
					((Activity) getContext()).runOnUiThread(new Runnable() {
							public void run()
							{
								invalidate();
							}
						});
				}
			}, 500, 500);
    }



    protected void onDraw(Canvas canvas)
	{
        int i;
        super.onDraw(canvas);
        float f = getResources().getDisplayMetrics().density;
        int paddingLeft = getPaddingLeft() + 0;
        int paddingTop = getPaddingTop() + 50;
        int width = (getWidth() - paddingLeft) - getPaddingRight();
        int height = (getHeight() - paddingTop) - getPaddingBottom();
        if (!(mBackground != null && mBackground.getWidth() == width && mBackground.getHeight() == height))
		{
			mBackground = generateBackground(paddingLeft + width, height);
        }
        canvas.drawBitmap(mBackground, (float) paddingLeft, (float) paddingTop, paint);
        long yScale = setYScale();
        int min = Math.min(samples.size(), (int) Math.ceil((double) ((((float) width) / f) / 2.0f)));
        for (i = 0; i < min; i++)
		{
            BandwidthSample bandwidthSample = (BandwidthSample) samples.elementAt(i);
            painterRect.right = ((float) (paddingLeft + width)) - ((((float) i) * f) * 2.0f);
            painterRect.left = painterRect.right - (3.0f * f);
            painterRect.bottom = (float) (paddingTop + height);
            painterRect.top = painterRect.bottom - ((float) ((((long) height) * bandwidthSample.download) / yScale));
            if (bandwidthSample.internet_type == BandwidthSampleType.VPN_ON)
			{
                canvas.drawRect(painterRect, paintDownloadVpnOn);
            }else{
                canvas.drawRect(painterRect, paintDownloadVpnOff);
            }
            RectF rectF = painterRect;
            rectF.left -= (float) ((int) (3.0f * f));
            rectF = painterRect;
            rectF.right -= (float) ((int) (3.0f * f));
            painterRect.top = painterRect.bottom - ((float) ((((long) height) * bandwidthSample.upload) / yScale));
            if (bandwidthSample.internet_type == BandwidthSampleType.VPN_ON)
			{
                canvas.drawRect(painterRect, paintUploadVpnOn);
            }else{
                canvas.drawRect(painterRect, paintUploadVpnOff);
            }
        }
		/*paintHintingOff.setColor(colorText);
        paintAntiAlias.setColor(colorDown);
        i = paddingLeft + ((int) (((double) width) * 0.4d));
        painterPath.reset();
        painterPath.moveTo((float) i, (float) ((int) (6.0f * f)));
        painterPath.lineTo((float) (((int) (6.0f * f)) + i), (float) ((int) (12.0f * f)));
        painterPath.lineTo((float) (((int) (3.0f * f)) + i), (float) ((int) (12.0f * f)));
        painterPath.lineTo((float) (((int) (3.0f * f)) + i), (float) ((int) (20.0f * f)));
        painterPath.lineTo((float) (i - ((int) (3.0f * f))), (float) ((int) (20.0f * f)));
        painterPath.lineTo((float) (i - ((int) (3.0f * f))), (float) ((int) (12.0f * f)));
        painterPath.lineTo((float) (i - ((int) (6.0f * f))), (float) ((int) (12.0f * f)));
        painterPath.close();
        canvas.drawPath(painterPath, paintAntiAlias);
        paintHintingOff.setTextSize(12.0f * f);
		canvas.drawText(Count(upload, false), (float) (((int) (10.0f * f)) + i), (float) ((int) (20.0f * f)), paintHintingOff);

		paintAntiAlias.setColor(colorUp);
        i = paddingLeft + ((int) (((double) width) * 0.7d));
        painterPath.reset();
        painterPath.moveTo((float) i, (float) ((int) (20.0f * f)));
        painterPath.lineTo((float) (((int) (6.0f * f)) + i), (float) ((int) (12.0f * f)));
        painterPath.lineTo((float) (((int) (3.0f * f)) + i), (float) ((int) (12.0f * f)));
        painterPath.lineTo((float) (((int) (3.0f * f)) + i), (float) ((int) (6.0f * f)));
        painterPath.lineTo((float) (i - ((int) (3.0f * f))), (float) ((int) (6.0f * f)));
        painterPath.lineTo((float) (i - ((int) (3.0f * f))), (float) ((int) (12.0f * f)));
        painterPath.lineTo((float) (i - ((int) (6.0f * f))), (float) ((int) (12.0f * f)));
        painterPath.close();
        canvas.drawPath(painterPath, paintAntiAlias);
        paintHintingOff.setTextSize(12.0f * f);
        canvas.drawText(Count(download, false), (float) (((int) (10.0f * f)) + i), (float) ((int) (20.0f * f)), paintHintingOff);*/
	}
	public static String Count(long bytes, boolean si){
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "KMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    private long setYScale()
	{
        long j = 0;
        for (int i = 0; i < samples.size(); i++)
		{
            BandwidthSample bandwidthSample = (BandwidthSample) samples.elementAt(i);
            if (bandwidthSample.download > j)
			{
                j = bandwidthSample.download;
            }
            if (bandwidthSample.upload > j)
			{
                j = bandwidthSample.upload;
            }
        }
        if (j > ((long) 500000000))
		{
            return 10000000000L;
        }
        if (j > ((long) 200000000))
		{
            return (long) 500000000;
        }
        if (j > ((long) 100000000))
		{
            return (long) 200000000;
        }
        if (j > ((long) 50000000))
		{
            return (long) 100000000;
        }
        if (j > ((long) 20000000))
		{
            return (long) 50000000;
        }
        if (j > ((long) 10000000))
		{
            return (long) 20000000;
        }
        if (j > ((long) 5000000))
		{
            return (long) 10000000;
        }
        if (j > ((long) 2000000))
		{
            return (long) 5000000;
        }
        if (j > ((long) 1000000))
		{
            return (long) 2000000;
        }
        if (j > ((long) 500000))
		{
            return (long) 1000000;
        }
        if (j > ((long) 200000))
		{
            return (long) 500000;
        }
        if (j > ((long) 100000))
		{
            return (long) 200000;
        }
        return (long) 100000;
    }

    private Bitmap generateBackground(int width, int height)
	{
        Bitmap ret = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        float scale = getResources().getDisplayMetrics().density;
        this.paint.setColor(Color.BLACK);
        this.paint.setStrokeWidth(1.0f * scale);
        this.paint.setStrokeCap(Cap.ROUND);      
        return ret;
    }

	public Bitmap a(int width, int height, String text)
	{
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect();
        paint.setStyle(Style.FILL);
        paint.setAlpha(50);
        canvas.drawPaint(paint);
        paint.setColor(Color.parseColor("#FF0F00"));
        canvas.setBitmap(createBitmap);
        paint.setTextSize(Build.VERSION.SDK_INT <= 11 ? 12.0f : 30.0f);
        paint.setTextAlign(Align.CENTER);
        paint.getTextBounds(text, 0, text.length(), rect);
        canvas.drawText(text, (float) (width / 2), (float) (height / 2), paint);
        canvas.drawBitmap(createBitmap, 0.0f, 0.0f, (Paint) null);
        canvas.save();
        canvas.restore();
        return  createBitmap;
    }

}
