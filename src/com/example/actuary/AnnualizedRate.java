package com.example.actuary;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AnnualizedRate extends Fragment
{
	EditText txtPV;
	EditText txtFV;
	EditText txtPeriod;
	EditText txtRateRtn;
	EditText txtcumRateReutrn;
	EditText txtFrequency;
	RadioGroup mRadioGroup;
	RadioButton presentValue;
	RadioButton futureValue;
	RadioButton period;
	RadioButton rateReutrn;
	RadioButton cumRateReutrn;
	Drawable drawPV;
	Drawable drawFV;
	Drawable drawPeriod;
	Drawable drawrateReturn;
	Drawable drawCumRateReturn;
	Drawable drawFrequency;
	Button btnCalc;
	Button btnClean;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return initView(inflater, container);
	}

	private View initView(LayoutInflater inflater, ViewGroup container)
	{
		View view = inflater.inflate(R.layout.annualizedrate, container, false);

		txtPV = (EditText) view.findViewById(R.id.txtPV);
		txtPeriod = (EditText) view.findViewById(R.id.txtPeriod);
		txtRateRtn = (EditText) view.findViewById(R.id.txtRateRtn);
		txtFV = (EditText) view.findViewById(R.id.txtFV);
		txtcumRateReutrn = (EditText) view.findViewById(R.id.txtCumRateRtn);
		txtFrequency = (EditText) view.findViewById(R.id.txtFrequency);
		presentValue = (RadioButton) view.findViewById(R.id.presentValue);
		futureValue = (RadioButton) view.findViewById(R.id.futureValue);
		period = (RadioButton) view.findViewById(R.id.period);
		rateReutrn = (RadioButton) view.findViewById(R.id.rateReutrn);
		cumRateReutrn = (RadioButton) view.findViewById(R.id.cumRateReutrn);
		btnCalc = (Button) view.findViewById(R.id.calc);
		btnClean = (Button) view.findViewById(R.id.clean);
		
		//¬ö¿ý±Ò¥Îª¬ºA&­I´ºÃC¦â
		txtPV.setTag(txtPV.getKeyListener());
		txtFV.setTag(txtFV.getKeyListener());
		txtPeriod.setTag(txtPeriod.getKeyListener());
		txtRateRtn.setTag(txtRateRtn.getKeyListener());
		txtcumRateReutrn.setTag(txtcumRateReutrn.getKeyListener());
		drawPV = txtPV.getBackground();
		drawFV = txtFV.getBackground();
		drawPeriod = txtPeriod.getBackground();
		drawrateReturn = txtRateRtn.getBackground();
		drawCumRateReturn = txtcumRateReutrn.getBackground();
		txtRateRtn.setKeyListener(null);
		txtRateRtn.setBackgroundColor(Color.LTGRAY);
		
		setListener();

		return view;
	}

	private void setListener()
	{
		btnCalc.setOnClickListener(calculate);
		btnClean.setOnClickListener(clean);
		presentValue.setOnClickListener(onRadioButtonClicked);
		futureValue.setOnClickListener(onRadioButtonClicked);
		period.setOnClickListener(onRadioButtonClicked);
		rateReutrn.setOnClickListener(onRadioButtonClicked);
		cumRateReutrn.setOnClickListener(onRadioButtonClicked);
	}

	private Button.OnClickListener calculate = new Button.OnClickListener()
	{

		@Override
		public void onClick(View arg0)
		{
			// get editText value -bigdecimal
			Double pv = (txtPV.getText().length() == 0 ? 0 : Double.parseDouble(txtPV.getText()
					.toString()));
			Double fv = (txtFV.getText().length() == 0 ? 0 : Double.parseDouble(txtFV.getText()
					.toString()));
			Double n = (txtPeriod.getText().length() == 0 ? 0 : Double.parseDouble(txtPeriod
					.getText().toString()));
			Double rate = (txtRateRtn.getText().length() == 0 ? 0 : Double.parseDouble(txtRateRtn
					.getText().toString()) / 100);
			Double m = (txtFrequency.getText().length() == 0 ? 0 : Double.parseDouble(txtFrequency
					.getText().toString()));
			Double cRate = 0d;
			// calculate
			if (rateReutrn.isChecked())
			{
				Double rateRtn = Math.round((Math.pow((fv / pv), (1 / (m * n))) - 1) * m * 100
						* 100) / 100.0;
				cRate = Math.round((fv / pv - 1) * 100 * 100) / 100.0;
				txtRateRtn.setText(rateRtn.toString());
			} else if (presentValue.isChecked())
			{
				Double pValue = Long.valueOf(Math.round(fv / Math.pow((1 + rate / m), m * n)))
						.doubleValue();
				cRate = Math.round((fv / pValue - 1) * 100 * 100) / 100.0;
				txtPV.setText(pValue.toString());
			} else if (futureValue.isChecked())
			{
				Double fValue = Long.valueOf(Math.round(pv * Math.pow((1 + rate / m), m * n)))
						.doubleValue();
				cRate = Math.round((fValue / pv - 1) * 100 * 100) / 100.0;
				txtFV.setText(fValue.toString());
			} else if (period.isChecked())
			{
				Double t = Math.round((Math.log10((-fv * rate) / (-pv * rate)) / Math
						.log10(1 + rate))) + 0.0;
				cRate = Math.round((fv / pv - 1) * 100 * 100) / 100.0;
				txtPeriod.setText(t.toString());
			}
			// else if(frequency.isChecked()){
			// Double f =
			// Long.valueOf(Math.round(Math.log10((-fv*rate)/(-pv*rate))/Math.log10(1+rate))).doubleValue()/n;
			// cRate = Long.valueOf(Math.round((fv/pv-1)*100)).doubleValue();
			// txtFrequency.setText(f.toString());
			// }
			txtcumRateReutrn.setText(cRate.toString());

		}

	};

	private Button.OnClickListener clean = new OnClickListener()
	{
		@Override
		public void onClick(View arg0)
		{
			txtPV.setText("");
			txtFV.setText("");
			txtPeriod.setText("");
			txtRateRtn.setText("");
			txtcumRateReutrn.setText("");
			txtFrequency.setText("");
		}
	};
	private RadioButton.OnClickListener onRadioButtonClicked = new RadioButton.OnClickListener()
	{
		public void onClick(View view)
		{
			// Is the button now checked?
			boolean checked = ((RadioButton) view).isChecked();
			if (checked)
			{
				txtPV.setKeyListener((KeyListener) txtPV.getTag());
				txtFV.setKeyListener((KeyListener) txtFV.getTag());
				txtPeriod.setKeyListener((KeyListener) txtPeriod.getTag());
				txtRateRtn.setKeyListener((KeyListener) txtRateRtn.getTag());
				txtcumRateReutrn.setKeyListener((KeyListener) txtcumRateReutrn.getTag());
				presentValue.setChecked(false);
				futureValue.setChecked(false);
				period.setChecked(false);
				rateReutrn.setChecked(false);
				cumRateReutrn.setChecked(false);
				txtPV.setBackground(drawPV);
				txtFV.setBackground(drawFV);
				txtRateRtn.setBackground(drawrateReturn);
				txtcumRateReutrn.setBackground(drawCumRateReturn);
				txtPeriod.setBackground(drawPeriod);

				// Check which radio button was clicked
				switch (view.getId())
				{
				case R.id.presentValue:
					txtPV.setKeyListener(null);
					txtPV.setBackgroundColor(Color.LTGRAY);
					presentValue.setChecked(true);
					break;
				case R.id.futureValue:
					txtFV.setKeyListener(null);
					txtFV.setBackgroundColor(Color.LTGRAY);
					futureValue.setChecked(true);
					break;
				case R.id.period:
					txtPeriod.setKeyListener(null);
					txtPeriod.setBackgroundColor(Color.LTGRAY);
					period.setChecked(true);
					break;
				case R.id.rateReutrn:
					txtRateRtn.setKeyListener(null);
					txtRateRtn.setBackgroundColor(Color.LTGRAY);
					rateReutrn.setChecked(true);
					break;
				case R.id.cumRateReutrn:
					txtcumRateReutrn.setKeyListener(null);
					txtcumRateReutrn.setBackgroundColor(Color.LTGRAY);
					cumRateReutrn.setChecked(true);
					break;
				// case R.id.frequency:
				// txtFrequency.setKeyListener(null);
				// txtFrequency.setBackgroundColor(Color.LTGRAY);
				// frequency.setChecked(true);
				// break;
				}
			}

		}
	};
}
