package com.dynamicdroides.virgendelcarmen.comedor;

import android.app.Activity;
import android.app.Application;

import com.dynamicdroides.virgendelcarmen.comedor.data.ProfessorData;
import com.dynamicdroides.virgendelcarmen.comedor.data.StudentData;
import com.dynamicdroides.virgendelcarmen.comedor.data.UserData;

import java.util.Date;
import java.util.List;

public class App extends Application
{

	public Activity activity;

	public UserData user;
	
	public List<StudentData> savedStudentList;
	public List<ProfessorData> savedProffesorList;
	public Date savedDate;
	public UserData savedUser;
	
	public String filterString;
	
}
