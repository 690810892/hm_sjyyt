package com.zysapp.sjyyt.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayParse;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.view.RefreshLoadmoreLayout;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zysapp.sjyyt.BaseActivity;
import com.zysapp.sjyyt.BaseApplication;
import com.zysapp.sjyyt.BaseHttpInformation;
import com.zysapp.sjyyt.BaseRecycleAdapter;
import com.zysapp.sjyyt.BaseUtil;
import com.zysapp.sjyyt.ToLogin;
import com.zysapp.sjyyt.adapter.AuthorAdapter;
import com.zysapp.sjyyt.adapter.LiveAdapter;
import com.zysapp.sjyyt.adapter.ReplyAdapter;
import com.zysapp.sjyyt.model.Author;
import com.zysapp.sjyyt.model.Count;
import com.zysapp.sjyyt.model.Reply;
import com.zysapp.sjyyt.model.Song;
import com.zysapp.sjyyt.model.User;
import com.zysapp.sjyyt.newgetui.PushModel;
import com.zysapp.sjyyt.util.EventBusConfig;
import com.zysapp.sjyyt.util.EventBusModel;
import com.zysapp.sjyyt.util.RecycleUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import de.greenrobot.event.EventBus;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
import xtom.frame.util.XtomFileUtil;
import xtom.frame.util.XtomToastUtil;
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * 播放
 */
public class PlayActivity extends BaseActivity implements PlatformActionListener {

    @BindView(R.id.title_btn_left)
    ImageButton titleBtnLeft;
    @BindView(R.id.title_btn_right)
    ImageButton titleBtnRight;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.line)
    ImageView line;
    @BindView(R.id.iv_music)
    ImageView ivMusic;
    @BindView(R.id.danmaku_view)
    DanmakuView danmakuView;
    @BindView(R.id.iv_open)
    ImageView ivOpen;
    @BindView(R.id.sb_play_progress)
    SeekBar sbPlayProgress;
    @BindView(R.id.tv_time_now)
    TextView tvTimeNow;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.tv_share)
    TextView tvShare;
    @BindView(R.id.tv_reply)
    TextView tvReply;
    @BindView(R.id.iv_previous)
    ImageView ivPrevious;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.iv_next)
    ImageView ivNext;
    //    @BindView(R.id.avatar)
