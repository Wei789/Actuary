package com.example.actuary;


import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class PeriodInv extends Fragment {
	EditText txtEpd;
	EditText txtDpd;
	EditText txtRate;
	EditText txtFvalue;
	RadioButton each_period;
	RadioButton during_preiod;
	RadioButton rate_return;
	RadioButton f_value;
	Button btnCalc;
	Button btnClean;
	Drawable drawEpd;
	Drawable drawDpd;
	Drawable drawRate;
	Drawable drawFvalue;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return initView(inflater, container);
	}

	private View initView(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(R.layout.periodinv, container, false);
		
		txtEpd = (EditText)  view.findViewById(R.id.txtEpd);
		txtDpd = (EditText)  view.findViewById(R.id.txtDpd);
		txtRate = (EditText)  view.findViewById(R.id.txtRate);
		txtFvalue = (EditText)  view.findViewById(R.id.txtFvalue);
		
		txtFvalue.setKeyListener(null);
		txtFvalue.setBackgroundColor(Color.LTGRAY);
		btnCalc = (Button) view.findViewById(R.id.calc);
		btnClean = (Button) view.findViewById(R.id.clean);
		setListener();
		return view;
	}

	/**
	 * 
	 */
	private void setListener()
	{
		btnCalc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				calculate();
			}
		});
		btnClean.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				txtEpd.setText("");
				txtDpd.setText("");
				txtRate.setText("");
				txtFvalue.setText("");
			}
		});
	}

	public void calculate() {
		Double pv = (txtEpd.getText().length() == 0 ? 0d : Double
				.parseDouble(txtEpd.getText().toString()));
		Double n = (txtDpd.getText().length() == 0 ? 0d : Double
				.parseDouble(txtDpd.getText().toString()));
		Double rate = (txtRate.getText().length() == 0 ? 0d : Double
				.parseDouble(txtRate.getText().toString()) / 100);
		int fv = (txtFvalue.getText().length() == 0 ? 0 : Integer.parseInt(txtFvalue.getText().toString()));

		double mRate = rate / 12;
		Double tmpfv = 0d;
		for (int i = 1; i <= n; i++) {
			tmpfv += pv * Math.pow((1 + mRate), i);
		}
		tmpfv = Math.round(tmpfv)+0.0;
		fv = tmpfv.intValue();
		txtFvalue.setText(String.valueOf(fv));
	
//		if (each_period.isChecked()) {
//			Double tmp = 0d;
//			for (int i = 1; i <= n; i++) {
//				tmp += Math.pow((1 + mRate), i);
//			}
//			pv = fv / tmp;
//			txtEpd.setText(pv.toString());
//		} else if (during_preiod.isChecked()) {
//			Double t = -Math.round((Math.log10((pv * (1 + rate) - fv * rate)
//					/ (pv * (1 + rate))) / Math.log10(1 + rate))) + 0.0;
//			txtDpd.setText(t.toString());
//		} else if (rate_return.isChecked()) {
//			rate = Math.round((Math.pow((fv / pv), (1 / n)) - 1))+0.0;
////			fv = 0d;
////			for (int i = 1; i <= n; i++) {
////				fv = pv * Math.pow((1 + mRate), i);
////				rate += Math.round((Math.pow((fv / pv), (1 / i)) - 1));
////				
////				
////			}
//			rate = (rate/12) * 100 * 100) / 100.0;
//			txtRate.setText(rate.toString());
//		} else if (f_value.isChecked()) {
//
//			for (int i = 1; i <= n; i++) {
//				fv += pv * Math.pow((1 + mRate), i);
//			}
//			txtFvalue.setText(fv.toString());
//		}
	}
}
