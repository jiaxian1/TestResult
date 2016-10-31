package edu.feicui.testresult.testresult;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unionpay.UPPayAssistEx;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 选择加班人员
     */
    LinearLayout mllChoose;
    /**
     * 相机
     */
    LinearLayout mCamera;
    /**
     * 图库
     */
    LinearLayout mPicker;
    /**
     * 银联支付
     */
    LinearLayout mBank;
    /**
     * 返回的加班人员
     */
    TextView mTvChoosePeople;
    /**
     * 通过图库拿到的图片
     */
    ImageView mIvPhoto;
    /**
     * 通过相机拿到的图片
     */
    ImageView mIvCamera;
    /**
     * 选择加班人员的请求码  请求码:来区分结果
     */
    static final int CHOOSE_PEO=200;
    /**
     * 选择相机的权限请求码
     */
    static final int CAMERA_PERMISSION=201;
    /**
     * 跳转相机的请求码
     */
    static final int GOTO_CAMERA=202;
    /**
     * 跳转图库的请求码
     */
    static final int GOTO_PICK=203;
    /**
     * 银联支付的请求码
     */
    static final int GOTO_BANK=204;
    /**
     * 图片存储文件夹路径
     */
    public static final String DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+ //根路径
            File.separator +"TestResult";//分隔符  和文件夹名
    /**
     * 图片存储路径
     */
    public static final String PHOTO_FILE_PATH =DIR_PATH + File.separator +"photo.jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//加载布局
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mllChoose= (LinearLayout) findViewById(R.id.ll_go);//找到加班人员文字的控件
        mllChoose.setOnClickListener(this);//给加班人员文字绑定点击事件
        mTvChoosePeople= (TextView) findViewById(R.id.tv_choose_people);//找到返回的加班人员的控件
        mCamera= (LinearLayout) findViewById(R.id.ll_camera);//找到打开相机的控件
        mPicker= (LinearLayout) findViewById(R.id.ll_pick);//找到打开图库的点击事件
        mIvCamera= (ImageView) findViewById(R.id.iv_camera);//找到拿到相机的图片所放位置的控件
        mIvPhoto= (ImageView) findViewById(R.id.iv_photo);//找到拿到的图片所放位置的控件
        mBank= (LinearLayout) findViewById(R.id.ll_bank);//找到银联支付
        mCamera.setOnClickListener(this);//给打开相机的控件绑定点击事件
        mPicker.setOnClickListener(this);//给打开图库的控件绑定点击事件
        mBank.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_go://选择加班人员
                Intent intent=new Intent(this,ChooseActivity.class);//跳转到选择界面
                //注意这里不用startActivity(intent);  要用一个有返回结果的开启跳转
                //startActivityForResult(Intent intent, int requestCode)
                /**
                 * Intent intent  数据
                 * int requestCode   请求码
                 */
                startActivityForResult(intent,CHOOSE_PEO);//开始跳转到选择界面 并且带着 加班人员的请求
                break;
            case R.id.ll_camera://相机
                //Builder
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){//判断版本是否大于23
                    if (checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED&&//相机有权限
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){//sdk有了权限
                        goToCamear();//调用跳转相机的方法
                    }else {//没有权限  申请
                        /**
                         * @NonNull String[] permissions  (这里是相机)权限
                         * int requestCode                 请求码(这里是相机的CAMERA_PERMISSION)
                         * new String[]{Manifest.permission.CAMERA}  数组  表示 可以一次请求多个
                         */
                        requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERA_PERMISSION);//申请
                    }
                }else {//版本小于23
                    goToCamear();//直接调用跳转相机的方法
                }
                break;
            case R.id.ll_pick://跳转图库
                Intent intent1=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent1,GOTO_PICK);
                break;
            case R.id.ll_bank:
                // “00” – 银联正式环境
                // “01” – 银联测试环境，该环境中不发生真实交易
                String serverMode = "01";
                //  TN码  从网址(http://101.231.204.84:8091/sim/getacptn)里面获取：：：："201610121454011345448"
                UPPayAssistEx.startPay (this, null, null,"201610121454011345448",serverMode);


                break;
        }
    }
    /**
     * 拿到回传数据的结果
     * @param requestCode   请求码   区分返回结果的 请求
     * @param resultCode    结果码   区分结果是否成功
     * @param data           回传的数据数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){//通过请求码来区分
            case CHOOSE_PEO://人员选择
                if (resultCode==RESULT_OK){  //通过结果码来判断是否有数据
                    //拿数据
                    ArrayList<String> list=data.getStringArrayListExtra("data");//拿到穿多了的数据  String类型的数组
                    //展示出来
                    StringBuffer buffer=new StringBuffer();//初始化StringBuffer
                    for (String name:list) {//遍历数组
                        buffer.append(name).append(",");//进行拼接   这样  最后会多拼接一个逗号
                    }
                    buffer.deleteCharAt(buffer.length()-1);//将最后一个逗号删除
                    mTvChoosePeople.setText(buffer.toString());//将拿到的数据设置到界面上
                }
                break;
            case GOTO_CAMERA://打开相机拍照的结果
                Toast.makeText(this, "requestCode="+requestCode+"--resultCode=="+resultCode, Toast.LENGTH_SHORT).show();
                if (resultCode==RESULT_OK){
                    //读取图片  路径
                    Bitmap bitmap= BitmapFactory.decodeFile(PHOTO_FILE_PATH);//通过路径拿到图片
                    mIvCamera.setImageBitmap(bitmap);//将图片展示出来
                }
                break;
            case GOTO_PICK://打开图库选择的结果
                if (resultCode==RESULT_OK){  //如果有数据
                    Uri uri=data.getData();
                    String[] filePathColumn={MediaStore.Images.Media.DATA};
                    Cursor cursor=getContentResolver().query(uri,filePathColumn,null,null,null);
                    cursor.moveToFirst();
                    int columIndex=cursor.getColumnIndex(filePathColumn[0]);
                    String path=cursor.getString(columIndex);
                    Log.e("aaa", "onActivityResult: "+path);
                    try {
                        Bitmap bit=BitmapFactory.decodeStream(new FileInputStream(path));
                        mIvPhoto.setImageBitmap(bit);//将拿到的图片展示出来
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case GOTO_BANK:
                if (data == null) {
                    return;
                }
                String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
                String str = data.getExtras().getString("pay_result");
                if (str.equalsIgnoreCase("success")) {
                    // 支付成功后，extra中如果存在result_data，取出校验
                    // result_data结构见c）result_data参数说明
                    if (data.hasExtra("result_data")) {
                        String result = data.getExtras().getString("result_data");
                        try {
                            JSONObject resultJson = new JSONObject(result);
                            String sign = resultJson.getString("sign");
                            String dataOrg = resultJson.getString("data");
                            // 验签证书同后台验签证书
                            // 此处的verify，商户需送去商户后台做验签
                            boolean ret = verify(dataOrg, sign, "01");
                            if (ret) {
                                // 验证通过后，显示支付结果
                                msg = "支付成功！";
                            } else {
                                // 验证不通过后的处理
                                // 建议通过商户后台查询支付结果
                                msg = "支付失败！";
                            }
                        } catch (JSONException e) {
                        }
                    } else {
                        // 未收到签名信息
                        // 建议通过商户后台查询支付结果
                        msg = "支付成功！";
                    }
                } else if (str.equalsIgnoreCase("fail")) {
                    msg = "支付失败！";
                } else if (str.equalsIgnoreCase("cancel")) {
                    msg = "用户取消了支付";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("支付结果通知");
                builder.setMessage(msg);
                builder.setInverseBackgroundForced(true);
                // builder.setCustomTitle();
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
        }
    }


    /**
     * 跳转系统相机
     */
    public void goToCamear(){
        //MediaStore.ACTION_IMAGE_CAPTURE 相当于action
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//跳转打开相机
        //传递此次拍照图片存储的路径
        //指定一个路径去存放 图片   手机SD卡路径
        File fileDir=new File(DIR_PATH);
        if (!fileDir.exists()){//如果不存在
            fileDir.mkdirs();//创建此文件夹以及父类文件夹
        }
        //向相机传递文件路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(PHOTO_FILE_PATH)));
        startActivityForResult(intent,GOTO_CAMERA);//开始跳转
    }

    /**
     *
     * @param requestCode   请求码
     * @param permissions   所请求的权限
     * @param grantResults  所请求权限的结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){//通过请求码来区分
            case CAMERA_PERMISSION://相机请求
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){  //同意相机权限
                    goToCamear();//调用跳转相机的方法
                }else {//拒绝相机权限
                    Toast.makeText(this,"打开相机需要赋予权限  权限管理-->应用-->",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean verify(String msg, String sign64, String mode) {
        // 此处的verify，商户需送去商户后台做验签
        return true;

    }

    /**
     * EventBurs    第三方的jar包
     */
}
