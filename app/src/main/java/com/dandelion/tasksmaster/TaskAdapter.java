package com.dandelion.tasksmaster;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.TaskQl;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    List <TaskQl> allTasks = new ArrayList<>();


    public TaskAdapter(List<TaskQl> allTasks) {
        this.allTasks = allTasks;
    }


    public static class TaskViewHolder extends RecyclerView.ViewHolder{
        public TaskQl taskQl;
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
        taskViewHolder.taskQl = allTasks.get(position);
        TextView taskTitle = taskViewHolder.itemView.findViewById(R.id.taskTitleInFragment);
        TextView taskBody = taskViewHolder.itemView.findViewById(R.id.taskBodyInFragment);
        TextView taskState= taskViewHolder.itemView.findViewById(R.id.taskStateInFragment);

        taskTitle.setText(taskViewHolder.taskQl.getTitle());
        taskBody.setText(taskViewHolder.taskQl.getBody());
        taskState.setText(taskViewHolder.taskQl.getState());


        taskViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TaskDetail.class);
                intent.putExtra("title", allTasks.get(position).getTitle());
                intent.putExtra("body", allTasks.get(position).getBody());
                intent.putExtra("state", allTasks.get(position).getState());
                intent.putExtra("image", allTasks.get(position).getImage());
                intent.putExtra("latitude",allTasks.get(position).getLatitude());
                intent.putExtra("longitude",allTasks.get(position).getLongitude());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allTasks.size();
    }

}
