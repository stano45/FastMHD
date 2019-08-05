package com.stanley.fastmhd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

class ZoznamSpojovAdapter extends ArrayAdapter<Spoj> {
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    ZoznamSpojovAdapter(Context context, int resource, ArrayList<Spoj> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final View result;

        ViewHolder holder = new ViewHolder();

        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);


            result = convertView;

            holder.c1 = convertView.findViewById(R.id.cas1);
            holder.c2 = convertView.findViewById(R.id.cas2);
            holder.c3 = convertView.findViewById(R.id.cas3);
            holder.c4 = convertView.findViewById(R.id.cas4);
            holder.l1 = convertView.findViewById(R.id.linka1);
            holder.l2 = convertView.findViewById(R.id.linka2);
            holder.z1 = convertView.findViewById(R.id.zastavka1);
            holder.z2 = convertView.findViewById(R.id.zastavka2);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;



        if (getItem(position).value != 1000) {
            String vychodzia = getItem(position).vychodzia.meno;
            String konecna = getItem(position).konecna.meno;
            String cas1 = getItem(position).casSpoja.get(0).hodina + ":" +
                    getItem(position).casSpoja.get(0).minuta;
            String cas2;
            int minuta = getItem(position).casSpoja.get(0).minuta + getItem(position).
                    casSpoja.get(0).casVSpoji;
            int hodina = getItem(position).casSpoja.get(0).hodina;
            if (minuta >= 60) {
                minuta = minuta % 60;
                hodina += 1;
            }
            if (minuta < 10) {
                cas2 = hodina + ":0" + minuta;
            } else {
                cas2 = hodina + ":" + minuta;
            }
            String linka1 = getItem(position).linky.get(0) + "\n\n" + vychodzia;

            holder.c1.setText(cas1);
            holder.c2.setText(cas2);
            holder.l1.setText(linka1);

            if (getItem(position).priamySpoj) {
                holder.z1.setText(konecna);
                holder.c3.setVisibility(View.INVISIBLE);
                holder.c4.setVisibility(View.INVISIBLE);
                holder.l2.setVisibility(View.INVISIBLE);
                holder.z2.setVisibility(View.INVISIBLE);


                return convertView;

            } else {
                String cas3 = getItem(position).casSpoja.get(1).hodina + ":" +
                        getItem(position).casSpoja.get(1).minuta;
                String prestup = getItem(position).prestup.meno;
                String linka2 = getItem(position).linky.get(1) + "\n\n" + prestup;
                String cas4;
                minuta = getItem(position).casSpoja.get(1).minuta + getItem(position).
                        casSpoja.get(1).casVSpoji;
                hodina = getItem(position).casSpoja.get(1).hodina;
                if (minuta >= 60) {
                    minuta = minuta % 60;
                    hodina += 1;
                }
                if (minuta < 10) {
                    cas4 = hodina + ":0" + minuta;
                } else {
                    cas4 = hodina + ":" + minuta;
                }
                holder.z1.setText(prestup);
                holder.c3.setText(cas3);
                holder.c4.setText(cas4);
                holder.z2.setText(konecna);
                holder.l2.setText(linka2);
                return convertView;

            }
        }


        return convertView;
    }

    private static class ViewHolder {
        TextView c1;
        TextView c2;
        TextView c3;
        TextView c4;
        TextView l1;
        TextView l2;
        TextView z1;
        TextView z2;
    }
}
