package com.dingapp.biz.page.choosephoto;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dingapp.andriod.z20.R;
import com.dingapp.core.app.BaseFragment;

/**
 * 多图选择
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/1/19.
 */
public class MultiImageSelectorActivity extends BaseFragment implements MultiImageSelectorFragment.Callback{

    /** 最大图片选择次数，int类型，默认9 */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** 图片选择模式，默认多选 */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /** 是否显示相机，默认显示 */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合  */
    public static final String EXTRA_RESULT = "select_result";
    /** 默认选择集 */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    /** 单选 */
    public static final int MODE_SINGLE = 0;
    /** 多选 */
    public static final int MODE_MULTI = 1;

    private ArrayList<String> resultList = new ArrayList<String>();
    private Button mSubmitButton;
    private int mDefaultCount = 3;
    private int mode = MODE_MULTI;
    private boolean isShow = true;
    private  MultiImageSelectorFragment mulFrag;
    @Override
    public View onCreateView(LayoutInflater inflater,
    		@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.activity_default, null);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
        Bundle bundle1 = getArguments();
        if(bundle1!=null){
        	if(bundle1.containsKey(EXTRA_SELECT_COUNT)){
        		mDefaultCount = bundle1.getInt(EXTRA_SELECT_COUNT);
        	}
        	if(bundle1.containsKey(EXTRA_SELECT_MODE)){
        		mode = bundle1.getInt(EXTRA_SELECT_MODE);
        	}
        	if(bundle1.containsKey(EXTRA_SHOW_CAMERA)){
        		isShow = bundle1.getBoolean(EXTRA_SHOW_CAMERA);
        	}
        	if(bundle1.containsKey(EXTRA_DEFAULT_SELECTED_LIST)){
        		resultList = bundle1.getStringArrayList(EXTRA_DEFAULT_SELECTED_LIST);
        	}
        }
        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
        bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);
         mulFrag = new MultiImageSelectorFragment(this);
        mulFrag.setArguments(bundle);
        FragmentTransaction fmTrans = getChildFragmentManager().beginTransaction();
        fmTrans.add(R.id.image_grid,mulFrag);
        fmTrans.commit();

        // 返回按钮
        getView().findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popBack(null);
            }
        });

        // 完成按钮
        mSubmitButton = (Button) getView().findViewById(R.id.commit);
        if(resultList == null || resultList.size()<=0){
            mSubmitButton.setText(R.string.action_done);
            mSubmitButton.setEnabled(false);
        }else{
            updateDoneText();
            mSubmitButton.setEnabled(true);
        }
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resultList != null && resultList.size() >0){
                    // 返回已选择的图片数据
                	Bundle bundle = new Bundle();
                	bundle.putStringArrayList("pic_list", resultList);
                	popBack(bundle);
                }
            }
        });
    }
    //刷新子fragment

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mulFrag.refreshChildFragment(requestCode, resultCode);
    }
    private void updateDoneText(){
        mSubmitButton.setText(String.format("%s(%d/%d)",
                getString(R.string.action_done), resultList.size(), mDefaultCount));
    }

    @Override
    public void onSingleImageSelected(String path) {
        resultList.add(path);
    	Bundle bundle = new Bundle();
    	bundle.putStringArrayList("pic_list", resultList);
    	popBack(bundle);
    }

    @Override
    public void onImageSelected(String path) {
        if(!resultList.contains(path)) {
            resultList.add(path);
        }
        // 有图片之后，改变按钮状态
        if(resultList.size() > 0){
            updateDoneText();
            if(!mSubmitButton.isEnabled()){
                mSubmitButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onImageUnselected(String path) {
        if(resultList.contains(path)){
            resultList.remove(path);
        }
        updateDoneText();
        // 当为选择图片时候的状态
        if(resultList.size() == 0){
            mSubmitButton.setText(R.string.action_done);
            mSubmitButton.setEnabled(false);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {
        if(imageFile != null) {

            // notify system
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
            resultList.add(imageFile.getAbsolutePath());
        	Bundle bundle = new Bundle();
        	bundle.putStringArrayList("pic_list", resultList);
        	popBack(bundle);
            
        }
    }
}
