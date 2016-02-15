package com.example.actuary;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends Activity
{

	private DrawerLayout layDrawer;
	private ExpandableListView listView;
	private LinearLayout llv_left_drawer;
	private ActionBarDrawerToggle drawerToggle;
	private LayoutInflater inflater;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private SparseArray<Group> groups = new SparseArray<Group>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer);
		inflater = getLayoutInflater();
		initActionBar();
		findView();
		initDrawer();
		initDrawListAdp();
	}

	// ================================================================================
	// Init actionbar
	// ================================================================================
	private void initActionBar()
	{
		// �ҥ�button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// ��ܤT���u�Ϯ״����ϥΪ̥i�H�I api 14 �H�W
		getActionBar().setHomeButtonEnabled(true);
	}

	private void findView()
	{
		// ���Եe��
		layDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		// ���Ԧ����e��
		listView = (ExpandableListView) findViewById(R.id.listView);
		listView.setOnGroupClickListener(groupClick);
		listView.setOnChildClickListener(childClick);
		llv_left_drawer = (LinearLayout) findViewById(R.id.llv_left_drawer);
	}

	private OnGroupClickListener groupClick = new OnGroupClickListener()
	{

		@Override
		public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
		{
			Fragment fragment = null;
			switch (groupPosition)
			{
			case 0:
				break;
			case 1:
				fragment = new Note();
				break;
			case 2:
				fragment = new WsStock();
				break;			
			default:
				return false;
			}
			if (fragment != null)
			{
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

				// ��s�Q��ܶ��ءA�����D��r�A�������
				// listView.setItemChecked(position, true);

				setTitle(groups.get(groupPosition).string);
				layDrawer.closeDrawer(llv_left_drawer);
			}

			return false;
		}
	};

	private OnChildClickListener childClick = new OnChildClickListener()
	{

		@Override
		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
				int childPosition, long id)
		{
			Fragment fragment = null;
			String combine = "" + groupPosition + childPosition;
			if ("00".equals(combine))
			{
				fragment = new AnnualizedRate();
			} else if ("01".equals(combine))
			{
				fragment = new PeriodInv();
			}
			if (fragment != null)
			{
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
				setTitle(groups.get(groupPosition).children.get(childPosition));
				layDrawer.closeDrawer(llv_left_drawer);
			}
			return false;
		}
	};

	private void initDrawer()
	{
		// �Ψӳ]�w�����Q�}�Үɪ����v�C
		layDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mTitle = mDrawerTitle = getTitle();
		drawerToggle = new ActionBarDrawerToggle(this, layDrawer, R.drawable.ic_drawer,
				R.string.drawer_open, R.string.drawer_close)
		{
			// �Ƽg���function open & close�����ʧ@�`�|�b�䤤��s ActionBar �����D�A�άO��ܬ����� Action
			// button�C
			@Override
			public void onDrawerClosed(View view)
			{
				super.onDrawerClosed(view);
				getActionBar().setTitle(mTitle);
			}

			@Override
			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(mDrawerTitle);
			}
		};
		// ��ActionBar ������^�b���m����Drawer ���T���u�ϥܡC
		drawerToggle.syncState();

		layDrawer.setDrawerListener(drawerToggle);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		// home
		if (drawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initDrawListAdp()
	{
		createData();
		ExpandableListAdapter adapter = new BaseExpandableListAdapter()
		{
			@Override
			public Object getChild(int groupPosition, int childPosition)
			{
				return groups.get(groupPosition).children.get(childPosition);
			}

			@Override
			public long getChildId(int groupPosition, int childPosition)
			{
				return 0;
			}

			@Override
			public View getChildView(int groupPosition, final int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent)
			{
				final String children = (String) getChild(groupPosition, childPosition);

				if (convertView == null)
				{
					convertView = inflater.inflate(R.layout.listrow_details, null);
				}
				final TextView text = (TextView) convertView.findViewById(R.id.textView1);
				text.setText(children);
				return convertView;
			}

			@Override
			public int getChildrenCount(int groupPosition)
			{
				return groups.get(groupPosition).children.size();
			}

			@Override
			public Object getGroup(int groupPosition)
			{
				return groups.get(groupPosition);
			}

			@Override
			public int getGroupCount()
			{
				return groups.size();
			}

			@Override
			public void onGroupCollapsed(int groupPosition)
			{
				super.onGroupCollapsed(groupPosition);
			}

			@Override
			public void onGroupExpanded(int groupPosition)
			{
				super.onGroupExpanded(groupPosition);
			}

			@Override
			public long getGroupId(int groupPosition)
			{
				return 0;
			}

			@Override
			public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
					ViewGroup parent)
			{
				if (convertView == null)
				{
					convertView = inflater.inflate(R.layout.listrow_group, null);
				}

				Group group = (Group) getGroup(groupPosition);
				((CheckedTextView) convertView).setText(group.string);
				((CheckedTextView) convertView).setChecked(isExpanded);
				return convertView;
			}

			@Override
			public boolean hasStableIds()
			{
				return false;
			}

			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition)
			{
				return true;
			}
		};

		listView.setAdapter(adapter);

	}

	public void createData()
	{
		Group group = new Group("�z�]�պ�");
		group.children.add("��s��I");
		group.children.add("�w���w�B");
		groups.append(0, group);

		group = new Group("���O��");
		groups.append(1, group);
		group = new Group("�Ѳ�");
		groups.append(2, group);
//		group = new Group("GoogleMap");
//		groups.append(3, group);
	}

	@Override
	public void setTitle(CharSequence title)
	{
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}
}