//    RoundedImageView avatar;
//    @BindView(R.id.tv_player)
//    TextView tvPlayer;
//    @BindView(R.id.tv_tip)
//    TextView tvTip;
    @BindView(R.id.tv_center)
    TextView tvCenter;
    @BindView(R.id.iv_line1)
    ImageView ivLine1;
    @BindView(R.id.lv_center)
    LinearLayout lvCenter;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_line2)
    ImageView ivLine2;
    @BindView(R.id.lv_content)
    LinearLayout lvContent;
    @BindView(R.id.tv_replylist)
    TextView tvReplylist;
    @BindView(R.id.iv_line3)
    ImageView ivLine3;
    @BindView(R.id.lv_replylist)
    LinearLayout lvReplylist;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    @BindView(R.id.rv_reply)
    RecyclerView rvReply;
    @BindView(R.id.refreshLoadmoreLayout)
    RefreshLoadmoreLayout refreshLoadmoreLayout;
    @BindView(R.id.tv_zhuboshuo)
    TextView tvZhuboshuo;
    @BindView(R.id.rv_author)
    RecyclerView rvAuthor;
    @BindView(R.id.lv_author_top)
    LinearLayout lvAuthorTop;
    private User user;
    private String name, token;
    private int screenWide;
    ArrayList<Song> songs = new ArrayList<>();
    private boolean showDanmaku;
    private DanmakuContext danmakuContext;
    private LiveAdapter liveAdapter;
    private ReplyAdapter replyAdapter;
    ArrayList<Reply> replies = new ArrayList<>();
    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };
    private Integer currentPosition = 0;
    private Integer currentPage = 0;
    MainActivity mainActivity;
    private PopupWindow mWindow_exit;
    private ViewGroup mViewGroup_exit;
    private String sys_plugins;
    private String pathWX;
    private String imageurl;
    private OnekeyShare oks;
    private AuthorAdapter authorAdapter;
    ArrayList<Author> authors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mainActivity = MainActivity.getInstance();
        screenWide = BaseUtil.getScreenWidth(mContext);
        user = BaseApplication.getInstance().getUser();
        if (user == null)
            token = "";
        else
            token = user.getToken();
        ivMusic.getLayoutParams().height = screenWide * 3 / 5;
        sbPlayProgress.setOnSeekBarChangeListener(mSeekBarChangeListener);
        refreshLoadmoreLayout.setRefreshable(false);
        liveAdapter = new LiveAdapter(mContext, songs);
        RecycleUtils.initVerticalRecyleNoScrll(rvContent);
        rvContent.setAdapter(liveAdapter);
        liveAdapter.setOnItemClickListener(new BaseRecycleAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                EventBus.getDefault().post(new EventBusModel(EventBusConfig.PLAY, songs, position, 2, Integer.parseInt(songs.get(position).getChannel_id())));
            }
        });
        replyAdapter = new ReplyAdapter(mContext, replies, getNetWorker());
        RecycleUtils.initVerticalRecyleNoScrll(rvReply);
        rvReply.setAdapter(replyAdapter);
        authorAdapter = new AuthorAdapter(mContext, authors, getNetWorker());
        RecycleUtils.initVerticalRecyleNoScrll(rvAuthor);
        rvAuthor.setAdapter(authorAdapter);
        refreshLoadmoreLayout.setOnStartListener(new XtomRefreshLoadmoreLayout.OnStartListener() {

            @Override
            public void onStartRefresh(XtomRefreshLoadmoreLayout v) {
            }

            @Override
            public void onStartLoadmore(XtomRefreshLoadmoreLayout v) {
                currentPage++;
                if (rvReply.getVisibility() == View.VISIBLE)
                    getNetWorker().replyList("1", songs.get(currentPosition).getId(), currentPage.toString());
            }
        });
        danmakuContext = DanmakuContext.create();
        danmakuContext.setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter);
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_LR, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_BOTTOM, true);
        danmakuContext.preventOverlapping(overlappingEnablePair);
        danmakuView.prepare(parser, danmakuContext);
        danmakuView.enableDanmakuDrawingCache(true);
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                showDanmaku = true;
                danmakuView.start();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        liveAdapter.notifyDataSetChanged();
        ivPlay.setImageResource(R.mipmap.img_play);
        tvName.setText(songs.get(currentPosition).getName());
        ImageLoader.getInstance().displayImage(songs.get(currentPosition).getImgurl(), ivMusic, BaseApplication.getInstance()
                .getOptions(R.mipmap.login_bg));
