package spkmods.build.Graph;

import java.util.*;
import com.github.mikephil.charting.data.*;
import android.content.*;
import spkmods.build.*;
//import net.openvpn.openvpn.Utils.*;

public class DataTrans
{

	private static Thread dataThread;
	private ArrayList<Entry> e1, e2;
	protected static ArrayList<Float> mDownload, mUpload;
    protected List<Long> dList;
    protected List<Long> uList;

	public void reaData(MainActivity h)
	{
		// TODO: Implement this method
		if (!StoredData.isSetData)
		{
			StoredData.setZero();
		}mDownload = new ArrayList<>();
		mUpload = new ArrayList<>();
		dataThread = new Thread(new MyThreadClass());
		dataThread.setName("showDataGraph");
		dataThread.start();
	}

	final class MyThreadClass implements Runnable{
        @Override
        public void run()
		{
            int i = 0;
            synchronized (this)
			{
                while (dataThread.getName() == "showDataGraph")
				{
                    //  Log.e("insidebroadcast", Integer.toString(service_id) + " " + Integer.toString(i));
                    getData2();
                    try
					{
                        wait(1000);
                        i++;
                    }
					catch (InterruptedException e)
					{
						//  sshMsg(e.getMessage());
                    }

                }
                //stopSelf(service_id);
            }

        }
    }
	public void getData1()
	{
        List<Long> allData;
        allData = RetrieveData.findData();
        Long mDownload, mUpload;
		long upload = DataTransferGraph.upload;
		long download = DataTransferGraph.access$1;
        download = allData.get(0);
        upload = allData.get(1);
		storedData2(download, upload);
    }
	public void getData2(){
        List<Long> allData;
        allData = RetrieveData.findData();
		long mDownload = SpkMods.download;
		long mUpload = SpkMods.upload;
		mDownload = allData.get(0);
        mUpload = allData.get(1);
		storedData2(mUpload,mDownload);
    }
	public static void storedData2(Long mDownload, Long mUpload)
	{
        StoredData.downloadSpeed = mDownload;
        StoredData.uploadSpeed = mUpload;
        if (StoredData.isSetData)
		{
            StoredData.downloadList.remove(0);
            StoredData.uploadList.remove(0);
            StoredData.downloadList.add(mDownload);
            StoredData.uploadList.add(mUpload);
        }
    }

}
