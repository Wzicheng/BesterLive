package com.neusoft.besterlive.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.neusoft.besterlive.control.adapter.MsgListAdapter;
import com.neusoft.besterlive.model.bean.MsgInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wzich on 2017/11/22.
 */

public class MsgListView extends ListView {
    private MsgListAdapter adapter;
    private List<MsgInfo> msgs = new ArrayList<>();

    public MsgListView(Context context) {
        super(context);
        init();
    }

    public MsgListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MsgListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        adapter = new MsgListAdapter(getContext(),msgs);
        setAdapter(adapter);
        setDividerHeight(0);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    public void addMsg(MsgInfo msgInfo){
        if (msgInfo != null){
            msgs.add(msgInfo);
            adapter.notifyDataSetChanged();
            setSelection(msgs.size());
        }
    }
}
