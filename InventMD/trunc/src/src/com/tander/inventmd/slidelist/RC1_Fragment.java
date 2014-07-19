package com.tander.inventmd.slidelist;


import com.tander.inventmd.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("NewApi")
public class RC1_Fragment extends Fragment {

	TextView tvRC1;
	
	private Context myContext;

	public RC1_Fragment(Context context) {
		super();
		myContext = context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.rc1_fragment, container, false);

		tvRC1 = (TextView) rootView.findViewById(R.id.tvRC1);
		

		return rootView;
	}
	
	

}
