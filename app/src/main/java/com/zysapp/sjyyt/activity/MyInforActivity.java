package com.zysapp.sjyyt.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayParse;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zysapp.config.BaseConfig;
import com.zysapp.sjyyt.BaseActivity;
import com.zysapp.sjyyt.BaseApplication;
import com.zysapp.sjyyt.BaseHttpInformation;
import com.zysapp.sjyyt.model.FileUploadResult;
import com.zysapp.sjyyt.model.JsonBean;
import com.zysapp.sjyyt.model.PCD;
import com.zysapp.sjyyt.model.User;
import com.zysapp.sjyyt.util.EventBusConfig;
import com.zysapp.sjyyt.util.EventBusModel;
import com.zysapp.sjyyt.view.ClearEditText;
import com.zysapp.sjyyt.view.ImageWay;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import xtom.frame.XtomActivityManager;
import xtom.frame.image.load.XtomImageTask;
import xtom.frame.image.load.XtomImageTask.Size;
import xtom.frame.util.XtomBaseUtil;
import xtom.frame.util.XtomFileUtil;
import xtom.frame.util.XtomImageUtil;

/**
 * 我的资料
 */
public class MyInforActivity extends BaseActivity {
    public ImageWay imageWay;
    @BindView(R.id.title_btn_left)
    ImageButton titleBtnLeft;
    @BindView(R.id.title_btn_right)
    Button titleBtnRight;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.avatar)
    RoundedImageView avatar;
    @BindView(R.id.tv_avatar)
    TextView tvAvatar;
    @BindView(R.id.ev_nickname)
    ClearEditText evNickname;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_sex_m)
    TextView tvSexM;
    @BindView(R.id.tv_sex_f)
    TextView tvSexF;
    @BindView(R.id.tv_district)
    TextView tvDistrict;
    @BindView(R.id.lv_district)
    LinearLayout lvDistrict;
    private String tempPath;
    private String nickname, realname, fy_telphone, qq_no;
    private String imagePathCamera;
    private User user;
    private String token = "", avatarUrl;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private String city, province;
    private String distrct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_editinfor);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        getNetWorker().districtList();
        if (savedInstanceState == null) {
            imageWay = new ImageWay(mContext, 1, 2);
        } else {
            imagePathCamera = savedInstanceState.getString("imagePathCamera");
            imageWay = new ImageWay(mContext, 1, 2);
        }
        user = BaseApplication.getInstance().getUser();
        if (user == null)
            token = "";
        else {
            token = user.getToken();
            init();
        }
        getNetWorker().clientGet(token, user.getId());
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageWay != null)
            outState.putString("imagePathCamera", imageWay.getCameraImage());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case 1:// 相册选择图片
                album(data);
                break;
            case 2:// 拍照
                camera();
                break;
            case 3:// 裁剪
                imageWorker.loadImage(new ImageTask(avatar, tempPath,
                        mContext, new Size(180, 180)));
                getNetWorker().fileUpload(token, "1", "0", tempPath);
                break;
        }
    }

    private void camera() {
        if (imagePathCamera == null) {
            imagePathCamera = imageWay.getCameraImage();
        }
        log_i("imagePathCamera=" + imagePathCamera);
        editImage(imagePathCamera, 3);
    }

    private void album(Intent data) {
        if (data == null)
            return;
        Uri selectedImageUri = data.getData();
        startPhotoZoom(selectedImageUri, 3);

    }

    private void editImage(String path, int requestCode) {
        File file = new File(path);
        startPhotoZoom(Uri.fromFile(file), requestCode);
    }

    private void startPhotoZoom(Uri uri, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", BaseConfig.IMAGE_WIDTH);
        intent.putExtra("aspectY", BaseConfig.IMAGE_WIDTH);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", BaseConfig.IMAGE_WIDTH);
        intent.putExtra("outputY", BaseConfig.IMAGE_WIDTH);
        intent.putExtra("return-data", false);
        startActivityForResult(intent, requestCode);
    }

    private Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }

    private File getTempFile() {
        String savedir = XtomFileUtil.getTempFileDir(mContext);
        File dir = new File(savedir);
        if (!dir.exists())
            dir.mkdirs();
        // 保存入sdCard
        tempPath = savedir + XtomBaseUtil.getFileName() + ".jpg";// 保存路径
        File file = new File(tempPath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return file;
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_ADD:
                showProgressDialog("正在保存");
                break;
            case CLIENT_SAVE:
                showProgressDialog("正在保存");
                break;
            case FILE_UPLOAD:
                showProgressDialog("正在上传头像");
                break;
            default:
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_ADD:
            case FILE_UPLOAD:
            case CLIENT_SAVE:
            case CLIENT_LOGIN:
                cancelProgressDialog();
                break;
            case THIRD_SAVE:
                cancelProgressDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_SAVE:
                complete();
                break;
            case FILE_UPLOAD:
                HemaArrayParse<FileUploadResult> fResult = (HemaArrayParse<FileUploadResult>) baseResult;
                FileUploadResult fileUploadResult = fResult.getObjects().get(0);
                avatarUrl = fileUploadResult.getItem2();
                break;
            case CLIENT_GET:
                HemaArrayParse<User> uResult = (HemaArrayParse<User>) baseResult;
                user = uResult.getObjects().get(0);
                BaseApplication.getInstance().setUser(user);
                init();
                break;
            case DISTRICT_LIST:
                @SuppressWarnings("unchecked")
                HemaArrayParse<PCD> dResult = (HemaArrayParse<PCD>) baseResult;
                options1Items = dResult.getObjects().get(0).getCityList();
                initJsonData();
                break;
            default:
                break;
        }
    }

    private void init() {
        if (!isNull(user.getAvatar()))
            tvAvatar.setText("修改头像");
        ImageLoader.getInstance().displayImage(user.getAvatar(), avatar, BaseApplication.getInstance()
                .getOptions(R.mipmap.default_avatar));
        avatarUrl = user.getAvatar();
        evNickname.setText(user.getNickname());
        tvSex.setText(user.getSex());
        if (user.getSex().equals("男")) {
            tvSexM.setBackgroundResource(R.drawable.bg_sex_p);
            tvSexF.setBackgroundResource(R.drawable.bg_sex_n);
        } else {
            tvSexM.setBackgroundResource(R.drawable.bg_sex_n);
            tvSexF.setBackgroundResource(R.drawable.bg_sex_p);
        }
    }

    private void complete() {
        EventBus.getDefault().post(new EventBusModel(EventBusConfig.REFRESH_USER));
        showTextDialog("保存成功");
        titleText.postDelayed(new Runnable() {

            @Override
            public void run() {
                setResult(RESULT_OK);
                finish();
            }
        }, 1000);
    }

    private void initJsonData() {//解析数据

        for (int i = 0; i < options1Items.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < options1Items.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = options1Items.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (options1Items.get(i).getCityList().get(c).getDistrictList() == null
                        || options1Items.get(i).getCityList().get(c).getDistrictList().size() == 0) {
                    City_AreaList.add("");
                } else {
                    for (int d = 0; d < options1Items.get(i).getCityList().get(c).getDistrictList().size(); d++) {//该城市对应地区所有数据
                        String AreaName = options1Items.get(i).getCityList().get(c).getDistrictList().get(d).getName();
                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }
            options2Items.add(CityList);
            options3Items.add(Province_AreaList);
        }
    }

    @Override
    public void onPublish(int progress) {

    }

    @Override
    public void onChange(int position) {

    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_ADD:
                showTextDialog(baseResult.getMsg());
                break;
            case CLIENT_SAVE:
                showTextDialog(baseResult.getMsg());
                break;
            case FILE_UPLOAD:
                log_i("头像上传失败");
                showTextDialog(baseResult.getMsg());
                break;
            default:
                break;
        }
    }

    private void toLogin() {
        XtomActivityManager.finishAll();
        Intent it = new Intent(mContext, LoginActivity.class);
        startActivity(it);
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_ADD:
                showTextDialog("注册失败");
                break;
            case CLIENT_SAVE:
                showTextDialog("保存失败");
                break;
            case FILE_UPLOAD:
                showTextDialog("保存失败");
                break;
            default:
                break;
        }
    }

    @Override
    protected void findView() {
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        titleText.setText("编辑个人信息");
        titleBtnRight.setText("保存");
        avatar.setCornerRadius(100);
        lvDistrict.setVisibility(View.GONE);
    }

    @OnClick({R.id.title_btn_left, R.id.title_btn_right, R.id.avatar, R.id.tv_sex_m, R.id.tv_sex_f, R.id.tv_district})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_btn_left:
                finish();
                break;
            case R.id.title_btn_right:
                String nickname = evNickname.getText().toString();
                if (isNull(nickname)) {
                    showTextDialog("请输入昵称");
                    return;
                }
                String sex = tvSex.getText().toString();
