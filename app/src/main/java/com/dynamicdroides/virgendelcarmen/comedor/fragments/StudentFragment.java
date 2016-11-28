package com.dynamicdroides.virgendelcarmen.comedor.fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dynamicdroides.virgendelcarmen.comedor.CalendarUtil;
import com.dynamicdroides.virgendelcarmen.comedor.DialogBuilder;
import com.dynamicdroides.virgendelcarmen.comedor.ErrorDialog;
import com.dynamicdroides.virgendelcarmen.comedor.LoginActivity;
import com.dynamicdroides.virgendelcarmen.comedor.MainActivity;
import com.dynamicdroides.virgendelcarmen.comedor.Preferences;
import com.dynamicdroides.virgendelcarmen.comedor.R;
import com.dynamicdroides.virgendelcarmen.comedor.App;
import com.dynamicdroides.virgendelcarmen.comedor.data.PersonData;
import com.dynamicdroides.virgendelcarmen.comedor.data.ProfessorData;
import com.dynamicdroides.virgendelcarmen.comedor.data.StudentData;
import com.dynamicdroides.virgendelcarmen.comedor.data.UserData;
import com.dynamicdroides.virgendelcarmen.service.WSMethod;
import com.dynamicdroides.virgendelcarmen.service.WSRunner;
import com.dynamicdroides.virgendelcarmen.service.data.Professor;
import com.dynamicdroides.virgendelcarmen.service.data.Student;
import com.dynamicdroides.virgendelcarmen.service.impl.WSFinishAssistance;
import com.dynamicdroides.virgendelcarmen.service.impl.WSFinishBehavior;
import com.dynamicdroides.virgendelcarmen.service.impl.WSGetCourses;
import com.dynamicdroides.virgendelcarmen.service.impl.WSGetProfessors;
import com.dynamicdroides.virgendelcarmen.service.impl.WSGetStudentsForDining;

public class StudentFragment extends Fragment implements WSRunner.WSListener
{

	App app;

	List<StudentData> students = new ArrayList<StudentData>();
	List<ProfessorData> professors = new ArrayList<ProfessorData>();

	RecyclerView studentListView;
	FloatingActionButton acceptAll;
	private int totalToDinnerFiltered;
	private int totalToDinner;
	
	public StudentFragment()
	{}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		setHasOptionsMenu(true);

		app = (App)getActivity().getApplication();

		/** View **/
		View view = inflater.inflate(R.layout.fragment_student, container, false);
		
		studentListView = (RecyclerView)view.findViewById(R.id.studentListView);
		studentListView.setLayoutManager(new LinearLayoutManager(getActivity()));
		
		acceptAll = (FloatingActionButton)view.findViewById(R.id.fab);
		acceptAll.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Date today = CalendarUtil.getInitialDailyDate();
				
				String studentList = new String();
				for (StudentData s: students)
					if (s.willDinner)
					{
						if(!studentList.equals(""))
							studentList=studentList+","+(Integer.toString(s.id));
						else
							studentList=(Integer.toString(s.id));
							
					}
				
