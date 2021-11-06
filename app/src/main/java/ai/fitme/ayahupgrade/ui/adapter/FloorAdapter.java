package ai.fitme.ayahupgrade.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ai.fitme.ayahupgrade.R;


public class FloorAdapter extends RecyclerView.Adapter<FloorAdapter.MyViewHolder>{

  private Context mContext;
  private FloorDataChangeListener listener;
  private List<String> floorList;
  private GridLayoutManager gridLayoutManager;
  private Context context;


  public FloorAdapter(Context context,List<String> floorList, FloorDataChangeListener listener, GridLayoutManager gridLayoutManager) {
    this.context = context;
    this.floorList = floorList;
    this.listener = listener;
    this.gridLayoutManager = gridLayoutManager;
  }


  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    mContext = viewGroup.getContext();
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_floor_selecter,viewGroup,false);
    return new MyViewHolder(view);
  }


  @Override
  public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

    //设置item长宽一致
    ViewGroup.LayoutParams params = myViewHolder.itemView.getLayoutParams();
    params.height = gridLayoutManager.getWidth()/ gridLayoutManager.getSpanCount()
            - 2*myViewHolder.itemView.getPaddingLeft() - 2*((ViewGroup.MarginLayoutParams)params).leftMargin;

    myViewHolder.tvItem.setText(""+(i+1));
    String floorVal = floorList.get(i);
    if ("".equals(floorVal)){
      myViewHolder.tvFloor.setVisibility(View.GONE);
      myViewHolder.llItemBg.setBackgroundResource(R.drawable.floor_button_shape);
      myViewHolder.tvItem.setTextColor(context.getResources().getColor(R.color.colorBlack));
      myViewHolder.tvFloor.setTextColor(context.getResources().getColor(R.color.colorBlack));
    }else {
      //选中的item
      myViewHolder.tvFloor.setVisibility(View.VISIBLE);
      myViewHolder.llItemBg.setBackgroundResource(R.drawable.floor_button_shape_selected);
      myViewHolder.tvItem.setTextColor(context.getResources().getColor(R.color.colorWhite));
      myViewHolder.tvFloor.setTextColor(context.getResources().getColor(R.color.colorWhite));
    }
    myViewHolder.tvFloor.setText(floorVal);
    myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        listener.onItemClick(i);
      }
    });


  }

  @Override
  public int getItemCount() {
    return floorList.size();
  }


  static class MyViewHolder extends RecyclerView.ViewHolder{
    private TextView tvItem;
    private TextView tvFloor;
    private LinearLayout llItemBg;

    MyViewHolder(View itemView) {
      super(itemView);
      tvItem = itemView.findViewById(R.id.tv_item_num);
      tvFloor = itemView.findViewById(R.id.tv_floor_num);
      llItemBg = itemView.findViewById(R.id.ll_item_bg);
    }
  }

  public void notifyData(List<String> floorList){
    this.floorList = floorList;
    notifyDataSetChanged();
  }


  public interface FloorDataChangeListener{
    void onItemClick(int position);
  }
}
