package radlab.app.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView deadlineView;
    private TextInputLayout description;
    private Spinner categories;

    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        TextView currentDateView = findViewById(R.id.add_date);
        deadlineView = findViewById(R.id.add_deadline);
        description = findViewById(R.id.add_description);
        categories = findViewById(R.id.add_category);

        //update the categories
        List<String> cate = new ArrayList<>();
        cate.add("High");
        cate.add("Medium");
        cate.add("Low");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cate);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        categories.setAdapter(dataAdapter);


        //get the current date
        currentDateView.setText(Tools.getCurrentDate());
    }

    public void closeActivity(View v) {
        finish();
    }

    public void updateDeadlineDate(View v) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        new DatePickerDialog(this, this, year, month, day).show();
    }

    public void saveTask(View v) {

        String currentDesc = description.getEditText().getText().toString();
        String selectedCatgory = (String) categories.getSelectedItem();

        //if the user does not input any test, print error
        if (deadlineView.getText().toString().equals("Task Deadline") ||
                currentDesc.isEmpty()
        ) {
            Toast.makeText(this, "Error. Please check input value", Toast.LENGTH_SHORT).show();
            return;
        }

        //no error was found
        TodoModel todoModel = new TodoModel();
        todoModel.setDeadlineDate(currentDate);
        todoModel.setDescription(currentDesc);
        todoModel.setCategory(selectedCatgory);
        todoModel.setCompleted("no");

        //append to the database
        Tools.addToDatabase(todoModel, getApplicationContext());


        finish();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        currentDate = "" + year + "-" + month + "-" + day;
        deadlineView.setText("" + day + "-" + (month + 1) + "-" + year);
    }
}
