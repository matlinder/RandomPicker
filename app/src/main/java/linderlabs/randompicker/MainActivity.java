package linderlabs.randompicker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void startAcitivyRestaurant(View view) {

        Intent intent = new Intent(getApplicationContext(), RandomListActivity.class);
        intent.putExtra("type", "food");

        startActivity(intent);
    }

    public void startAcitivyToDo(View view) {
        Intent intent = new Intent(getApplicationContext(), RandomListActivity.class);
        intent.putExtra("type", "activities");

        startActivity(intent);
    }

    public void startAcitivyCustomList(View view) {
        Intent intent = new Intent(getApplicationContext(), NearbyActivity.class);
        startActivity(intent);
    }

    /**
     * Clicking the back button on the title bar returns to the previous activity on the stack
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.about:



        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method for back button on title bar
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
}
