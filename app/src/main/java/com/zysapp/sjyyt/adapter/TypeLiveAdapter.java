package com.zysapp.sjyyt.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zysapp.sjyyt.BaseApplication;
import com.zysapp.sjyyt.BaseRecycleAdapter;
import com.zysapp.sjyyt.activity.PlayActivity;
import com.zysapp.sjyyt.activity.R;
import com.zysapp.sjyyt.model.Song;

import java.util.List;

/**
 * 分类-节目列表
 */
public class TypeLiveAdapter extends BaseRecycleAdapter<Song> {
    private Context mContext;

    public TypeLiveAdapter(Context mContext, List<Song> datas) {
        super(datas);
        this.mContext = mContext;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final int position) {
        holder.getView(R.id.tv_state).setVisibility(View.GONE);
        ((TextView) holder.getView(R.id.tv_name)).setText(datas.get(position).getName());
        ((TextView) holder.getView(R.id.tv_author)).setText(datas.get(position).getAuthor());
        ((TextView) holder.getView(R.id.tv_content)).setText(datas.get(position).getDescription());
        ((TextView) holder.getView(R.id.tv_count)).setText("评论/"+datas.get(position).getReplycount()+"           收听/"+datas.get(position).getListencount());
        RoundedImageView avatar = (RoundedImageView) holder.getView(R.id.iv_image);
        ImageLoader.getInstance().displayImage(datas.get(position).getImgurl(), avatar, BaseApplication.getInstance()
                .getOptions(R.mipmap.default_blog_img));
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent it=new Intent(mContext, PlayActivity.class);
//                it.putExtra("songs", (List<Song>) datas);
//            }
//        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.listitem_huiting;
    }
}
