package linderlabs.randompicker;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
    private String listType;
    private ArrayList<String> listOfItems = new ArrayList<String>();
    private EditText item;
    private ImageButton trashButton;
    private LinearLayout listLayout;
    private static final String FOOD_FILE_NAMES = "food_list_names";
    private String saveFileName = "";
    private String[] fileNames;


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
                getSupportActionBar().setTitle("Random Restaurant");
            }else if(listType.equals("activities"))
            {
                getSupportActionBar().setTitle("Random Activity");
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
                FileInputStream names = null;
                //open a filestream to read the file
                if(listType.equals("food")) {
                    names = openFileInput(FOOD_FILE_NAMES);
                }else if(listType.equals("activities")){
                    names = openFileInput(FOOD_FILE_NAMES);
                }
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
                if(length != 0 && fileNames[length-1] != null)
                {
                    //open a filestream to read the file
                    names = openFileInput(fileNames[length-1]);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save List as:");

        // Set up the input
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setPadding(20,30,0,30);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveFileName = input.getText().toString();
                try {
                    FileOutputStream outputStream = openFileOutput(saveFileName, Context.MODE_PRIVATE);

                    for(String temp : listOfItems)
                    {
                        if(temp != null)
                        {
                            outputStream.write(temp.getBytes());
                            outputStream.write("\n".getBytes());
                        }
                    }
                    outputStream.close();

                    FileOutputStream outputStream2 = openFileOutput(FOOD_FILE_NAMES, Context.MODE_APPEND);
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

    public void loadList()
    {
        try {
            //open a filestream to read the file
            FileInputStream names = openFileInput(FOOD_FILE_NAMES);
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
        if(fileNames == null || fileNames.length == 0) {
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
                    for(String item : listItems)
                    {
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

    public void deleteSavedList()
    {
        try {
            //open a filestream to read the file
            FileInputStream names = openFileInput(FOOD_FILE_NAMES);
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
                    outputStream2 = openFileOutput(FOOD_FILE_NAMES, Context.MODE_PRIVATE);

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
            case R.id.about:
                Toast.makeText(getApplicationContext(),"More to come", Toast.LENGTH_LONG).show();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method for back button on title bar
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