//        ImageLoader.getInstance().displayImage(songs.get(currentPosition).getAuthor_imgurl(), avatar, BaseApplication.getInstance()
//                .getOptions(R.mipmap.default_avatar));
//        tvPlayer.setText(songs.get(currentPosition).getAuthor());
        authors.clear();
        authors.addAll(songs.get(currentPosition).getAuthors());
        authorAdapter.setLive_id(songs.get(currentPosition).getId());
        authorAdapter.notifyDataSetChanged();
        if (authors.size() == 0) {
            lvAuthorTop.setVisibility(View.GONE);
            //lvCenter.setVisibility(View.GONE);
        } else {
            lvAuthorTop.setVisibility(View.VISIBLE);
            //lvCenter.setVisibility(View.VISIBLE);
        }
        tvReply.setText(songs.get(currentPosition).getReplycount());
        tvShare.setText(songs.get(currentPosition).getSharecount());
        if (songs.get(currentPosition).getDyflag().equals("1")) {
            tvSave.setTextColor(0xffFFC80C);
            tvSave.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.save_p, 0, 0, 0);
        } else {
            tvSave.setTextColor(0xffffffff);
            tvSave.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.save_n, 0, 0, 0);
        }
        if (songs.get(currentPosition).getLoveflag().equals("1")) {
            titleBtnRight.setImageResource(R.mipmap.love_p);
        } else {
            titleBtnRight.setImageResource(R.mipmap.love_n);
        }
        tvTime.setText(BaseUtil.formatTime(mainActivity.mPlayService.getDuration()));
        tvTimeNow.setText(BaseUtil.formatTime(mainActivity.mPlayService.getPlayingPosition()));
        currentPage = 0;
        sbPlayProgress.setMax(mainActivity.mPlayService.getDuration());
        EventBus.getDefault().post(new EventBusModel(EventBusConfig.REFRESH_FIRST_SONG, songs, currentPosition, 2, Integer.parseInt(songs.get(currentPosition).getChannel_id())));
        getNetWorker().replyList("1", songs.get(currentPosition).getId(), "0");
    }

    public void onEventMainThread(EventBusModel event) {
        switch (event.getType()) {
            case REFRESH_SONG:
                currentPosition = event.getCode();
                log_d("666--" + currentPosition);
                tvName.setText(songs.get(currentPosition).getName());
                // tvZhuboshuo.setText(songs.get(currentPosition).getAuthor_content());
                String Author_content = songs.get(currentPosition).getAuthor_content();
                if (!isNull(Author_content)) {
                    Author_content = Author_content.replace("\\n", "\n");
                    tvZhuboshuo.setText(Author_content);
                } else {
                    tvZhuboshuo.setText("");
                }
                ImageLoader.getInstance().displayImage(songs.get(currentPosition).getImgurl(), ivMusic, BaseApplication.getInstance()
                        .getOptions(R.mipmap.login_bg));
//                ImageLoader.getInstance().displayImage(songs.get(currentPosition).getAuthor_imgurl(), avatar, BaseApplication.getInstance()
//                        .getOptions(R.mipmap.default_avatar));
//                tvPlayer.setText(songs.get(currentPosition).getAuthor());
                authors.clear();
                authors.addAll(songs.get(currentPosition).getAuthors());
                authorAdapter.setLive_id(songs.get(currentPosition).getId());
                authorAdapter.notifyDataSetChanged();
                if (authors.size() == 0) {
                    lvAuthorTop.setVisibility(View.GONE);
                   // lvCenter.setVisibility(View.GONE);
                } else {
                    lvAuthorTop.setVisibility(View.VISIBLE);
                   // lvCenter.setVisibility(View.VISIBLE);
                }
                tvReply.setText(songs.get(currentPosition).getReplycount());
                tvShare.setText(songs.get(currentPosition).getSharecount());
                if (songs.get(currentPosition).getDyflag().equals("1")) {
                    tvSave.setTextColor(0xffFFC80C);
                    tvSave.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.save_p, 0, 0, 0);
                } else {
                    tvSave.setTextColor(0xffffffff);
                    tvSave.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.save_n, 0, 0, 0);
                }
                if (songs.get(currentPosition).getLoveflag().equals("1")) {
                    titleBtnRight.setImageResource(R.mipmap.love_p);
                } else {
                    titleBtnRight.setImageResource(R.mipmap.love_n);
                }
                tvTime.setText(BaseUtil.formatTime(mainActivity.mPlayService.getDuration()));
                tvTimeNow.setText(BaseUtil.formatTime(mainActivity.mPlayService.getPlayingPosition()));
                currentPage = 0;
                sbPlayProgress.setMax(mainActivity.mPlayService.getDuration());
                getNetWorker().replyList("1", songs.get(currentPosition).getId(), "0");
                break;
            case STATE_PLAY:
                ivPlay.setImageResource(R.mipmap.img_play);
                break;
            case STATE_PAUSE:
                ivPlay.setImageResource(R.mipmap.img_pause);
                break;
            case REFRESH_REPLY:
