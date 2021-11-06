package ai.fitme.ayahupgrade.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.AdvertisingBean;

public class AdvertisingSettingAdapter extends RecyclerView.Adapter<AdvertisingSettingAdapter.MyViewHolder> {

    private Context mContext;
    private List<AdvertisingBean> advertisingBeanList;
    private String floor;
    private String adTTsContent;
    private AdSettingListener listener;

    public AdvertisingSettingAdapter(List<AdvertisingBean> advertisingBeanList,AdSettingListener listener) {
        this.advertisingBeanList = advertisingBeanList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.ad_setting_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        floor = advertisingBeanList.get(position).getFloor();
        adTTsContent = advertisingBeanList.get(position).getAdContent();
        holder.tvFloor.setText(floor);
        holder.tvAdContent.setText(adTTsContent);
        if (advertisingBeanList.get(position).isCanListen()){
            holder.ivPlay.setImageResource(R.mipmap.play_checked);
        }else {
            holder.ivPlay.setImageResource(R.mipmap.play_onchecked);
        }

        holder.tvAdContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出设置广告tts dialog
                listener.onItemClickListener(position);
            }
        });
        holder.ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除该item
                listener.deleteItem(position);
            }
        });
        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放音频试听
                listener.audition(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return advertisingBeanList.size();
    }

    public void notifyData(List<AdvertisingBean> advertisingBeans){
        this.advertisingBeanList = advertisingBeans;
        notifyDataSetChanged();
    }

    public void addItem(List<AdvertisingBean> advertisingBeans){
        this.advertisingBeanList = advertisingBeans;
        notifyItemInserted(advertisingBeanList.size());
    }

    public void removeItem(int position){
        advertisingBeanList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,getItemCount());
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tvFloor;
        private TextView tvAdContent;
        private ImageView ivPlay;
        private ImageButton ibClose;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFloor = itemView.findViewById(R.id.tv_floor);
            tvAdContent = itemView.findViewById(R.id.tv_ad_content);
            ivPlay = itemView.findViewById(R.id.bt_play_sound);
            ibClose = itemView.findViewById(R.id.ib_close);
        }
    }

    public interface AdSettingListener{
        void onItemClickListener(int i);
        void deleteItem(int position);
        void audition(int position);
    }
}
