package radlab.app.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class ListActivity extends AppCompatActivity {

    private TextView statusView;
    TaskAdapter taskAdapter;

    List<TodoModel> todoModels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);

        TextView currentDateView = findViewById(R.id.list_date);
        statusView = findViewById(R.id.list_status);
        RecyclerView recyclerView = findViewById(R.id.list_recycle);

        //get the current date
        currentDateView.setText(Tools.getCurrentDate());

        //get all task data from database and update with list
        todoModels = Tools.getAllDatabase(getApplicationContext());
        taskAdapter = new TaskAdapter(todoModels);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(taskAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //update status view by getting the total completed task
        updateStatusView();

        //get new new fresh data
        todoModels.clear();
        todoModels.addAll(Tools.getAllDatabase(getApplicationContext()));
        taskAdapter.notifyDataSetChanged();

    }

    //add a new task button clicked
    public void addNewTask(View c){
        startActivity(new Intent(this, AddActivity.class));
    }

    private void updateStatusView(){
        //return all data from the database
        List<TodoModel> todoModels = Tools.getAllDatabase(getApplicationContext());

        final int totalTask = todoModels.size();
        int totalCompletedTask = 0;  //this will be updated

        for(TodoModel model: todoModels){

            //increment the completed task count if it is mark yes
            if(model.getCompleted().equals("yes")){
                totalCompletedTask = totalCompletedTask + 1;
            }
        }

        //update the status view
        String statusText = "" + totalCompletedTask + "/" +  totalTask + " completed"; // e.g. 1/4 completed
        statusView.setText(statusText);
    }

    private void editTask(final int position){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_edit_task);

        Button completeTask = dialog.findViewById(R.id.edit_complete);
        Button deleteTask = dialog.findViewById(R.id.edit_delete);
        Button doNothing = dialog.findViewById(R.id.edit_nothing);


        final TodoModel todoModel = todoModels.get(position);

        //mark task as complete and close when clicked
        completeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todoModel.setCompleted("yes");

                //update the database
                Tools.updateDatabase(todoModels,getApplicationContext());
                taskAdapter.notifyDataSetChanged();

                dialog.dismiss();
            }
        });

        //delete current selected task and close when clicked
        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                todoModels.remove(position);
                //update the database
                Tools.updateDatabase(todoModels,getApplicationContext());
                taskAdapter.notifyDataSetChanged();

                dialog.dismiss();
            }
        });

        // just close dialog when clicked
        doNothing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //update the database
                Tools.updateDatabase(todoModels,getApplicationContext());
                taskAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    //recycler adapter class for the list of all Task
    private class TaskAdapter extends RecyclerView.Adapter{
        private List<TodoModel> todoModels;

        TaskAdapter(List<TodoModel> todoModels){
            this.todoModels = todoModels;
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_todo,parent,false);

            return new CustomViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

            CustomViewHolder customViewHolder = (CustomViewHolder) holder;
            TodoModel currentModel = todoModels.get(position);

            //set the description
            customViewHolder.description.setText(currentModel.getDescription());
            customViewHolder.priority.setText(currentModel.getCategory());
            customViewHolder.checkBox.setChecked(false);

            String []dateSplit = currentModel.getDeadlineDate().split("-");
            String month = Tools.monthName[Integer.parseInt(dateSplit[1])]; //index of 1 holds month
            String parseDate = dateSplit[2] + " " + month;
            customViewHolder.deadline.setText(parseDate);

            //draw a stroke stroke line across description
            customViewHolder.description.setPaintFlags(customViewHolder.description.getPaintFlags()  & (~ Paint.STRIKE_THRU_TEXT_FLAG));



            if(currentModel.getCompleted().equals("yes")){
                customViewHolder.checkBox.setChecked(true);

                //draw a stroke stroke line across description
                customViewHolder.description.setPaintFlags(customViewHolder.description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            customViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editTask(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return todoModels.size();
        }


        private void stateChange(int position){
            editTask(position);
        }


        private class CustomViewHolder extends RecyclerView.ViewHolder{

            TextView description;
            CheckBox checkBox;
            TextView priority;
            TextView deadline;

            CustomViewHolder(View v){
                super(v);

                description = v.findViewById(R.id.custom_description);
                checkBox =  v.findViewById(R.id.custom_check);
                priority =  v.findViewById(R.id.custom_priority);
                deadline =  v.findViewById(R.id.custom_deadline);

                //for updating or removing task
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stateChange(getAdapterPosition());
                    }
                });


            }
        }

    }
}



