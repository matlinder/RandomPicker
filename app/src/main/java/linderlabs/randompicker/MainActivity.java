package linderlabs.randompicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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
        Intent intent = new Intent(getApplicationContext(), CustomListActivity.class);

        startActivity(intent);
    }
}
