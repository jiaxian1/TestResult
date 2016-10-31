package edu.feicui.testresult.testresult;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 选择节目的适配器
 * Created by jiaXian on 2016/10/10.
 */
public class ChooseAdapter extends RecyclerView.Adapter<ChooseAdapter.MyHolder>{

    ArrayList<String> mList;
    Context mContext;
    LayoutInflater mInflate;
    public ChooseAdapter(ArrayList<String> mList,Context mContext){

        this.mList=mList;
        this.mContext=mContext;
        mInflate= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=mInflate.inflate(R.layout.item_choose,parent,false);
        MyHolder holder=new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.mTvName.setText(mList.get(position));
        //选择状态
        holder.mIvChoose.setSelected(ChooseActivity.mChooseArray[position]);
        holder.itemView.setOnClickListener(new MyClickListener(holder,position));
    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();//  三目 判断  如果是空  返回为0
    }
    public class MyHolder extends RecyclerView.ViewHolder{
        TextView mTvName;
        ImageView mIvChoose;
        View itemView;
        public MyHolder(View itemView) {
            super(itemView);
            this.itemView=itemView;
            mTvName= (TextView) itemView.findViewById(R.id.tv_name);
            mIvChoose= (ImageView) itemView.findViewById(R.id.iv_chose);
        }
    }

    public class MyClickListener implements View.OnClickListener{
        MyHolder holder;
        int position;
        public MyClickListener(MyHolder holder,int position){
            this.holder=holder;
            this.position=position;
        }

        @Override
        public void onClick(View v) {
            //响应  4.用在响应
            mOnItemClickListener.onItemClick(holder,position);
        }
    }
    //3.给外部暴露一个方法
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener=mOnItemClickListener;
    }
    //2.声明对象
    OnItemClickListener mOnItemClickListener;//声明对象
    // 1.先写接口
    public interface OnItemClickListener{
        void onItemClick(MyHolder holder,int position);
    }
}
