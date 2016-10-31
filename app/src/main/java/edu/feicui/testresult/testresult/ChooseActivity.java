package edu.feicui.testresult.testresult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * 选择界面
 * Created by jiaXian on 2016/10/10.
 */
public class ChooseActivity extends Activity implements View.OnClickListener, ChooseAdapter.OnItemClickListener {
    RecyclerView mRecyclerView;//声明RecyclerView
    ChooseAdapter adapter;//声明适配器
    public static boolean[] mChooseArray;//声明一个选中状态的数组(有几条数据 就有几个选中状态  所以 用boolean的数组)
    Button mBtnOK;//声明选择完成  确定的按钮
    ArrayList<String> list;//声明数据源的数组

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 布局
         */
        LinearLayout linearLayout=new LinearLayout(this);//线性布局
        linearLayout.setOrientation(LinearLayout.VERTICAL);//竖向的
        mRecyclerView=new RecyclerView(this);//RecyclerView
        mBtnOK=new Button(this);//Button
        mBtnOK.setText("确认");//设置文字
        linearLayout.addView(mRecyclerView);//将RecyclerView加到线性布局中
        linearLayout.addView(mBtnOK);//将Button加到线性布局中
        setContentView(linearLayout);//加载布局
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        //1.数据源
        list=new ArrayList<>();
        list.add("张一");//给数组添加数据
        list.add("张二");
        list.add("张三");
        list.add("张四");
        list.add("张五");
        list.add("张六");
        list.add("张七");
        list.add("张八");
        list.add("张九");
        //选中状态的数量(有几个数据，就有几个 是否选中的状态)
        mChooseArray=new boolean[list.size()];//此数组的长度是数据源的长度
        //2.布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //3.适配器
        adapter=new ChooseAdapter(list,this);//初始化适配器
        mRecyclerView.setAdapter(adapter);//绑定适配器
        //**********注意：这里是用适配器点的  自己写的接口   和ListView的原理一样
        adapter.setOnItemClickListener(this);
        mBtnOK.setOnClickListener(this);//给选择完成的按钮绑定点击事件
    }

    @Override
    public void onItemClick(ChooseAdapter.MyHolder holder, int position) {
        //点击一下  将数据的选中状态写成和上次相反
        mChooseArray[position]=!mChooseArray[position];
        //和ListView不同   RecyclerView可以刷新一条数据
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onClick(View v) {
        //结束选择-----需要将选中的数据传递给上一级
        ArrayList<String> resultData=new ArrayList<>();
        //需要下标应该用for循环
        for (int i = 0; i <mChooseArray.length ; i++) {
            if (mChooseArray[i]){//如果数据被选中
                resultData.add(list.get(i));//则将拿到的数据加到ArrayList<String> resultData 这个集合中
            }
        }
        //数据返回
        /**
         * 返回数据给之前的Activity
         * int resultCode    结果码  区分此次跳转操作是否成功
         * Intent data       数据
         */
        Intent intent=new Intent();//拿到Intent对象
        intent.putExtra("data",resultData);//传递数据
        setResult(RESULT_OK,intent);//设置结果
        //在结束之前  将数据传递给上一个Activity
        finish();
    }


}
