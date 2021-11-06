package ai.fitme.ayahupgrade.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.ui.adapter.FloorAdapter;
import ai.fitme.ayahupgrade.ui.fragment.imp.OnFragmentListener;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.SharedPreferencesUtils;

import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.CLOSEDOOR_SERIAL;
import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.DATA_FLOOR;
import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.FLOOR;
import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.OPENDOOR_SERIAL;

public class FloorConfigFragment extends Fragment {

    private OnFragmentListener listener;
    private Button btNext;
    private TextView tvBlank;
    private RecyclerView rvFloor;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private List<String> floorList;
    private FloorAdapter adapter;
    private final String[] items = new String[]{"开门","关门","-9楼","-8楼","-7楼","-6楼","-5楼","-4楼","-3楼",
            "-2楼", "-1楼","1楼","2楼","3楼","4楼","5楼","6楼","7楼","8楼","9楼","10楼","11楼","12楼",
            "13楼","14楼","15楼","16楼","17楼","18楼","19楼","20楼","21楼","22楼","23楼","24楼","25楼",
            "26楼","27楼","28楼","29楼","30楼","31楼","32楼","33楼","34楼","35楼","36楼","37楼","38楼","39楼","40楼"
            ,"41楼","42楼","43楼","44楼","45楼","46楼","47楼","48楼","49楼","50楼","51楼","52楼","53楼","54楼"
            ,"55楼","56楼","57楼","58楼","59楼","60楼","61楼","62楼","63楼","64楼"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_floor_config,container,false);
        initData();
        initView(v);
        L.i("FloorConfigFragment onCreateView");
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnFragmentListener) context;
        L.i("FloorConfigFragment onAttach");
    }

    private void initView(View v){
        rvFloor = v.findViewById(R.id.rv_floor);
        tvBlank = v.findViewById(R.id.tv_blank);
        btNext = v.findViewById(R.id.bt_next);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNext(2);
                syncFloorData();
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 4);
        rvFloor.setLayoutManager(gridLayoutManager);
        adapter = new FloorAdapter(getActivity(),floorList,floorDataChangeListener, gridLayoutManager);
        rvFloor.setAdapter(adapter);
        try {
            initOriginalData();
        }catch (Exception e){
            L.i("版本不对应 e："+e.toString());
        }
    }

    private void initData(){
        floorList = new ArrayList<>();
        for (int i=0;i<64;i++){
            floorList.add("");
        }
    }

    //载入板子上原数据
    private void initOriginalData(){
        String dataFloor = SharedPreferencesUtils.getInstance(getContext().getApplicationContext()).getStringValueByKey(DATA_FLOOR);
        String floor = SharedPreferencesUtils.getInstance(getContext().getApplicationContext()).getStringValueByKey(FLOOR);
        L.i("dataFloor:"+dataFloor);
        L.i("floor:"+floor);
        try {
            JSONArray dataFloorArr = new JSONArray(dataFloor);
            JSONArray floorArr = new JSONArray(floor);
            for (int i=0;i<dataFloorArr.length();i++){
                floorList.set(dataFloorArr.getInt(i)-1,floorArr.getString(i)+"楼");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //TODO add开关门信号引脚
        if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_OPTICAL || MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_RELAY){
            //光耦合版本 || 继电器版本
            String opendoor_serial = SharedPreferencesUtils.getInstance(getActivity().getApplicationContext()).getStringValueByKey(OPENDOOR_SERIAL);
            String closedoor_serial = SharedPreferencesUtils.getInstance(getActivity().getApplicationContext()).getStringValueByKey(CLOSEDOOR_SERIAL);
            L.i("opendoor_serial:"+opendoor_serial);
            L.i("closedoor_serial:"+closedoor_serial);
            if (null!=opendoor_serial && !"".equals(opendoor_serial)){
                floorList.set(Integer.parseInt(opendoor_serial.substring(2,4))-1,"开门");
            }
            if (null!=closedoor_serial && !"".equals(closedoor_serial)){
                floorList.set(Integer.parseInt(closedoor_serial.substring(2,4))-1,"关门");
            }
        }else{
            //TODO 三星版本开关门

        }

        adapter.notifyData(floorList);
        syncFloorData();
    }

    private FloorAdapter.FloorDataChangeListener floorDataChangeListener = new FloorAdapter.FloorDataChangeListener() {
        @Override
        public void onItemClick(int position) {
            if ("".equals(floorList.get(position))){
                showMenuDialog(position);
            }else {
                //如果已经选中，则取消选中
                floorList.set(position,"");
                adapter.notifyData(floorList);
                syncFloorData();
            }
        }
    };

    private void showMenuDialog(int position) {
        //防止多次点击
        tvBlank.setVisibility(View.VISIBLE);
        tvBlank.setFocusable(true);
        tvBlank.setClickable(true);

        QMUIDialog qmuiDialog = new QMUIDialog.MenuDialogBuilder(getActivity())
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        tvBlank.setVisibility(View.GONE);
                        floorList.set(position,items[which]);
                        adapter.notifyData(floorList);
                        syncFloorData();
                    }
                }).create(mCurrentDialogStyle);
        qmuiDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                L.i("取消");
                tvBlank.setVisibility(View.GONE);
            }
        });
        qmuiDialog.show();
    }

    private void syncFloorData(){
        //清空数据
        MainApplication.dataFloorList.clear();
        MainApplication.floorList.clear();
        MainApplication.CLOSE_DOOR_INDEX = Constants.DEFAULT_CLOSE_DOOR_INDEX;
        MainApplication.OPEN_DOOR_INDEX = Constants.DEFAULT_OPEN_DOOR_INDEX;

        //遍历数组
        for (int i=0;i<64;i++){
            //L.i("i="+i+" value:"+floorMap.get(i));
            if (floorList.get(i)!=null){

                if (!floorList.get(i).equals("开门")&&!floorList.get(i).equals("关门")&&!floorList.get(i).equals("")){
                    MainApplication.dataFloorList.add(String.valueOf(i+1));
                    MainApplication.floorList.add(floorList.get(i).replace("楼",""));
                }else if (floorList.get(i).equals("开门")){
                    if (i<9){
                        MainApplication.OPEN_DOOR_INDEX = "0"+(i+1);
                    }else {
                        MainApplication.OPEN_DOOR_INDEX = String.valueOf(i+1);
                    }
                }else if (floorList.get(i).equals("关门")){
                    if (i<9){
                        MainApplication.CLOSE_DOOR_INDEX = "0"+(i+1);
                    }else {
                        MainApplication.CLOSE_DOOR_INDEX = String.valueOf(i+1);
                    }
                }
            }
        }
    }
}