				String professorList = new String();
				for (ProfessorData p: professors)
					if (p.willDinner)
					{
						if(!professorList.equals(""))
							professorList=professorList+","+(Integer.toString(p.id));
						else
							professorList=(Integer.toString(p.id));
							
					}				
				
				
				new WSRunner(new WSFinishAssistance(studentList, professorList, today), StudentFragment.this).execute();
			}
		});
		
		return view;
	}
	
	public void startCoursesWS()
	{
		UserData user = app.user;
		new WSRunner(new WSGetCourses(), StudentFragment.this).execute();
	}
	
	public void startStudentsWS()
	{
		Date initialDate = CalendarUtil.getInitialDailyDate();
		Date finalDate = CalendarUtil.getFinalDailyDate();

		UserData user = app.user;
		WSRunner wsRunner = new WSRunner(new WSGetStudentsForDining(user.username, user.password, initialDate, finalDate), this);
		wsRunner.execute();
	}
	
	@Override
	public void onPause() 
	{
	    super.onPause();
	    onSaveInstanceState(new Bundle());      
	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState)
	{
		super.onViewStateRestored(savedInstanceState);
		
		Date today = CalendarUtil.getInitialDailyDate();
		Date date = null;
		if (app.savedUser != null && app.user == app.savedUser)
		{
			date = app.savedDate;
			students = app.savedStudentList;
			professors = app.savedProffesorList;
		}
		if (date == null || !today.equals(date) || students == null || students.size() == 0 || professors == null || professors.size() == 0)
		{
			startStudentsWS();
		}
		else
		{
			populateAdapter(app.filterString);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		Date today = CalendarUtil.getInitialDailyDate();
		app.savedStudentList = students;
		app.savedProffesorList = professors;
		app.savedDate = today;
		app.savedUser = app.user;
	}	

	@Override
	public void onWSResult(WSMethod[] methods)
	{
		WSMethod method = methods[0];
        if (method.hasError())
        {
			Log.e(StudentFragment.class.getName(), method.getErrorMessage());
            if (method.getResultType() == WSRunner.WSRunnerResult.FAIL_ON_CONNECTION)
                ErrorDialog.showConnectionError(getActivity());
            else
                ErrorDialog.showResultError(getActivity());
            return;
        }

		if (method instanceof WSGetStudentsForDining)
		{
			WSGetStudentsForDining m = (WSGetStudentsForDining)method;
			students = new ArrayList<StudentData>();
            for (Student s: m.getResult())
                students.add(StudentData.importFrom(s));

			UserData user = app.user;
			((MainActivity)getActivity()).fillDrawerMenu(students);
			new WSRunner(new WSGetProfessors(user.username, user.password), StudentFragment.this).execute();
		}
		if (method instanceof WSGetProfessors)
		{
			WSGetProfessors m = (WSGetProfessors)method;
			professors = new ArrayList<ProfessorData>();
            for (Professor p: m.getResult())
                professors.add(ProfessorData.importFrom(p));

			app.filterString = getString(R.string.action_professors);

			populateAdapter(app.filterString);
		}
		if (method instanceof WSFinishAssistance)
		{
            DialogBuilder.showOneButtonDialog(getActivity(), getString(R.string.dialog_sent_ok));
		}
	}
	
	public void populateAdapter(String filterStr)
	{
        if (filterStr == null || filterStr.equals(""))
            return;

		((MainActivity)getActivity()).getSupportActionBar().setTitle(filterStr);

		List<StudentData> studentList = students;
		List<ProfessorData> professorList = professors;

		totalToDinner = countTotalToDinner(studentList, professorList);
		
		if (filterStr.equals(getString(R.string.action_professors)))
			studentList = new ArrayList<StudentData>();
		else if (filterStr.equals(getString(R.string.action_students)))
			professorList = new ArrayList<ProfessorData>();
		else
		{
			professorList = new ArrayList<ProfessorData>();
			studentList = new ArrayList<StudentData>();
			for (StudentData s: students)
				if (s.course.equals(filterStr))
					studentList.add(s);
		}

        ArrayList<PersonData> persons = new ArrayList<>();
        persons.addAll(professorList);
        persons.addAll(studentList);

		totalToDinnerFiltered = countTotalToDinner(studentList, professorList);
		printSummary();
		
		final StudentListAdapter adapter = new StudentListAdapter(this, persons);
		studentListView.setAdapter(adapter);
	}
	
	private int countTotalToDinner(List<StudentData> studentList, List<ProfessorData> professorList)
	{
		int count = 0;
		for (StudentData s: studentList)
			if (s.willDinner)
				count++;
		for (ProfessorData p: professorList)
			if (p.willDinner)
				count++;
		return count;
	}
	
	private void printSummary()
	{
		String summaryText = getActivity().getString(R.string.summary_text);
		summaryText = summaryText.replace("${selected}", Integer.toString(totalToDinnerFiltered));
		summaryText = summaryText.replace("${total}", Integer.toString(totalToDinner));		
		// summaryTextView.setText(summaryText);
	}
	
}
