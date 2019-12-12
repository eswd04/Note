package com.mynotead.md;
import android.widget.TextView;
import android.content.Context;
import android.util.AttributeSet;

public class MyTextView extends TextView{
	public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public boolean isFocused() {
		/*
		*用于解决ListView中的TextView不能获取焦点的问题
		*/
        return true;
    }
}
