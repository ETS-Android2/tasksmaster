package com.dandelion.tasksmaster;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    List <Task> allTasks = new ArrayList<>();


    public TaskAdapter(List<Task> allTasks) {
        this.allTasks = allTasks;
    }


    public static class TaskViewHolder extends RecyclerView.ViewHolder{
        public Task task;
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
        taskViewHolder.task = allTasks.get(position);
        TextView taskTitle = taskViewHolder.itemView.findViewById(R.id.taskTitleInFragment);
        TextView taskBody = taskViewHolder.itemView.findViewById(R.id.taskBodyInFragment);
        TextView taskState= taskViewHolder.itemView.findViewById(R.id.taskStateInFragment);

        taskTitle.setText(taskViewHolder.task.title);
        taskBody.setText(taskViewHolder.task.body);
        taskState.setText(taskViewHolder.task.state);
        taskState.setText(taskViewHolder.task.state);


        taskViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TaskDetail.class);
                intent.putExtra("title", allTasks.get(position).title);
                intent.putExtra("body", allTasks.get(position).body);
                intent.putExtra("state", allTasks.get(position).state);
                intent.putExtra("image", allTasks.get(position).image);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allTasks.size();
    }

}
