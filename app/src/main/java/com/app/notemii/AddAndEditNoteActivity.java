package com.app.notemii;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddAndEditNoteActivity extends AppCompatActivity {
    public static String TAG = "AddAndEditNoteActivity";
    public static final int PICK_IMAGE = 1;

    private TextView inputText;
    private TextView title;
    private EditText note_title;
    private Button addButton;
    private CheckBox checkBox;
    private ImageView image;
    private ImageButton imageButton;

    private ReadAndWriteSnippets snippets;
    private String noteId;
    private String noteTitle;
    private String noteContent;
    private String dateAlarm;
    private String timeAlarm;

    private EditText dateEditText;
    private EditText timeEditText;
    private final Calendar myCalendar = Calendar.getInstance();

    private ImageView zoomImage;


    final static int RQS_1 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_and_edit_note);

        inputText = findViewById(R.id.editText);
        addButton = findViewById(R.id.add_button);
        title = findViewById(R.id.add_note_title);
        note_title = findViewById(R.id.note_title);
        dateEditText = findViewById(R.id.date_input);
        timeEditText = findViewById(R.id.time_input);
        checkBox = findViewById(R.id.checkBox);
        image = findViewById(R.id.image_view);
        zoomImage = findViewById(R.id.zoom_image);
        imageButton  = findViewById(R.id.add_image_button);

        snippets = new ReadAndWriteSnippets(this);

        if(getIntent().getExtras() != null){
            noteId = getIntent().getExtras().getString(BoardActivity.NOTE_ID);
            noteTitle = getIntent().getExtras().getString(BoardActivity.NOTE_TITLE);
            noteContent = getIntent().getExtras().getString(BoardActivity.NOTE_CONTENT);
            timeAlarm = getIntent().getExtras().getString(BoardActivity.NOTE_ALARM_DATE);
            dateAlarm = getIntent().getExtras().getString(BoardActivity.NOTE_ALARM_TIME);



        }

        if(noteId != null) {
            inputText.setText(noteContent);
            title.setText(getString(R.string.edit_title));
            note_title.setText(noteTitle);

            if(!timeAlarm.isEmpty() && !dateAlarm.isEmpty()) {
                //Show checkBox
                dateEditText.setVisibility(EditText.VISIBLE);
                timeEditText.setVisibility(EditText.VISIBLE);
                dateEditText.setText(getIntent().getExtras().getString(BoardActivity.NOTE_ALARM_DATE));
                timeEditText.setText(getIntent().getExtras().getString(BoardActivity.NOTE_ALARM_TIME));

                checkBox.setChecked(true);
            }

            //snippets.downloadPhoto(noteId, image, imageButton);

        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dateEditText.setVisibility(EditText.VISIBLE);
                    timeEditText.setVisibility(EditText.VISIBLE);
                }else {
                    dateEditText.setText("");
                    timeEditText.setText("");
                    dateEditText.setVisibility(EditText.INVISIBLE);
                    timeEditText.setVisibility(EditText.INVISIBLE);
                }
            }
        });

        initDatePicker();
        initTimePicker();

    }

    private void initTimePicker() {
        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                updateTimeLabel();
            }
        };

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddAndEditNoteActivity.this, time, myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE), true).show();
            }
        });
    }

    private void updateTimeLabel() {
        timeEditText.setText(myCalendar.get(Calendar.HOUR_OF_DAY)+ ":"+myCalendar.get(Calendar.MINUTE));
    }

    private void initDatePicker(){
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
            }

        };

        dateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddAndEditNoteActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    private void updateDateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateEditText.setText(sdf.format(myCalendar.getTime()));
    }

    public void onAddButtonClick(View view) {
        String noteTitle = note_title.getText().toString().trim();
        String noteContent = inputText.getText().toString().trim();

        if(noteTitle.equals("")){
            note_title.setError(getString(R.string.empty_content));
            return;
        }

        if(noteContent.equals("")){
            inputText.setError(getString(R.string.empty_content));
            return;
        }

        if(checkBox.isChecked()){
            boolean flg = true;
            if(dateEditText.getText().toString().trim().equals("")){
                dateEditText.setError(getString(R.string.empty_content));
                flg = false;
            }
            if(timeEditText.getText().toString().trim().equals("")){
                timeEditText.setError(getString(R.string.empty_content));
                flg = false;
            }
            if(!flg)
                return;
            else if(myCalendar.after(Calendar.getInstance()))
                setAlarm(myCalendar, noteContent);
            else{
                dateEditText.setError(getString(R.string.empty_content));
                timeEditText.setError(getString(R.string.empty_content));
                return;
            }


        }

        Date c = Calendar.getInstance().getTime();
        Log.i(TAG, "onAddButtonClick: Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM dd, yyyy");
        String formattedDate = df.format(c);

        if(noteId == null)
            snippets.writeNote(noteTitle, noteContent, formattedDate, dateEditText.getText().toString().trim(),
                    timeEditText.getText().toString().trim(), FirebaseAuth.getInstance().getUid());
        else
            snippets.editNote(noteId, noteTitle, noteContent, formattedDate, dateEditText.getText().toString().trim(),
                    timeEditText.getText().toString().trim(), FirebaseAuth.getInstance().getUid());


        startActivity(new Intent(this, BoardActivity.class));
    }

    private void setAlarm(Calendar targetCal, String noteContent){

        Toast.makeText(this, "Alarm is set @" + targetCal.getTime(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getBaseContext(), MyAlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putString(MyAlarmReceiver.TITLE, noteContent);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

    }

    public void onAddImageButtonClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                image.setVisibility(View.VISIBLE);
                image.setImageBitmap(selectedImage);
                zoomImage.setImageBitmap(selectedImage);
                snippets.uploadImage(imageUri, noteId);
                imageButton.setEnabled(false);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    public void onImageClick(View view) {
        Toast.makeText(getApplicationContext(), "FULLSCREEN!", Toast.LENGTH_LONG).show();
        zoomImage.setVisibility(View.VISIBLE);
    }

    public void onZoomImageClick(View view) {
        Toast.makeText(getApplicationContext(), "NORMAL SIZE!", Toast.LENGTH_LONG).show();
        zoomImage.setVisibility(View.GONE);
    }
}
