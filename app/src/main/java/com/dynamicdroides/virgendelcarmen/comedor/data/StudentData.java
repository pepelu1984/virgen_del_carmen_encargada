package com.dynamicdroides.virgendelcarmen.comedor.data;

import com.dynamicdroides.virgendelcarmen.service.data.Behavior;
import com.dynamicdroides.virgendelcarmen.service.data.Student;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudentData extends PersonData
{

	public String course;
	public boolean isDaily;
	public boolean isFormFilled;
	public List<BehaviorData> behavior;
	public Date initialDate;
	public Date finalDate;
	public String emailParents;
	public boolean dinning;
	
	public static StudentData importFrom(Student s)
	{
		boolean isDaily = Boolean.valueOf(s.getCurso()).booleanValue();
		boolean dinning = false;
        ArrayList<BehaviorData> behaviors = new ArrayList<>();
        for (Behavior b: s.getComportamientos())
            behaviors.add(BehaviorData.importFrom(b));
		return new StudentData(s.getIdalumno(), s.getNombre(), s.getApellidos(), s.getCursoname(), isDaily, s.isComportamientoFilled(), behaviors, s.getFechaInicio(), s.getFechaFin(), s.getCorreopadres(), dinning);
	}
	
	public StudentData(int id, String name, String lastName, String course, boolean isDaily, boolean isFormFilled, List<BehaviorData> behavior, Date initialDate, Date finalDate, String emailParents, boolean dinning)
	{
		this.id = id;
		this.name = name;
		this.lastName = lastName;
		this.course = course;
		this.isDaily = isDaily;
		this.isFormFilled = isFormFilled;
		this.behavior = behavior;
		this.initialDate = initialDate;
		this.finalDate = finalDate;
		this.emailParents = emailParents;
		this.dinning = dinning;
		
		this.isEdited = false;
		this.willDinner = true;
	}
	
	@Override
	public String toString()
	{
		return name + " " + lastName;
	}
}