//                String content = event.getContent();
//                addDanmaKuShowTextAndImage(user.getAvatar(), user.getNickname(), content, true);
                currentPage = 0;
                getNetWorker().replyList("1", songs.get(currentPosition).getId(), "0");
                getNetWorker().liveGet(songs.get(currentPosition).getId());
                break;
            case REFRESH_FIRST_SONG:
//                songs.clear();
//                songs.addAll(event.getSongs());
//                currentPosition = event.getCode();
//                liveAdapter.notifyDataSetChanged();
//                if (songs.size() > 0)
//                    EventBus.getDefault().post(new EventBusModel(EventBusConfig.PLAY, songs, currentPosition, event.getPlaytype(), event.getTypeid()));
                break;
            case onPublish:
                sbPlayProgress.setProgress(event.getCode());
                tvTimeNow.setText(BaseUtil.formatTime(event.getCode()));
                break;
            case REFRESH_USER:
                user = BaseApplication.getInstance().getUser();
                if (user == null)
                    token = "";
                else
                    token = user.getToken();
                break;
            case REFRESH_DANMU:
                PushModel pushModel = (PushModel) event.getObject();
                if (pushModel.getKeyId().equals(songs.get(currentPosition).getId()))
                    addDanmaKuShowTextAndImage(pushModel.getMsg_avatar(), pushModel.getMsg_nickname(), pushModel.getMsg_content(), true);
                log_e("pushid=======" + pushModel.getKeyId());
                log_e("songsid=======" + songs.get(currentPosition).getId());
                break;
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DATA_SAVEOPERATE:
                showProgressDialog("请稍后");
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
            case DATA_SAVEOPERATE:
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
            case ADVICE_ADD:
                showTextDialog(baseResult.getMsg());
                titleText.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
                break;
            case REPLY_LIST:
                String page = netTask.getParams().get("page");
                String live_id = netTask.getParams().get("keyid");
                @SuppressWarnings("unchecked")
                HemaArrayParse<Reply> gResult = (HemaArrayParse<Reply>) baseResult;
                ArrayList<Reply> goods = gResult.getObjects();
                if (page.equals("0")) {// 刷新
                    refreshLoadmoreLayout.refreshSuccess();
                    this.replies.clear();
                    this.replies.addAll(goods);
                    int sysPagesize = BaseApplication.getInstance().getSysInitInfo()
                            .getSys_pagesize();
                    if (goods.size() < sysPagesize)
                        refreshLoadmoreLayout.setLoadmoreable(false);
                    else
                        refreshLoadmoreLayout.setLoadmoreable(true);
                } else {// 更多
                    refreshLoadmoreLayout.loadmoreSuccess();
                    if (goods.size() > 0)
                        this.replies.addAll(goods);
                    else {
                        refreshLoadmoreLayout.setLoadmoreable(false);
                        XtomToastUtil.showShortToast(mContext, "已经到最后啦");
                    }
                }
                refreshLoadmoreLayout.setRefreshable(false);
                replyAdapter.notifyDataSetChanged();
                replyAdapter.setLive_id(live_id);
                break;
            case LIVE_GET:
                HemaArrayParse<Count> cResult = (HemaArrayParse<Count>) baseResult;
                Count cc = cResult.getObjects().get(0);
                tvShare.setText(cc.getSharecount());
                tvReply.setText(cc.getReplycount());
                break;
            case DATA_SAVEOPERATE:
                String keytype = netTask.getParams().get("keytype");
                if (keytype.equals("6") || keytype.equals("8")) {
                    currentPage = 0;
                    getNetWorker().replyList("1", songs.get(currentPosition).getId(), "0");
                } else if (keytype.equals("5")) {
                    songs.get(currentPosition).setDyflag("1");
                    tvSave.setTextColor(0xffFFC80C);
                    tvSave.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.save_p, 0, 0, 0);
                } else if (keytype.equals("10")) {
                    songs.get(currentPosition).setDyflag("0");
                    tvSave.setTextColor(0xffffffff);
                    tvSave.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.save_n, 0, 0, 0);
                } else if (keytype.equals("4")) {
                    songs.get(currentPosition).setLoveflag("1");
                    titleBtnRight.setImageResource(R.mipmap.love_p);
                } else if (keytype.equals("1")) {
                    songs.get(currentPosition).setLoveflag("0");
                    titleBtnRight.setImageResource(R.mipmap.love_n);
                }
                EventBus.getDefault().post(new EventBusModel(EventBusConfig.REFRESH_TYPELIVE_LIST));
                break;
            default:
                break;
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
            case DATA_SAVEOPERATE:
                showTextDialog(baseResult.getMsg());
                if (baseResult.getMsg().equals("您已订阅")) {
                    songs.get(currentPosition).setDyflag("1");
                    tvSave.setTextColor(0xffFFC80C);
                    tvSave.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.save_p, 0, 0, 0);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DATA_SAVEOPERATE:
                showTextDialog("操作失败");
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        showDanmaku = false;
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }

    @Override
    protected void findView() {
    }

    @Override
    protected void getExras() {
        name = mIntent.getStringExtra("name");
        songs = (ArrayList<Song>) mIntent.getSerializableExtra("songs");
        currentPosition = mIntent.getIntExtra("position", 0);
    }

    @Override
    protected void setListener() {
        titleText.setText(name);
        titleBtnRight.setImageResource(R.mipmap.love_n);
        //tvZhuboshuo.setText(songs.get(currentPosition).getAuthor_content());
        String Author_content = songs.get(currentPosition).getAuthor_content();
        if (!isNull(Author_content)) {
            Author_content = Author_content.replace("\\n", "\n");
            tvZhuboshuo.setText(Author_content);
        } else {
            tvZhuboshuo.setText("");
        }
    }

    @OnClick({R.id.title_btn_left, R.id.title_btn_right, R.id.iv_open, R.id.tv_save, R.id.tv_share, R.id.tv_reply, R.id.iv_previous, R.id.iv_play, R.id.iv_next, R.id.tv_tip, R.id.lv_center, R.id.lv_content, R.id.lv_replylist})
    public void onViewClicked(View view) {
        Intent it;
        user = BaseApplication.getInstance().getUser();
        switch (view.getId()) {
            case R.id.title_btn_left:
                finish();
                break;
            case R.id.title_btn_right:
                if (user == null) {
                    ToLogin.showLogin(mContext);
                    break;
                }
                if (songs.get(currentPosition).getLoveflag().equals("0")) {
                    getNetWorker().dataOperate(token, "4", songs.get(currentPosition).getId());
                } else {
                    getNetWorker().dataOperate(token, "1", songs.get(currentPosition).getId());
                }
                break;
            case R.id.iv_open:
                if (showDanmaku) {
                    danmakuView.hide();
                    showDanmaku = false;
                } else {
                    danmakuView.show();
                    showDanmaku = true;
                }
                break;
            case R.id.tv_save:
                if (user == null) {
                    ToLogin.showLogin(mContext);
                    break;
                }
                if (songs.get(currentPosition).getDyflag().equals("0")) {
                    getNetWorker().dataOperate(token, "5", songs.get(currentPosition).getChannel_id());
                } else {
                    getNetWorker().dataOperate(token, "10", songs.get(currentPosition).getChannel_id());
                }
                break;
            case R.id.tv_share:
                share();
                break;
            case R.id.tv_reply:
                if (user == null) {
                    ToLogin.showLogin(mContext);
                    break;
                }
                it = new Intent(mContext, ReplyAddActivity.class);
                it.putExtra("live_id", songs.get(currentPosition).getId());
                it.putExtra("comment_id", "0");
                startActivityForResult(it, 1);
                break;
            case R.id.iv_previous:
                if (songs.size() > 0)
                    EventBus.getDefault().post(new EventBusModel(EventBusConfig.PRE, songs, currentPosition));
                break;
            case R.id.iv_play:
                if (songs.size() > 0)
                    EventBus.getDefault().post(new EventBusModel(EventBusConfig.PLAY, songs, currentPosition, 2, Integer.parseInt(songs.get(currentPosition).getChannel_id())));
                break;
            case R.id.iv_next:
                if (songs.size() > 0)
                    EventBus.getDefault().post(new EventBusModel(EventBusConfig.NEXT, songs, currentPosition));
                break;
            case R.id.tv_tip:
                break;
            case R.id.lv_center:
                tvCenter.setTextColor(0xff000000);
                tvContent.setTextColor(0xff212121);
                tvReplylist.setTextColor(0xff212121);
                ivLine1.setVisibility(View.VISIBLE);
                ivLine2.setVisibility(View.INVISIBLE);
                ivLine3.setVisibility(View.INVISIBLE);
                rvContent.setVisibility(View.GONE);
                rvReply.setVisibility(View.GONE);
                tvZhuboshuo.setVisibility(View.VISIBLE);
                refreshLoadmoreLayout.setLoadmoreable(false);
                break;
            case R.id.lv_content:
                tvCenter.setTextColor(0xff212121);
                tvContent.setTextColor(0xff000000);
                tvReplylist.setTextColor(0xff212121);
                ivLine1.setVisibility(View.INVISIBLE);
                ivLine2.setVisibility(View.VISIBLE);
                ivLine3.setVisibility(View.INVISIBLE);
                rvContent.setVisibility(View.VISIBLE);
                rvReply.setVisibility(View.GONE);
                tvZhuboshuo.setVisibility(View.GONE);
                refreshLoadmoreLayout.setLoadmoreable(false);
                break;
            case R.id.lv_replylist:
                tvCenter.setTextColor(0xff212121);
                tvContent.setTextColor(0xff212121);
                tvReplylist.setTextColor(0xff000000);
                ivLine1.setVisibility(View.INVISIBLE);
                ivLine2.setVisibility(View.INVISIBLE);
                ivLine3.setVisibility(View.VISIBLE);
                rvContent.setVisibility(View.GONE);
                rvReply.setVisibility(View.VISIBLE);
                tvZhuboshuo.setVisibility(View.GONE);
                refreshLoadmoreLayout.setLoadmoreable(true);
                break;
        }
    }

    /**
     * 向弹幕View中添加一条弹幕
     *
     * @param content    弹幕的具体内容
     * @param withBorder 弹幕是否有边框
     */
    private void addDanmaku(String content, boolean withBorder) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = sp2px(20);
        danmaku.textColor = Color.BLACK;
        danmaku.setTime(danmakuView.getCurrentTime());
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        danmakuView.addDanmaku(danmaku);
    }

    private void addDanmaKuShowTextAndImage(String avatar, String name, String content, boolean islive) {
        View view = getLayoutInflater().inflate(R.layout.listitem_danmu, null);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        RoundedImageView iv_avatar = (RoundedImageView) view.findViewById(R.id.iv_image);
        iv_avatar.setCornerRadius(100);
        tv_content.setText(content);
        tv_name.setText(name);
        ImageLoader.getInstance().loadImage(avatar, new imgload(iv_avatar, view));
//        ImageLoader.getInstance().displayImage( avatar,iv_avatar, BaseApplication.getInstance()
//                .getOptions(R.mipmap.default_avatar));

    }

    private class imgload implements ImageLoadingListener {
        public imgload(RoundedImageView iv_avatar, View view) {
            this.iv_avatar = iv_avatar;
            this.view0 = view;
        }

        RoundedImageView iv_avatar;
        View view0;

        @Override
        public void onLoadingStarted(String s, View view) {

        }

        @Override
        public void onLoadingFailed(String s, View view, FailReason failReason) {
            iv_avatar.setImageResource(R.mipmap.default_avatar);
            Bitmap bitmap = BaseUtil.convertViewToBitmap(view0);
            BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
            SpannableStringBuilder spannable = createSpannable(bitmap, "");
            danmaku.text = spannable;
            danmaku.padding = 20;
            danmaku.priority = 1;  // 一定会显示, 一般用于本机发送的弹幕
            danmaku.isLive = true;
            danmaku.setTime(danmakuView.getCurrentTime() + 1500);
            danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
            danmakuView.addDanmaku(danmaku);
        }

        @Override
        public void onLoadingComplete(String s, View view, Bitmap bit) {
            iv_avatar.setImageBitmap(bit);
            Bitmap bitmap = BaseUtil.convertViewToBitmap(view0);
            BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
            SpannableStringBuilder spannable = createSpannable(bitmap, "");
            danmaku.text = spannable;
            danmaku.padding = 20;
            danmaku.priority = 1;  // 一定会显示, 一般用于本机发送的弹幕
            danmaku.isLive = true;
            danmaku.setTime(danmakuView.getCurrentTime() + 1500);
            danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
            danmakuView.addDanmaku(danmaku);

        }

        @Override
        public void onLoadingCancelled(String s, View view) {

        }
    }

    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        /**
         * 在弹幕显示前使用新的text,使用新的text
         * @param danmaku
         * @param fromWorkerThread 是否在工作(非UI)线程,在true的情况下可以做一些耗时操作(例如更新Span的drawblae或者其他IO操作)
         * @return 如果不需重置，直接返回danmaku.text
         */
        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
