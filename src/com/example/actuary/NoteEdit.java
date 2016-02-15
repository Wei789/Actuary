/**
 * 
 */
package com.example.actuary;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Administrator
 *
 */
public class NoteEdit extends Activity
{
	private DB mDbHelper;
	private EditText field_note;
	private Button button_confirm;
	private Long mRowID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new DB(this);
		mDbHelper.open();
		setContentView(R.layout.note_edit);
		findViews();
		showViews();
	}
	
	/** Find View*/
	private void findViews()
	{
		field_note = (EditText)findViewById(R.id.noteEdit);
		button_confirm = (Button)findViewById(R.id.btnOk);
	}
	
	/**取得DB中note 內文*/
	private void showViews(){
		if(mRowID == null){
			Bundle extras = getIntent().getExtras();
			if(extras != null){
				mRowID = extras.getLong(DB.KEY_ROWID);
			}else{
				mRowID = null;
			}
		}
		
		if(mRowID != null)
		{
			Cursor note = mDbHelper.get(mRowID);
			field_note.setText(note.getString(note.getColumnIndexOrThrow(DB.KEY_NOTE)));
		}
		
		button_confirm.setOnClickListener(new  View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				mDbHelper.update(mRowID, field_note.getText().toString(),null);
				setResult(RESULT_OK);
				finish();
			}
		});
	}
}