//                String d = tvDistrict.getText().toString();
//                if (isNull(d)) {
//                    showTextDialog("请选择地址");
//                    return;
//                }
                getNetWorker().clientSave(token, nickname, sex, avatarUrl, user.getDistrict_name());
                break;
            case R.id.avatar:
                imageWay.show();
                break;
            case R.id.tv_sex_m:
                tvSex.setText("男");
                tvSexM.setBackgroundResource(R.drawable.bg_sex_p);
                tvSexF.setBackgroundResource(R.drawable.bg_sex_n);
                break;
            case R.id.tv_sex_f:
                tvSex.setText("女");
                tvSexM.setBackgroundResource(R.drawable.bg_sex_n);
                tvSexF.setBackgroundResource(R.drawable.bg_sex_p);
                break;
            case R.id.tv_district:
                if (options1Items.size() == 0) {
                    showTextDialog("正在获取地区数据");
                    break;
                }
                ShowPickerView();
                break;
        }
    }

    private class ImageTask extends XtomImageTask {

        public ImageTask(ImageView imageView, String path, Object context,
                         Size size) {
            super(imageView, path, context, size);
        }

        public ImageTask(ImageView imageView, URL url, Object context, Size size) {
            super(imageView, url, context, size);
        }

        @Override
        public void setBitmap(Bitmap bitmap) {
            bitmap = XtomImageUtil.getRoundedCornerBitmap(bitmap, 2);
            super.setBitmap(bitmap);
        }

    }

    private void ShowPickerView() {// 弹出选择器
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(mContext, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2) +
                        options3Items.get(options1).get(options2).get(options3);
                province = options1Items.get(options1).getName();
                city = options2Items.get(options1).get(options2);
                distrct = options3Items.get(options1).get(options2).get(options3);
                tvDistrict.setText(tx);
            }
        }).setTitleText("城市选择").setCancelColor(0xffFFC80C).setSubmitColor(0xff313131)
                .setDividerColor(0xffFFC80C)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(18)
                .setOutSideCancelable(true)// default is true
                .build();
        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

}
