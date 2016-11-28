package com.dynamicdroides.virgendelcarmen.comedor.data;

import com.dynamicdroides.virgendelcarmen.service.data.Professor;

public class ProfessorData extends PersonData
{

	public String email;
	public String phone;

	public static ProfessorData importFrom(Professor p)
    {
        return new ProfessorData(p.getIdprofesor(), p.getNombre(), p.getApellidos(), p.getEmail(), p.getTelefono());
    }
	
	public ProfessorData(int id, String name, String lastName, String email, String phone)
	{
		this.id = id;
		this.name = name;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		
		this.isEdited = false;
		this.willDinner = true;
	}
	
	@Override
	public String toString()
	{
		return name + " " + lastName;
	}
	
}
