package spkmods.build.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.InputStream;
import java.util.ArrayList;
import org.json.JSONObject;
import vpn.minapronet.com.eg.R;

public class SpinnerAdapter extends ArrayAdapter<JSONObject> {

    private int spinner_id;

    public SpinnerAdapter(Context context, int spinner_id, ArrayList<JSONObject> list) {
        super(context, R.layout.spinner_item, list);
        this.spinner_id = spinner_id;
    }

    @Override
    public JSONObject getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return view(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return view(position, convertView, parent);
    }

    private View view(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
        RelativeLayout pbg = (RelativeLayout)v.findViewById(R.id.pbg);
        TextView tv = v.findViewById(R.id.itemName);
        TextView protype = v.findViewById(R.id.category1_txt);
        TextView info = v.findViewById(R.id.spkdevs);
        ImageView im = v.findViewById(R.id.itemImage);
        try {
            String name = getItem(position).getString("Name");
            tv. setText(name);
            boolean isPrem = getItem(position).getBoolean("isPremium");
            boolean isVip =  getItem(position).getBoolean("isVIP");
            boolean isPriv = getItem(position).getBoolean("isPrivate");
          //  String result = getItem(position).getString("Name").toLowerCase();
            if(isPrem){
                protype.setText("Premium");
                pbg.setBackgroundResource(R.drawable.hf);
            }else if(isVip){
                protype.setText("VIP");
                pbg.setBackgroundResource(R.drawable.hv);
            }else if(isPriv){
                protype.setText("Private");
                pbg.setBackgroundResource(R.drawable.hp);

            }

            if (spinner_id == R.id.serverSpinner) {
                getServerIcon(position, im, info);
				info.setText(getItem(position).getString("sInfo"));      

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    private void getServerIcon(int position, ImageView im, TextView info) throws Exception {
        InputStream inputStream = getContext().getAssets().open("flags/" + getItem(position).getString("FLAG"));
        im.setImageDrawable(Drawable.createFromStream(inputStream, getItem(position).getString("FLAG")));
        if (inputStream != null) {
            inputStream.close();
        }
    }
}

