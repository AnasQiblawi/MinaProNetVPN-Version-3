package spkmods.build.Graph;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.TrafficStats;
import android.util.AttributeSet;
import android.view.View;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import spkmods.build.*;

public final class SpkMods extends View {

    ShadowBandwidthType internet_type;
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
    private Vector<ShadowBandwidth> samples;
    Timer updateTimer;
    public static long upload;
	public static long download;

    public class ShadowBandwidth {
        long download;
        ShadowBandwidthType internet_type;
        long upload;

        ShadowBandwidth(long download, long upload, ShadowBandwidthType internet_type) {
            this.download = download;
            this.upload = upload;
            this.internet_type = internet_type;
        }
    }

    public enum ShadowBandwidthType {
        VPN_OFF,
        VPN_ON
		}

    public SpkMods(Context context) {
        super(context);
        init(null, 0);
    }

    public SpkMods(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SpkMods(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public synchronized void setInternetType(ShadowBandwidthType type) {
        this.internet_type = type;
    }
    private void init(AttributeSet attrs, int defStyle) {
        this.samples = new Vector();
        this.last_download = -1;
        this.last_upload = -1;
        this.download = 0;
        this.upload = 0;
        this.paint = new Paint(0);
        this.paintDownloadVpnOn = new Paint(0);
        this.paintUploadVpnOn = new Paint(0);
        this.paintDownloadVpnOff = new Paint(0);
        this.paintUploadVpnOff = new Paint(0);
		this.paintDownloadVpnOn.setColor(Color.TRANSPARENT); 
        this.paintUploadVpnOn.setColor(Color.TRANSPARENT); 
        this.paintDownloadVpnOff.setColor(Color.TRANSPARENT); 
        this.paintUploadVpnOff.setColor(Color.TRANSPARENT); 
        this.paintAntiAlias = new Paint(1);
        this.paintHintingOff = new Paint(0);
        this.painterRect = new RectF();
        this.painterPath = new Path();
        this.updateTimer = new Timer();
        this.updateTimer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					synchronized (SpkMods.this) {
						long current_download = TrafficStats.getTotalRxBytes();
						long current_upload = TrafficStats.getTotalTxBytes();
						if (current_download < 0) {
							current_download = 0;
						}
						if (current_upload < 0) {
							current_upload = 0;
						}
						if (SpkMods.this.last_download < 0) {
							SpkMods.this.last_download = current_download;
						}
						if (SpkMods.this.last_upload < 0) {
							SpkMods.this.last_upload = current_upload;
						}
						SpkMods.this.download = current_download - SpkMods.this.last_download;
						SpkMods.this.upload = current_upload - SpkMods.this.last_upload;
						if (SpkMods.this.download < 0) {
							SpkMods.this.download = 0;
						}
						if (SpkMods.this.upload < 0) {
							SpkMods.this.upload = 0;
						}
						SpkMods.this.samples.add(0, new ShadowBandwidth(SpkMods.this.download, SpkMods.this.upload, SpkMods.this.internet_type));
						while (SpkMods.this.samples.size() > 100) {
							SpkMods.this.samples.remove(SpkMods.this.samples.size() - 1);
						}
						SpkMods.this.last_download = current_download;
						SpkMods.this.last_upload = current_upload;
					}
					((Activity) SpkMods.this.getContext()).runOnUiThread(new Runnable() {
							public void run() {
								SpkMods.this.invalidate();
							}
						});
				}
			}, 1000, 1000);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float scale = getResources().getDisplayMetrics().density;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int contentWidth = (getWidth() - paddingLeft) - getPaddingRight();
        int contentHeight = (getHeight() - paddingTop) - getPaddingBottom();
        if (!(this.mBackground != null && this.mBackground.getWidth() == contentWidth && this.mBackground.getHeight() == contentHeight)) {
            this.mBackground = generateBackground(contentWidth, contentHeight);
        }
        canvas.drawBitmap(this.mBackground, (float) paddingLeft, (float) paddingTop, this.paint);
        long yScale = setYScale();
        int no_samples_to_draw = Math.min(this.samples.size(), (int) Math.ceil((double) ((((float) contentWidth) / scale) / 2.0f)));
        for (int i = 0; i < no_samples_to_draw; i++) {
            ShadowBandwidth sample = (ShadowBandwidth) this.samples.elementAt(i);
            this.painterRect.right = ((float) (paddingLeft + contentWidth)) - ((((float) i) * scale) * 3.6f);
            this.painterRect.left = this.painterRect.right - (1.0f * scale);
            this.painterRect.bottom = (float) (paddingTop + contentHeight);
            this.painterRect.top = this.painterRect.bottom - ((float) ((((long) contentHeight) * sample.download) / yScale));
            if (sample.internet_type == ShadowBandwidthType.VPN_ON) {
                canvas.drawRect(this.painterRect, this.paintDownloadVpnOn);
            } else {
                canvas.drawRect(this.painterRect, this.paintDownloadVpnOff);
            }
            RectF rectF = this.painterRect;
            rectF.left -= (float) ((int) (1.0f * scale));
            rectF = this.painterRect;
            rectF.right -= (float) ((int) (1.0f * scale));
            this.painterRect.top = this.painterRect.bottom - ((float) ((((long) contentHeight) * sample.upload) / yScale));
            if (sample.internet_type == ShadowBandwidthType.VPN_ON) {
                canvas.drawRect(this.painterRect, this.paintUploadVpnOn);
            } else {
                canvas.drawRect(this.painterRect, this.paintUploadVpnOff);
            }
        }
        if (this.internet_type == ShadowBandwidthType.VPN_ON) {
			this.paintAntiAlias.setColor(Color.TRANSPARENT);
        } else {
            this.paintAntiAlias.setColor(Color.TRANSPARENT);
        }
        int x = paddingLeft + ((int) (((double) contentWidth) * 0.4d));
        this.painterPath.reset();
        this.painterPath.moveTo((float) x, (float) ((int) (6.0f * scale)));
        this.painterPath.lineTo((float) (((int) (6.0f * scale)) + x), (float) ((int) (12.0f * scale)));
        this.painterPath.lineTo((float) (((int) (3.0f * scale)) + x), (float) ((int) (12.0f * scale)));
        this.painterPath.lineTo((float) (((int) (3.0f * scale)) + x), (float) ((int) (20.0f * scale)));
        this.painterPath.lineTo((float) (x - ((int) (3.0f * scale))), (float) ((int) (20.0f * scale)));
        this.painterPath.lineTo((float) (x - ((int) (3.0f * scale))), (float) ((int) (12.0f * scale)));
        this.painterPath.lineTo((float) (x - ((int) (6.0f * scale))), (float) ((int) (12.0f * scale)));
        this.painterPath.close();
        canvas.drawPath(this.painterPath, this.paintAntiAlias);
        this.paintHintingOff.setTextSize(12.0f * scale);
        if (this.internet_type == ShadowBandwidthType.VPN_ON) {
            this.paintAntiAlias.setColor(Color.TRANSPARENT);
        } else {
            this.paintAntiAlias.setColor(Color.TRANSPARENT);
        }
        x = paddingLeft + ((int) (((double) contentWidth) * 0.7d));
        this.painterPath.reset();
        this.painterPath.moveTo((float) x, (float) ((int) (20.0f * scale)));
        this.painterPath.lineTo((float) (((int) (6.0f * scale)) + x), (float) ((int) (12.0f * scale)));
        this.painterPath.lineTo((float) (((int) (3.0f * scale)) + x), (float) ((int) (12.0f * scale)));
        this.painterPath.lineTo((float) (((int) (3.0f * scale)) + x), (float) ((int) (6.0f * scale)));
        this.painterPath.lineTo((float) (x - ((int) (3.0f * scale))), (float) ((int) (6.0f * scale)));
        this.painterPath.lineTo((float) (x - ((int) (3.0f * scale))), (float) ((int) (12.0f * scale)));
        this.painterPath.lineTo((float) (x - ((int) (6.0f * scale))), (float) ((int) (12.0f * scale)));
        this.painterPath.close();
        canvas.drawPath(this.painterPath, this.paintAntiAlias);
        this.paintHintingOff.setTextSize(12.0f * scale);
        Canvas canvas2 = canvas;
	}



    private long setYScale() {
        long maxSample = 0;
        for (int c = 0; c < this.samples.size(); c++) {
            ShadowBandwidth sample = (ShadowBandwidth) this.samples.elementAt(c);
            if (sample.download > maxSample) {
                maxSample = sample.download;
            }
            if (sample.upload > maxSample) {
                maxSample = sample.upload;
            }
        }
        if (maxSample > 500000000) {
            return 10000000000L;
        }
        if (maxSample > 200000000) {
            return 500000000;
        }
        if (maxSample > 100000000) {
            return 200000000;
        }
        if (maxSample > 50000000) {
            return 100000000;
        }
        if (maxSample > 20000000) {
            return 50000000;
        }
        if (maxSample > 10000000) {
            return 20000000;
        }
        if (maxSample > 5000000) {
            return 10000000;
        }
        if (maxSample > 2000000) {
            return 5000000;
        }
        if (maxSample > 1000000) {
            return 2000000;
        }
        if (maxSample > 500000) {
            return 1000000;
        }
        if (maxSample > 200000) {
            return 500000;
        }
        if (maxSample > 100000) {
            return 200000;
        }
        return 100000;
    }
	/*
	 private String bytesToBitsString(long bytes) {
	 long bits = bytes * 10;
	 if (bits > 100000000) {
	 return String.format("%.2f", new Object[]{Double.valueOf(((double) bits) / 1.0E9d)}) + "Gbit";
	 } else if (bits > 100000) {
	 return String.format("%.2f", new Object[]{Double.valueOf(((double) bits) / 1000000.0d)}) + "Mbit";
	 } else if (bits > 100) {
	 return String.format("%.2f", new Object[]{Double.valueOf(((double) bits) / 1000.0d)}) + "Kbit";
	 } else {
	 return String.format("%.2f", new Object[]{Double.valueOf((double) bits)}) + "bits";
	 }
	 }*/

    private Bitmap generateBackground(int width, int height) {
        Bitmap ret = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(ret);
        float scale = getResources().getDisplayMetrics().density;
        this.paint.setColor(Color.TRANSPARENT);
        this.paint.setStrokeWidth(0.5f * scale);
        this.paint.setStrokeCap(Cap.ROUND);
        for (int y = height - 1; y >= 0; y -= (int) (10.0f * scale)) {
            canvas.drawLine(((float) height) % (10.0f * scale), (float) y, (float) width, (float) y, this.paint);
        }
        for (int x = width - 1; x >= 0; x -= (int) (10.0f * scale)) {
            canvas.drawLine((float) x, ((float) width) % (10.0f * scale), (float) x, (float) height, this.paint);
        }
        return ret;
    }
}

