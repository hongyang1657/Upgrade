package ai.fitme.ayahupgrade.presenter;


import ai.fitme.ayahupgrade.bean.IsOccupiedMobile;
import ai.fitme.ayahupgrade.model.IsOccupiedMobileModel;
import ai.fitme.ayahupgrade.presenter.impl.IIsOccupiedPhonePresenter;
import ai.fitme.ayahupgrade.ui.activity.impl.IloginActivityView;

/**
 * 验证手机号是否存在的presenter
 * Created by blw on 2016/8/28.
 */
public class IsOccupiedPhonePresenter implements IIsOccupiedPhonePresenter, IsOccupiedMobileModel.OnPhoneEditListener{
    //网络数据处理model
    private IsOccupiedMobileModel mIsOccupiedMobileModel;
    //视图fragment
    private IloginActivityView iloginActivityView;

    public IsOccupiedPhonePresenter(IloginActivityView iloginActivityView){
        this.mIsOccupiedMobileModel=new IsOccupiedMobileModel(this);
        this.iloginActivityView=iloginActivityView;
    }
    @Override
    public void isOccupiedMobile(String mobile) {
        mIsOccupiedMobileModel.isOccupiedMobile(mobile);
    }

    //验证手机是否占用，结果请求成功时调用

    @Override
    public void onSuccess(IsOccupiedMobile jsonObject) {
        iloginActivityView.showIsOccupiedPhone(jsonObject);
    }
    //验证手机是否占用，结果请求失败时调用
    @Override
    public void onFailure(Throwable e) {

    }
}
