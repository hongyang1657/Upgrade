package ai.fitme.ayahupgrade.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.ui.fragment.imp.OnFragmentListener;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.ToastUtil;

public class DeviceInfoFragment extends Fragment {

  private OnFragmentListener listener;
  private Button btNext;
  private EditText et1,et2,et3,et4,et5,et6;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_device_info,container,false);
    init(v);
    return v;
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    listener = (OnFragmentListener) context;
  }

  private void init(View view){
    et1 = view.findViewById(R.id.et_device_info_1);
    et2 = view.findViewById(R.id.et_device_info_2);
    et3 = view.findViewById(R.id.et_device_info_3);
    et4 = view.findViewById(R.id.et_device_info_4);
    et5 = view.findViewById(R.id.et_device_info_5);
    et6 = view.findViewById(R.id.et_device_info_6);
    et1.setOnFocusChangeListener(onFocusChangeListener);
    et2.setOnFocusChangeListener(onFocusChangeListener);
    et3.setOnFocusChangeListener(onFocusChangeListener);
    et4.setOnFocusChangeListener(onFocusChangeListener);
    et5.setOnFocusChangeListener(onFocusChangeListener);
    et6.setOnFocusChangeListener(onFocusChangeListener);
    btNext = view.findViewById(R.id.bt_next);
    btNext.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //TODO 输入2表示版本切换为光耦合
        if (et3.getText().toString().trim().equals("1")){
          MainApplication.PROTOCOL_VERSION = Constants.PROTOCOL_RELAY;
          ToastUtil.showToast(getContext().getApplicationContext(),"切换为继电器版本");
        }else if (et3.getText().toString().trim().equals("2")){
          MainApplication.PROTOCOL_VERSION = Constants.PROTOCOL_OPTICAL;
          ToastUtil.showToast(getContext().getApplicationContext(),"切换为光耦合版本");
        }else if (et3.getText().toString().trim().equals("3")){
          MainApplication.PROTOCOL_VERSION = Constants.PROTOCOL_SAMSUNG;
          ToastUtil.showToast(getContext().getApplicationContext(),"切换为三星版本");
        }
        listener.onNext(1);
      }
    });
  }

  private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
      if(!hasFocus){
        InputMethodManager manager = ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
        if (manager != null)
          manager.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
      }
    }
  };
}