//            if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
//                addDanmaKuShowTextAndImage("http://fangchan.dpthinking.com/uploadfiles/2017/07/201707181254382898.jpg", danmaku.text.toString(), true);
//            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
        }
    };

    /**
     * 创建图文混排模式
     *
     * @param drawable
     * @return
     */
    private SpannableStringBuilder createSpannable(Bitmap drawable, String content) {
        String text = " ";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        ImageSpan span = new ImageSpan(mContext, drawable, ImageSpan.ALIGN_BASELINE);//ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        spannableStringBuilder.append(content);
//        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#ffffff")), 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }

    /**
     * 拖动进度条
     */
    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int progress = seekBar.getProgress();
//                    EventBus.getDefault().post(new EventBusModel(EventBusConfig.MAIN_SEEK, progress));
                    mainActivity.mPlayService.seek(progress);
                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case 1://
//                String content = data.getStringExtra("content");
//                addDanmaKuShowTextAndImage(user.getAvatar(), user.getNickname(), content, true);
                break;
        }
    }

    @SuppressWarnings("deprecation")
    private void share() {
        if (mWindow_exit != null) {
            mWindow_exit.dismiss();
        }
        mWindow_exit = new PopupWindow(mContext);
        mWindow_exit.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow_exit.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow_exit.setBackgroundDrawable(new BitmapDrawable());
        mWindow_exit.setFocusable(true);
        mWindow_exit.setAnimationStyle(R.style.PopupAnimation);
        mViewGroup_exit = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pop_share, null);
        TextView wechat = (TextView) mViewGroup_exit.findViewById(R.id.wechat);
        TextView moment = (TextView) mViewGroup_exit.findViewById(R.id.moment);
        TextView qqshare = (TextView) mViewGroup_exit.findViewById(R.id.qq);
        TextView qzone = (TextView) mViewGroup_exit.findViewById(R.id.zone);
        TextView weibo = (TextView) mViewGroup_exit.findViewById(R.id.weibo);
        TextView cancel = (TextView) mViewGroup_exit.findViewById(R.id.tv_cancel);
        mWindow_exit.setContentView(mViewGroup_exit);
        mWindow_exit.showAtLocation(mViewGroup_exit, Gravity.CENTER, 0, 0);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mWindow_exit.dismiss();
            }
        });
        qqshare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showShare(QQ.NAME);
                mWindow_exit.dismiss();
            }
        });
        wechat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showShare(Wechat.NAME);
                mWindow_exit.dismiss();
            }
        });
        moment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showShare(WechatMoments.NAME);
                mWindow_exit.dismiss();
            }
        });
        qzone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showShare(QZone.NAME);
                mWindow_exit.dismiss();
            }
        });
        weibo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showShare(SinaWeibo.NAME);
                mWindow_exit.dismiss();
            }
        });

    }

    private void showShare(String platform) {
        pathWX = sys_plugins + "share/sdk.php?id=0&keytype=0";
        imageurl = initImagePath();
        if (oks == null) {
            oks = new OnekeyShare();
            oks.setTitle("手机音乐台");
            oks.setTitleUrl(pathWX); // 标题的超链接
            oks.setText("手机音乐台软件");
            oks.setImageUrl(imageurl);
            oks.setFilePath(imageurl);
            oks.setImagePath(imageurl);
            oks.setUrl(pathWX);
            oks.setSiteUrl(pathWX);
            oks.setCallback(this);
        }
        oks.setPlatform(platform);
        oks.show(mContext);
    }

    @Override
    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> hashMap) {
        if (arg0.getName().equals(Wechat.NAME)) {// 判断成功的平台是不是微信
            handler.sendEmptyMessage(1);
        }
        if (arg0.getName().equals(WechatMoments.NAME)) {// 判断成功的平台是不是微信朋友圈
            handler.sendEmptyMessage(2);
        }
        if (arg0.getName().equals(QQ.NAME)) {// 判断成功的平台是不是QQ
            handler.sendEmptyMessage(3);
        }
        if (arg0.getName().equals(QZone.NAME)) {// 判断成功的平台是不是空间
            handler.sendEmptyMessage(4);
        }
        if (arg0.getName().equals(WechatFavorite.NAME)) {// 判断成功的平台是不是微信收藏
            handler.sendEmptyMessage(5);
        }
        if (arg0.getName().equals(SinaWeibo.NAME)) {// 判断成功的平台是不是微博
            handler.sendEmptyMessage(8);
        }
    }

    @Override
    public void onError(Platform arg0, int arg1, Throwable arg2) {
        arg2.printStackTrace();
        Message msg = new Message();
        msg.what = 6;
        msg.obj = arg2.getMessage();
        handler.sendMessage(msg);

    }

    @Override
    public void onCancel(Platform platform, int i) {
        handler.sendEmptyMessage(7);
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(mContext, "微信分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(mContext, "朋友圈分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(mContext, "QQ分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    Toast.makeText(mContext, "QQ空间分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 5:
                    Toast.makeText(mContext, "微信收藏分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 8:
                    Toast.makeText(mContext, "微博分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 7:
                    Toast.makeText(mContext, "取消分享", Toast.LENGTH_LONG).show();
                    break;
                case 6:
                    Toast.makeText(mContext, "分享失败", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    };

    private String initImagePath() {
        String imagePath;
        try {

            String cachePath_internal = XtomFileUtil.getCacheDir(mContext)
                    + "images/";// 获取缓存路径
            File dirFile = new File(cachePath_internal);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            imagePath = cachePath_internal + "share.png";
            File file = new File(imagePath);
            if (!file.exists()) {
                file.createNewFile();
                Bitmap pic;

                pic = BitmapFactory.decodeResource(mContext.getResources(),
                        R.mipmap.ic_launcher);

                FileOutputStream fos = new FileOutputStream(file);
                pic.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            imagePath = null;
        }
        log_i("imagePath:" + imagePath);
        return imagePath;
    }
}
