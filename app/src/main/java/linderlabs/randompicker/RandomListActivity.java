package linderlabs.randompicker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class RandomListActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_WRITE = 1;
    public static final int REQUEST_CODE_READ = 2;
    private String listType;
    private ArrayList<String> listOfItems = new ArrayList<String>();
    private EditText item;
    private ImageButton trashButton;
    private LinearLayout listLayout;
    private static final String FOOD_FILE_NAMES = "food_list_names";
    private static final String ACTIVITIES_FILE_NAMES = "activities_list_names";
    private static final String CUSTOM_FILE_NAMES = "custom_list_names";
    private String saveFileName = "";
    private String[] fileNames;
    private String currentActivityFileName = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Bundle extras = this.getIntent().getExtras();
        if(extras != null)
        {
            listType = extras.getString("type");
            if(listType.equals("food"))
            {
                getSupportActionBar().setTitle("Pick a Restaurant");
                currentActivityFileName = FOOD_FILE_NAMES;
            }else if(listType.equals("activities"))
            {
                getSupportActionBar().setTitle("Pick an Activity");
                currentActivityFileName = CUSTOM_FILE_NAMES;
            }else if(listType.equals("custom"))
            {
                getSupportActionBar().setTitle("Anything List");
                currentActivityFileName = ACTIVITIES_FILE_NAMES;
            }

        }

        item = findViewById(R.id.addItem);
        item.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (keyEvent.getAction() == KeyEvent.ACTION_UP)
                {
                    switch (i)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            addItemToList(view);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        listLayout = findViewById(R.id.listItems);
        trashButton = findViewById(R.id.trashButton);

        loadLastList(listType);

    }

    private void loadLastList(String listType) {

            try {
                FileInputStream names = openFileInput(currentActivityFileName);
                InputStreamReader isr = new InputStreamReader(names);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                //read the file line by line
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                //put the contents in string
                String content = sb.toString();
                fileNames = content.split("\\n");
                int length = fileNames.length;
                if(length != 0 && fileNames[length- REQUEST_CODE_WRITE] != null)
                {
                    //open a filestream to read the file
                    names = openFileInput(fileNames[length- REQUEST_CODE_WRITE]);
                    isr = new InputStreamReader(names);
                    bufferedReader = new BufferedReader(isr);
                    sb = new StringBuilder();
                    String line2;
                    //read the file line by line
                    while ((line2 = bufferedReader.readLine()) != null) {
                        sb.append(line2).append("\n");
                    }
                    //put the contents in string
                    String contentNew = sb.toString();
                    String[] listItems = contentNew.split("\\n");
                    for(String item : listItems)
                    {
                        listOfItems.add(item);
                        addItemToLayout(item);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    public void addItemToList(View view) {

        String itemToAdd = item.getText().toString();
        if(itemToAdd.length() == 0)
        {
            Toast.makeText(getApplicationContext(), "Can't pick from nothing!", Toast.LENGTH_LONG).show();
            return;
        }
        item.setText("");
        listOfItems.add(itemToAdd);
        addItemToLayout(itemToAdd);



    }

    private void addItemToLayout(String itemToAdd) {

        final TextView temp = new TextView(getApplicationContext());
        temp.setText(itemToAdd);
        temp.setTextColor(Color.BLACK);
        temp.setTextSize(30);
        temp.setPadding(20, 0,20, 0);
        temp.isClickable();
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {

                if(!temp.isSelected())
                {
                    temp.setBackgroundColor(getResources().getColor(R.color.flagColor));
                    temp.setSelected(true);
                    trashButton.setVisibility(View.VISIBLE);
                }else
                {
                    temp.setBackgroundColor(Color.WHITE);
                    temp.setSelected(false);

                    final int childCount = listLayout.getChildCount();
                    boolean selected = false;
                    for(int i = 0; i < childCount; i++)
                    {
                        View v = listLayout.getChildAt(i);
                        if(v != null && v.isSelected())
                        {
                            selected = true;
                        }
                    }
                    if(!selected)
                    {
                        trashButton.setVisibility(View.GONE);
                    }
                }
            }
        });
        listLayout.addView(temp);
    }

    public void pickOne(View view) {

        if(listOfItems == null || listOfItems.size() == 0)
        {
            Toast.makeText(getApplicationContext(), "Put some items in your list first!", Toast.LENGTH_LONG).show();
            return;
        }

        int numberOfItems = listOfItems.size();

        Random ran = new Random();
        int index = ran.nextInt(numberOfItems) ;
        String item = listOfItems.get(index);

        AlertDialog.Builder alert = new AlertDialog.Builder(RandomListActivity.this);
        alert.setTitle("Your Pick!");
        alert.setMessage("I picked " + item + " for you, enjoy!" );
        alert.setPositiveButton("Thanks!", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

//        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });

        alert.show();
    }

    public void saveList()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Writing to storage permission has not been granted.
            requestWritePermission();

        } else {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Save List as:");

            // Set up the input
            final EditText input = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            input.setPadding(20, 30, 0, 30);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveFileName = input.getText().toString();
                    try {
                        FileOutputStream outputStream = openFileOutput(saveFileName, Context.MODE_PRIVATE);

                        for (String temp : listOfItems) {
                            if (temp != null) {
                                outputStream.write(temp.getBytes());
                                outputStream.write("\n".getBytes());
                            }
                        }
                        outputStream.close();

                        FileOutputStream outputStream2 = openFileOutput(currentActivityFileName, Context.MODE_APPEND);
                        outputStream2.write(saveFileName.getBytes());
                        outputStream2.write("\n".getBytes());
                        outputStream2.close();


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "File not found!", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Trouble writing to the file", Toast.LENGTH_LONG).show();
                    }


                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }


    public void loadList()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Reading storage permission has not been granted.
            requestReadPermission();

        } else {
            try {
                //open a filestream to read the file
                FileInputStream names = openFileInput(currentActivityFileName);
                InputStreamReader isr = new InputStreamReader(names);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                //read the file line by line
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                //put the contents in string
                String content = sb.toString();
                fileNames = content.split("\\n");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Load List");
            if (fileNames == null || fileNames.length == 0) {
                builder.setTitle("No lists have been created yet");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
            builder.setItems(fileNames, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listLayout.removeAllViews();
                    listOfItems.clear();
                    try {
                        //open a filestream to read the file
                        FileInputStream names = openFileInput(fileNames[which]);
                        InputStreamReader isr = new InputStreamReader(names);
                        BufferedReader bufferedReader = new BufferedReader(isr);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        //read the file line by line
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        //put the contents in string
                        String content = sb.toString();
                        String[] listItems = content.split("\\n");
                        for (String item : listItems) {
                            listOfItems.add(item);
                            addItemToLayout(item);
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            builder.show();
        }
    }

    public void deleteSavedList()
    {
        try {
            //open a filestream to read the file
            FileInputStream names = openFileInput(currentActivityFileName);
            InputStreamReader isr = new InputStreamReader(names);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            //read the file line by line
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            //put the contents in string
            String content = sb.toString();
            fileNames = content.split("\\n");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select File to Delete");
        builder.setItems(fileNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFile(fileNames[which]);
                fileNames[which] = null;

                FileOutputStream outputStream2 = null;
                try {
                    outputStream2 = openFileOutput(currentActivityFileName, Context.MODE_PRIVATE);

                    for(String temp :fileNames) {
                        if(temp != null)
                        {
                            outputStream2.write(temp.getBytes());
                            outputStream2.write("\n".getBytes());
                        }

                    }
                    outputStream2.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        builder.show();
    }


    public void deleteSelected(View view) {
        final int childCount = listLayout.getChildCount();
        for(int i = 0; i < childCount; i++)
        {
            TextView v = (TextView)listLayout.getChildAt(i);

            if(v != null && v.isSelected())
            {
                listOfItems.remove(v.getText().toString());
            }
        }
        listLayout.removeAllViews();
        for(String temp : listOfItems)
        {
            if(temp != null) {
                addItemToLayout(temp);
            }
        }

        trashButton.setVisibility(View.GONE);
    }
    private void requestWritePermission() {
        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Camera permission has not been granted yet. Request it directly.
            showExplanation("Permission Needed to Save Files", "This permission is needed to save the lists to files located on your device, that is all.", Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_CODE_WRITE);

        }else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_WRITE);
        }
        // END_INCLUDE(camera_permission_request)
    }
    private void requestReadPermission() {
        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Camera permission has not been granted yet. Request it directly.
            showExplanation("Permission Needed to Load files", "This permissions is needed to load the lists from files located on your device, that is all.", Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_CODE_READ);

        }else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_READ);
        }
        // END_INCLUDE(camera_permission_request)
    }
    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == REQUEST_CODE_WRITE) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
//            Log.i(TAG, "Received response for Camera permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_LONG).show();
                saveList();
            }else
            {
                Toast.makeText(getApplicationContext(), "Write permission denied, cannot save", Toast.LENGTH_LONG).show();
            }

            // END_INCLUDE(permission_result)

        } else if(requestCode == REQUEST_CODE_READ)
        {
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_LONG).show();
                loadList();
            }else
            {
                Toast.makeText(getApplicationContext(), "Read permission denied, cannot load", Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }
    /**
     * Clicking the back button on the title bar returns to the previous activity on the stack
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.clearAll:
                listLayout.removeAllViews();
                listOfItems.clear();
                return true;

            case R.id.delete:
                deleteSavedList();
                return true;
            case R.id.load:
                loadList();
                return true;
            case R.id.save:
                saveList();
                return true;


        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method for back button on title bar
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    /**
     * What to do when the acitivty is destroyed
     */
    public void onDestroy() {

        super.onDestroy();
        finish();
    }

    /**
     * what to do when the activity is paused
     */
    public void onPause()
    {
        super.onPause();

    }

    /**
     * what to do when the activity is resumed
     */
    public void onResume()
    {
        super.onResume();
    }



}
