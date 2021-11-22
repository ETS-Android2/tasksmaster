package com.dandelion.tasksmaster;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    List <UserTasks> allUserTasks = new ArrayList<>();


    public TaskAdapter(List<UserTasks> allUserTasks) {
        this.allUserTasks = allUserTasks;
    }


    public static class TaskViewHolder extends RecyclerView.ViewHolder{
        public UserTasks userTasks;
        View itemView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_task,viewGroup,false);
        TaskViewHolder taskViewHolder = new TaskViewHolder(view);
        return taskViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder taskViewHolder, @SuppressLint("RecyclerView") int position) {
        taskViewHolder.userTasks = allUserTasks.get(position);
        TextView taskTitle = taskViewHolder.itemView.findViewById(R.id.taskTitleInFragment);
        TextView taskBody = taskViewHolder.itemView.findViewById(R.id.taskBodyInFragment);
        TextView taskState= taskViewHolder.itemView.findViewById(R.id.taskStateInFragment);

        taskTitle.setText(taskViewHolder.userTasks.title);
        taskBody.setText(taskViewHolder.userTasks.body);
        taskState.setText(taskViewHolder.userTasks.state);
        taskState.setText(taskViewHolder.userTasks.state);


        taskViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TaskDetail.class);
                intent.putExtra("title", allUserTasks.get(position).title);
                intent.putExtra("body", allUserTasks.get(position).body);
                intent.putExtra("state", allUserTasks.get(position).state);
                intent.putExtra("image", allUserTasks.get(position).image);
                intent.putExtra("latitude",allUserTasks.get(position).Latitude);
                intent.putExtra("longitude",allUserTasks.get(position).Longitude);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allUserTasks.size();
    }

}
