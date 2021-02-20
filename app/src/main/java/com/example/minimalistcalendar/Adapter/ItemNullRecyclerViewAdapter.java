package com.example.minimalistcalendar.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.minimalistcalendar.R;

import org.jetbrains.annotations.NotNull;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Adapter
 * @ClassName: ItemNullRecyclerViewAdapter
 * @Description: 这个类的作用就是当item为空的时候显示提示 暂时没有数据请创建
 * @Author: 作者名
 * @CreateDate: 2021/2/4 15:26
 */
public class ItemNullRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    //构造方法，传入数据
    public ItemNullRecyclerViewAdapter(Context ctx){
        context=ctx;
    }

    @Override
    public ItemNullMyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =View.inflate(parent.getContext(),R.layout.item_null_recyclerview,null);
        return new ItemNullMyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ItemNullMyViewHolder itemNullMyViewHolder=(ItemNullMyViewHolder) holder;
        itemNullMyViewHolder.textView.setText("暂时没有数据，请通过悬浮按钮创建！");

    }
    @Override
    public int getItemCount() {
        //返回Item总条数
        return 1;
    }

    //内部类，绑定控件
    class ItemNullMyViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;
        public ItemNullMyViewHolder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.nullitem_tv);
        }
    }


}

