package com.dynamicdroides.virgendelcarmen.comedor;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dynamicdroides.virgendelcarmen.comedor.R;
import com.dynamicdroides.virgendelcarmen.comedor.data.StudentData;
import com.dynamicdroides.virgendelcarmen.comedor.data.UserData;
import com.dynamicdroides.virgendelcarmen.comedor.fragments.FragmentNavigator;
import com.dynamicdroides.virgendelcarmen.comedor.fragments.StudentFragment;
import com.dynamicdroides.virgendelcarmen.service.WSMethod;
import com.dynamicdroides.virgendelcarmen.service.WSRunner;
import com.dynamicdroides.virgendelcarmen.service.data.Course;
import com.dynamicdroides.virgendelcarmen.service.impl.WSCheckUser;
import com.dynamicdroides.virgendelcarmen.service.impl.WSGetCourses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WSRunner.WSListener
{

    public static MainActivity instance = null;

	App app;

	DrawerLayout drawerLayout;
	NavigationView navigationView;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
        instance = this;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		app = (App)getApplication();
		app.activity = this;
        // WSRunner.setBaseUrl("http://10.42.0.1:8080/dynamicschool-app/");
        WSRunner.setBaseUrl("http://www.dynamicdroides.com:8080/SpringLoader/");
		FragmentNavigator.fragmentActivity = this;

		Preferences pref = new Preferences(app.activity);
		String user = pref.get(Preferences.PREF_USERNAME);
		String password = pref.get(Preferences.PREF_PASSWORD);
		String fullName = pref.get(Preferences.PREF_FULLNAME);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.vdc_form_background)));
		actionBar.setDisplayHomeAsUpEnabled(true);

		drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

		navigationView = (NavigationView)findViewById(R.id.navigation_view);
		View headerView = navigationView.inflateHeaderView(R.layout.drawer_header);
		TextView userNameTextView = (TextView)headerView.findViewById(R.id.drawer_header_user_name);
		userNameTextView.setText(fullName);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item)
            {
                if (item.getItemId() == R.id.action_exit)
                {
                    Preferences pref = new Preferences(MainActivity.this);
                    // pref.put(Preferences.PREF_USERNAME, null);
                    pref.put(Preferences.PREF_PASSWORD, null);
                    FragmentNavigator.cleanBackStack();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }

                StudentFragment sf = (StudentFragment)FragmentNavigator.getDefaultFragment();
                sf.populateAdapter(item.getTitle().toString());
                drawerLayout.closeDrawers();
                return true;
            }
        });

		if ((user != null && !user.equals("")) && password != null && !password.equals(""))
		{
			new WSRunner(new WSCheckUser(user, password), this).execute();
		}
		else
		{
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
            drawerLayout.openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onWSResult(WSMethod[] methods)
	{
        if (methods[0] instanceof WSCheckUser)
        {
            WSCheckUser m = (WSCheckUser)methods[0];
            if (m.hasError())
            {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            UserData result = UserData.importFrom(m.getResult());
            app.user = result;
            Preferences pref = new Preferences(app.activity);
            pref.put(Preferences.PREF_USERNAME, result.username);
            pref.put(Preferences.PREF_PASSWORD, result.password);

            Fragment f = FragmentNavigator.getDefaultFragment();
            FragmentNavigator.add(f, false);
        }
	}

    public void fillDrawerMenu(List<StudentData> students)
    {
        ArrayList<String> courses = new ArrayList<>();
        for (StudentData data: students)
            if (!courses.contains(data.course))
                courses.add(data.course);
        Collections.sort(courses);

        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.student);
        for (String c: courses)
            navigationView.getMenu().add(c);
    }

}
