package spkmods.build.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.json.JSONObject;
import vpn.minapronet.com.eg.R;

public class PromoAdapter extends ArrayAdapter<JSONObject> {

    private int spinner_id;

    public PromoAdapter(Context context, int spinner_id, ArrayList<JSONObject> list) {
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

			if (spinner_id == R.id.payloadSpinner) {
                getPayloadIcon(position, im, protype, info, pbg);
                info.setText(getItem(position).getString("Info"));
                boolean directType = getItem(position).getBoolean("isDirect");
                boolean httpType =  getItem(position).getBoolean("isHTTP");
                boolean sslType = getItem(position).getBoolean("isSSL");
                boolean sslPayType = getItem(position).getBoolean("isSSLPayload");
                boolean slowDnsType = getItem(position).getBoolean("isSlowDNS");
                if (directType) {
                    protype.setText("DIRECT");
                    pbg.setBackgroundResource(R.drawable.hd);
                } else if (httpType) {
                    protype.setText("INJECT");
                    pbg.setBackgroundResource(R.drawable.hi);
                } else if(sslType) {
                    protype.setText("TLS/SSL");
                    pbg.setBackgroundResource(R.drawable.hl);
                } else if(sslPayType) {
                    protype.setText("WS/SSL");
                    pbg.setBackgroundResource(R.drawable.hw);
                } else if(slowDnsType) {
                    protype.setText("DNS");
                    pbg.setBackgroundResource(R.drawable.hp);
                } else{
                    protype.setText("");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }


    private void getPayloadIcon(int position, ImageView im, TextView protype, TextView info, RelativeLayout pbg ) throws Exception {
        String name = getItem(position).getString("Name").toLowerCase();
        
        if (name.contains("vodafone")) {
            im.setImageResource(R.drawable.ic_vodafone);
        } else if (name.contains("orange")) {
            im.setImageResource(R.drawable.net_orange);
        } else if (name.contains("whats")) {
            im.setImageResource(R.drawable.net_whatsapp);
        }else if(name.contains("facebook")) {
            im.setImageResource(R.drawable.faceb);
        }else if(name.contains("flex")) {
            im.setImageResource(R.drawable.flex);
        }else if(name.contains("etisal")) {
            im.setImageResource(R.drawable.ic_etisalat);
		}else if (name.contains("ads")) {
            im.setImageResource(R.drawable.adsl);
        } else if (name.contains("slowdns")) {
            im.setImageResource(R.drawable.slowdns);
        }else if(name.contains("game")) {
            im.setImageResource(R.drawable.games);
        }else if(name.contains("sound")) {
            im.setImageResource(R.drawable.sound);
        }else if(name.contains("we")) {
            im.setImageResource(R.drawable.we);
        }else if(name.contains("airtel")) {
            im.setImageResource(R.drawable.airtel);
        }else if(name.contains("asiacell")) {
            im.setImageResource(R.drawable.asiacell);
        }else if(name.contains("claro")) {
            im.setImageResource(R.drawable.claro);
        }else if (name.contains("gomo")) {
            im.setImageResource(R.drawable.gomo);
        }else if (name.contains("gtm")) {
            im.setImageResource(R.drawable.gtm);
        }else if (name.contains("globe")) {
            im.setImageResource(R.drawable.globe);
        }else if(name.contains("dhiraagu")) {
            im.setImageResource(R.drawable.dhiraagu);
        }else if(name.contains("digicel")) {
            im.setImageResource(R.drawable.digicel);
        }else if (name.contains("du")) {
            im.setImageResource(R.drawable.du);
        }else if (name.contains("djezzy")) {
            im.setImageResource(R.drawable.djezzy);   
        }else if (name.contains("wifi")) {
            im.setImageResource(R.drawable.wifi);
        }else if (name.contains("imagin")) {
            im.setImageResource(R.drawable.imagin);
        }else if (name.contains("lebara")) {
            im.setImageResource(R.drawable.lebara);
        }else if (name.contains("mobily")) {
            im.setImageResource(R.drawable.mobily);
        }else if (name.contains("oi")) {
            im.setImageResource(R.drawable.oi);
        }else if (name.contains("playstation")) {
            im.setImageResource(R.drawable.playstation);
        }else if (name.contains("dns")) {
            im.setImageResource(R.drawable.dns);
        }else if (name.contains("smart")) {
            im.setImageResource(R.drawable.smart);
        }else if (name.contains("social")) {
            im.setImageResource(R.drawable.social);
        }else if (name.contains("stc")) {
            im.setImageResource(R.drawable.stc);       
		 }else if (name.contains("yaqoot")) {
            im.setImageResource(R.drawable.yaqoot);
        }else if (name.contains("sun")) {
            im.setImageResource(R.drawable.sun);
        }else if (name.contains("tim")) {
            im.setImageResource(R.drawable.tim);
        }else if (name.contains("tmsim")) {
            im.setImageResource(R.drawable.tmsim);
        }else if (name.contains("tnt")) {
            im.setImageResource(R.drawable.tnt);
        } else if (name.contains("voxi")) {
            im.setImageResource(R.drawable.voxi);
        } else if (name.contains("cellcom")) {
            im.setImageResource(R.drawable.cellcom);
        } else if (name.contains("three")) {
            im.setImageResource(R.drawable.three);
        } else if (name.contains("o2")) {
            im.setImageResource(R.drawable.o2);
        } else if (name.contains("tiktok")) {
            im.setImageResource(R.drawable.tiktok);
        } else if (name.contains("iam")) {
            im.setImageResource(R.drawable.iam);
        } else if (name.contains("maroc")) {
            im.setImageResource(R.drawable.maroc);
        } else if (name.contains("tigo")) {
            im.setImageResource(R.drawable.tigo);
        } else if (name.contains("bsnl")) {
            im.setImageResource(R.drawable.bsnl);
        } else if (name.contains("ttcl")) {
            im.setImageResource(R.drawable.ttcl);
        } else if (name.contains("mobilis")) {
            im.setImageResource(R.drawable.mobilis);
        } else if (name.contains("zong")) {
            im.setImageResource(R.drawable.zong);
        } else if (name.contains("turkcell")) {
            im.setImageResource(R.drawable.turkcell);
        } else if (name.contains("giffgaff")) {
            im.setImageResource(R.drawable.giffgaff);
        } else if (name.contains("dito")) {
            im.setImageResource(R.drawable.dito);
        } else if (name.contains("flow")) {
            im.setImageResource(R.drawable.flow);
        } else if (name.contains("mtn")) {
            im.setImageResource(R.drawable.mtn);
        } else if (name.contains("togocel")) {
            im.setImageResource(R.drawable.togocel);
        } else if (name.contains("jazz")) {
            im.setImageResource(R.drawable.jazz);
        }else if (name.contains("vivo")) {
            im.setImageResource(R.drawable.vivo);  
        }else if (name.contains("zain")) {
            im.setImageResource(R.drawable.zain);
        }else if (name.contains("omantel")) {
            im.setImageResource(R.drawable.omantel);
        }else if (name.contains("ooredoo")) {
            im.setImageResource(R.drawable.ooredoo);
        }else if(name.contains("youtube")) {
            im.setImageResource(R.drawable.youtube);
        }else if(name.contains("eeuk")) {
            im.setImageResource(R.drawable.ee);
        }else if(name.contains("")) {
            im.setImageResource(R.drawable.ic_launcher);
    }
  }
}
