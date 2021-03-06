package com.asiainfo.testapp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;
import com.asiainfo.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.asiainfo.model.PublishData;
import com.asiainfo.model.SfsErrorCode;
import com.asiainfo.model.SfsResult;
import com.asiainfo.model.User;

import java.util.ArrayList;

public  class PublishListFragment extends Fragment implements onSfsDataReceiver {
    int mNum;
    public static final String TAG="PublishListFragment";
    public static PublishListFragment newInstance(int num) {
        PublishListFragment f = new PublishListFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }


    private SfsListView mpubItemListView;
    private pubItemAdapter mpubItemAdpater;
    private boolean mIsLoading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;


    }



    //这里是初始化页面内容的函数，我们的监听就是在这儿
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("MYDEBUG", "CreateView " + mNum);
        View v = inflater.inflate(R.layout.board_list, container, false);
        mpubItemListView = (SfsListView) v.findViewById(R.id.pubboard);
        mpubItemAdpater = new pubItemAdapter(inflater);
        ((sfsFrame)getActivity()).registerSfsDataReciever(this);
        mpubItemListView.setAdapter(mpubItemAdpater);
        mpubItemListView.setonRefreshListener(new SfsListView.OnRefreshListener() {
            public void onRefresh() {
                loadHistory();
            }
        });



        Intent intent = new Intent();
        intent.setClass(getActivity(), com.asiainfo.testapp.sfsService.class);
        intent.setAction("QueryPublishData");

        intent.putExtra("User", ((sfsFrame) getActivity()).getUser());
        getActivity().startService(intent);

        intent = new Intent();
        intent.setClass(getActivity(), com.asiainfo.testapp.sfsService.class);
        intent.setAction("ClearNotify");

        //intent.putExtra("User", ((sfsFrame) getActivity()).getUser());
        getActivity().startService(intent);

//        text.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                System.out.println("点击成功！");
//            }
//        });
        return v;
    }


    @Override
    public void onDestroyView() {
        ((sfsFrame)getActivity()).unregisterSfsDataReciever(this);
        super.onDestroyView();    //To change body of overridden methods use File | Settings | File Templates.
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDataCome(Intent intent) {
        SfsResult res = intent.getParcelableExtra("UI_RESULT");
        if (res.err_code == SfsErrorCode.Success) {
            ArrayList<PublishData> pblist  = intent.getParcelableArrayListExtra("PublicDatas");
            if (pblist != null) {
                for (int i=0;i<pblist.size();i++) {
                    PublishData d =  pblist.get(i);
                    pubItemAdapter.pubItem item = new pubItemAdapter.pubItem();
                    item.pubName = d.nick_name;
                    item.pubContext = d.pub_context;
                    item.pubImg = d.context_img;

                    mpubItemAdpater.add(item);
                }
                mpubItemListView.requestFocusFromTouch();
                mpubItemListView.setSelection(0);
                mIsLoading = false;
                Toast.makeText(getActivity(),"收到"+pblist.size()+"条记录，一共"+ mpubItemAdpater.getCount(),Toast.LENGTH_SHORT).show();
                //mpubItemListView.setAdapter(mpubItemAdpater);
            }
        } else {
            Toast.makeText(getActivity(),res.err_msg+" code= "+res.err_code,Toast.LENGTH_LONG).show();
        }


    }

    private void loadHistory(){
        Intent intent = new Intent();
        intent.setClass(getActivity(), com.asiainfo.testapp.sfsService.class);
        intent.setAction("QueryPublishData");

        intent.putExtra("User", ((sfsFrame) getActivity()).getUser());
        getActivity().startService(intent);
        mIsLoading = true;

    }


//    @Override
//    public void onItemClick(ListView l, View v, int position, long id) {
//        Log.i("FragmentList", "Item clicked: " + id);
//    }




}