package com.news.gamersky;

import static com.news.gamersky.util.AppUtil.format;
import static com.news.gamersky.util.AppUtil.is2s;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.news.gamersky.adapter.RepliesAdapter;
import com.news.gamersky.databean.CommentDataBean;
import com.news.gamersky.util.NightModeUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RepliesActivity extends AppCompatActivity {
    private String commentId;
    private String clubContentId;
    private ConstraintLayout constraintLayout;
    private TextView headTextView;
    private ImageView mask;
    private ImageButton imageButton;
    private CommentDataBean comment;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<CommentDataBean> repliesData;
    private RepliesAdapter repliesAdapter;
    private Point point;
    private int page;
    private int flag;
    private int lastFlag;
    private ExecutorService executor;
    private AnimatorSet animSetIn;
    private AnimatorSet animSetOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replies);
        init();
        //loadReplies();
        startListen();
    }

    public void init() {

        if (!NightModeUtil.isNightMode(this)) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        } else {
            getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        headTextView = findViewById(R.id.textView20);
        constraintLayout = findViewById(R.id.constraintLayout);
        imageButton = findViewById(R.id.imageButton);
        mask = findViewById(R.id.imageView13);
        Intent intent = getIntent();
        commentId = intent.getStringExtra("commentId");
        clubContentId = intent.getStringExtra("clubContentId");
        System.out.println(commentId + "    " + clubContentId);
        comment = (CommentDataBean) intent.getSerializableExtra("comment");
        headTextView.setText(comment.userName + "的回复");
        point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);
        constraintLayout.setY(point.y);
        mask.setAlpha(0f);
        constraintLayout.setTranslationY(point.y);
        repliesData = new ArrayList<>();

        recyclerView = findViewById(R.id.replies_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        repliesAdapter = new RepliesAdapter(this, comment, repliesData);
        recyclerView.setAdapter(repliesAdapter);

        page = 1;
        flag = 0;
        lastFlag = 0;
        executor = Executors.newSingleThreadExecutor();
        animSetIn = new AnimatorSet();
        animSetOut = new AnimatorSet();
        animSetIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                loadReplies();
                animSetIn.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        startAnimator();


    }

    public void loadReplies() {
        page = 1;
        flag = 0;
        lastFlag = 0;
        repliesAdapter.setNoMore(false);
        if (clubContentId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String pageIndex = "1";
                        String pageSize = "10";
                        String src = "https://club.gamersky.com/club/api/getclubitemactivity?" +
                                "jsondata=" +
                                "{\"commentId\":" + commentId + "," +
                                "\"clubContentId\":" + clubContentId + "," +
                                "\"pageIndex\":" + pageIndex + "," +
                                "\"pageSize\":" + pageSize + "," +
                                "}";
                        URL url = new URL(src);
                        //得到connection对象。
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        //设置请求方式
                        connection.setRequestMethod("GET");
                        //连接
                        connection.connect();
                        //得到响应码
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            //得到响应流
                            InputStream inputStream = connection.getInputStream();
                            //将响应流转换成字符串
                            String result = is2s(inputStream);//将流转换为字符串
                            result = result.substring(1, result.length() - 1);
                            System.out.println(result);
                            JSONObject jsonObject = new JSONObject(result);
                            String s = jsonObject.getString("body");
                            Document doc = Jsoup.parse(s);
                            Elements es1 = doc.getElementsByClass("ccmt_reply_cont");
                            String repliesCommentId = "";
                            for (int i = 0; i < es1.size(); i++) {
                                Element e1 = es1.get(i);
                                String objectUserName = comment.userName;
                                try {
                                    objectUserName = e1.getElementsByClass("uname").get(1).html();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                String contentReply = e1.getElementsByClass("content").get(0).html();
                                try {
                                    String temp = e1.getElementsByClass("ccmt_all").attr("data-content");
                                    if (!temp.equals("")) contentReply = temp;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                repliesData.add(new CommentDataBean(
                                        e1.attr("cmtid"),
                                        e1.getElementsByTag("img").get(0).attr("src"),
                                        e1.getElementsByClass("uname").get(0).html(),
                                        e1.getElementsByClass("ccmt_time").get(0).html(),
                                        "赞:" + e1.getElementsByClass("digg-btn").get(0).html(),
                                        contentReply,
                                        objectUserName

                                ));

                                repliesCommentId += e1.attr("cmtid") + ",";

                            }
                            System.out.println(repliesCommentId);
                            String src1 = "https://club.gamersky.com/club/api/getcommentlike?" +
                                    "jsondata=" +
                                    "{\"commentIds\":" + "\"" + repliesCommentId + "\"" + "}";
                            URL url1 = new URL(src1);
                            //得到connection对象。
                            HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
                            //设置请求方式
                            connection1.setRequestMethod("GET");
                            //连接
                            connection1.connect();
                            //得到响应码
                            int responseCode1 = connection1.getResponseCode();
                            String s2 = "0";
                            if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                //得到响应流
                                InputStream inputStream1 = connection1.getInputStream();
                                //将响应流转换成字符串
                                String result1 = is2s(inputStream1);//将流转换为字符串。
                                result1 = result1.substring(1, result1.length() - 1);
                                System.out.println(result1);
                                JSONObject jsonObject1 = new JSONObject(result1);
                                JSONArray jsonArray = new JSONArray(jsonObject1.getString("body"));
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(j);
                                    repliesData.get(j).setLikeNum("赞:" + jsonObject2.getString("digg"));
                                }
                            }
                            connection.disconnect();
                            connection1.disconnect();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    repliesAdapter.notifyDataSetChanged();
                                    if (repliesData.size() < 10) {
                                        repliesAdapter.setNoMore(true);
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String pageIndex = "1";
                        String pageSize = "10";
                        String src = "https://cm.gamersky.com/appapi/getCommentRepliesListWithClubStyle?" +
                                "request=" +
                                "{\"commentId\":" + commentId + "," +
                                "\"pageIndex\":" + pageIndex + "," +
                                "\"pageSize\":" + pageSize + "," +
                                "\"order\":\"timeASC\"}";
                        URL url = new URL(src);
                        //得到connection对象。
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        //设置请求方式
                        connection.setRequestMethod("GET");
                        //连接
                        connection.connect();
                        //得到响应码
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            //得到响应流
                            InputStream inputStream = connection.getInputStream();
                            //将响应流转换成字符串
                            String result = is2s(inputStream);//将流转换为字符串
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray jsonArray1 = jsonObject.getJSONObject("result").getJSONArray("replies");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                repliesData.add(new CommentDataBean(
                                        jsonObject1.getString("replyId"),
                                        jsonObject1.getString("userHeadImageURL"),
                                        jsonObject1.getString("userName"),
                                        format(jsonObject1.getLong("createTime")),
                                        "赞:" + jsonObject1.getString("praisesCount"),
                                        jsonObject1.getString("replyContent"),
                                        jsonObject1.getString("objectUserName")

                                ));

                            }
                            connection.disconnect();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    repliesAdapter.notifyDataSetChanged();
                                    if (repliesData.size() < 10) {
                                        repliesAdapter.setNoMore(true);
                                    }
                                    startAnimator();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startAnimator();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    public Thread loadMoreReplies() {
        final String lastReplyId = repliesData.get(repliesData.size() - 1).replyId;
        if (clubContentId != null) {
            return new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        page++;
                        String pageSize = "10";
                        String src = "https://club.gamersky.com/club/api/getclubitemactivity?" +
                                "jsondata=" +
                                "{\"commentId\":" + commentId + "," +
                                "\"clubContentId\":" + clubContentId + "," +
                                "\"pageIndex\":" + page + "," +
                                "\"pageSize\":" + pageSize + "," +
                                "}";
                        URL url = new URL(src);
                        //得到connection对象。
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        //设置请求方式
                        connection.setRequestMethod("GET");
                        //连接
                        connection.connect();
                        //得到响应码
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            //得到响应流
                            InputStream inputStream = connection.getInputStream();
                            //将响应流转换成字符串
                            String result = is2s(inputStream);//将流转换为字符串
                            result = result.substring(1, result.length() - 1);
                            System.out.println(result);
                            JSONObject jsonObject = new JSONObject(result);
                            String s = jsonObject.getString("body");
                            Document doc = Jsoup.parse(s);
                            Elements es1 = doc.getElementsByClass("ccmt_reply_cont");
                            String repliesCommentId = "";
                            int loadRepliesNum = es1.size();
                            for (int i = 0; i < es1.size(); i++) {
                                Element e1 = es1.get(i);
                                String objectUserName = comment.userName;
                                try {
                                    objectUserName = e1.getElementsByClass("uname").get(1).html();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                String contentReply = e1.getElementsByClass("content").get(0).html();
                                try {
                                    String temp = e1.getElementsByClass("ccmt_all").attr("data-content");
                                    if (!temp.equals("")) contentReply = temp;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                repliesData.add(new CommentDataBean(
                                        e1.attr("cmtid"),
                                        e1.getElementsByTag("img").get(0).attr("src"),
                                        e1.getElementsByClass("uname").get(0).html(),
                                        e1.getElementsByClass("ccmt_time").get(0).html(),
                                        "赞:" + e1.getElementsByClass("digg-btn").get(0).html(),
                                        contentReply,
                                        objectUserName

                                ));

                                repliesCommentId += e1.attr("cmtid") + ",";

                            }
                            System.out.println(repliesCommentId);
                            if (!repliesCommentId.equals("")) {
                                String src1 = "https://club.gamersky.com/club/api/getcommentlike?" +
                                        "jsondata=" +
                                        "{\"commentIds\":" + "\"" + repliesCommentId + "\"" + "}";
                                URL url1 = new URL(src1);
                                //得到connection对象。
                                HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
                                //设置请求方式
                                connection1.setRequestMethod("GET");
                                //连接
                                connection1.connect();
                                //得到响应码
                                int responseCode1 = connection1.getResponseCode();
                                String s2 = "0";
                                if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                    //得到响应流
                                    InputStream inputStream1 = connection1.getInputStream();
                                    //将响应流转换成字符串
                                    String result1 = is2s(inputStream1);//将流转换为字符串。
                                    result1 = result1.substring(1, result1.length() - 1);
                                    System.out.println(result1);
                                    JSONObject jsonObject1 = new JSONObject(result1);
                                    JSONArray jsonArray = new JSONArray(jsonObject1.getString("body"));
                                    for (int j = 0; j < jsonArray.length(); j++) {
                                        JSONObject jsonObject2 = jsonArray.getJSONObject(j);
                                        repliesData.get(j).setLikeNum("赞:" + jsonObject2.getString("digg"));
                                    }
                                }
                                connection1.disconnect();
                            }
                            connection.disconnect();
                            final int finalLoadRepliesNum = loadRepliesNum;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    repliesAdapter.notifyItemRangeInserted(repliesAdapter.getItemCount(), finalLoadRepliesNum);
                                    String nowReplyId = repliesData.get(repliesData.size() - 1).replyId;
                                    if (lastReplyId.equals(nowReplyId)) {
                                        repliesAdapter.setNoMore(true);
                                        repliesAdapter.notifyItemChanged(repliesAdapter.getItemCount() - 1);
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        flag = lastFlag;
                    }
                }
            });

        } else {
            return new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        page++;
                        String pageSize = "10";
                        String src = "https://cm.gamersky.com/appapi/getCommentRepliesListWithClubStyle?" +
                                "request=" +
                                "{\"commentId\":" + commentId + "," +
                                "\"pageIndex\":" + page + "," +
                                "\"pageSize\":" + pageSize + "," +
                                "\"order\":\"timeASC\"}";
                        URL url = new URL(src);
                        //得到connection对象。
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        //设置请求方式
                        connection.setRequestMethod("GET");
                        //连接
                        connection.connect();
                        //得到响应码
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            //得到响应流
                            InputStream inputStream = connection.getInputStream();
                            //将响应流转换成字符串
                            String result = is2s(inputStream);//将流转换为字符串
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray jsonArray1 = jsonObject.getJSONObject("result").getJSONArray("replies");
                            int loadRepliesNum = jsonArray1.length();
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                repliesData.add(new CommentDataBean(
                                        jsonObject1.getString("replyId"),
                                        jsonObject1.getString("userHeadImageURL"),
                                        jsonObject1.getString("userName"),
                                        format(jsonObject1.getLong("createTime")),
                                        "赞:" + jsonObject1.getString("praisesCount"),
                                        jsonObject1.getString("replyContent"),
                                        jsonObject1.getString("objectUserName")

                                ));

                            }
                            connection.disconnect();
                            final int finalLoadRepliesNum = loadRepliesNum;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    repliesAdapter.notifyItemRangeInserted(repliesAdapter.getItemCount(), finalLoadRepliesNum);
                                    String nowReplyId = repliesData.get(repliesData.size() - 1).replyId;
                                    if (lastReplyId.equals(nowReplyId)) {
                                        repliesAdapter.setNoMore(true);
                                        repliesAdapter.notifyItemChanged(repliesAdapter.getItemCount() - 1);
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        flag = lastFlag;
                        page--;
                    }
                }
            });
        }

    }

    public void upTop() {
        recyclerView.smoothScrollToPosition(0);
    }

    public void startAnimator() {
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(constraintLayout, "translationY", constraintLayout.getTranslationY(), 0f);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(mask, "alpha", mask.getAlpha(), 1f);
        objectAnimator1.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animSetIn.play(objectAnimator1).with(objectAnimator2);
        animSetIn.setDuration(300);
        animSetIn.start();
    }

    public void endAnimator() {
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(constraintLayout, "translationY", constraintLayout.getTranslationY(), point.y);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(mask, "alpha", mask.getAlpha(), 0f);
        objectAnimator1.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animSetOut.play(objectAnimator1).with(objectAnimator2);
        animSetOut.setDuration(300);
        animSetOut.start();
        animSetOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    public void startListen() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastItem = layoutManager.findLastVisibleItemPosition();
                int dataNum = repliesData.size();
                int line = dataNum;
                if (lastItem > dataNum) {
                    line = dataNum + 1;
                }
                //System.out.println(lastItem+"      "+flag+"       "+line);
                if (lastItem > 10 && lastItem != flag && lastItem == line) {
                    lastFlag = flag;
                    flag = lastItem;
                    System.out.println("加载评论");
                    executor.submit(loadMoreReplies());
                }

            }

        });
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            int yVelocity = 0;
            int lastYVelocity = 0;
            float ry = 0;
            float dis = 0;
            boolean canScrollVertically = false;
            boolean isCanScrollVertically = false;
            boolean consumed = false;
            VelocityTracker velocityTracker;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Log.i("TAG", event.toString());
                velocityTracker = VelocityTracker.obtain();
                velocityTracker.addMovement(event);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;

                    case MotionEvent.ACTION_MOVE:
                        consumed = false;
                        if (animSetIn.isRunning() || animSetOut.isRunning()) break;
                        if (ry == 0f) {
                            ry = event.getY();
                            isCanScrollVertically = recyclerView.canScrollVertically(-1);
                        }
                        dis = constraintLayout.getTranslationY() + event.getY() - ry;
                        if (!recyclerView.canScrollVertically(-1) && dis > 0) {
                            if (recyclerView.canScrollVertically(-1) == canScrollVertically) {
                                constraintLayout.setTranslationY(dis);
                                consumed = true;
                            } else {
                                ry = event.getY();
                            }
                        } else if (!recyclerView.canScrollVertically(-1) && dis < 0) {
                            constraintLayout.setTranslationY(0);
                        }
                        canScrollVertically = recyclerView.canScrollVertically(-1);
                        velocityTracker.computeCurrentVelocity(1000);
                        if (velocityTracker.getYVelocity() > 0) {
                            lastYVelocity = (int) velocityTracker.getYVelocity();
                        }
                        if (yVelocity < (int) velocityTracker.getYVelocity()) {
                            yVelocity = (int) velocityTracker.getYVelocity();
                        }
                        return consumed;
                    case MotionEvent.ACTION_UP:
                        ry = 0f;
                        consumed = false;
                        //Log.i("TAG", "onTouch: "+yVelocity+"\t"+lastYVelocity);
                        velocityTracker.recycle();
                        if (yVelocity > 3000 && lastYVelocity > 3000 && !isCanScrollVertically) {
                            onBackPressed();
                            yVelocity = 0;
                            lastYVelocity = 0;
                            break;
                        } else {
                            yVelocity = 0;
                            lastYVelocity = 0;
                        }
                        if (constraintLayout.getTranslationY() > constraintLayout.getHeight() / 3f) {
                            onBackPressed();
                        } else {
                            startAnimator();
                        }
                        break;
                }
                return consumed;
            }
        });

        headTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upTop();
            }
        });
        headTextView.setOnTouchListener(new View.OnTouchListener() {
            float ry = 0;
            float dis = 0;
            int yVelocity = 0;
            int lastYVelocity = 0;
            boolean consumed = false;
            VelocityTracker velocityTracker;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                velocityTracker = VelocityTracker.obtain();
                velocityTracker.addMovement(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ry = event.getY();
                        consumed = false;
                        yVelocity = 0;
                        lastYVelocity = 0;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        consumed = true;
                        dis = constraintLayout.getTranslationY() + event.getY() - ry;
                        if (dis > 0)
                            constraintLayout.setTranslationY(dis);
                        velocityTracker.computeCurrentVelocity(1000);
                        if (velocityTracker.getYVelocity() > 0) {
                            lastYVelocity = (int) velocityTracker.getYVelocity();
                        }
                        if (yVelocity < (int) velocityTracker.getYVelocity()) {
                            yVelocity = (int) velocityTracker.getYVelocity();
                        }

                        //Log.i("TAG", "onTouch: "+velocityTracker.getYVelocity());
                        break;

                    case MotionEvent.ACTION_UP:
                        consumed = false;
                        velocityTracker.getYVelocity();
                        //Log.i("TAG", "onTouch: "+yVelocity+"\t"+velocityTracker.getYVelocity());
                        velocityTracker.recycle();
                        if (yVelocity > 3000 && lastYVelocity > 3000) {
                            onBackPressed();
                            break;
                        }
                        if (constraintLayout.getTranslationY() > constraintLayout.getHeight() / 3f) {
                            onBackPressed();
                        } else {
                            startAnimator();
                        }
                        if (constraintLayout.getTranslationY() != 0f) {
                            consumed = true;
                        }
                        break;
                }

                return consumed;
            }
        });

    }

    @Override
    public void onBackPressed() {
        endAnimator();
        //super.onBackPressed();
    }


}