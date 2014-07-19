package com.tander.inventmd;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class EditTextEx extends EditText {
	public EditTextEx(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EditTextEx(Context context) {
		super(context);
	}

	public EditTextEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			dispatchKeyEvent(event);
			return false;
		}
		return super.onKeyPreIme(keyCode, event);
	}
}
