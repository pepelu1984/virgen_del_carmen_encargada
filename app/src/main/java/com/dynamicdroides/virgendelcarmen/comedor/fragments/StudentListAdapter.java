package com.dynamicdroides.virgendelcarmen.comedor.fragments;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dynamicdroides.virgendelcarmen.comedor.R;
import com.dynamicdroides.virgendelcarmen.comedor.data.PersonData;
import com.dynamicdroides.virgendelcarmen.comedor.data.ProfessorData;
import com.dynamicdroides.virgendelcarmen.comedor.data.StudentData;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder>
{

	public class ViewHolder extends RecyclerView.ViewHolder
	{
        private final RelativeLayout layout;
		private final ImageView status;
		private final TextView name;

		ViewHolder(View v)
		{
			super(v);
            layout = (RelativeLayout)v.findViewById(R.id.personCellLayout);
			status = (ImageView)v.findViewById(R.id.personStatusImageView);
			name = (TextView)v.findViewById(R.id.personNameTextView);
		}
	}

    StudentFragment fragment;
    List<PersonData> persons;
	
	public StudentListAdapter(StudentFragment fragment, List<PersonData> persons)
	{
		this.fragment = fragment;
		this.persons = persons;
	}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_person, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        final PersonData item = persons.get(i);
        viewHolder.name.setText(item.name + " " + item.lastName);

        int backgroundId = 0;
        if (item.willDinner)
            backgroundId = R.drawable.icon_yes;
        else
            backgroundId = R.drawable.icon_no;
        viewHolder.status.setBackgroundResource(backgroundId);

        viewHolder.layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                item.willDinner = !item.willDinner;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return persons.size();
    }

}