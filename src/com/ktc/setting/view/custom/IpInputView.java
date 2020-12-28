package com.ktc.setting.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.setting.R;
import com.ktc.setting.helper.NetworkHelper;

public class IpInputView extends RelativeLayout {

    private TextView addressTitle;
    private IpEditText addressOne;
    private IpEditText addressTwo;
    private IpEditText addressThree;
    private IpEditText addressFour;

    private String strTitle;
    private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                if (view instanceof EditText) {
                    EditText editText = (EditText) view;
                    if (editText.isEnabled())
                        editText.selectAll();
                }
            }
        }
    };

    public IpInputView(Context context) {
        this(context, null);
    }

    public IpInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IpInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_input_ip, this);
        bindViews(context, attrs, defStyleAttr);
    }

    private void bindViews(Context context, AttributeSet attrs, int defStyle) {
        addressTitle = (TextView) findViewById(R.id.ip_edit_text_title);
        addressOne = (IpEditText) findViewById(R.id.ip_input_1);
        addressTwo = (IpEditText) findViewById(R.id.ip_input_2);
        addressThree = (IpEditText) findViewById(R.id.ip_input_3);
        addressFour = (IpEditText) findViewById(R.id.ip_input_4);
        addressOne.setDuplicateParentStateEnabled(true);

        initViews(context, attrs, defStyle);
        addListener();
    }

    private void initViews(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IpInputView, defStyle, 0);
        strTitle = a.getString(R.styleable.IpInputView_title);
        a.recycle();
        addressTitle.setText(strTitle);
    }

    private void addListener() {
        addressOne.setOnFocusChangeListener(mOnFocusChangeListener);
        addressTwo.setOnFocusChangeListener(mOnFocusChangeListener);
        addressThree.setOnFocusChangeListener(mOnFocusChangeListener);
        addressFour.setOnFocusChangeListener(mOnFocusChangeListener);
    }

    public String getAddress() {
        StringBuffer address = new StringBuffer();
        address.append(addressOne.getText().toString().trim());
        address.append(".");
        address.append(addressTwo.getText().toString().trim());
        address.append(".");
        address.append(addressThree.getText().toString().trim());
        address.append(".");
        address.append(addressFour.getText().toString().trim());

        return address.toString();
    }

    public void setAddress(int address) {
        String[] array = NetworkHelper.resolutionIP(NetworkHelper.int2Ip(address));

        addressOne.setText(array[0]);
        addressTwo.setText(array[1]);
        addressThree.setText(array[2]);
        addressFour.setText(array[3]);
    }

    public void setAddress(String address) {
        String[] array = NetworkHelper.resolutionIP(address);

        addressOne.setText(array[0]);
        addressTwo.setText(array[1]);
        addressThree.setText(array[2]);
        addressFour.setText(array[3]);
    }

    public boolean isSameAddress(int address) {
        return NetworkHelper.int2Ip(address).equals(getAddress());
    }

    @Override
    public void setEnabled(boolean b) {
        setDescendantFocusability(b ? ViewGroup.FOCUS_AFTER_DESCENDANTS : ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        setAlpha(b ? (float) 1.0 : (float) 0.5);
    }
}
