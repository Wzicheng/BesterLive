package com.neusoft.besterlive.control.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.neusoft.besterlive.R;
import com.neusoft.besterlive.control.activity.MainActivity;
import com.neusoft.besterlive.control.adapter.LiveListAdapter;
import com.neusoft.besterlive.control.http.request.GetLiveRoomRequest;
import com.neusoft.besterlive.model.bean.RoomInfo;
import com.neusoft.besterlive.utils.BaseRequest;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Wzich on 2017/11/1.
 */

public class LiveListFragment extends Fragment {
    private Toolbar mTitlebar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mLiveListView;

    private LiveListAdapter adapter;
    private List<RoomInfo> roomInfoList = new ArrayList<>();
    private int pageIndex = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_list,container,false);
        findAllViews(view);
        setupTitleBar();
        getLiveRoomList(pageIndex);
        return view;
    }

    //获取房间列表信息
    private void getLiveRoomList(final int pageIndex) {
        GetLiveRoomRequest request = new GetLiveRoomRequest();
        request.setOnResultListener(new BaseRequest.OnResultListener<List<RoomInfo>>() {
            @Override
            public void onFail(int code, String msg) {
                Toast.makeText(LiveListFragment.this.getActivity(), "请求失败：" + msg, Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(List<RoomInfo> roomInfos) {
                //刷新，清空原有数据
                if (pageIndex == 0){
                    roomInfoList.clear();
                }

                //添加获取到的新数据
                if (roomInfos != null){
                    roomInfoList.addAll(roomInfos);
                    adapter.notifyDataSetChanged();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        GetLiveRoomRequest.GetLiveRoomListParam param = new GetLiveRoomRequest.GetLiveRoomListParam();
        param.pageIndex = pageIndex;
        request.request(param);
    }

    private void setupTitleBar() {
        mTitlebar.setTitle("直播列表");
        mTitlebar.setTitleTextColor(Color.WHITE);
        Activity activity = getActivity();
        if (activity instanceof AppCompatActivity){
            ((AppCompatActivity)activity).setSupportActionBar(mTitlebar);
        }
    }

    private void findAllViews(View view) {
        mTitlebar = (Toolbar) view.findViewById(R.id.titlebar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_list);
        mLiveListView = (ListView) view.findViewById(R.id.live_list);
        adapter = new LiveListAdapter(getContext(),roomInfoList);
        mLiveListView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex = 0;
                getLiveRoomList(pageIndex);
            }
        });
    }

}
